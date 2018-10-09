package gov.usgs.aqcu.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.time.Instant;
import java.time.ZoneOffset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Qualifier;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDataServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescription;

import gov.usgs.aqcu.parameter.ExtremesRequestParameters;
import gov.usgs.aqcu.util.AqcuTimeUtils;
import gov.usgs.aqcu.util.TimeSeriesUtils;
import gov.usgs.aqcu.model.*;
import gov.usgs.aqcu.retrieval.*;

@Service
public class ReportBuilderService {
	public static final String REPORT_TITLE = "Extremes";
	public static final String REPORT_TYPE = "extremes";

	private static final Logger LOG = LoggerFactory.getLogger(ReportBuilderService.class);

	private LocationDescriptionListService locationDescriptionListService;
	private TimeSeriesDescriptionListService timeSeriesDescriptionListService;
	private TimeSeriesDescriptionService timeSeriesDescriptionService;
	private TimeSeriesDataCorrectedService timeSeriesDataCorrectedService;
	private QualifierLookupService qualifierLookupService;

	@Autowired
	public ReportBuilderService(
		LocationDescriptionListService locationDescriptionListService,
		TimeSeriesDescriptionListService timeSeriesDescriptionListService,
		TimeSeriesDescriptionService timeSeriesDescriptionService,
		TimeSeriesDataCorrectedService timeSeriesDataCorrectedService,
		QualifierLookupService qualifierLookupService) {
		this.timeSeriesDataCorrectedService = timeSeriesDataCorrectedService;
		this.locationDescriptionListService = locationDescriptionListService;
		this.timeSeriesDescriptionListService = timeSeriesDescriptionListService;
		this.timeSeriesDescriptionService = timeSeriesDescriptionService;
		this.qualifierLookupService = qualifierLookupService;
	}

	public ExtremesReport buildReport(ExtremesRequestParameters requestParameters, String requestingUser) {
		ExtremesReport report = new ExtremesReport();

		Map<String, TimeSeriesDescription> timeSeriesDescriptions = timeSeriesDescriptionService
				.getTimeSeriesDescriptions(requestParameters);
		List<Qualifier> qualifiers = new ArrayList<>();
		
		//Primary TS Metadata
		TimeSeriesDescription primaryDescription = timeSeriesDescriptionListService.getTimeSeriesDescription(requestParameters.getPrimaryTimeseriesIdentifier());
		ZoneOffset primaryZoneOffset = TimeSeriesUtils.getZoneOffset(primaryDescription);
		Instant primaryStartTime = requestParameters.getStartInstant(primaryZoneOffset);
		Instant primaryEndTime = requestParameters.getEndInstant(primaryZoneOffset);
		String primaryStationId = primaryDescription.getLocationIdentifier();
		
		//Time Series Corrected Data for Primary
		TimeSeriesDataServiceResponse primaryData = getCorrectedData(requestParameters.getPrimaryTimeseriesIdentifier(), primaryStartTime, primaryEndTime);
		qualifiers.addAll(primaryData.getQualifiers());
		
		//Time Series Corrected Metadata and Data for Daily
		if (timeSeriesDescriptions.containsKey(requestParameters.getDvId())) {
			//metadata
			ZoneOffset dvZoneOffset = TimeSeriesUtils.getZoneOffset(timeSeriesDescriptions.get(requestParameters.getDvId()));
			Instant dvStartTime = requestParameters.getStartInstant(dvZoneOffset);
			Instant dvEndTime = requestParameters.getEndInstant(dvZoneOffset);
			//corrected data
			TimeSeriesDataServiceResponse dvData = getCorrectedData(requestParameters.getDvId(), dvStartTime, dvEndTime);
			qualifiers.addAll(dvData.getQualifiers());
		}
				
		//Time Series Corrected Metadata and Data for Upchain		
		if (timeSeriesDescriptions.containsKey(requestParameters.getUpchainId())) {
			//metadata
			ZoneOffset upchainZoneOffset = TimeSeriesUtils.getZoneOffset(timeSeriesDescriptions.get(requestParameters.getUpchainId()));
			Instant upchainStartTime = requestParameters.getStartInstant(upchainZoneOffset);
			Instant upchainEndTime = requestParameters.getEndInstant(upchainZoneOffset);
			//corrected data
			TimeSeriesDataServiceResponse upchainData = getCorrectedData(requestParameters.getUpchainId(), upchainStartTime, upchainEndTime);
			qualifiers.addAll(upchainData.getQualifiers());
		}
				
		//Report Metadata
		report.setReportMetadata(getReportMetadata(requestParameters,
			timeSeriesDescriptions,
			primaryDescription,
			requestingUser,
			qualifiers
		));

		return report;
	}

	protected ExtremesReportMetadata getReportMetadata(ExtremesRequestParameters requestParameters, 
			Map<String, TimeSeriesDescription> timeSeriesDescriptions,
			TimeSeriesDescription primarySeriesDescription,
			String requestingUser, 
			List<Qualifier> qualifierList) {
		ExtremesReportMetadata metadata = new ExtremesReportMetadata();
		metadata.setTitle(REPORT_TITLE);
		metadata.setRequestingUser(requestingUser);
		metadata.setRequestParameters(requestParameters);
		metadata.setStationId(primarySeriesDescription.getLocationIdentifier());
		metadata.setStationName(locationDescriptionListService.getByLocationIdentifier(primarySeriesDescription.getLocationIdentifier()).getName());
		metadata.setPrimaryParameter(primarySeriesDescription.getParameter());
		metadata.setPrimaryLabel(primarySeriesDescription.getLabel());
		metadata.setTimezone(AqcuTimeUtils.getTimezone(primarySeriesDescription.getUtcOffset()));
		
		if (timeSeriesDescriptions.containsKey(requestParameters.getDvId())) {
			metadata.setDvLabel(
					timeSeriesDescriptions.get(requestParameters.getDvId()).getIdentifier());
			metadata.setDvComputation(
					timeSeriesDescriptions.get(requestParameters.getDvId()).getComputationIdentifier());
			metadata.setDvParameter(
					timeSeriesDescriptions.get(requestParameters.getDvId()).getParameter());
			metadata.setDvUnit(
					timeSeriesDescriptions.get(requestParameters.getDvId()).getUnit());
		}
		
		if (timeSeriesDescriptions.containsKey(requestParameters.getUpchainId())) {
			metadata.setUpchainLabel(
					timeSeriesDescriptions.get(requestParameters.getUpchainId()).getIdentifier());
			metadata.setUpchainParameter(
					timeSeriesDescriptions.get(requestParameters.getUpchainId()).getParameter());
			metadata.setUpchainUnit(
					timeSeriesDescriptions.get(requestParameters.getUpchainId()).getUnit());
		}
		
		if(qualifierList != null && !qualifierList.isEmpty()) {
			metadata.setQualifierMetadata(qualifierLookupService.getByQualifierList(qualifierList));
		}
		
		return metadata;
	}
	
	protected TimeSeriesDataServiceResponse getCorrectedData(String timeSeriesIdentifier, Instant startTime, Instant endTime) {
		//Fetch Corrected Data
		TimeSeriesDataServiceResponse dataResponse = timeSeriesDataCorrectedService.get(
			timeSeriesIdentifier, 
			startTime, 
			endTime);

		return dataResponse;
	}

}
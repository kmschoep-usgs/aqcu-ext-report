package gov.usgs.aqcu.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Qualifier;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDataServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescription;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesPoint;

import gov.usgs.aqcu.parameter.ExtremesRequestParameters;
import gov.usgs.aqcu.util.AqcuTimeUtils;
import gov.usgs.aqcu.util.DoubleWithDisplayUtil;
import gov.usgs.aqcu.util.TimeSeriesUtils;
import gov.usgs.aqcu.calc.MinMaxFinder;
import gov.usgs.aqcu.calc.MinMaxFinder.MinMaxSummary;
import gov.usgs.aqcu.model.*;
import gov.usgs.aqcu.retrieval.*;

@Service
public class ReportBuilderService {
	public static final String REPORT_TITLE = "Extremes";
	public static final String REPORT_TYPE = "extremes";
	public static final String PRIMARY = "Primary";
	public static final String UPCHAIN = "Upchain";

	private static final Logger LOG = LoggerFactory.getLogger(ReportBuilderService.class);

	private LocationDescriptionListService locationDescriptionListService;
	private MinMaxBuilderService minMaxBuilderService;
	private TimeSeriesDescriptionListService timeSeriesDescriptionListService;
	private TimeSeriesDataCorrectedService timeSeriesDataCorrectedService;
	private QualifierLookupService qualifierLookupService;

	@Autowired
	public ReportBuilderService(
		LocationDescriptionListService locationDescriptionListService,
		MinMaxBuilderService minMaxBuilderService,
		TimeSeriesDescriptionListService timeSeriesDescriptionListService,
		TimeSeriesDataCorrectedService timeSeriesDataCorrectedService,
		QualifierLookupService qualifierLookupService) {
		this.locationDescriptionListService = locationDescriptionListService;
		this.minMaxBuilderService = minMaxBuilderService;
		this.timeSeriesDescriptionListService = timeSeriesDescriptionListService;
		this.timeSeriesDataCorrectedService = timeSeriesDataCorrectedService;
		this.qualifierLookupService = qualifierLookupService;
	}

	public ExtremesReport buildReport(ExtremesRequestParameters requestParameters, String requestingUser) {
		ExtremesReport report = new ExtremesReport();

		Map<String, TimeSeriesDescription> timeSeriesDescriptions = timeSeriesDescriptionListService
				.getTimeSeriesDescriptions(requestParameters.getPrimaryTimeseriesIdentifier(), 
						requestParameters.getUpchainTimeseriesIdentifier(), 
						requestParameters.getDerivedTimeseriesIdentifier());
		List<Qualifier> qualifiers = new ArrayList<>();
		TimeSeriesCorrectedData upchainData = new TimeSeriesCorrectedData();
		TimeSeriesCorrectedData dvData = new TimeSeriesCorrectedData();
		ExtremesMinMax primaryMinMax = new ExtremesMinMax();
		ExtremesMinMax upchainMinMax = new ExtremesMinMax();
		ExtremesMinMax derivedMinMax = new ExtremesMinMax();
		
		//Primary TS Metadata
		TimeSeriesDescription primaryDescription = timeSeriesDescriptionListService.getTimeSeriesDescription(requestParameters.getPrimaryTimeseriesIdentifier());
		
		//Time Series Corrected Data for Primary
		TimeSeriesCorrectedData primaryData = buildTimeSeriesCorrectedData(timeSeriesDescriptions,
				requestParameters.getPrimaryTimeseriesIdentifier(), requestParameters);
		
		//Time Series Corrected Metadata and Data for derived
		if (timeSeriesDescriptions.containsKey(requestParameters.getDerivedTimeseriesIdentifier())) {
			//corrected data
			dvData = buildTimeSeriesCorrectedData(timeSeriesDescriptions,
					requestParameters.getDerivedTimeseriesIdentifier(), requestParameters);
			// get min/max of derived series
			MinMaxSummary dvMinMaxSummary = getSummary(dvData);
			// min/max primary
			derivedMinMax = minMaxBuilderService.getMinMaxSummary(dvMinMaxSummary, dvData.getName(), null, null);
			qualifiers.addAll(addQualifiers(derivedMinMax.getQualifiers()));
			report.setDv(derivedMinMax);
		}
				
		//Time Series Corrected Metadata and Data for Upchain		
		if (timeSeriesDescriptions.containsKey(requestParameters.getUpchainTimeseriesIdentifier())) {
			
			//corrected data
			upchainData = buildTimeSeriesCorrectedData(timeSeriesDescriptions,
					requestParameters.getUpchainTimeseriesIdentifier(), requestParameters);
			
			// get min/max of primary series and upchain series
			MinMaxSummary primaryMinMaxSummary = getSummary(primaryData, upchainData);
			
			// min/max primary, related upchain
			primaryMinMax = minMaxBuilderService.getMinMaxSummary(primaryMinMaxSummary, primaryData.getName(), upchainData.getName(), UPCHAIN);
			qualifiers.addAll(addQualifiers(primaryMinMax.getQualifiers()));
			// min/max upchain, related primary
			upchainMinMax = minMaxBuilderService.getMinMaxSummary(primaryMinMaxSummary, upchainData.getName(), primaryData.getName(), PRIMARY);
			qualifiers.addAll(addQualifiers(upchainMinMax.getQualifiers()));
			report.setPrimary(primaryMinMax);
			report.setUpchain(upchainMinMax);
		} else {
			MinMaxSummary primaryMinMaxSummary = getSummary(primaryData);
			// min/max primary, related upchain
			primaryMinMax = minMaxBuilderService.getMinMaxSummary(primaryMinMaxSummary, primaryData.getName(), null, null);
			report.setPrimary(primaryMinMax);
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
		metadata.setPrimaryUnit(primarySeriesDescription.getUnit());
		metadata.setPrimaryLabel(primarySeriesDescription.getIdentifier());
		metadata.setTimezone(AqcuTimeUtils.getTimezone(primarySeriesDescription.getUtcOffset()));
		
		if (timeSeriesDescriptions.containsKey(requestParameters.getDerivedTimeseriesIdentifier())) {
			metadata.setDvLabel(
					timeSeriesDescriptions.get(requestParameters.getDerivedTimeseriesIdentifier()).getIdentifier());
			metadata.setDvComputation(
					timeSeriesDescriptions.get(requestParameters.getDerivedTimeseriesIdentifier()).getComputationIdentifier());
			metadata.setDvParameter(
					timeSeriesDescriptions.get(requestParameters.getDerivedTimeseriesIdentifier()).getParameter());
			metadata.setDvUnit(
					timeSeriesDescriptions.get(requestParameters.getDerivedTimeseriesIdentifier()).getUnit());
		}
		
		if (timeSeriesDescriptions.containsKey(requestParameters.getUpchainTimeseriesIdentifier())) {
			metadata.setUpchainLabel(
					timeSeriesDescriptions.get(requestParameters.getUpchainTimeseriesIdentifier()).getIdentifier());
			metadata.setUpchainParameter(
					timeSeriesDescriptions.get(requestParameters.getUpchainTimeseriesIdentifier()).getParameter());
			metadata.setUpchainUnit(
					timeSeriesDescriptions.get(requestParameters.getUpchainTimeseriesIdentifier()).getUnit());
		}
		
		if(qualifierList != null && !qualifierList.isEmpty()) {
			metadata.setQualifierMetadata(qualifierLookupService.getByQualifierList(qualifierList));
		}
		
		return metadata;
	}
	
	protected TimeSeriesCorrectedData buildTimeSeriesCorrectedData(
			Map<String, TimeSeriesDescription> timeSeriesDescriptions, String timeSeriesIdentifier,
			ExtremesRequestParameters requestParameters) {
		TimeSeriesCorrectedData timeSeriesCorrectedData = null;

		if (timeSeriesDescriptions != null && timeSeriesDescriptions.containsKey(timeSeriesIdentifier)) {
			boolean isDaily = TimeSeriesUtils.isDailyTimeSeries(timeSeriesDescriptions.get(timeSeriesIdentifier));
			ZoneOffset zoneOffset = TimeSeriesUtils.getZoneOffset(timeSeriesDescriptions.get(timeSeriesIdentifier));
			TimeSeriesDataServiceResponse timeSeriesDataServiceResponse = timeSeriesDataCorrectedService
					.get(timeSeriesIdentifier, requestParameters, isDaily, zoneOffset);

			if (timeSeriesDataServiceResponse != null) {
				timeSeriesCorrectedData = createTimeSeriesCorrectedData(timeSeriesDataServiceResponse, isDaily,
				zoneOffset);
			}
		}

		return timeSeriesCorrectedData;
	}

	
	/**
	 * This method should only be called if the timeSeriesDataServiceResponse is not null.
	 */
	protected TimeSeriesCorrectedData createTimeSeriesCorrectedData(
			TimeSeriesDataServiceResponse timeSeriesDataServiceResponse, boolean isDaily, ZoneOffset zoneOffset) {
		TimeSeriesCorrectedData timeSeriesCorrectedData = new TimeSeriesCorrectedData();

		if (timeSeriesDataServiceResponse.getTimeRange() != null) {
			timeSeriesCorrectedData.setStartTime(AqcuTimeUtils
					.getTemporal(timeSeriesDataServiceResponse.getTimeRange().getStartTime(), isDaily, zoneOffset));
			timeSeriesCorrectedData.setEndTime(AqcuTimeUtils
					.getTemporal(timeSeriesDataServiceResponse.getTimeRange().getEndTime(), isDaily, zoneOffset));
		}

		timeSeriesCorrectedData.setUnit(timeSeriesDataServiceResponse.getUnit());
		timeSeriesCorrectedData.setType(timeSeriesDataServiceResponse.getParameter());
		timeSeriesCorrectedData.setName(timeSeriesDataServiceResponse.getUniqueId());
		timeSeriesCorrectedData.setQualifiers(timeSeriesDataServiceResponse.getQualifiers());
		

		if (timeSeriesDataServiceResponse.getPoints() != null) {
			timeSeriesCorrectedData
					.setPoints(createExtremesPoints(timeSeriesDataServiceResponse.getPoints(), isDaily, zoneOffset));
		}

		return timeSeriesCorrectedData;
	}

	/**
	 * This method should only be called if the timeSeriesPoints list is not null.
	 */
	protected List<ExtremesPoint> createExtremesPoints(List<TimeSeriesPoint> timeSeriesPoints,
			boolean isDaily, ZoneOffset zoneOffset) {
		List<ExtremesPoint> extPoints = timeSeriesPoints.parallelStream()
				.filter(x -> x.getValue().getNumeric() != null)
				.map(x -> {
					ExtremesPoint extPoint = new ExtremesPoint();
					extPoint.setTime(isDaily ? x.getTimestamp().getDateTimeOffset().minus(1, ChronoUnit.DAYS) : x.getTimestamp().getDateTimeOffset());
					extPoint.setValue(DoubleWithDisplayUtil.getRoundedValue(x.getValue()));
					return extPoint;
				})
				.collect(Collectors.toList());
		return extPoints;
	}
	
	protected MinMaxSummary getSummary(TimeSeriesCorrectedData... expectedTimeSeries) {

		HashMap<String, TimeSeriesCorrectedData> inTimeSeries = new HashMap<>();
		for(TimeSeriesCorrectedData ts : expectedTimeSeries) {
			inTimeSeries.put(ts.getName(), ts);
		}
		MinMaxSummary summary = new MinMaxFinder(inTimeSeries).getCalculatedSummary();
		
		return summary;
	}
	
	protected List<Qualifier> addQualifiers(List<Qualifier> inQuals){
		List<Qualifier> outQuals = new ArrayList<>();
		if (inQuals != null) {
			outQuals.addAll(inQuals);
		} 
		
		return outQuals;
	}

}
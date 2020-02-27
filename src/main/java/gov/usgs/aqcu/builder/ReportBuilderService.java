package gov.usgs.aqcu.builder;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Qualifier;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDataServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescription;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesPoint;

import gov.usgs.aqcu.model.ExtremesMinMax;
import gov.usgs.aqcu.model.ExtremesPoint;
import gov.usgs.aqcu.model.ExtremesQualifier;
import gov.usgs.aqcu.model.ExtremesReport;
import gov.usgs.aqcu.model.ExtremesReportMetadata;
import gov.usgs.aqcu.model.TimeSeriesMinMax;
import gov.usgs.aqcu.parameter.ExtremesRequestParameters;
import gov.usgs.aqcu.retrieval.LocationDescriptionListService;
import gov.usgs.aqcu.retrieval.QualifierLookupService;
import gov.usgs.aqcu.retrieval.TimeSeriesDataService;
import gov.usgs.aqcu.retrieval.TimeSeriesDescriptionListService;
import gov.usgs.aqcu.util.AqcuTimeUtils;
import gov.usgs.aqcu.util.LogExecutionTime;
import gov.usgs.aqcu.util.TimeSeriesUtils;

@Service
public class ReportBuilderService {
	private Logger log = LoggerFactory.getLogger(ReportBuilderService.class);
	public static final String REPORT_TITLE = "Extremes";
	public static final String REPORT_TYPE = "extremes";
	public static final String PRIMARY_RELATED_KEY = "relatedPrimary";
	public static final String UPCHAIN_RELATED_KEY = "relatedUpchain";

	private LocationDescriptionListService locationDescriptionListService;
	private MinMaxBuilderService minMaxBuilderService;
	private TimeSeriesDescriptionListService timeSeriesDescriptionListService;
	private TimeSeriesDataService timeSeriesDataService;
	private QualifierLookupService qualifierLookupService;

	@Autowired
	public ReportBuilderService(
		LocationDescriptionListService locationDescriptionListService,
		MinMaxBuilderService minMaxBuilderService,
		TimeSeriesDescriptionListService timeSeriesDescriptionListService,
		TimeSeriesDataService timeSeriesDataService,
		QualifierLookupService qualifierLookupService) {
		this.locationDescriptionListService = locationDescriptionListService;
		this.minMaxBuilderService = minMaxBuilderService;
		this.timeSeriesDescriptionListService = timeSeriesDescriptionListService;
		this.timeSeriesDataService = timeSeriesDataService;
		this.qualifierLookupService = qualifierLookupService;
	}
	
	@LogExecutionTime
	public ExtremesReport buildReport(ExtremesRequestParameters requestParameters, String requestingUser) {
	ExtremesReport report = new ExtremesReport();
	ExtremesMinMax primaryOutput = new ExtremesMinMax();
	ExtremesMinMax upchainOutput = new ExtremesMinMax();
	ExtremesMinMax derivedOutput = new ExtremesMinMax();
	List<Qualifier> qualifiers = new ArrayList<>();
	
		// All TS Metadata
		log.debug("Get time series descriptions");
		Map<String, TimeSeriesDescription> timeSeriesDescriptions = 
				timeSeriesDescriptionListService.getTimeSeriesDescriptionList(new ArrayList<>(requestParameters.getTsIdSet()))
					.stream().collect(Collectors.toMap(t -> t.getUniqueId(), t -> t));
		
		// Primary TS Data
		log.debug("Get primary time series description/data/min max points");
		TimeSeriesDescription primaryDescription = timeSeriesDescriptions.get(requestParameters.getPrimaryTimeseriesIdentifier());
		ZoneOffset primaryZoneOffset = TimeSeriesUtils.getZoneOffset(primaryDescription);
		Boolean primaryIsDaily = TimeSeriesUtils.isDailyTimeSeries(primaryDescription);
		
		TimeSeriesDataServiceResponse primaryData = timeSeriesDataService
				.get(primaryDescription.getUniqueId(), requestParameters,  primaryZoneOffset, primaryIsDaily, false, false, null);
		
		TimeSeriesMinMax primaryMinMax = null;

		if(primaryData != null && !primaryData.getPoints().isEmpty()) {
			primaryMinMax = minMaxBuilderService.findMinMaxPoints(primaryData.getPoints());
			primaryOutput.setMaxPoints(getExtremesPoints(primaryMinMax.getMaxPoints(), primaryIsDaily, primaryZoneOffset));
			primaryOutput.setMultipleMaxFlag(getMultipleMinMaxFlag(primaryMinMax.getMaxPoints().get(0),primaryData.getPoints()));
			primaryOutput.setMinPoints(getExtremesPoints(primaryMinMax.getMinPoints(), primaryIsDaily, primaryZoneOffset));
			primaryOutput.setMultipleMinFlag(getMultipleMinMaxFlag(primaryMinMax.getMinPoints().get(0),primaryData.getPoints()));
			primaryOutput.setQualifiers(getExtremesQualifiers(primaryData.getQualifiers(), primaryIsDaily, primaryZoneOffset));
			qualifiers.addAll(primaryData.getQualifiers());
		}

		// Upchain TS Data
		log.debug("Get upchain time series description/data/min max points");
		TimeSeriesDescription upchainDescription = timeSeriesDescriptions.get(requestParameters.getUpchainTimeseriesIdentifier());
		TimeSeriesDataServiceResponse upchainData = null;

		if(upchainDescription != null) {
			ZoneOffset upchainZoneOffset = TimeSeriesUtils.getZoneOffset(upchainDescription);
			Boolean upchainIsDaily = TimeSeriesUtils.isDailyTimeSeries(upchainDescription);
			upchainData = timeSeriesDataService
				.get(upchainDescription.getUniqueId(), requestParameters,  upchainZoneOffset, upchainIsDaily, false, false, null);
			if(upchainData != null && !upchainData.getPoints().isEmpty()) {
				TimeSeriesMinMax upchainMinMax = minMaxBuilderService.findMinMaxPoints(upchainData.getPoints());
				upchainOutput.setMaxPoints(getExtremesPoints(upchainMinMax.getMaxPoints(), upchainIsDaily, upchainZoneOffset));
				upchainOutput.setMultipleMaxFlag(getMultipleMinMaxFlag(upchainMinMax.getMaxPoints().get(0),upchainData.getPoints()));
				upchainOutput.setMinPoints(getExtremesPoints(upchainMinMax.getMinPoints(), upchainIsDaily, upchainZoneOffset));
				upchainOutput.setMultipleMinFlag(getMultipleMinMaxFlag(upchainMinMax.getMinPoints().get(0),upchainData.getPoints()));
				upchainOutput.setQualifiers(getExtremesQualifiers(upchainData.getQualifiers(), upchainIsDaily, upchainZoneOffset));
				qualifiers.addAll(upchainData.getQualifiers());

				// Find related data
				if(primaryMinMax != null && upchainMinMax != null) {
					TimeSeriesMinMax relatedUpchainMinMax = minMaxBuilderService.findMinMaxMatchingPoints(primaryMinMax, upchainData.getPoints());
					TimeSeriesMinMax relatedPrimaryMinMax = minMaxBuilderService.findMinMaxMatchingPoints(upchainMinMax, primaryData.getPoints());

					primaryOutput.setMaxRelatedPoints(
						getExtremesPoints(relatedUpchainMinMax.getMaxPoints(), upchainIsDaily, upchainZoneOffset),
						UPCHAIN_RELATED_KEY
					);
					primaryOutput.setMinRelatedPoints(
						getExtremesPoints(relatedUpchainMinMax.getMinPoints(), upchainIsDaily, upchainZoneOffset),
						UPCHAIN_RELATED_KEY
					);
					upchainOutput.setMaxRelatedPoints(
						getExtremesPoints(relatedPrimaryMinMax.getMaxPoints(), primaryIsDaily, primaryZoneOffset),
						PRIMARY_RELATED_KEY
					);
					upchainOutput.setMinRelatedPoints(
						getExtremesPoints(relatedPrimaryMinMax.getMinPoints(), primaryIsDaily, primaryZoneOffset),
						PRIMARY_RELATED_KEY
					);
				}
			}
		}
		
		// derived TS Data
		log.debug("Get derived time series description/data/min max points");
		TimeSeriesDescription derivedDescription = timeSeriesDescriptions.get(requestParameters.getDerivedTimeseriesIdentifier());
		TimeSeriesDataServiceResponse derivedData = null;

		if(derivedDescription != null) {
			ZoneOffset derivedZoneOffset = TimeSeriesUtils.getZoneOffset(derivedDescription);
			derivedData = timeSeriesDataService
				.get(derivedDescription.getUniqueId(), requestParameters,  derivedZoneOffset, true, false, false, null);

			if(derivedData != null && !derivedData.getPoints().isEmpty()) {
				TimeSeriesMinMax derivedMinMax = minMaxBuilderService.findMinMaxPoints(derivedData.getPoints());
				derivedOutput.setMaxPoints(getExtremesPoints(derivedMinMax.getMaxPoints(), true, derivedZoneOffset));
				derivedOutput.setMultipleMaxFlag(getMultipleMinMaxFlag(derivedMinMax.getMaxPoints().get(0),derivedData.getPoints()));
				derivedOutput.setMinPoints(getExtremesPoints(derivedMinMax.getMinPoints(), true, derivedZoneOffset));
				derivedOutput.setMultipleMinFlag(getMultipleMinMaxFlag(derivedMinMax.getMinPoints().get(0),derivedData.getPoints()));
				derivedOutput.setQualifiers(getExtremesQualifiers(derivedData.getQualifiers(), true, derivedZoneOffset));
				qualifiers.addAll(derivedData.getQualifiers());
			}
		}

		// Output to report
		report.setPrimary(primaryOutput);
		report.setUpchain(upchainOutput);
		report.setDv(derivedOutput);

		//Report Metadata
		report.setReportMetadata(getReportMetadata(requestParameters,
			timeSeriesDescriptions,
			primaryDescription,
			requestingUser,
			qualifiers
		));
		return report;
	}

	protected List<ExtremesPoint> getExtremesPoints(List<TimeSeriesPoint> points, Boolean isDaily, ZoneOffset zoneOffset) {
		if(points != null && !points.isEmpty()) {
			return points.stream().map(p -> new ExtremesPoint(p, isDaily, zoneOffset)).collect(Collectors.toList());
		}
		return new ArrayList<>();
	}

	protected List<ExtremesQualifier> getExtremesQualifiers(List<Qualifier> quals, Boolean isDaily, ZoneOffset zoneOffset) {
		if(quals != null && !quals.isEmpty()) {
			return quals.stream().map(q -> new ExtremesQualifier(q, isDaily, zoneOffset)).collect(Collectors.toList());
		}
		return new ArrayList<>();
	}
	
	protected ExtremesReportMetadata getReportMetadata(ExtremesRequestParameters requestParameters, 
			Map<String, TimeSeriesDescription> timeSeriesDescriptions,
			TimeSeriesDescription primarySeriesDescription,
			String requestingUser, 
			List<Qualifier> qualifierList) {
		ExtremesReportMetadata metadata = new ExtremesReportMetadata();
		try {
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
		} catch (Exception e) {
			log.error("Exception in getReportMetadata: ", e.getMessage());
		}
		return metadata;
	}
	
	protected Boolean getMultipleMinMaxFlag(TimeSeriesPoint extremePoint, List<TimeSeriesPoint> points) {
		boolean multipleMinMax = false;
		
		List<TimeSeriesPoint> multipleMinMaxPoints = points.stream()
				.filter(p -> p.getValue().getDisplay().equals(extremePoint.getValue().getDisplay())
						&& p.getTimestamp().DateTimeOffset.compareTo(extremePoint.getTimestamp().DateTimeOffset) != 0)
				.collect(Collectors.toList());
		
		if (multipleMinMaxPoints.size() > 0) {
			multipleMinMax = true;
		}
		
		return multipleMinMax;
	}
}
package gov.usgs.aqcu.retrieval;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDataCorrectedServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDataRawServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDataServiceResponse;

import gov.usgs.aqcu.parameter.DateRangeRequestParameters;

@Repository
public class ExtTimeSeriesDataService {
	private static final Logger LOG = LoggerFactory.getLogger(TimeSeriesDataService.class);

	private AquariusRetrievalService aquariusRetrievalService;

	@Autowired
	public ExtTimeSeriesDataService(AquariusRetrievalService aquariusRetrievalService) {
		this.aquariusRetrievalService = aquariusRetrievalService;
	}

	public TimeSeriesDataServiceResponse get(String timeseriesIdentifier, DateRangeRequestParameters requestParameters, ZoneOffset zoneOffset, Boolean isDaily, Boolean isRaw, Boolean doIncludeGaps, String getParts) {
		TimeSeriesDataServiceResponse timeSeriesResponse = new TimeSeriesDataServiceResponse();

		//Daily values time series need to be offset a day into the future to handle the "2400" situation.
		//Instant startDate = adjustIfDv(requestParameters.getStartInstant(zoneOffset), isDaily);
		Instant endDate = adjustIfDv(requestParameters.getEndInstant(zoneOffset), isDaily);

		try {
			timeSeriesResponse = get(timeseriesIdentifier, requestParameters.getStartInstant(zoneOffset), endDate, isRaw, doIncludeGaps, getParts);
		} catch (Exception e) {
			String msg = "An unexpected error occurred while attempting to fetch TimeSeriesData" + 
				(isRaw ? "Raw" : "Corrected") + "ServiceRequest from Aquarius: ";
			LOG.error(msg, e);
			throw new RuntimeException(msg, e);
		}
		
		if(isDaily) {
			timeSeriesResponse.setPoints(new ArrayList<>(timeSeriesResponse.getPoints().subList(1, timeSeriesResponse.getPoints().size())));
		}
		return timeSeriesResponse;
	}

	protected TimeSeriesDataServiceResponse get(String timeSeriesIdentifier, Instant startDate, Instant endDate, Boolean isRaw, Boolean doIncludeGaps, String getParts) {
		TimeSeriesDataServiceResponse timeSeriesResponse;

		if(isRaw != null && !isRaw) {
			TimeSeriesDataCorrectedServiceRequest request = new TimeSeriesDataCorrectedServiceRequest()
				.setTimeSeriesUniqueId(timeSeriesIdentifier)
				.setQueryFrom(startDate)
				.setQueryTo(endDate)
				.setApplyRounding(true)
				.setIncludeGapMarkers(doIncludeGaps)
				.setGetParts(getParts);
			timeSeriesResponse = aquariusRetrievalService.executePublishApiRequest(request);
		} else {
			TimeSeriesDataRawServiceRequest request = new TimeSeriesDataRawServiceRequest()
				.setTimeSeriesUniqueId(timeSeriesIdentifier)
				.setQueryFrom(startDate)
				.setQueryTo(endDate)
				.setApplyRounding(true)
				.setGetParts(getParts);
			timeSeriesResponse = aquariusRetrievalService.executePublishApiRequest(request);
		}
		
		return timeSeriesResponse;
	}

	protected Instant adjustIfDv(Instant instant, boolean isDaily) {
		return isDaily ? instant.plus(Duration.ofDays(1)) : instant;
	}
}

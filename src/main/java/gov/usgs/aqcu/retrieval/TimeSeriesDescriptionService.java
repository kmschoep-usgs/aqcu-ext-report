package gov.usgs.aqcu.retrieval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescription;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescriptionListByUniqueIdServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescriptionListByUniqueIdServiceResponse;

import gov.usgs.aqcu.parameter.ExtremesRequestParameters;

@Repository
public class TimeSeriesDescriptionService {
	private static final Logger LOG = LoggerFactory.getLogger(TimeSeriesDescriptionService.class);

	private AquariusRetrievalService aquariusRetrievalService;

	@Autowired
	public TimeSeriesDescriptionService(AquariusRetrievalService aquariusRetrievalService) {
		this.aquariusRetrievalService = aquariusRetrievalService;
	}

	public Map<String, TimeSeriesDescription> getTimeSeriesDescriptions(ExtremesRequestParameters requestParameters) {
		ArrayList<String> uniqueTimeseriesIdentifiers = buildUniqueIdentifierList(requestParameters);
		Map<String, TimeSeriesDescription> timeSeriesDescriptions = new HashMap<>();

		try {
			List<TimeSeriesDescription> response = get(uniqueTimeseriesIdentifiers);
			timeSeriesDescriptions = buildDescriptionMap(uniqueTimeseriesIdentifiers, response);
		} catch (Exception e) {
			String msg = "An unexpected error occurred while attempting to fetch TimeSeriesDescriptions from Aquarius: ";
			LOG.error(msg, e);
			throw new RuntimeException(msg, e);
		}

		return timeSeriesDescriptions;
	}

	protected ArrayList<String> buildUniqueIdentifierList(ExtremesRequestParameters requestParameters) {
		ArrayList<String> timeseriesIdentifiers = new ArrayList<>();

		if (StringUtils.isNotBlank(requestParameters.getPrimaryTimeseriesIdentifier())) {
			timeseriesIdentifiers.add(requestParameters.getPrimaryTimeseriesIdentifier());
		}

		if (StringUtils.isNotBlank(requestParameters.getDerivedTimeseriesIdentifier())
				&& !timeseriesIdentifiers.contains(requestParameters.getDerivedTimeseriesIdentifier())) {
			timeseriesIdentifiers.add(requestParameters.getDerivedTimeseriesIdentifier());
		}

		if (StringUtils.isNotBlank(requestParameters.getUpchainTimeseriesIdentifier())
				&& !timeseriesIdentifiers.contains(requestParameters.getUpchainTimeseriesIdentifier())) {
			timeseriesIdentifiers.add(requestParameters.getUpchainTimeseriesIdentifier());
		}

		return timeseriesIdentifiers;
	}

	protected List<TimeSeriesDescription> get(ArrayList<String> timeSeriesUniqueIds) throws Exception {
		TimeSeriesDescriptionListByUniqueIdServiceRequest request = new TimeSeriesDescriptionListByUniqueIdServiceRequest()
				.setTimeSeriesUniqueIds(timeSeriesUniqueIds);
		TimeSeriesDescriptionListByUniqueIdServiceResponse tssDesc = aquariusRetrievalService.executePublishApiRequest(request);
		return tssDesc.getTimeSeriesDescriptions();
	}

	protected Map<String, TimeSeriesDescription> buildDescriptionMap(List<String> uniqueTimeseriesIdentifiers, List<TimeSeriesDescription> timeSeriesDescriptions) {
		if (uniqueTimeseriesIdentifiers.size() != timeSeriesDescriptions.size()) {
			String errorString = "Failed to fetch descriptions for all requested Time Series Identifiers: \nRequested: " + 
				uniqueTimeseriesIdentifiers.size() + "\nGot: " + timeSeriesDescriptions.size();
			LOG.error(errorString);
			throw new RuntimeException(errorString);
		}

		Map<String, TimeSeriesDescription> descriptionMap = timeSeriesDescriptions.stream().collect(Collectors.toMap(x -> x.getUniqueId(), x -> x));
		return descriptionMap;
	}

}

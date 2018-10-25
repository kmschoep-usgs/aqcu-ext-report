package gov.usgs.aqcu.retrieval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescription;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescriptionListByUniqueIdServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescriptionListByUniqueIdServiceResponse;

@Repository
public class TimeSeriesDescriptionListService {
	private static final Logger LOG = LoggerFactory.getLogger(TimeSeriesDescriptionListService.class);

	private AquariusRetrievalService aquariusRetrievalService;

	@Autowired
	public TimeSeriesDescriptionListService(AquariusRetrievalService aquariusRetrievalService) {
		this.aquariusRetrievalService = aquariusRetrievalService;
	}

	public Map<String, TimeSeriesDescription> getTimeSeriesDescriptions(String...timeSeriesUniqueIds) {
		ArrayList<String> uniqueTimeseriesIdentifiers = new ArrayList<>();
		for (String id : timeSeriesUniqueIds) {
			uniqueTimeseriesIdentifiers.add(id);
		}
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
	
	public TimeSeriesDescription getTimeSeriesDescription(String timeSeriesUniqueIds) {
		TimeSeriesDescription timeSeriesDescription = getTimeSeriesDescriptions(timeSeriesUniqueIds).get(timeSeriesUniqueIds);
		return timeSeriesDescription;
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

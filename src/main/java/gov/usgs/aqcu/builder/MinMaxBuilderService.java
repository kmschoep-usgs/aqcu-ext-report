package gov.usgs.aqcu.builder;

import java.util.Map;
import java.util.List;

import static gov.usgs.aqcu.calc.OrderingComparators.MAX;
import static gov.usgs.aqcu.calc.OrderingComparators.MIN;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import gov.usgs.aqcu.calc.MinMaxFinder.MinMaxSummary;
import gov.usgs.aqcu.calc.OrderingComparators;
import gov.usgs.aqcu.model.ExtremesMinMax;
import gov.usgs.aqcu.model.ExtremesPoint;

@Service
public class MinMaxBuilderService {
	private static final Logger LOG = LoggerFactory.getLogger(MinMaxBuilderService.class);
	
	public ExtremesMinMax getMinMaxSummary(MinMaxSummary minMaxSummary, String primarySeriesIdentifier, String relatedSeriesIdentifier, String related){
		try {
			ExtremesMinMax result = new ExtremesMinMax();
			Map<String, List<ExtremesPoint>> maxSummary = new HashMap<>();
			Map<String, List<ExtremesPoint>> minSummary = new HashMap<>();
			// max primary, related 
			maxSummary = getPoints(MAX, minMaxSummary, primarySeriesIdentifier, relatedSeriesIdentifier, related);
					
			//min primary, related 
			minSummary = getPoints(MIN, minMaxSummary, primarySeriesIdentifier, relatedSeriesIdentifier, related);
			
			result.setMax(maxSummary);
			result.setMin(minSummary);
			result.setQualifiers(minMaxSummary.getQualifiers(primarySeriesIdentifier));				
			
			return result;
		} catch (Exception e) {
			String msg = "An unexpected error occurred while attempting to build min/max summaries: ";
			LOG.error(msg, e);
			throw new RuntimeException(msg, e);
		}
	}
	
	private Map<String, List<ExtremesPoint>> getPoints(OrderingComparators minOrMax, MinMaxSummary primaryMinMaxSummary, String primarySeriesIdentifier, String relatedSeriesIdentifier, String related){
		try {
			Map<String, List<ExtremesPoint>> result = new HashMap<>();
			result.put("points", primaryMinMaxSummary.get(minOrMax, primarySeriesIdentifier));
			if (relatedSeriesIdentifier != null) {
				result.put("related" + related, primaryMinMaxSummary.getAt(minOrMax, primarySeriesIdentifier, relatedSeriesIdentifier));				
			}
			return result;
		} catch (Exception e) {
			String msg = "An unexpected error occurred while attempting to build min/max summaries: ";
			LOG.error(msg, e);
			throw new RuntimeException(msg, e);
		}
	}
	
}
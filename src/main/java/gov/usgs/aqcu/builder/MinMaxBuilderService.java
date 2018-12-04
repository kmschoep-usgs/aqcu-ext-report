package gov.usgs.aqcu.builder;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.List;

import static gov.usgs.aqcu.calc.OrderingComparators.MAX;
import static gov.usgs.aqcu.calc.OrderingComparators.MIN;

import java.time.ZoneOffset;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescription;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesPoint;

import gov.usgs.aqcu.calc.MinMaxFinder.MinMaxSummary;
import gov.usgs.aqcu.calc.OrderingComparators;
import gov.usgs.aqcu.model.ExtremesMinMax;
import gov.usgs.aqcu.model.ExtremesPoint;
import gov.usgs.aqcu.util.AqcuTimeUtils;
import gov.usgs.aqcu.util.DoubleWithDisplayUtil;
import gov.usgs.aqcu.util.TimeSeriesUtils;

@Service
public class MinMaxBuilderService {
	private static final Logger LOG = LoggerFactory.getLogger(MinMaxBuilderService.class);
	
	public ExtremesMinMax getMinMaxSummary(MinMaxSummary minMaxSummary, String primarySeriesIdentifier, String relatedSeriesIdentifier, String related, Map<String, TimeSeriesDescription> timeSeriesDescriptions){
		try {
			ExtremesMinMax result = new ExtremesMinMax();
			Map<String, List<ExtremesPoint>> maxSummary = new HashMap<>();
			Map<String, List<ExtremesPoint>> minSummary = new HashMap<>();
			// max primary, related 
			maxSummary = getPoints(MAX, minMaxSummary, primarySeriesIdentifier, relatedSeriesIdentifier, related, timeSeriesDescriptions);
					
			//min primary, related 
			minSummary = getPoints(MIN, minMaxSummary, primarySeriesIdentifier, relatedSeriesIdentifier, related, timeSeriesDescriptions);
			
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
	
	private Map<String, List<ExtremesPoint>> getPoints(OrderingComparators minOrMax, MinMaxSummary primaryMinMaxSummary, String primarySeriesIdentifier, String relatedSeriesIdentifier, String related, Map<String, TimeSeriesDescription> timeSeriesDescriptions){
		boolean isDaily = TimeSeriesUtils.isDailyTimeSeries(timeSeriesDescriptions.get(primarySeriesIdentifier));
		ZoneOffset zoneOffset = TimeSeriesUtils.getZoneOffset(timeSeriesDescriptions.get(primarySeriesIdentifier));
		try {
			Map<String, List<ExtremesPoint>> result = new HashMap<>();
			result.put("points", createExtremesPoints(primaryMinMaxSummary.get(minOrMax, primarySeriesIdentifier), isDaily, zoneOffset));
			if (relatedSeriesIdentifier != null) {
				isDaily = TimeSeriesUtils.isDailyTimeSeries(timeSeriesDescriptions.get(relatedSeriesIdentifier));
				zoneOffset = TimeSeriesUtils.getZoneOffset(timeSeriesDescriptions.get(relatedSeriesIdentifier));
				result.put("related" + related, createExtremesPoints(primaryMinMaxSummary.getAt(minOrMax, primarySeriesIdentifier, relatedSeriesIdentifier), isDaily, zoneOffset));				
			}
			return result;
		} catch (Exception e) {
			String msg = "An unexpected error occurred while attempting to build min/max summaries: ";
			LOG.error(msg, e);
			throw new RuntimeException(msg, e);
		}
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
					extPoint.setTime(AqcuTimeUtils.getTemporal(x.getTimestamp(), isDaily, zoneOffset));
					extPoint.setValue(DoubleWithDisplayUtil.getRoundedValue(x.getValue()));
					return extPoint;
				})
				.collect(Collectors.toList());
		return extPoints;
	}
	
}
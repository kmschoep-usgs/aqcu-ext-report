package gov.usgs.aqcu.builder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.math.BigDecimal;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import gov.usgs.aqcu.model.TimeSeriesMinMax;
import gov.usgs.aqcu.util.DoubleWithDisplayUtil;
import gov.usgs.aqcu.util.LogStep;

@Service
public class MinMaxBuilderService {
	private Logger log = LoggerFactory.getLogger(MinMaxBuilderService.class);
	
	@LogStep
	public TimeSeriesMinMax findMinMaxPoints(List<TimeSeriesPoint> points) {
        TimeSeriesMinMax result = new TimeSeriesMinMax();

        List<TimeSeriesPoint> maxSet = new ArrayList<>();
        List<TimeSeriesPoint> minSet = new ArrayList<>();
        BigDecimal maxValue = null;
        BigDecimal minValue = null;
        
        try {
	        if(points != null && !points.isEmpty()) {
	            for(TimeSeriesPoint point : points) {
	                BigDecimal pointValue = DoubleWithDisplayUtil.getRoundedValue(point.getValue());
	
	                // Check for Max
	                if(maxValue == null || pointValue.compareTo(maxValue) > 0) {
	                    maxValue = pointValue;
	                    maxSet = new ArrayList<>();
	                    maxSet.add(point);
	                } else if(pointValue.compareTo(maxValue) == 0) {
	                    maxSet.add(point);
	                }
	
	                // Check for Min
	                if(minValue == null || pointValue.compareTo(minValue) < 0) {
	                    minValue = pointValue;
	                    minSet = new ArrayList<>();
	                    minSet.add(point);
	                } else if(pointValue.compareTo(minValue) == 0) {
	                    minSet.add(point);
	                }
	            }
	        }
	
	        result.setMaxPoints(maxSet);
	        result.setMinPoints(minSet);
        } catch (Exception e) {
        	log.error("Exception in findMinMaxPoints: ", e.getMessage());
        }
	    return result;
        
    }
	
	@LogStep
    public TimeSeriesMinMax findMinMaxMatchingPoints(TimeSeriesMinMax primaryMinMax, List<TimeSeriesPoint> relatedPoints) {
        TimeSeriesMinMax result = new TimeSeriesMinMax();

        if(primaryMinMax != null) {
            result.setMaxPoints(findMatchingPoints(primaryMinMax.getMaxPoints(), relatedPoints));
            result.setMinPoints(findMatchingPoints(primaryMinMax.getMinPoints(), relatedPoints));
        }
        
        return result;
    }
	
	@LogStep
    protected List<TimeSeriesPoint> findMatchingPoints(List<TimeSeriesPoint> primaryPoints, List<TimeSeriesPoint> relatedPoints) {
        List<TimeSeriesPoint> matchingPoints = new ArrayList<>();
        
        try {
	        if(primaryPoints != null && !primaryPoints.isEmpty() && relatedPoints != null && !relatedPoints.isEmpty()) {
	            Map<Instant, TimeSeriesPoint> relatedPointMap = pointListToMap(relatedPoints);
	
	            for(TimeSeriesPoint primaryPoint : primaryPoints) {
	                TimeSeriesPoint relatedPoint = relatedPointMap.get(primaryPoint.getTimestamp().DateTimeOffset);
	                if(relatedPoint != null) {
	                    matchingPoints.add(relatedPoint);
	                }
	            }
	        }
        } catch (Exception e) {
        	log.error("Exception in findMatchingPoints: ", e.getMessage());
        }
        return matchingPoints;
    }

	@LogStep
    protected Map<Instant, TimeSeriesPoint> pointListToMap(List<TimeSeriesPoint> pointList) {
    	try {
	        if(pointList != null && !pointList.isEmpty()) {
	            return pointList.stream().collect(Collectors.toMap(t -> t.getTimestamp().getDateTimeOffset(), t->t));
	        } 
    	} catch (Exception e) {
        	log.error("Exception in pointListToMap: ", e.getMessage());
        }
	        
        return new HashMap<>();
    }
}
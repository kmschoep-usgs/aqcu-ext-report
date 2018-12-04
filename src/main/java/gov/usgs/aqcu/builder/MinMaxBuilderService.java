package gov.usgs.aqcu.builder;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.stereotype.Service;

import gov.usgs.aqcu.model.TimeSeriesMinMax;
import gov.usgs.aqcu.util.DoubleWithDisplayUtil;

@Service
public class MinMaxBuilderService {
    public TimeSeriesMinMax findMinMaxPoints(List<TimeSeriesPoint> points) {
        TimeSeriesMinMax result = new TimeSeriesMinMax();

        List<TimeSeriesPoint> maxSet = new ArrayList<>();
        List<TimeSeriesPoint> minSet = new ArrayList<>();
        BigDecimal maxValue = null;
        BigDecimal minValue = null;

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
        
        return result;
    }

    public TimeSeriesMinMax findMinMaxMatchingPoints(TimeSeriesMinMax primaryMinMax, List<TimeSeriesPoint> relatedPoints) {
        TimeSeriesMinMax result = new TimeSeriesMinMax();

        if(relatedPoints != null && !relatedPoints.isEmpty()) {
            result.setMaxPoints(findMatchingPoints(primaryMinMax.getMaxPoints(), relatedPoints));
            result.setMinPoints(findMatchingPoints(primaryMinMax.getMinPoints(), relatedPoints));
        }
        
        return result;
    }

    protected List<TimeSeriesPoint> findMatchingPoints(List<TimeSeriesPoint> primaryPoints, List<TimeSeriesPoint> relatedPoints) {
        List<TimeSeriesPoint> matchingPoints = new ArrayList<>();

        if(primaryPoints != null && !primaryPoints.isEmpty()) {
            Map<Instant, TimeSeriesPoint> relatedPointMap = pointListToMap(relatedPoints);

            for(TimeSeriesPoint primaryPoint : primaryPoints) {
                TimeSeriesPoint relatedPoint = relatedPointMap.get(primaryPoint.getTimestamp().DateTimeOffset);
                if(relatedPoint != null) {
                    matchingPoints.add(relatedPoint);
                }
            }
        }

        return matchingPoints;
    }

    protected Map<Instant, TimeSeriesPoint> pointListToMap(List<TimeSeriesPoint> pointList) {
        if(pointList != null && !pointList.isEmpty()) {
            return pointList.stream().collect(Collectors.toMap(t -> t.getTimestamp().getDateTimeOffset(), t->t));
        }
        return new HashMap<>();
    }
}
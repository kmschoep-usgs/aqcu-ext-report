package gov.usgs.aqcu.model;

import java.util.ArrayList;
import java.util.List;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesPoint;

public class TimeSeriesMinMax {
    private List<TimeSeriesPoint> minPoints;
    private List<TimeSeriesPoint> maxPoints;
    
    public TimeSeriesMinMax() {
        minPoints = new ArrayList<>();
        maxPoints = new ArrayList<>();
    };

    public void setMaxPoints(List<TimeSeriesPoint> maxPoints) {
        this.maxPoints = maxPoints;
    }

    public void setMinPoints(List<TimeSeriesPoint> minPoints) {
        this.minPoints = minPoints;
    }

    public List<TimeSeriesPoint> getMaxPoints() {
        return maxPoints;
    }

    public List<TimeSeriesPoint> getMinPoints() {
        return minPoints;
    }
}
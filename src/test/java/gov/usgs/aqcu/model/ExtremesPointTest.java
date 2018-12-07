package gov.usgs.aqcu.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.DoubleWithDisplay;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.StatisticalDateTimeOffset;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesPoint;

import org.junit.Test;

public class ExtremesPointTest {
    
	private Instant time = Instant.parse("2000-01-02T09:32:00Z");
    private TimeSeriesPoint timeSeriesPoint;

	@Test
	public void constructorTestFull() {
        timeSeriesPoint = new TimeSeriesPoint()
            .setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(time).setRepresentsEndOfTimePeriod(true))
            .setValue(new DoubleWithDisplay().setNumeric(1.0D).setDisplay("1.0"));
        ExtremesPoint testPoint = new ExtremesPoint(timeSeriesPoint, true, ZoneOffset.UTC);
        assertEquals(testPoint.getTime(), LocalDate.parse("2000-01-01"));
        assertEquals(testPoint.getValue(), BigDecimal.valueOf(1.0D));
        testPoint = new ExtremesPoint(timeSeriesPoint, false, ZoneOffset.UTC);
        assertEquals(testPoint.getTime(), time);
        assertEquals(testPoint.getValue(), BigDecimal.valueOf(1.0D));
    }

	@Test
	public void constructorTestNoTime() {
        timeSeriesPoint = new TimeSeriesPoint()
            .setValue(new DoubleWithDisplay().setNumeric(1.0D).setDisplay("1.0"));
		ExtremesPoint testPoint = new ExtremesPoint(timeSeriesPoint, true, ZoneOffset.UTC);
        assertNull(testPoint.getTime());
        assertEquals(testPoint.getValue(), BigDecimal.valueOf(1.0D));
    }

	@Test
	public void constructorTestNoValue() {
        timeSeriesPoint = new TimeSeriesPoint()
            .setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(time).setRepresentsEndOfTimePeriod(true));
        ExtremesPoint testPoint = new ExtremesPoint(timeSeriesPoint, true, ZoneOffset.UTC);
        assertEquals(testPoint.getTime(), LocalDate.parse("2000-01-01"));
        assertNull(testPoint.getValue());
    }

	@Test
	public void constructorTestNoTimeOrValue() {
        timeSeriesPoint = new TimeSeriesPoint();
        ExtremesPoint testPoint = new ExtremesPoint(timeSeriesPoint, true, ZoneOffset.UTC);
        assertNull(testPoint.getTime());
        assertNull(testPoint.getValue());
    }
}
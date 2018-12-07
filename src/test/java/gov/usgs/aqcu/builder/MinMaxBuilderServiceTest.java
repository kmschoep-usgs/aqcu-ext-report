package gov.usgs.aqcu.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.DoubleWithDisplay;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.StatisticalDateTimeOffset;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesPoint;

import org.junit.Before;
import org.junit.Test;

import gov.usgs.aqcu.model.TimeSeriesMinMax;

public class MinMaxBuilderServiceTest {
	private MinMaxBuilderService service;

	@Before
	public void setup() {
		service = new MinMaxBuilderService();
	}

	@Test
	public void pointListToMapTest() {
		List<TimeSeriesPoint> pointList = Arrays.asList(
			new TimeSeriesPoint()
				.setTimestamp(new StatisticalDateTimeOffset()
					.setDateTimeOffset(Instant.parse("2018-01-01T00:00:00Z"))
					.setRepresentsEndOfTimePeriod(false)
				)
				.setValue(new DoubleWithDisplay()
					.setDisplay("1.0")
					.setNumeric(1.0D)
				),
			new TimeSeriesPoint()
				.setTimestamp(new StatisticalDateTimeOffset()
					.setDateTimeOffset(Instant.parse("2018-01-02T00:00:00Z"))
					.setRepresentsEndOfTimePeriod(false)
				)
				.setValue(new DoubleWithDisplay()
					.setDisplay("2.0")
					.setNumeric(2.0D)
				)
		);

		Map<Instant, TimeSeriesPoint> result = service.pointListToMap(pointList);
		assertEquals(result.size(), 2);
		assertEquals(result.get(pointList.get(0).getTimestamp().getDateTimeOffset()), pointList.get(0));
		assertEquals(result.get(pointList.get(1).getTimestamp().getDateTimeOffset()), pointList.get(1));
	}

	@Test
	public void pointListToMapEmptyTest() {
		Map<Instant, TimeSeriesPoint> result = service.pointListToMap(new ArrayList<>());
		assertTrue(result.isEmpty());
	}

	@Test
	public void pointListToMapNullTest() {
		Map<Instant, TimeSeriesPoint> result = service.pointListToMap(null);
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void findMatchingPointsTest() {
		List<TimeSeriesPoint> pointList = Arrays.asList(
			new TimeSeriesPoint()
				.setTimestamp(new StatisticalDateTimeOffset()
					.setDateTimeOffset(Instant.parse("2018-01-01T00:00:00Z"))
					.setRepresentsEndOfTimePeriod(false)
				)
				.setValue(new DoubleWithDisplay()
					.setDisplay("1.0")
					.setNumeric(1.0D)
				),
			new TimeSeriesPoint()
				.setTimestamp(new StatisticalDateTimeOffset()
					.setDateTimeOffset(Instant.parse("2018-01-02T00:00:00Z"))
					.setRepresentsEndOfTimePeriod(false)
				)
				.setValue(new DoubleWithDisplay()
					.setDisplay("2.0")
					.setNumeric(2.0D)
				)
		);

		List<TimeSeriesPoint> relatedPointList1 = Arrays.asList(
			new TimeSeriesPoint()
				.setTimestamp(new StatisticalDateTimeOffset()
					.setDateTimeOffset(Instant.parse("2018-01-01T00:00:00Z"))
					.setRepresentsEndOfTimePeriod(false)
				)
				.setValue(new DoubleWithDisplay()
					.setDisplay("1.1")
					.setNumeric(1.1D)
				),
			new TimeSeriesPoint()
				.setTimestamp(new StatisticalDateTimeOffset()
					.setDateTimeOffset(Instant.parse("2018-01-02T00:00:00Z"))
					.setRepresentsEndOfTimePeriod(false)
				)
				.setValue(new DoubleWithDisplay()
					.setDisplay("2.1")
					.setNumeric(2.1D)
				)
		);

		List<TimeSeriesPoint> relatedPointList2 = Arrays.asList(
			new TimeSeriesPoint()
				.setTimestamp(new StatisticalDateTimeOffset()
					.setDateTimeOffset(Instant.parse("2018-01-01T12:00:00Z"))
					.setRepresentsEndOfTimePeriod(false)
				)
				.setValue(new DoubleWithDisplay()
					.setDisplay("1.2")
					.setNumeric(1.2D)
				),
			new TimeSeriesPoint()
				.setTimestamp(new StatisticalDateTimeOffset()
					.setDateTimeOffset(Instant.parse("2018-01-02T00:00:00Z"))
					.setRepresentsEndOfTimePeriod(false)
				)
				.setValue(new DoubleWithDisplay()
					.setDisplay("2.2")
					.setNumeric(2.2D)
				)
		);

		List<TimeSeriesPoint> relatedPointList3 = Arrays.asList(
			new TimeSeriesPoint()
				.setTimestamp(new StatisticalDateTimeOffset()
					.setDateTimeOffset(Instant.parse("2018-01-01T12:00:00Z"))
					.setRepresentsEndOfTimePeriod(false)
				)
				.setValue(new DoubleWithDisplay()
					.setDisplay("1.3")
					.setNumeric(1.3D)
				),
			new TimeSeriesPoint()
				.setTimestamp(new StatisticalDateTimeOffset()
					.setDateTimeOffset(Instant.parse("2018-01-02T12:00:00Z"))
					.setRepresentsEndOfTimePeriod(false)
				)
				.setValue(new DoubleWithDisplay()
					.setDisplay("2.3")
					.setNumeric(2.3D)
				)
		);

		List<TimeSeriesPoint> result = service.findMatchingPoints(pointList, relatedPointList1);
		assertEquals(result.size(), 2);
		assertEquals(result.get(0), relatedPointList1.get(0));
		assertEquals(result.get(1), relatedPointList1.get(1));
		result = service.findMatchingPoints(pointList, relatedPointList2);
		assertEquals(result.size(), 1);
		assertEquals(result.get(0), relatedPointList2.get(1));
		result = service.findMatchingPoints(pointList, relatedPointList3);
		assertEquals(result.size(), 0);
	}
		
	@Test
	public void findMatchingPointsEmptyTest() {
		List<TimeSeriesPoint> pointList = Arrays.asList(
			new TimeSeriesPoint()
				.setTimestamp(new StatisticalDateTimeOffset()
					.setDateTimeOffset(Instant.parse("2018-01-01T00:00:00Z"))
					.setRepresentsEndOfTimePeriod(false)
				)
				.setValue(new DoubleWithDisplay()
					.setDisplay("1.0")
					.setNumeric(1.0D)
				),
			new TimeSeriesPoint()
				.setTimestamp(new StatisticalDateTimeOffset()
					.setDateTimeOffset(Instant.parse("2018-01-02T00:00:00Z"))
					.setRepresentsEndOfTimePeriod(false)
				)
				.setValue(new DoubleWithDisplay()
					.setDisplay("2.0")
					.setNumeric(2.0D)
				)
		);

		List<TimeSeriesPoint> result = service.findMatchingPoints(pointList, new ArrayList<>());
		assertEquals(result.size(), 0);
		result = service.findMatchingPoints(new ArrayList<>(), pointList);
		assertEquals(result.size(), 0);
		result = service.findMatchingPoints(new ArrayList<>(), new ArrayList<>());
		assertEquals(result.size(), 0);
	}
		
	@Test
	public void findMatchingPointsNullTest() {
		List<TimeSeriesPoint> pointList = Arrays.asList(
			new TimeSeriesPoint()
				.setTimestamp(new StatisticalDateTimeOffset()
					.setDateTimeOffset(Instant.parse("2018-01-01T00:00:00Z"))
					.setRepresentsEndOfTimePeriod(false)
				)
				.setValue(new DoubleWithDisplay()
					.setDisplay("1.0")
					.setNumeric(1.0D)
				),
			new TimeSeriesPoint()
				.setTimestamp(new StatisticalDateTimeOffset()
					.setDateTimeOffset(Instant.parse("2018-01-02T00:00:00Z"))
					.setRepresentsEndOfTimePeriod(false)
				)
				.setValue(new DoubleWithDisplay()
					.setDisplay("2.0")
					.setNumeric(2.0D)
				)
		);

		List<TimeSeriesPoint> result = service.findMatchingPoints(pointList, null);
		assertEquals(result.size(), 0);
		result = service.findMatchingPoints(null, pointList);
		assertEquals(result.size(), 0);
		result = service.findMatchingPoints(null, null);
		assertEquals(result.size(), 0);
	}

	@Test
	public void findMinMaxMatchingPointsTest() {
		List<TimeSeriesPoint> primaryPointListMax = Arrays.asList(
			new TimeSeriesPoint()
				.setTimestamp(new StatisticalDateTimeOffset()
					.setDateTimeOffset(Instant.parse("2018-01-01T12:00:00Z"))
					.setRepresentsEndOfTimePeriod(false)
				)
				.setValue(new DoubleWithDisplay()
					.setDisplay("1.2")
					.setNumeric(1.2D)
				),
			new TimeSeriesPoint()
				.setTimestamp(new StatisticalDateTimeOffset()
					.setDateTimeOffset(Instant.parse("2018-01-02T00:00:00Z"))
					.setRepresentsEndOfTimePeriod(false)
				)
				.setValue(new DoubleWithDisplay()
					.setDisplay("2.2")
					.setNumeric(2.2D)
				)
		);

		List<TimeSeriesPoint> primaryPointListMin = Arrays.asList(
			new TimeSeriesPoint()
				.setTimestamp(new StatisticalDateTimeOffset()
					.setDateTimeOffset(Instant.parse("2018-01-01T12:00:00Z"))
					.setRepresentsEndOfTimePeriod(false)
				)
				.setValue(new DoubleWithDisplay()
					.setDisplay("1.3")
					.setNumeric(1.3D)
				),
			new TimeSeriesPoint()
				.setTimestamp(new StatisticalDateTimeOffset()
					.setDateTimeOffset(Instant.parse("2018-01-02T12:00:00Z"))
					.setRepresentsEndOfTimePeriod(false)
				)
				.setValue(new DoubleWithDisplay()
					.setDisplay("2.3")
					.setNumeric(2.3D)
				)
		);

		List<TimeSeriesPoint> relatedPointList = Arrays.asList(
			new TimeSeriesPoint()
				.setTimestamp(new StatisticalDateTimeOffset()
					.setDateTimeOffset(Instant.parse("2018-01-01T00:00:00Z"))
					.setRepresentsEndOfTimePeriod(false)
				)
				.setValue(new DoubleWithDisplay()
					.setDisplay("1.0")
					.setNumeric(1.0D)
				),
			new TimeSeriesPoint()
				.setTimestamp(new StatisticalDateTimeOffset()
					.setDateTimeOffset(Instant.parse("2018-01-02T00:00:00Z"))
					.setRepresentsEndOfTimePeriod(false)
				)
				.setValue(new DoubleWithDisplay()
					.setDisplay("2.0")
					.setNumeric(2.0D)
				)
		);

		TimeSeriesMinMax primaryMinMax = new TimeSeriesMinMax();
		primaryMinMax.setMaxPoints(primaryPointListMax);
		primaryMinMax.setMinPoints(primaryPointListMin);

		TimeSeriesMinMax result = service.findMinMaxMatchingPoints(primaryMinMax, relatedPointList);
		assertEquals(result.getMaxPoints().size(), 1);
		assertEquals(result.getMaxPoints().get(0), relatedPointList.get(1));
		assertEquals(result.getMinPoints().size(), 0);
	}

	@Test
	public void findMinMaxMatchingPointsEmptyTest() {
		List<TimeSeriesPoint> primaryPointList = Arrays.asList(
			new TimeSeriesPoint()
				.setTimestamp(new StatisticalDateTimeOffset()
					.setDateTimeOffset(Instant.parse("2018-01-01T00:00:00Z"))
					.setRepresentsEndOfTimePeriod(false)
				)
				.setValue(new DoubleWithDisplay()
					.setDisplay("1.3")
					.setNumeric(1.3D)
				),
			new TimeSeriesPoint()
				.setTimestamp(new StatisticalDateTimeOffset()
					.setDateTimeOffset(Instant.parse("2018-01-02T00:00:00Z"))
					.setRepresentsEndOfTimePeriod(false)
				)
				.setValue(new DoubleWithDisplay()
					.setDisplay("2.3")
					.setNumeric(2.3D)
				)
		);

		List<TimeSeriesPoint> relatedPointList = Arrays.asList(
			new TimeSeriesPoint()
				.setTimestamp(new StatisticalDateTimeOffset()
					.setDateTimeOffset(Instant.parse("2018-01-01T00:00:00Z"))
					.setRepresentsEndOfTimePeriod(false)
				)
				.setValue(new DoubleWithDisplay()
					.setDisplay("1.0")
					.setNumeric(1.0D)
				),
			new TimeSeriesPoint()
				.setTimestamp(new StatisticalDateTimeOffset()
					.setDateTimeOffset(Instant.parse("2018-01-02T00:00:00Z"))
					.setRepresentsEndOfTimePeriod(false)
				)
				.setValue(new DoubleWithDisplay()
					.setDisplay("2.0")
					.setNumeric(2.0D)
				)
		);

		TimeSeriesMinMax primaryMinMax = new TimeSeriesMinMax();
		primaryMinMax.setMaxPoints(primaryPointList);
		primaryMinMax.setMinPoints(new ArrayList<>());
		TimeSeriesMinMax result = service.findMinMaxMatchingPoints(primaryMinMax, relatedPointList);
		assertEquals(result.getMaxPoints().size(), 2);
		assertEquals(result.getMinPoints().size(), 0);
		primaryMinMax.setMaxPoints(new ArrayList<>());
		primaryMinMax.setMinPoints(primaryPointList);
		result = service.findMinMaxMatchingPoints(primaryMinMax, relatedPointList);
		assertEquals(result.getMaxPoints().size(), 0);
		assertEquals(result.getMinPoints().size(), 2);
		primaryMinMax.setMaxPoints(new ArrayList<>());
		primaryMinMax.setMinPoints(new ArrayList<>());
		result = service.findMinMaxMatchingPoints(primaryMinMax, relatedPointList);
		assertEquals(result.getMaxPoints().size(), 0);
		assertEquals(result.getMinPoints().size(), 0);
		primaryMinMax.setMaxPoints(primaryPointList);
		primaryMinMax.setMinPoints(primaryPointList);
		result = service.findMinMaxMatchingPoints(primaryMinMax, new ArrayList<>());
		assertEquals(result.getMaxPoints().size(), 0);
		assertEquals(result.getMinPoints().size(), 0);
		primaryMinMax.setMaxPoints(new ArrayList<>());
		primaryMinMax.setMinPoints(new ArrayList<>());
		result = service.findMinMaxMatchingPoints(primaryMinMax, new ArrayList<>());
		assertEquals(result.getMaxPoints().size(), 0);
		assertEquals(result.getMinPoints().size(), 0);
		result = service.findMinMaxMatchingPoints(new TimeSeriesMinMax(), new ArrayList<>());
		assertEquals(result.getMaxPoints().size(), 0);
		assertEquals(result.getMinPoints().size(), 0);
	}

	@Test
	public void findMinMaxMatchingPointsNullTest() {
		List<TimeSeriesPoint> primaryPointList = Arrays.asList(
			new TimeSeriesPoint()
				.setTimestamp(new StatisticalDateTimeOffset()
					.setDateTimeOffset(Instant.parse("2018-01-01T00:00:00Z"))
					.setRepresentsEndOfTimePeriod(false)
				)
				.setValue(new DoubleWithDisplay()
					.setDisplay("1.3")
					.setNumeric(1.3D)
				),
			new TimeSeriesPoint()
				.setTimestamp(new StatisticalDateTimeOffset()
					.setDateTimeOffset(Instant.parse("2018-01-02T00:00:00Z"))
					.setRepresentsEndOfTimePeriod(false)
				)
				.setValue(new DoubleWithDisplay()
					.setDisplay("2.3")
					.setNumeric(2.3D)
				)
		);

		List<TimeSeriesPoint> relatedPointList = Arrays.asList(
			new TimeSeriesPoint()
				.setTimestamp(new StatisticalDateTimeOffset()
					.setDateTimeOffset(Instant.parse("2018-01-01T00:00:00Z"))
					.setRepresentsEndOfTimePeriod(false)
				)
				.setValue(new DoubleWithDisplay()
					.setDisplay("1.0")
					.setNumeric(1.0D)
				),
			new TimeSeriesPoint()
				.setTimestamp(new StatisticalDateTimeOffset()
					.setDateTimeOffset(Instant.parse("2018-01-02T00:00:00Z"))
					.setRepresentsEndOfTimePeriod(false)
				)
				.setValue(new DoubleWithDisplay()
					.setDisplay("2.0")
					.setNumeric(2.0D)
				)
		);

		TimeSeriesMinMax primaryMinMax = new TimeSeriesMinMax();
		primaryMinMax.setMaxPoints(primaryPointList);
		primaryMinMax.setMinPoints(null);
		TimeSeriesMinMax result = service.findMinMaxMatchingPoints(primaryMinMax, relatedPointList);
		assertEquals(result.getMaxPoints().size(), 2);
		assertEquals(result.getMinPoints().size(), 0);
		primaryMinMax.setMaxPoints(null);
		primaryMinMax.setMinPoints(primaryPointList);
		result = service.findMinMaxMatchingPoints(primaryMinMax, relatedPointList);
		assertEquals(result.getMaxPoints().size(), 0);
		assertEquals(result.getMinPoints().size(), 2);
		primaryMinMax.setMaxPoints(null);
		primaryMinMax.setMinPoints(null);
		result = service.findMinMaxMatchingPoints(primaryMinMax, relatedPointList);
		assertEquals(result.getMaxPoints().size(), 0);
		assertEquals(result.getMinPoints().size(), 0);
		primaryMinMax.setMaxPoints(primaryPointList);
		primaryMinMax.setMinPoints(primaryPointList);
		result = service.findMinMaxMatchingPoints(primaryMinMax, null);
		assertEquals(result.getMaxPoints().size(), 0);
		assertEquals(result.getMinPoints().size(), 0);
		primaryMinMax.setMaxPoints(null);
		primaryMinMax.setMinPoints(null);
		result = service.findMinMaxMatchingPoints(primaryMinMax, null);
		assertEquals(result.getMaxPoints().size(), 0);
		assertEquals(result.getMinPoints().size(), 0);
		result = service.findMinMaxMatchingPoints(null, null);
		assertEquals(result.getMaxPoints().size(), 0);
		assertEquals(result.getMinPoints().size(), 0);
	}
	
	@Test
	public void findMinMaxPointsTest() {
		List<TimeSeriesPoint> pointList = Arrays.asList(
			new TimeSeriesPoint()
				.setTimestamp(new StatisticalDateTimeOffset()
					.setDateTimeOffset(Instant.parse("2018-01-01T00:00:00Z"))
					.setRepresentsEndOfTimePeriod(false)
				)
				.setValue(new DoubleWithDisplay()
					.setDisplay("1.0")
					.setNumeric(1.0D)
				),
			new TimeSeriesPoint()
				.setTimestamp(new StatisticalDateTimeOffset()
					.setDateTimeOffset(Instant.parse("2018-01-02T00:00:00Z"))
					.setRepresentsEndOfTimePeriod(false)
				)
				.setValue(new DoubleWithDisplay()
					.setDisplay("2.0")
					.setNumeric(2.0D)
				),
			new TimeSeriesPoint()
				.setTimestamp(new StatisticalDateTimeOffset()
					.setDateTimeOffset(Instant.parse("2018-01-03T00:00:00Z"))
					.setRepresentsEndOfTimePeriod(false)
				)
				.setValue(new DoubleWithDisplay()
					.setDisplay("3.0")
					.setNumeric(3.0D)
				),
			new TimeSeriesPoint()
				.setTimestamp(new StatisticalDateTimeOffset()
					.setDateTimeOffset(Instant.parse("2018-01-04T00:00:00Z"))
					.setRepresentsEndOfTimePeriod(false)
				)
				.setValue(new DoubleWithDisplay()
					.setDisplay("1.0")
					.setNumeric(1.0D)
				),
			new TimeSeriesPoint()
				.setTimestamp(new StatisticalDateTimeOffset()
					.setDateTimeOffset(Instant.parse("2018-01-05T00:00:00Z"))
					.setRepresentsEndOfTimePeriod(false)
				)
				.setValue(new DoubleWithDisplay()
					.setDisplay("0.5")
					.setNumeric(0.5D)
				),
			new TimeSeriesPoint()
				.setTimestamp(new StatisticalDateTimeOffset()
					.setDateTimeOffset(Instant.parse("2018-01-06T00:00:00Z"))
					.setRepresentsEndOfTimePeriod(false)
				)
				.setValue(new DoubleWithDisplay()
					.setDisplay("0.5")
					.setNumeric(0.5D)
				),
			new TimeSeriesPoint()
				.setTimestamp(new StatisticalDateTimeOffset()
					.setDateTimeOffset(Instant.parse("2018-01-07T00:00:00Z"))
					.setRepresentsEndOfTimePeriod(false)
				)
				.setValue(new DoubleWithDisplay()
					.setDisplay("1.0")
					.setNumeric(1.0D)
				)
		);

		TimeSeriesMinMax result = service.findMinMaxPoints(pointList);
		assertEquals(result.getMaxPoints().size(), 1);
		assertEquals(result.getMaxPoints().get(0), pointList.get(2));
		assertEquals(result.getMaxPoints().get(0).getValue().getDisplay(), "3.0");
		assertEquals(result.getMinPoints().size(), 2);
		assertEquals(result.getMinPoints().get(0), pointList.get(4));
		assertEquals(result.getMinPoints().get(0).getValue().getDisplay(), "0.5");
		assertEquals(result.getMinPoints().get(1), pointList.get(5));
		assertEquals(result.getMinPoints().get(1).getValue().getDisplay(), "0.5");
	}

	@Test
	public void findMinMaxPointsEmptyTest() {
		TimeSeriesMinMax result = service.findMinMaxPoints(new ArrayList<>());
		assertEquals(result.getMaxPoints().size(), 0);
		assertEquals(result.getMaxPoints().size(), 0);
	}

	@Test
	public void findMinMaxPointsNullTest() {
		TimeSeriesMinMax result = service.findMinMaxPoints(null);
		assertEquals(result.getMaxPoints().size(), 0);
		assertEquals(result.getMaxPoints().size(), 0);
	}
}
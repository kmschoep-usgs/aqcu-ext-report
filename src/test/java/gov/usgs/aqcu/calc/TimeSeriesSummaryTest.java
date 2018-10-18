package gov.usgs.aqcu.calc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import gov.usgs.aqcu.model.ExtremesPoint;
import gov.usgs.aqcu.model.TimeSeriesCorrectedData;

/**
 *
 * @author kmschoep
 */
public class TimeSeriesSummaryTest {
	private static final Logger log = LoggerFactory.getLogger(TimeSeriesSummaryTest.class);
	
	protected static final Temporal baseTime = LocalDateTime.now().minusYears(1);
	
	protected static final String DISCHARGE = "DISCHARGE";
	protected static final String STAGE = "STAGE";
	protected static final String DAILYDISCHARGE = "DAILYDISCHARGE";
	
	private static Map<String, TimeSeriesCorrectedData> incomingDefaultCalculateSummariesTimeSeries = new HashMap<>();
	private static Map<String, TimeSeriesCorrectedData> incomingDefaultCalculateSummariesWithRelativesTimeSeries = new HashMap<>();
	private static Map<String, TimeSeriesCorrectedData> incomingMultipleCalculateSummariesWithRelativesTimeSeries = new HashMap<>();
	private static List<TimeSeriesSummary> expectedDefaultCalculateSummaries = new ArrayList<>();
	private static List<TimeSeriesSummary> expectedDefaultCalculateSummariesWithRelatives = new ArrayList<>();
	private static List<TimeSeriesSummary> expectedMultipleCalculateSummariesWithRelatives = new ArrayList<>();
	
	
	public TimeSeriesSummaryTest() {
	}
	
	@BeforeClass
	public static void setUpClass() {
	}
	
	@AfterClass
	public static void tearDownClass() {
	}
	
	@Before
	public void setUp() {
		
		incomingDefaultCalculateSummariesTimeSeries = new ImmutableMap.Builder<String, TimeSeriesCorrectedData>()
				.put(DISCHARGE, new TimeSeriesCorrectedData()
					.setPoints(Arrays.asList(
							new ExtremesPoint().setTime(baseTime.minus(5, ChronoUnit.HOURS)).setValue(new BigDecimal("12.0")),
							new ExtremesPoint().setTime(baseTime.minus(4, ChronoUnit.HOURS)).setValue(new BigDecimal("15.0")),
							new ExtremesPoint().setTime(baseTime.minus(3, ChronoUnit.HOURS)).setValue(new BigDecimal("19.0")),
							new ExtremesPoint().setTime(baseTime.minus(2, ChronoUnit.HOURS)).setValue(new BigDecimal("14.0")),
							new ExtremesPoint().setTime(baseTime.minus(1, ChronoUnit.HOURS)).setValue(new BigDecimal("16.0"))
							)))
				.build();
		
		expectedDefaultCalculateSummaries = new ImmutableList.Builder<TimeSeriesSummary>()
				.add(new TimeSeriesSummary(null, null, null,
				new ImmutableMap.Builder<OrderingComparators, List<ExtremesPoint>>()
						.put(OrderingComparators.MAX, Arrays.asList(new ExtremesPoint[] { new ExtremesPoint().setTime(baseTime.minus(3, ChronoUnit.HOURS)).setValue(new BigDecimal("19.0")) }))
						.put(OrderingComparators.MIN, Arrays.asList(new ExtremesPoint[] { new ExtremesPoint().setTime(baseTime.minus(5, ChronoUnit.HOURS)).setValue(new BigDecimal("12.0")) }))
						.build(),
				new ImmutableMap.Builder<OrderingComparators, Map<String, List<ExtremesPoint>>>()
						.build()))
				.build();
		
		incomingDefaultCalculateSummariesWithRelativesTimeSeries = new ImmutableMap.Builder<String, TimeSeriesCorrectedData>()
				.put(DISCHARGE, new TimeSeriesCorrectedData()
					.setPoints(Arrays.asList(
							new ExtremesPoint().setTime(baseTime.minus(5, ChronoUnit.HOURS)).setValue(new BigDecimal("121.0")),
							new ExtremesPoint().setTime(baseTime.minus(4, ChronoUnit.HOURS)).setValue(new BigDecimal("152.0")),
							new ExtremesPoint().setTime(baseTime.minus(3, ChronoUnit.HOURS)).setValue(new BigDecimal("193.0")),
							new ExtremesPoint().setTime(baseTime.minus(2, ChronoUnit.HOURS)).setValue(new BigDecimal("144.0")),
							new ExtremesPoint().setTime(baseTime.minus(1, ChronoUnit.HOURS)).setValue(new BigDecimal("165.0"))
							)))
				.put(STAGE, new TimeSeriesCorrectedData()
					.setPoints(Arrays.asList(
							new ExtremesPoint().setTime(baseTime.minus(5, ChronoUnit.HOURS)).setValue(new BigDecimal("152.0")),
							new ExtremesPoint().setTime(baseTime.minus(4, ChronoUnit.HOURS)).setValue(new BigDecimal("115.0")),
							new ExtremesPoint().setTime(baseTime.minus(3, ChronoUnit.HOURS)).setValue(new BigDecimal("129.0")),
							new ExtremesPoint().setTime(baseTime.minus(2, ChronoUnit.HOURS)).setValue(new BigDecimal("154.0")),
							new ExtremesPoint().setTime(baseTime.minus(1, ChronoUnit.HOURS)).setValue(new BigDecimal("126.0"))
							)))
				.put(DAILYDISCHARGE, new TimeSeriesCorrectedData()
					.setPoints(Arrays.asList(
							new ExtremesPoint().setTime(baseTime.minus(5, ChronoUnit.HOURS)).setValue(new BigDecimal("132.0")),
							new ExtremesPoint().setTime(baseTime.minus(4, ChronoUnit.HOURS)).setValue(new BigDecimal("145.0")),
							new ExtremesPoint().setTime(baseTime.minus(3, ChronoUnit.HOURS)).setValue(new BigDecimal("179.0")),
							new ExtremesPoint().setTime(baseTime.minus(2, ChronoUnit.HOURS)).setValue(new BigDecimal("124.0")),
							new ExtremesPoint().setTime(baseTime.minus(1, ChronoUnit.HOURS)).setValue(new BigDecimal("161.0"))
							)))
				.build();
		
		expectedDefaultCalculateSummariesWithRelatives = new ImmutableList.Builder<TimeSeriesSummary>()
				.add(new TimeSeriesSummary(null, null, null,
				new ImmutableMap.Builder<OrderingComparators, List<ExtremesPoint>>()
						.put(OrderingComparators.MAX, Arrays.asList(new ExtremesPoint[] { new ExtremesPoint().setTime(baseTime.minus(3, ChronoUnit.HOURS)).setValue(new BigDecimal("193.0")) }))
						.put(OrderingComparators.MIN, Arrays.asList(new ExtremesPoint[] { new ExtremesPoint().setTime(baseTime.minus(5, ChronoUnit.HOURS)).setValue(new BigDecimal("121.0")) }))
						.build(),
				new ImmutableMap.Builder<OrderingComparators, Map<String, List<ExtremesPoint>>>()
						.put(OrderingComparators.MAX, new ImmutableMap.Builder<String, List<ExtremesPoint>>()
								.put(STAGE, Arrays.asList(new ExtremesPoint[] { new ExtremesPoint().setTime(baseTime.minus(3, ChronoUnit.HOURS)).setValue(new BigDecimal("129.0")) }))
								.put(DAILYDISCHARGE, Arrays.asList(new ExtremesPoint[] { new ExtremesPoint().setTime(baseTime.minus(3, ChronoUnit.HOURS)).setValue(new BigDecimal("179.0")) }))
								.build())
						.put(OrderingComparators.MIN, new ImmutableMap.Builder<String, List<ExtremesPoint>>()
								.put(STAGE, Arrays.asList(new ExtremesPoint[] { new ExtremesPoint().setTime(baseTime.minus(5, ChronoUnit.HOURS)).setValue(new BigDecimal("152.0")) }))
								.put(DAILYDISCHARGE, Arrays.asList(new ExtremesPoint[] { new ExtremesPoint().setTime(baseTime.minus(5, ChronoUnit.HOURS)).setValue(new BigDecimal("132.0")) }))
								.build())
						.build()))
				.build();
		
		incomingMultipleCalculateSummariesWithRelativesTimeSeries = new ImmutableMap.Builder<String, TimeSeriesCorrectedData>()
				.put(DISCHARGE, new TimeSeriesCorrectedData()
					.setPoints(Arrays.asList(
							new ExtremesPoint().setTime(baseTime.minus(6, ChronoUnit.HOURS)).setValue(new BigDecimal("193.0")),
							new ExtremesPoint().setTime(baseTime.minus(5, ChronoUnit.HOURS)).setValue(new BigDecimal("121.0")),
							new ExtremesPoint().setTime(baseTime.minus(4, ChronoUnit.HOURS)).setValue(new BigDecimal("152.0")),
							new ExtremesPoint().setTime(baseTime.minus(3, ChronoUnit.HOURS)).setValue(new BigDecimal("193.0")),
							new ExtremesPoint().setTime(baseTime.minus(2, ChronoUnit.HOURS)).setValue(new BigDecimal("144.0")),
							new ExtremesPoint().setTime(baseTime.minus(1, ChronoUnit.HOURS)).setValue(new BigDecimal("165.0"))
							)))
				.put(STAGE, new TimeSeriesCorrectedData()
					.setPoints(Arrays.asList(
							new ExtremesPoint().setTime(baseTime.minus(6, ChronoUnit.HOURS)).setValue(new BigDecimal("101.0")),
							new ExtremesPoint().setTime(baseTime.minus(5, ChronoUnit.HOURS)).setValue(new BigDecimal("152.0")),
							new ExtremesPoint().setTime(baseTime.minus(4, ChronoUnit.HOURS)).setValue(new BigDecimal("115.0")),
							new ExtremesPoint().setTime(baseTime.minus(3, ChronoUnit.HOURS)).setValue(new BigDecimal("129.0")),
							new ExtremesPoint().setTime(baseTime.minus(2, ChronoUnit.HOURS)).setValue(new BigDecimal("154.0")),
							new ExtremesPoint().setTime(baseTime.minus(1, ChronoUnit.HOURS)).setValue(new BigDecimal("126.0"))
							)))
				.put(DAILYDISCHARGE, new TimeSeriesCorrectedData()
					.setPoints(Arrays.asList(
							new ExtremesPoint().setTime(baseTime.minus(6, ChronoUnit.HOURS)).setValue(new BigDecimal("141.0")),
							new ExtremesPoint().setTime(baseTime.minus(5, ChronoUnit.HOURS)).setValue(new BigDecimal("132.0")),
							new ExtremesPoint().setTime(baseTime.minus(4, ChronoUnit.HOURS)).setValue(new BigDecimal("145.0")),
							new ExtremesPoint().setTime(baseTime.minus(3, ChronoUnit.HOURS)).setValue(new BigDecimal("179.0")),
							new ExtremesPoint().setTime(baseTime.minus(2, ChronoUnit.HOURS)).setValue(new BigDecimal("124.0")),
							new ExtremesPoint().setTime(baseTime.minus(1, ChronoUnit.HOURS)).setValue(new BigDecimal("161.0"))
							)))
				.build();
		
		expectedDefaultCalculateSummariesWithRelatives = new ImmutableList.Builder<TimeSeriesSummary>()
				.add(new TimeSeriesSummary(null, null, null,
				new ImmutableMap.Builder<OrderingComparators, List<ExtremesPoint>>()
						.put(OrderingComparators.MAX, Arrays.asList(new ExtremesPoint[] { new ExtremesPoint().setTime(baseTime.minus(3, ChronoUnit.HOURS)).setValue(new BigDecimal("193.0")) }))
						.put(OrderingComparators.MIN, Arrays.asList(new ExtremesPoint[] { new ExtremesPoint().setTime(baseTime.minus(5, ChronoUnit.HOURS)).setValue(new BigDecimal("121.0")) }))
						.build(),
				new ImmutableMap.Builder<OrderingComparators, Map<String, List<ExtremesPoint>>>()
						.put(OrderingComparators.MAX, new ImmutableMap.Builder<String, List<ExtremesPoint>>()
								.put(STAGE, Arrays.asList(new ExtremesPoint[] { new ExtremesPoint().setTime(baseTime.minus(3, ChronoUnit.HOURS)).setValue(new BigDecimal("129.0")) }))
								.put(DAILYDISCHARGE, Arrays.asList(new ExtremesPoint[] { new ExtremesPoint().setTime(baseTime.minus(3, ChronoUnit.HOURS)).setValue(new BigDecimal("179.0")) }))
								.build())
						.put(OrderingComparators.MIN, new ImmutableMap.Builder<String, List<ExtremesPoint>>()
								.put(STAGE, Arrays.asList(new ExtremesPoint[] { new ExtremesPoint().setTime(baseTime.minus(5, ChronoUnit.HOURS)).setValue(new BigDecimal("152.0")) }))
								.put(DAILYDISCHARGE, Arrays.asList(new ExtremesPoint[] { new ExtremesPoint().setTime(baseTime.minus(5, ChronoUnit.HOURS)).setValue(new BigDecimal("132.0")) }))
								.build())
						.build()))
				.build();
		
		expectedMultipleCalculateSummariesWithRelatives = new ImmutableList.Builder<TimeSeriesSummary>()
				.add(new TimeSeriesSummary(null, null, null,
				new ImmutableMap.Builder<OrderingComparators, List<ExtremesPoint>>()
						.put(OrderingComparators.MAX, Arrays.asList(new ExtremesPoint[] { new ExtremesPoint().setTime(baseTime.minus(6, ChronoUnit.HOURS)).setValue(new BigDecimal("193.0")) ,
								new ExtremesPoint().setTime(baseTime.minus(3, ChronoUnit.HOURS)).setValue(new BigDecimal("193.0")) }))
						.put(OrderingComparators.MIN, Arrays.asList(new ExtremesPoint[] { new ExtremesPoint().setTime(baseTime.minus(5, ChronoUnit.HOURS)).setValue(new BigDecimal("121.0")) }))
						.build(),
				new ImmutableMap.Builder<OrderingComparators, Map<String, List<ExtremesPoint>>>()
						.put(OrderingComparators.MAX, new ImmutableMap.Builder<String, List<ExtremesPoint>>()
								.put(STAGE, Arrays.asList(new ExtremesPoint[] { new ExtremesPoint().setTime(baseTime.minus(6, ChronoUnit.HOURS)).setValue(new BigDecimal("101.0")),
										new ExtremesPoint().setTime(baseTime.minus(3, ChronoUnit.HOURS)).setValue(new BigDecimal("129.0"))}))
								.put(DAILYDISCHARGE, Arrays.asList(new ExtremesPoint[] { new ExtremesPoint().setTime(baseTime.minus(6, ChronoUnit.HOURS)).setValue(new BigDecimal("141.0")),
										new ExtremesPoint().setTime(baseTime.minus(3, ChronoUnit.HOURS)).setValue(new BigDecimal("179.0"))}))
								.build())
						.put(OrderingComparators.MIN, new ImmutableMap.Builder<String, List<ExtremesPoint>>()
								.put(STAGE, Arrays.asList(new ExtremesPoint[] { new ExtremesPoint().setTime(baseTime.minus(5, ChronoUnit.HOURS)).setValue(new BigDecimal("152.0")) }))
								.put(DAILYDISCHARGE, Arrays.asList(new ExtremesPoint[] { new ExtremesPoint().setTime(baseTime.minus(5, ChronoUnit.HOURS)).setValue(new BigDecimal("132.0")) }))
								.build())
						.build()))
				.build();
	}
	
	@After
	public void tearDown() {
	}
	
	/**
	 * Test of calculateSummaries method, of class TimeSeriesSummary.
	 */
	@Test
	public void testDefaultCalculateSummaries() {
		log.debug("testDefaultCalculateSummaries");
		Map<String, TimeSeriesCorrectedData> timeseries = incomingDefaultCalculateSummariesTimeSeries;
		String series = DISCHARGE;
		List<TimeSeriesSummary> expResult = expectedDefaultCalculateSummaries;
		List<TimeSeriesSummary> result = TimeSeriesSummary.calculateSummaries(timeseries, series);
		assertEquals(expResult.get(0).get(OrderingComparators.MAX).get(0).getTime(), result.get(0).get(OrderingComparators.MAX).get(0).getTime());
		assertEquals(expResult.get(0).get(OrderingComparators.MAX).get(0).getValue(), result.get(0).get(OrderingComparators.MAX).get(0).getValue());
		assertEquals(expResult.get(0).get(OrderingComparators.MIN).get(0).getTime(), result.get(0).get(OrderingComparators.MIN).get(0).getTime());
		assertEquals(expResult.get(0).get(OrderingComparators.MIN).get(0).getValue(), result.get(0).get(OrderingComparators.MIN).get(0).getValue());

	}
	
	/**
	 * Test of calculateSummaries method, of class TimeSeriesSummary.
	 */
	@Test
	public void testDefaultCalculateSummariesWithRelatives() {
		log.debug("testDefaultCalculateSummariesWithRelatives");
		Map<String, TimeSeriesCorrectedData> timeseries = incomingDefaultCalculateSummariesWithRelativesTimeSeries;
		String series = DISCHARGE;
		List<TimeSeriesSummary> expResult = expectedDefaultCalculateSummariesWithRelatives;
		List<TimeSeriesSummary> result = TimeSeriesSummary.calculateSummaries(timeseries, series);
		assertEquals(expResult.get(0).get(OrderingComparators.MAX).get(0).getTime(), result.get(0).get(OrderingComparators.MAX).get(0).getTime());
		assertEquals(expResult.get(0).get(OrderingComparators.MAX).get(0).getValue(), result.get(0).get(OrderingComparators.MAX).get(0).getValue());
		assertEquals(expResult.get(0).get(OrderingComparators.MIN).get(0).getTime(), result.get(0).get(OrderingComparators.MIN).get(0).getTime());
		assertEquals(expResult.get(0).get(OrderingComparators.MIN).get(0).getValue(), result.get(0).get(OrderingComparators.MIN).get(0).getValue());
		assertEquals(expResult.get(0).getAt(OrderingComparators.MAX, DAILYDISCHARGE).get(0).getTime(), result.get(0).getAt(OrderingComparators.MAX, DAILYDISCHARGE).get(0).getTime());
		assertEquals(expResult.get(0).getAt(OrderingComparators.MAX, DAILYDISCHARGE).get(0).getValue(), result.get(0).getAt(OrderingComparators.MAX, DAILYDISCHARGE).get(0).getValue());
		assertNotEquals(result.get(0).getAt(OrderingComparators.MAX, DAILYDISCHARGE).get(0).getValue(), result.get(0).get(OrderingComparators.MAX).get(0).getValue());
		assertEquals(expResult.get(0).getAt(OrderingComparators.MIN, DAILYDISCHARGE).get(0).getTime(), result.get(0).getAt(OrderingComparators.MIN, DAILYDISCHARGE).get(0).getTime());
		assertEquals(expResult.get(0).getAt(OrderingComparators.MIN, DAILYDISCHARGE).get(0).getValue(), result.get(0).getAt(OrderingComparators.MIN, DAILYDISCHARGE).get(0).getValue());
		assertNotEquals(result.get(0).getAt(OrderingComparators.MIN, DAILYDISCHARGE).get(0).getValue(), result.get(0).get(OrderingComparators.MIN).get(0).getValue());
		assertEquals(expResult.get(0).getAt(OrderingComparators.MAX, STAGE).get(0).getTime(), result.get(0).getAt(OrderingComparators.MAX, STAGE).get(0).getTime());
		assertEquals(expResult.get(0).getAt(OrderingComparators.MAX, STAGE).get(0).getValue(), result.get(0).getAt(OrderingComparators.MAX, STAGE).get(0).getValue());
		assertNotEquals(result.get(0).getAt(OrderingComparators.MAX, STAGE).get(0).getValue(), result.get(0).get(OrderingComparators.MAX).get(0).getValue());
		assertEquals(expResult.get(0).getAt(OrderingComparators.MIN, STAGE).get(0).getTime(), result.get(0).getAt(OrderingComparators.MIN, STAGE).get(0).getTime());
		assertEquals(expResult.get(0).getAt(OrderingComparators.MIN, STAGE).get(0).getValue(), result.get(0).getAt(OrderingComparators.MIN, STAGE).get(0).getValue());
		assertNotEquals(result.get(0).getAt(OrderingComparators.MIN, STAGE).get(0).getValue(), result.get(0).get(OrderingComparators.MIN).get(0).getValue());

	}
	
	/**
	 * Test of calculateSummaries method, of class TimeSeriesSummary.
	 */
	@Test
	public void testMultipleCalculateSummariesWithRelatives() {
		log.debug("testMultipleCalculateSummariesWithRelatives");
		Map<String, TimeSeriesCorrectedData> timeseries = incomingMultipleCalculateSummariesWithRelativesTimeSeries;
		String series = DISCHARGE;
		List<TimeSeriesSummary> expResult = expectedMultipleCalculateSummariesWithRelatives;
		List<TimeSeriesSummary> result = TimeSeriesSummary.calculateSummaries(timeseries, series);
		assertEquals(expResult.get(0).get(OrderingComparators.MAX).get(0).getTime(), result.get(0).get(OrderingComparators.MAX).get(0).getTime());
		assertEquals(expResult.get(0).get(OrderingComparators.MAX).get(0).getValue(), result.get(0).get(OrderingComparators.MAX).get(0).getValue());
		assertEquals(expResult.get(0).get(OrderingComparators.MAX).get(1).getTime(), result.get(0).get(OrderingComparators.MAX).get(1).getTime());
		assertEquals(expResult.get(0).get(OrderingComparators.MAX).get(1).getValue(), result.get(0).get(OrderingComparators.MAX).get(1).getValue());
		assertEquals(expResult.get(0).get(OrderingComparators.MIN).get(0).getTime(), result.get(0).get(OrderingComparators.MIN).get(0).getTime());
		assertEquals(expResult.get(0).get(OrderingComparators.MIN).get(0).getValue(), result.get(0).get(OrderingComparators.MIN).get(0).getValue());
		assertEquals(expResult.get(0).getAt(OrderingComparators.MAX, DAILYDISCHARGE).get(0).getTime(), result.get(0).getAt(OrderingComparators.MAX, DAILYDISCHARGE).get(0).getTime());
		assertEquals(expResult.get(0).getAt(OrderingComparators.MAX, DAILYDISCHARGE).get(0).getValue(), result.get(0).getAt(OrderingComparators.MAX, DAILYDISCHARGE).get(0).getValue());
		assertNotEquals(result.get(0).getAt(OrderingComparators.MAX, DAILYDISCHARGE).get(0).getValue(), result.get(0).get(OrderingComparators.MAX).get(0).getValue());
		assertEquals(expResult.get(0).getAt(OrderingComparators.MIN, DAILYDISCHARGE).get(0).getTime(), result.get(0).getAt(OrderingComparators.MIN, DAILYDISCHARGE).get(0).getTime());
		assertEquals(expResult.get(0).getAt(OrderingComparators.MIN, DAILYDISCHARGE).get(0).getValue(), result.get(0).getAt(OrderingComparators.MIN, DAILYDISCHARGE).get(0).getValue());
		assertNotEquals(result.get(0).getAt(OrderingComparators.MIN, DAILYDISCHARGE).get(0).getValue(), result.get(0).get(OrderingComparators.MIN).get(0).getValue());
		assertEquals(expResult.get(0).getAt(OrderingComparators.MAX, DAILYDISCHARGE).get(1).getTime(), result.get(0).getAt(OrderingComparators.MAX, DAILYDISCHARGE).get(1).getTime());
		assertEquals(expResult.get(0).getAt(OrderingComparators.MAX, DAILYDISCHARGE).get(1).getValue(), result.get(0).getAt(OrderingComparators.MAX, DAILYDISCHARGE).get(1).getValue());
		assertNotEquals(result.get(0).getAt(OrderingComparators.MAX, DAILYDISCHARGE).get(1).getValue(), result.get(0).get(OrderingComparators.MAX).get(1).getValue());
		assertEquals(expResult.get(0).getAt(OrderingComparators.MAX, STAGE).get(0).getTime(), result.get(0).getAt(OrderingComparators.MAX, STAGE).get(0).getTime());
		assertEquals(expResult.get(0).getAt(OrderingComparators.MAX, STAGE).get(0).getValue(), result.get(0).getAt(OrderingComparators.MAX, STAGE).get(0).getValue());
		assertNotEquals(result.get(0).getAt(OrderingComparators.MAX, STAGE).get(0).getValue(), result.get(0).get(OrderingComparators.MAX).get(0).getValue());
		assertEquals(expResult.get(0).getAt(OrderingComparators.MIN, STAGE).get(0).getTime(), result.get(0).getAt(OrderingComparators.MIN, STAGE).get(0).getTime());
		assertEquals(expResult.get(0).getAt(OrderingComparators.MIN, STAGE).get(0).getValue(), result.get(0).getAt(OrderingComparators.MIN, STAGE).get(0).getValue());
		assertNotEquals(result.get(0).getAt(OrderingComparators.MIN, STAGE).get(0).getValue(), result.get(0).get(OrderingComparators.MIN).get(0).getValue());
		assertEquals(expResult.get(0).getAt(OrderingComparators.MAX, STAGE).get(1).getTime(), result.get(0).getAt(OrderingComparators.MAX, STAGE).get(1).getTime());
		assertEquals(expResult.get(0).getAt(OrderingComparators.MAX, STAGE).get(1).getValue(), result.get(0).getAt(OrderingComparators.MAX, STAGE).get(1).getValue());
		assertNotEquals(result.get(0).getAt(OrderingComparators.MAX, STAGE).get(1).getValue(), result.get(0).get(OrderingComparators.MAX).get(1).getValue());

	}

}

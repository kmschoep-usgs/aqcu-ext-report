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
	private static List<TimeSeriesSummary> expectedDefaultCalculateSummaries = new ArrayList<>();
	private static List<TimeSeriesSummary> expectedDefaultCalculateSummariesWithRelatives = new ArrayList<>();
	
	
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
		boolean calculateRelativeSummary = false;
		int topCount = 1;
		List<TimeSeriesSummary> expResult = expectedDefaultCalculateSummaries;
		List<TimeSeriesSummary> result = TimeSeriesSummary.calculateSummaries(timeseries, series, calculateRelativeSummary, topCount);
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
		boolean calculateRelativeSummary = true;
		int topCount = 1;
		List<TimeSeriesSummary> expResult = expectedDefaultCalculateSummariesWithRelatives;
		List<TimeSeriesSummary> result = TimeSeriesSummary.calculateSummaries(timeseries, series, calculateRelativeSummary, topCount);
		assertEquals(expResult.get(0).get(OrderingComparators.MAX).get(0).getTime(), result.get(0).get(OrderingComparators.MAX).get(0).getTime());
		assertEquals(expResult.get(0).get(OrderingComparators.MAX).get(0).getValue(), result.get(0).get(OrderingComparators.MAX).get(0).getValue());
		assertEquals(expResult.get(0).get(OrderingComparators.MIN).get(0).getTime(), result.get(0).get(OrderingComparators.MIN).get(0).getTime());
		assertEquals(expResult.get(0).get(OrderingComparators.MIN).get(0).getValue(), result.get(0).get(OrderingComparators.MIN).get(0).getValue());
		assertEquals(expResult.get(0).getAt(OrderingComparators.MAX, DAILYDISCHARGE).get(0).getTime(), result.get(0).getAt(OrderingComparators.MAX, DAILYDISCHARGE).get(0).getTime());
		assertEquals(expResult.get(0).getAt(OrderingComparators.MAX, DAILYDISCHARGE).get(0).getValue(), result.get(0).getAt(OrderingComparators.MAX, DAILYDISCHARGE).get(0).getValue());
		assertNotEquals(result.get(0).getAt(OrderingComparators.MAX, DAILYDISCHARGE).get(0).getValue(), result.get(0).get(OrderingComparators.MAX).get(0).getValue());
		assertEquals(expResult.get(0).getAt(OrderingComparators.MAX, STAGE).get(0).getTime(), result.get(0).getAt(OrderingComparators.MAX, STAGE).get(0).getTime());
		assertEquals(expResult.get(0).getAt(OrderingComparators.MAX, STAGE).get(0).getValue(), result.get(0).getAt(OrderingComparators.MAX, STAGE).get(0).getValue());
		assertNotEquals(result.get(0).getAt(OrderingComparators.MAX, STAGE).get(0).getValue(), result.get(0).get(OrderingComparators.MAX).get(0).getValue());

	}
	
	
		
	
	/**
	 * Test of calculateSummaries method, of class TimeSeriesSummary.
	 */
	/*
	@Test
	public void testMultipleCalculateSummaries() {
		log.debug("testMultipleCalculateSummaries");
		Map<String, TimeSeries> timeseries = incomingMultipleCalculateSummariesTimeSeries;
		String series = DISCHARGE;
		boolean calculateRelativeSummary = false;
		DateRange effectiveDateRange = null;
		int topCount = 2;
		List<TimeSeriesSummary> expResult = expectedMultipleCalculateSummaries;
		List<TimeSeriesSummary> result = TimeSeriesSummary.calculateSummaries(timeseries, series, calculateRelativeSummary, effectiveDateRange, topCount);
		assertEquals(expResult, result);
	}
	
	static final Map<String, TimeSeriesDataServiceResponse> incomingMultipleCalculateSummariesTimeSeries = new ImmutableMap.Builder<String, TimeSeries>()
			.put(DISCHARGE, new TimeSeriesDataServiceResponse()
				.setPoints(new ImmutableList.Builder<TimeSeriesPoint>()
						.add(new TimeSeriesPoint().setTime(baseTime.minus(5, ChronoUnit.HOURS)).setValue(new BigDecimal("12.0")))
						.add(new TimeSeriesPoint().setTime(baseTime.minus(4, ChronoUnit.HOURS)).setValue(new BigDecimal("15.0")))
						.add(new TimeSeriesPoint().setTime(baseTime.minus(3, ChronoUnit.HOURS)).setValue(new BigDecimal("19.0")))
						.add(new TimeSeriesPoint().setTime(baseTime.minus(2, ChronoUnit.HOURS)).setValue(new BigDecimal("14.0")))
						.add(new TimeSeriesPoint().setTime(baseTime.minus(1, ChronoUnit.HOURS)).setValue(new BigDecimal("16.0")))
						.build()))
			.build();
	
	static final List<TimeSeriesSummary> expectedMultipleCalculateSummaries = new ImmutableList.Builder<TimeSeriesSummary>()
			.add(new TimeSeriesSummary(null, null, null,
					new ImmutableMap.Builder<OrderingComparators, List<TimeSeriesPoint>>()
							.put(OrderingComparators.MAX, 
									Arrays.asList(new TimeSeriesPoint[] { new TimeSeriesPoint().setTime(baseTime.minus(3, ChronoUnit.HOURS)).setValue(new BigDecimal("19.0")) 
									}))
							.put(OrderingComparators.MIN,  
									Arrays.asList(new TimeSeriesPoint[] { new TimeSeriesPoint().setTime(baseTime.minus(5, ChronoUnit.HOURS)).setValue(new BigDecimal("12.0")) 
									}))
							.build(),
					new ImmutableMap.Builder<OrderingComparators, Map<String, List<TimeSeriesPoint>>>()
							.build()))
					.add(new TimeSeriesSummary(null, null, null,
					new ImmutableMap.Builder<OrderingComparators, List<TimeSeriesPoint>>()
							.put(OrderingComparators.MAX,  
									Arrays.asList(new TimeSeriesPoint[] { new TimeSeriesPoint().setTime(baseTime.minus(1, ChronoUnit.HOURS)).setValue(new BigDecimal("16.0")) 
									}))
							.put(OrderingComparators.MIN,  
									Arrays.asList(new TimeSeriesPoint[] { new TimeSeriesPoint().setTime(baseTime.minus(2, ChronoUnit.HOURS)).setValue(new BigDecimal("14.0")) 
									}))
							.build(),
			new ImmutableMap.Builder<OrderingComparators, Map<String, List<TimeSeriesPoint>>>()
					.build()))
			.build();
		*/
	/**
	 * Test of calculateSummaries method, of class TimeSeriesSummary.
	 */
	/*
	@Test
	public void testMultipleWithTiesCalculateSummaries() {
		log.debug("testMultipleCalculateSummaries");
		Map<String, TimeSeriesDataServiceResponse> timeseries = incomingMultipleWithTiesCalculateSummariesTimeSeries;
		String series = DISCHARGE;
		boolean calculateRelativeSummary = false;
		DateRange effectiveDateRange = null;
		int topCount = 2;
		List<TimeSeriesSummary> expResult = expectedMultipleWithTiesCalculateSummaries;
		List<TimeSeriesSummary> result = TimeSeriesSummary.calculateSummaries(timeseries, series, calculateRelativeSummary, effectiveDateRange, topCount);
		assertEquals(expResult, result);
	}
	
	static final Map<String, TimeSeriesDataServiceResponse> incomingMultipleWithTiesCalculateSummariesTimeSeries = new ImmutableMap.Builder<String, TimeSeries>()
			.put(DISCHARGE, new TimeSeriesDataServiceResponse()
				.setPoints(new ImmutableList.Builder<TimeSeriesPoint>()
						.add(new TimeSeriesPoint().setTime(baseTime.minus(9, ChronoUnit.HOURS)).setValue(new BigDecimal("13.0")))
						.add(new TimeSeriesPoint().setTime(baseTime.minus(8, ChronoUnit.HOURS)).setValue(new BigDecimal("16.0")))
						.add(new TimeSeriesPoint().setTime(baseTime.minus(7, ChronoUnit.HOURS)).setValue(new BigDecimal("19.0")))
						.add(new TimeSeriesPoint().setTime(baseTime.minus(6, ChronoUnit.HOURS)).setValue(new BigDecimal("15.0")))
						.add(new TimeSeriesPoint().setTime(baseTime.minus(5, ChronoUnit.HOURS)).setValue(new BigDecimal("8.0")))
						.add(new TimeSeriesPoint().setTime(baseTime.minus(4, ChronoUnit.HOURS)).setValue(new BigDecimal("15.0")))
						.add(new TimeSeriesPoint().setTime(baseTime.minus(3, ChronoUnit.HOURS)).setValue(new BigDecimal("19.0")))
						.add(new TimeSeriesPoint().setTime(baseTime.minus(2, ChronoUnit.HOURS)).setValue(new BigDecimal("8.0")))
						.add(new TimeSeriesPoint().setTime(baseTime.minus(1, ChronoUnit.HOURS)).setValue(new BigDecimal("16.0")))
						.build()))
			.build();
	
	static final List<TimeSeriesSummary> expectedMultipleWithTiesCalculateSummaries = new ImmutableList.Builder<TimeSeriesSummary>()
			.add(new TimeSeriesSummary(null, null, null,
					new ImmutableMap.Builder<OrderingComparators, List<TimeSeriesPoint>>()
							.put(OrderingComparators.MAX, 
									Arrays.asList(new TimeSeriesPoint[] { 
											new TimeSeriesPoint().setTime(baseTime.minus(7, ChronoUnit.HOURS)).setValue(new BigDecimal("19.0")), 
											new TimeSeriesPoint().setTime(baseTime.minus(3, ChronoUnit.HOURS)).setValue(new BigDecimal("19.0"))
									}))
							.put(OrderingComparators.MIN,  
									Arrays.asList(new TimeSeriesPoint[] { 
											new TimeSeriesPoint().setTime(baseTime.minus(5, ChronoUnit.HOURS)).setValue(new BigDecimal("8.0")),
											new TimeSeriesPoint().setTime(baseTime.minus(2, ChronoUnit.HOURS)).setValue(new BigDecimal("8.0"))
									}))
							.build(),
					new ImmutableMap.Builder<OrderingComparators, Map<String, List<TimeSeriesPoint>>>()
							.build()))
					.add(new TimeSeriesSummary(null, null, null,
					new ImmutableMap.Builder<OrderingComparators, List<TimeSeriesPoint>>()
							.put(OrderingComparators.MAX,  
									Arrays.asList(new TimeSeriesPoint[] { 
											new TimeSeriesPoint().setTime(baseTime.minus(8, ChronoUnit.HOURS)).setValue(new BigDecimal("16.0")) ,
											new TimeSeriesPoint().setTime(baseTime.minus(1, ChronoUnit.HOURS)).setValue(new BigDecimal("16.0"))
									}))
							.put(OrderingComparators.MIN,  
									Arrays.asList(new TimeSeriesPoint[] { 
											new TimeSeriesPoint().setTime(baseTime.minus(9, ChronoUnit.HOURS)).setValue(new BigDecimal("13.0")) 
									}))
							.build(),
			new ImmutableMap.Builder<OrderingComparators, Map<String, List<TimeSeriesPoint>>>()
					.build()))
			.build();
		*/
	/**
	 * Test of calculateSummaries method, of class TimeSeriesSummary.
	 */
	/*
	@Test
	public void testMultipleCalculateSummariesWithRelatives() {
		log.debug("testMultipleCalculateSummariesWithRelatives");
		Map<String, TimeSeriesDataServiceResponse> timeseries = incomingMultipleCalculateSummariesWithRelativesTimeSeries;
		String series = DISCHARGE;
		boolean calculateRelativeSummary = true;
		DateRange effectiveDateRange = null;
		int topCount = 2;
		List<TimeSeriesSummary> expResult = expectedMultipleCalculateSummariesWithRelatives;
		List<TimeSeriesSummary> result = TimeSeriesSummary.calculateSummaries(timeseries, series, calculateRelativeSummary, effectiveDateRange, topCount);
		assertEquals(expResult, result);
	}
	
	static final Map<String, TimeSeriesDataServiceResponse> incomingMultipleCalculateSummariesWithRelativesTimeSeries = new ImmutableMap.Builder<String, TimeSeries>()
			.put(DISCHARGE, new TimeSeriesDataServiceResponse()
				.setPoints(new ImmutableList.Builder<TimeSeriesPoint>()
						.add(new TimeSeriesPoint().setTime(baseTime.minus(5, ChronoUnit.HOURS)).setValue(new BigDecimal("121.0")))
						.add(new TimeSeriesPoint().setTime(baseTime.minus(4, ChronoUnit.HOURS)).setValue(new BigDecimal("152.0")))
						.add(new TimeSeriesPoint().setTime(baseTime.minus(3, ChronoUnit.HOURS)).setValue(new BigDecimal("193.0")))
						.add(new TimeSeriesPoint().setTime(baseTime.minus(2, ChronoUnit.HOURS)).setValue(new BigDecimal("144.0")))
						.add(new TimeSeriesPoint().setTime(baseTime.minus(1, ChronoUnit.HOURS)).setValue(new BigDecimal("165.0")))
						.build()))
			.put(DAILYDISCHARGE, new TimeSeriesDataServiceResponse()
				.setPoints(new ImmutableList.Builder<TimeSeriesPoint>()
						.add(new TimeSeriesPoint().setTime(baseTime.minus(5, ChronoUnit.HOURS)).setValue(new BigDecimal("132.0")))
						.add(new TimeSeriesPoint().setTime(baseTime.minus(4, ChronoUnit.HOURS)).setValue(new BigDecimal("145.0")))
						.add(new TimeSeriesPoint().setTime(baseTime.minus(3, ChronoUnit.HOURS)).setValue(new BigDecimal("179.0")))
						.add(new TimeSeriesPoint().setTime(baseTime.minus(2, ChronoUnit.HOURS)).setValue(new BigDecimal("124.0")))
						.add(new TimeSeriesPoint().setTime(baseTime.minus(1, ChronoUnit.HOURS)).setValue(new BigDecimal("161.0")))
						.build()))
			.put(STAGE, new TimeSeriesDataServiceResponse()
				.setPoints(new ImmutableList.Builder<TimeSeriesPoint>()
						.add(new TimeSeriesPoint().setTime(baseTime.minus(5, ChronoUnit.HOURS)).setValue(new BigDecimal("152.0")))
						.add(new TimeSeriesPoint().setTime(baseTime.minus(4, ChronoUnit.HOURS)).setValue(new BigDecimal("115.0")))
						.add(new TimeSeriesPoint().setTime(baseTime.minus(3, ChronoUnit.HOURS)).setValue(new BigDecimal("129.0")))
						.add(new TimeSeriesPoint().setTime(baseTime.minus(2, ChronoUnit.HOURS)).setValue(new BigDecimal("154.0")))
						.add(new TimeSeriesPoint().setTime(baseTime.minus(1, ChronoUnit.HOURS)).setValue(new BigDecimal("126.0")))
						.build()))
			.build();
	static final List<TimeSeriesSummary> expectedMultipleCalculateSummariesWithRelatives = new ImmutableList.Builder<TimeSeriesSummary>()
			.add(new TimeSeriesSummary(null, null, null,
			new ImmutableMap.Builder<OrderingComparators, List<TimeSeriesPoint>>()
					.put(OrderingComparators.MAX, Arrays.asList(new TimeSeriesPoint[] { new TimeSeriesPoint().setTime(baseTime.minus(3, ChronoUnit.HOURS)).setValue(new BigDecimal("193.0")) }))
					.put(OrderingComparators.MIN, Arrays.asList(new TimeSeriesPoint[] { new TimeSeriesPoint().setTime(baseTime.minus(5, ChronoUnit.HOURS)).setValue(new BigDecimal("121.0")) }))
					.build(),
			new ImmutableMap.Builder<OrderingComparators, Map<String, List<TimeSeriesPoint>>>()
					.put(OrderingComparators.MAX, new ImmutableMap.Builder<String, List<TimeSeriesPoint>>()
							.put(DAILYDISCHARGE, Arrays.asList(new TimeSeriesPoint[] { new TimeSeriesPoint().setTime(baseTime.minus(3, ChronoUnit.HOURS)).setValue(new BigDecimal("179.0"))}))
							.put(STAGE, Arrays.asList(new TimeSeriesPoint[] { new TimeSeriesPoint().setTime(baseTime.minus(3, ChronoUnit.HOURS)).setValue(new BigDecimal("129.0")) }))
							.build())
					.put(OrderingComparators.MIN, new ImmutableMap.Builder<String, List<TimeSeriesPoint>>()
							.put(DAILYDISCHARGE, Arrays.asList(new TimeSeriesPoint[] { new TimeSeriesPoint().setTime(baseTime.minus(5, ChronoUnit.HOURS)).setValue(new BigDecimal("132.0")) }))
							.put(STAGE, Arrays.asList(new TimeSeriesPoint[] { new TimeSeriesPoint().setTime(baseTime.minus(5, ChronoUnit.HOURS)).setValue(new BigDecimal("152.0")) }))
							.build())
					.build()))
			.add(new TimeSeriesSummary(null, null, null,
			new ImmutableMap.Builder<OrderingComparators, List<TimeSeriesPoint>>()
					.put(OrderingComparators.MAX, Arrays.asList(new TimeSeriesPoint[] { new TimeSeriesPoint().setTime(baseTime.minus(1, ChronoUnit.HOURS)).setValue(new BigDecimal("165.0")) }))
					.put(OrderingComparators.MIN, Arrays.asList(new TimeSeriesPoint[] { new TimeSeriesPoint().setTime(baseTime.minus(2, ChronoUnit.HOURS)).setValue(new BigDecimal("144.0")) }))
					.build(),
			new ImmutableMap.Builder<OrderingComparators, Map<String, List<TimeSeriesPoint>>>()
					.put(OrderingComparators.MAX, new ImmutableMap.Builder<String, List<TimeSeriesPoint>>()
							.put(DAILYDISCHARGE, Arrays.asList(new TimeSeriesPoint[] { new TimeSeriesPoint().setTime(baseTime.minus(1, ChronoUnit.HOURS)).setValue(new BigDecimal("161.0")) }))
							.put(STAGE, Arrays.asList(new TimeSeriesPoint[] { new TimeSeriesPoint().setTime(baseTime.minus(1, ChronoUnit.HOURS)).setValue(new BigDecimal("126.0")) }))
							.build())
					.put(OrderingComparators.MIN, new ImmutableMap.Builder<String, List<TimeSeriesPoint>>()
							.put(DAILYDISCHARGE, Arrays.asList(new TimeSeriesPoint[] { new TimeSeriesPoint().setTime(baseTime.minus(2, ChronoUnit.HOURS)).setValue(new BigDecimal("124.0")) }))
							.put(STAGE, Arrays.asList(new TimeSeriesPoint[] { new TimeSeriesPoint().setTime(baseTime.minus(2, ChronoUnit.HOURS)).setValue(new BigDecimal("154.0")) }))
							.build())
					.build()))
			.build();
	*/
	
	/**
	 * Test of calculateSummaries method, of class TimeSeriesSummary.
	 */
	/*
	@Test
	public void testMultipleWithTiesCalculateSummariesWithRelatives() {
		log.debug("testMultipleCalculateSummariesWithRelatives");
		Map<String, TimeSeriesDataServiceResponse> timeseries = incomingMultipleWithTiesCalculateSummariesWithRelativesTimeSeries;
		String series = DISCHARGE;
		boolean calculateRelativeSummary = true;
		DateRange effectiveDateRange = null;
		int topCount = 2;
		List<TimeSeriesSummary> expResult = expectedMultipleWithTiesCalculateSummariesWithRelatives;
		List<TimeSeriesSummary> result = TimeSeriesSummary.calculateSummaries(timeseries, series, calculateRelativeSummary, effectiveDateRange, topCount);
		assertEquals(expResult, result);
	}
	
	static final Map<String, TimeSeriesDataServiceResponse> incomingMultipleWithTiesCalculateSummariesWithRelativesTimeSeries = new ImmutableMap.Builder<String, TimeSeries>()
			.put(DISCHARGE, new TimeSeriesDataServiceResponse()
				.setPoints(new ImmutableList.Builder<TimeSeriesPoint>()
						.add(new TimeSeriesPoint().setTime(baseTime.minus(7, ChronoUnit.HOURS)).setValue(new BigDecimal("151.0")))
						.add(new TimeSeriesPoint().setTime(baseTime.minus(6, ChronoUnit.HOURS)).setValue(new BigDecimal("12.0")))
						.add(new TimeSeriesPoint().setTime(baseTime.minus(5, ChronoUnit.HOURS)).setValue(new BigDecimal("193.0")))
						.add(new TimeSeriesPoint().setTime(baseTime.minus(4, ChronoUnit.HOURS)).setValue(new BigDecimal("152.0")))
						.add(new TimeSeriesPoint().setTime(baseTime.minus(3, ChronoUnit.HOURS)).setValue(new BigDecimal("193.0")))
						.add(new TimeSeriesPoint().setTime(baseTime.minus(2, ChronoUnit.HOURS)).setValue(new BigDecimal("12.0")))
						.add(new TimeSeriesPoint().setTime(baseTime.minus(1, ChronoUnit.HOURS)).setValue(new BigDecimal("165.0")))
						.build()))
			.put(DAILYDISCHARGE, new TimeSeriesDataServiceResponse()
				.setPoints(new ImmutableList.Builder<TimeSeriesPoint>()
						.add(new TimeSeriesPoint().setTime(baseTime.minus(7, ChronoUnit.HOURS)).setValue(new BigDecimal("134.0")))
						.add(new TimeSeriesPoint().setTime(baseTime.minus(6, ChronoUnit.HOURS)).setValue(new BigDecimal("137.0")))
						.add(new TimeSeriesPoint().setTime(baseTime.minus(5, ChronoUnit.HOURS)).setValue(new BigDecimal("132.0")))
						.add(new TimeSeriesPoint().setTime(baseTime.minus(4, ChronoUnit.HOURS)).setValue(new BigDecimal("145.0")))
						.add(new TimeSeriesPoint().setTime(baseTime.minus(3, ChronoUnit.HOURS)).setValue(new BigDecimal("179.0")))
						.add(new TimeSeriesPoint().setTime(baseTime.minus(2, ChronoUnit.HOURS)).setValue(new BigDecimal("124.0")))
						.add(new TimeSeriesPoint().setTime(baseTime.minus(1, ChronoUnit.HOURS)).setValue(new BigDecimal("161.0")))
						.build()))
			.put(STAGE, new TimeSeriesDataServiceResponse()
				.setPoints(new ImmutableList.Builder<TimeSeriesPoint>()
						.add(new TimeSeriesPoint().setTime(baseTime.minus(7, ChronoUnit.HOURS)).setValue(new BigDecimal("111.0")))
						.add(new TimeSeriesPoint().setTime(baseTime.minus(6, ChronoUnit.HOURS)).setValue(new BigDecimal("113.0")))
						.add(new TimeSeriesPoint().setTime(baseTime.minus(5, ChronoUnit.HOURS)).setValue(new BigDecimal("154.0")))
						.add(new TimeSeriesPoint().setTime(baseTime.minus(4, ChronoUnit.HOURS)).setValue(new BigDecimal("115.0")))
						.add(new TimeSeriesPoint().setTime(baseTime.minus(3, ChronoUnit.HOURS)).setValue(new BigDecimal("92.0")))
						.add(new TimeSeriesPoint().setTime(baseTime.minus(2, ChronoUnit.HOURS)).setValue(new BigDecimal("154.0")))
						.add(new TimeSeriesPoint().setTime(baseTime.minus(1, ChronoUnit.HOURS)).setValue(new BigDecimal("126.0")))
						.build()))
			.build();
	static final List<TimeSeriesSummary> expectedMultipleWithTiesCalculateSummariesWithRelatives = new ImmutableList.Builder<TimeSeriesSummary>()
			.add(new TimeSeriesSummary(null, null, null,
			new ImmutableMap.Builder<OrderingComparators, List<TimeSeriesPoint>>()
					.put(OrderingComparators.MAX, Arrays.asList(new TimeSeriesPoint[] { 
							new TimeSeriesPoint().setTime(baseTime.minus(5, ChronoUnit.HOURS)).setValue(new BigDecimal("193.0")),
							new TimeSeriesPoint().setTime(baseTime.minus(3, ChronoUnit.HOURS)).setValue(new BigDecimal("193.0"))
							}))
					.put(OrderingComparators.MIN, Arrays.asList(new TimeSeriesPoint[] { 
							new TimeSeriesPoint().setTime(baseTime.minus(6, ChronoUnit.HOURS)).setValue(new BigDecimal("12.0")),
							new TimeSeriesPoint().setTime(baseTime.minus(2, ChronoUnit.HOURS)).setValue(new BigDecimal("12.0"))
							}))
					.build(),
			new ImmutableMap.Builder<OrderingComparators, Map<String, List<TimeSeriesPoint>>>()
					.put(OrderingComparators.MAX, new ImmutableMap.Builder<String, List<TimeSeriesPoint>>()
							.put(DAILYDISCHARGE, Arrays.asList(new TimeSeriesPoint[] { 
									new TimeSeriesPoint().setTime(baseTime.minus(5, ChronoUnit.HOURS)).setValue(new BigDecimal("132.0")),
									new TimeSeriesPoint().setTime(baseTime.minus(3, ChronoUnit.HOURS)).setValue(new BigDecimal("179.0"))
									}))
							.put(STAGE, Arrays.asList(new TimeSeriesPoint[] { 
									new TimeSeriesPoint().setTime(baseTime.minus(5, ChronoUnit.HOURS)).setValue(new BigDecimal("154.0")),
									new TimeSeriesPoint().setTime(baseTime.minus(3, ChronoUnit.HOURS)).setValue(new BigDecimal("92.0"))  
									}))
							.build())
					.put(OrderingComparators.MIN, new ImmutableMap.Builder<String, List<TimeSeriesPoint>>()
							.put(DAILYDISCHARGE, Arrays.asList(new TimeSeriesPoint[] { 
									new TimeSeriesPoint().setTime(baseTime.minus(6, ChronoUnit.HOURS)).setValue(new BigDecimal("137.0")),
									new TimeSeriesPoint().setTime(baseTime.minus(2, ChronoUnit.HOURS)).setValue(new BigDecimal("124.0"))
									}))
							.put(STAGE, Arrays.asList(new TimeSeriesPoint[] { 
									new TimeSeriesPoint().setTime(baseTime.minus(6, ChronoUnit.HOURS)).setValue(new BigDecimal("113.0")),
									new TimeSeriesPoint().setTime(baseTime.minus(2, ChronoUnit.HOURS)).setValue(new BigDecimal("154.0"))
									}))
							.build())
					.build()))
			.add(new TimeSeriesSummary(null, null, null,
			new ImmutableMap.Builder<OrderingComparators, List<TimeSeriesPoint>>()
					.put(OrderingComparators.MAX, Arrays.asList(new TimeSeriesPoint[] { new TimeSeriesPoint().setTime(baseTime.minus(1, ChronoUnit.HOURS)).setValue(new BigDecimal("165.0")) }))
					.put(OrderingComparators.MIN, Arrays.asList(new TimeSeriesPoint[] { new TimeSeriesPoint().setTime(baseTime.minus(7, ChronoUnit.HOURS)).setValue(new BigDecimal("151.0")) }))
					.build(),
			new ImmutableMap.Builder<OrderingComparators, Map<String, List<TimeSeriesPoint>>>()
					.put(OrderingComparators.MAX, new ImmutableMap.Builder<String, List<TimeSeriesPoint>>()
							.put(DAILYDISCHARGE, Arrays.asList(new TimeSeriesPoint[] { new TimeSeriesPoint().setTime(baseTime.minus(1, ChronoUnit.HOURS)).setValue(new BigDecimal("161.0")) }))
							.put(STAGE, Arrays.asList(new TimeSeriesPoint[] { new TimeSeriesPoint().setTime(baseTime.minus(1, ChronoUnit.HOURS)).setValue(new BigDecimal("126.0")) }))
							.build())
					.put(OrderingComparators.MIN, new ImmutableMap.Builder<String, List<TimeSeriesPoint>>()
							.put(DAILYDISCHARGE, Arrays.asList(new TimeSeriesPoint[] { new TimeSeriesPoint().setTime(baseTime.minus(7, ChronoUnit.HOURS)).setValue(new BigDecimal("134.0")) }))
							.put(STAGE, Arrays.asList(new TimeSeriesPoint[] { new TimeSeriesPoint().setTime(baseTime.minus(7, ChronoUnit.HOURS)).setValue(new BigDecimal("111.0")) }))
							.build())
					.build()))
			.build();
			*/
}

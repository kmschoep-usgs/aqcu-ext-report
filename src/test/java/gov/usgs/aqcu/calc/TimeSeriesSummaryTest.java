package gov.usgs.aqcu.calc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Qualifier;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesPoint;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.StatisticalDateTimeOffset;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.DoubleWithDisplay;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import gov.usgs.aqcu.model.TimeSeriesCorrectedData;

/**
 *
 * @author kmschoep
 */
public class TimeSeriesSummaryTest {
	private static final Logger log = LoggerFactory.getLogger(TimeSeriesSummaryTest.class);
	
	protected static final Instant baseTime = Instant.now().minus(365, ChronoUnit.DAYS);
	
	protected static final String DISCHARGE = "DISCHARGE";
	protected static final String STAGE = "STAGE";
	protected static final String DAILYDISCHARGE = "DAILYDISCHARGE";
	private static Qualifier qualifier1 = new Qualifier();
	private static Qualifier qualifier2 = new Qualifier();
	private static Qualifier qualifier3 = new Qualifier();
	private static ArrayList<Qualifier> qualifiers = new ArrayList<>();
	
	private static Map<String, TimeSeriesCorrectedData> incomingDefaultCalculateSummariesTimeSeries = new HashMap<>();
	private static Map<String, TimeSeriesCorrectedData> incomingDefaultCalculateSummariesWithRelativesTimeSeries = new HashMap<>();
	private static Map<String, TimeSeriesCorrectedData> incomingMultipleCalculateSummariesWithRelativesTimeSeries = new HashMap<>();
	private static Map<OrderingComparators, List<TimeSeriesPoint>> incomingDefaultFilterQualifiersTimeSeriesPoints = new HashMap<>();
	private static Map<OrderingComparators, List<TimeSeriesPoint>> incomingEdgeFilterQualifiersTimeSeriesPoints = new HashMap<>();
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
		qualifier1 = new Qualifier();
		qualifier2 = new Qualifier();
		qualifier3 = new Qualifier();
		qualifiers = new ArrayList<>();
		
		incomingDefaultCalculateSummariesTimeSeries = new ImmutableMap.Builder<String, TimeSeriesCorrectedData>()
				.put(DISCHARGE, new TimeSeriesCorrectedData()
					.setPoints(Arrays.asList(
							new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(5, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("12.0").setNumeric(12.0)),
							new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(4, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("15.0").setNumeric(15.0)),
							new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(3, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("19.0").setNumeric(19.0)),
							new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(2, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("14.0").setNumeric(14.0)),
							new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(1, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("16.0").setNumeric(16.0))
							)))
				.build();
		
		expectedDefaultCalculateSummaries = new ImmutableList.Builder<TimeSeriesSummary>()
				.add(new TimeSeriesSummary(null, null, null,
				new ImmutableMap.Builder<OrderingComparators, List<TimeSeriesPoint>>()
						.put(OrderingComparators.MAX, Arrays.asList(new TimeSeriesPoint[] { new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(3, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("19.0").setNumeric(19.0)) }))
						.put(OrderingComparators.MIN, Arrays.asList(new TimeSeriesPoint[] { new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(5, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("12.0").setNumeric(12.0)) }))
						.build(),
				new ImmutableMap.Builder<OrderingComparators, Map<String, List<TimeSeriesPoint>>>()
						.build()))
				.build();
		
		incomingDefaultCalculateSummariesWithRelativesTimeSeries = new ImmutableMap.Builder<String, TimeSeriesCorrectedData>()
				.put(DISCHARGE, new TimeSeriesCorrectedData()
					.setPoints(Arrays.asList(
							new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(5, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("121.0").setNumeric(121.0)),
							new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(4, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("152.0").setNumeric(152.0)),
							new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(3, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("193.0").setNumeric(193.0)),
							new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(2, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("144.0").setNumeric(144.0)),
							new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(1, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("165.0").setNumeric(165.0))
							)))
				.put(STAGE, new TimeSeriesCorrectedData()
					.setPoints(Arrays.asList(
							new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(5, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("152.0").setNumeric(152.0)),
							new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(4, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("115.0").setNumeric(115.0)),
							new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(3, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("129.0").setNumeric(129.0)),
							new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(2, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("154.0").setNumeric(154.0)),
							new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(1, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("126.0").setNumeric(126.0))
							)))
				.put(DAILYDISCHARGE, new TimeSeriesCorrectedData()
					.setPoints(Arrays.asList(
							new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(5, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("132.0").setNumeric(132.0)),
							new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(4, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("145.0").setNumeric(145.0)),
							new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(3, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("179.0").setNumeric(179.0)),
							new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(2, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("124.0").setNumeric(124.0)),
							new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(1, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("161.0").setNumeric(161.0))
							)))
				.build();
		
		expectedDefaultCalculateSummariesWithRelatives = new ImmutableList.Builder<TimeSeriesSummary>()
				.add(new TimeSeriesSummary(null, null, null,
				new ImmutableMap.Builder<OrderingComparators, List<TimeSeriesPoint>>()
						.put(OrderingComparators.MAX, Arrays.asList(new TimeSeriesPoint[] { new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(3, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("193.0").setNumeric(193.0)) }))
						.put(OrderingComparators.MIN, Arrays.asList(new TimeSeriesPoint[] { new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(5, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("121.0").setNumeric(121.0)) }))
						.build(),
				new ImmutableMap.Builder<OrderingComparators, Map<String, List<TimeSeriesPoint>>>()
						.put(OrderingComparators.MAX, new ImmutableMap.Builder<String, List<TimeSeriesPoint>>()
								.put(STAGE, Arrays.asList(new TimeSeriesPoint[] { new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(3, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("129.0").setNumeric(129.0)) }))
								.put(DAILYDISCHARGE, Arrays.asList(new TimeSeriesPoint[] { new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(3, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("179.0").setNumeric(179.0)) }))
								.build())
						.put(OrderingComparators.MIN, new ImmutableMap.Builder<String, List<TimeSeriesPoint>>()
								.put(STAGE, Arrays.asList(new TimeSeriesPoint[] { new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(5, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("152.0").setNumeric(152.0)) }))
								.put(DAILYDISCHARGE, Arrays.asList(new TimeSeriesPoint[] { new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(5, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("132.0").setNumeric(132.0)) }))
								.build())
						.build()))
				.build();
		
		incomingMultipleCalculateSummariesWithRelativesTimeSeries = new ImmutableMap.Builder<String, TimeSeriesCorrectedData>()
				.put(DISCHARGE, new TimeSeriesCorrectedData()
					.setPoints(Arrays.asList(
							new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(6, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("193.0").setNumeric(193.0)),
							new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(5, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("121.0").setNumeric(121.0)),
							new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(4, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("152.0").setNumeric(152.0)),
							new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(3, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("193.0").setNumeric(193.0)),
							new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(2, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("144.0").setNumeric(144.0)),
							new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(1, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("165.0").setNumeric(165.0))
							)))
				.put(STAGE, new TimeSeriesCorrectedData()
					.setPoints(Arrays.asList(
							new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(6, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("101.0").setNumeric(101.0)),
							new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(5, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("152.0").setNumeric(152.0)),
							new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(4, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("115.0").setNumeric(115.0)),
							new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(3, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("129.0").setNumeric(129.0)),
							new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(2, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("154.0").setNumeric(154.0)),
							new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(1, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("126.0").setNumeric(126.0))
							)))
				.put(DAILYDISCHARGE, new TimeSeriesCorrectedData()
					.setPoints(Arrays.asList(
							new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(6, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("141.0").setNumeric(141.0)),
							new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(5, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("132.0").setNumeric(132.0)),
							new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(4, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("145.0").setNumeric(145.0)),
							new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(3, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("179.0").setNumeric(179.0)),
							new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(2, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("124.0").setNumeric(124.0)),
							new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(1, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("161.0").setNumeric(161.0))
							)))
				.build();
		
		expectedDefaultCalculateSummariesWithRelatives = new ImmutableList.Builder<TimeSeriesSummary>()
				.add(new TimeSeriesSummary(null, null, null,
				new ImmutableMap.Builder<OrderingComparators, List<TimeSeriesPoint>>()
						.put(OrderingComparators.MAX, Arrays.asList(new TimeSeriesPoint[] { new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(3, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("193.0").setNumeric(193.0)) }))
						.put(OrderingComparators.MIN, Arrays.asList(new TimeSeriesPoint[] { new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(5, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("121.0").setNumeric(121.0)) }))
						.build(),
				new ImmutableMap.Builder<OrderingComparators, Map<String, List<TimeSeriesPoint>>>()
						.put(OrderingComparators.MAX, new ImmutableMap.Builder<String, List<TimeSeriesPoint>>()
								.put(STAGE, Arrays.asList(new TimeSeriesPoint[] { new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(3, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("129.0").setNumeric(129.0)) }))
								.put(DAILYDISCHARGE, Arrays.asList(new TimeSeriesPoint[] { new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(3, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("179.0").setNumeric(179.0)) }))
								.build())
						.put(OrderingComparators.MIN, new ImmutableMap.Builder<String, List<TimeSeriesPoint>>()
								.put(STAGE, Arrays.asList(new TimeSeriesPoint[] { new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(5, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("152.0").setNumeric(152.0)) }))
								.put(DAILYDISCHARGE, Arrays.asList(new TimeSeriesPoint[] { new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(5, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("132.0").setNumeric(132.0)) }))
								.build())
						.build()))
				.build();
		
		expectedMultipleCalculateSummariesWithRelatives = new ImmutableList.Builder<TimeSeriesSummary>()
				.add(new TimeSeriesSummary(null, null, null,
				new ImmutableMap.Builder<OrderingComparators, List<TimeSeriesPoint>>()
						.put(OrderingComparators.MAX, Arrays.asList(new TimeSeriesPoint[] { new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(6, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("193.0").setNumeric(193.0)) ,
								new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(3, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("193.0")) }))
						.put(OrderingComparators.MIN, Arrays.asList(new TimeSeriesPoint[] { new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(5, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("121.0").setNumeric(121.0)) }))
						.build(),
				new ImmutableMap.Builder<OrderingComparators, Map<String, List<TimeSeriesPoint>>>()
						.put(OrderingComparators.MAX, new ImmutableMap.Builder<String, List<TimeSeriesPoint>>()
								.put(STAGE, Arrays.asList(new TimeSeriesPoint[] { new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(6, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("101.0").setNumeric(101.0)),
										new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(3, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("129.0").setNumeric(129.0))}))
								.put(DAILYDISCHARGE, Arrays.asList(new TimeSeriesPoint[] { new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(6, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("141.0").setNumeric(141.0)),
										new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(3, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("179.0").setNumeric(179.0))}))
								.build())
						.put(OrderingComparators.MIN, new ImmutableMap.Builder<String, List<TimeSeriesPoint>>()
								.put(STAGE, Arrays.asList(new TimeSeriesPoint[] { new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(5, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("152.0").setNumeric(152.0)) }))
								.put(DAILYDISCHARGE, Arrays.asList(new TimeSeriesPoint[] { new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(5, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("132.0").setNumeric(132.0)) }))
								.build())
						.build()))
				.build();
		
		incomingDefaultFilterQualifiersTimeSeriesPoints = new ImmutableMap.Builder<OrderingComparators, List<TimeSeriesPoint>>()
				.put(OrderingComparators.MAX, Arrays.asList(new TimeSeriesPoint[] { new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(3, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("19.0").setNumeric(19.0)) }))
				.put(OrderingComparators.MIN, Arrays.asList(new TimeSeriesPoint[] { new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(5, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("12.0").setNumeric(12.0)) }))
				.build();
		
		incomingEdgeFilterQualifiersTimeSeriesPoints = new ImmutableMap.Builder<OrderingComparators, List<TimeSeriesPoint>>()
				.put(OrderingComparators.MAX, Arrays.asList(new TimeSeriesPoint[] { new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(Instant.parse("2018-08-02T05:00:00Z"))).setValue(new DoubleWithDisplay().setDisplay("19.0").setNumeric(19.0)) }))
				.put(OrderingComparators.MIN, Arrays.asList(new TimeSeriesPoint[] { new TimeSeriesPoint().setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(baseTime.minus(5, ChronoUnit.HOURS))).setValue(new DoubleWithDisplay().setDisplay("12.0").setNumeric(12.0)) }))
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
		assertEquals(expResult.get(0).get(OrderingComparators.MAX).get(0).getTimestamp().getDateTimeOffset(), result.get(0).get(OrderingComparators.MAX).get(0).getTimestamp().getDateTimeOffset());
		assertEquals(expResult.get(0).get(OrderingComparators.MAX).get(0).getValue().getDisplay(), result.get(0).get(OrderingComparators.MAX).get(0).getValue().getDisplay());
		assertEquals(expResult.get(0).get(OrderingComparators.MIN).get(0).getTimestamp().getDateTimeOffset(), result.get(0).get(OrderingComparators.MIN).get(0).getTimestamp().getDateTimeOffset());
		assertEquals(expResult.get(0).get(OrderingComparators.MIN).get(0).getValue().getDisplay(), result.get(0).get(OrderingComparators.MIN).get(0).getValue().getDisplay());

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
		assertEquals(expResult.get(0).get(OrderingComparators.MAX).get(0).getTimestamp().getDateTimeOffset(), result.get(0).get(OrderingComparators.MAX).get(0).getTimestamp().getDateTimeOffset());
		assertEquals(expResult.get(0).get(OrderingComparators.MAX).get(0).getValue().getDisplay(), result.get(0).get(OrderingComparators.MAX).get(0).getValue().getDisplay());
		assertEquals(expResult.get(0).get(OrderingComparators.MIN).get(0).getTimestamp().getDateTimeOffset(), result.get(0).get(OrderingComparators.MIN).get(0).getTimestamp().getDateTimeOffset());
		assertEquals(expResult.get(0).get(OrderingComparators.MIN).get(0).getValue().getDisplay(), result.get(0).get(OrderingComparators.MIN).get(0).getValue().getDisplay());
		assertEquals(expResult.get(0).getAt(OrderingComparators.MAX, DAILYDISCHARGE).get(0).getTimestamp().getDateTimeOffset(), result.get(0).getAt(OrderingComparators.MAX, DAILYDISCHARGE).get(0).getTimestamp().getDateTimeOffset());
		assertEquals(expResult.get(0).getAt(OrderingComparators.MAX, DAILYDISCHARGE).get(0).getValue().getDisplay(), result.get(0).getAt(OrderingComparators.MAX, DAILYDISCHARGE).get(0).getValue().getDisplay());
		assertNotEquals(result.get(0).getAt(OrderingComparators.MAX, DAILYDISCHARGE).get(0).getValue().getDisplay(), result.get(0).get(OrderingComparators.MAX).get(0).getValue().getDisplay());
		assertEquals(expResult.get(0).getAt(OrderingComparators.MIN, DAILYDISCHARGE).get(0).getTimestamp().getDateTimeOffset(), result.get(0).getAt(OrderingComparators.MIN, DAILYDISCHARGE).get(0).getTimestamp().getDateTimeOffset());
		assertEquals(expResult.get(0).getAt(OrderingComparators.MIN, DAILYDISCHARGE).get(0).getValue().getDisplay(), result.get(0).getAt(OrderingComparators.MIN, DAILYDISCHARGE).get(0).getValue().getDisplay());
		assertNotEquals(result.get(0).getAt(OrderingComparators.MIN, DAILYDISCHARGE).get(0).getValue().getDisplay(), result.get(0).get(OrderingComparators.MIN).get(0).getValue().getDisplay());
		assertEquals(expResult.get(0).getAt(OrderingComparators.MAX, STAGE).get(0).getTimestamp().getDateTimeOffset(), result.get(0).getAt(OrderingComparators.MAX, STAGE).get(0).getTimestamp().getDateTimeOffset());
		assertEquals(expResult.get(0).getAt(OrderingComparators.MAX, STAGE).get(0).getValue().getDisplay(), result.get(0).getAt(OrderingComparators.MAX, STAGE).get(0).getValue().getDisplay());
		assertNotEquals(result.get(0).getAt(OrderingComparators.MAX, STAGE).get(0).getValue().getDisplay(), result.get(0).get(OrderingComparators.MAX).get(0).getValue().getDisplay());
		assertEquals(expResult.get(0).getAt(OrderingComparators.MIN, STAGE).get(0).getTimestamp().getDateTimeOffset(), result.get(0).getAt(OrderingComparators.MIN, STAGE).get(0).getTimestamp().getDateTimeOffset());
		assertEquals(expResult.get(0).getAt(OrderingComparators.MIN, STAGE).get(0).getValue().getDisplay(), result.get(0).getAt(OrderingComparators.MIN, STAGE).get(0).getValue().getDisplay());
		assertNotEquals(result.get(0).getAt(OrderingComparators.MIN, STAGE).get(0).getValue().getDisplay(), result.get(0).get(OrderingComparators.MIN).get(0).getValue().getDisplay());

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
		assertEquals(expResult.get(0).get(OrderingComparators.MAX).get(0).getTimestamp().getDateTimeOffset(), result.get(0).get(OrderingComparators.MAX).get(0).getTimestamp().getDateTimeOffset());
		assertEquals(expResult.get(0).get(OrderingComparators.MAX).get(0).getValue().getDisplay(), result.get(0).get(OrderingComparators.MAX).get(0).getValue().getDisplay());
		assertEquals(expResult.get(0).get(OrderingComparators.MAX).get(1).getTimestamp().getDateTimeOffset(), result.get(0).get(OrderingComparators.MAX).get(1).getTimestamp().getDateTimeOffset());
		assertEquals(expResult.get(0).get(OrderingComparators.MAX).get(1).getValue().getDisplay(), result.get(0).get(OrderingComparators.MAX).get(1).getValue().getDisplay());
		assertEquals(expResult.get(0).get(OrderingComparators.MIN).get(0).getTimestamp().getDateTimeOffset(), result.get(0).get(OrderingComparators.MIN).get(0).getTimestamp().getDateTimeOffset());
		assertEquals(expResult.get(0).get(OrderingComparators.MIN).get(0).getValue().getDisplay(), result.get(0).get(OrderingComparators.MIN).get(0).getValue().getDisplay());
		assertEquals(expResult.get(0).getAt(OrderingComparators.MAX, DAILYDISCHARGE).get(0).getTimestamp().getDateTimeOffset(), result.get(0).getAt(OrderingComparators.MAX, DAILYDISCHARGE).get(0).getTimestamp().getDateTimeOffset());
		assertEquals(expResult.get(0).getAt(OrderingComparators.MAX, DAILYDISCHARGE).get(0).getValue().getDisplay(), result.get(0).getAt(OrderingComparators.MAX, DAILYDISCHARGE).get(0).getValue().getDisplay());
		assertNotEquals(result.get(0).getAt(OrderingComparators.MAX, DAILYDISCHARGE).get(0).getValue().getDisplay(), result.get(0).get(OrderingComparators.MAX).get(0).getValue().getDisplay());
		assertEquals(expResult.get(0).getAt(OrderingComparators.MIN, DAILYDISCHARGE).get(0).getTimestamp().getDateTimeOffset(), result.get(0).getAt(OrderingComparators.MIN, DAILYDISCHARGE).get(0).getTimestamp().getDateTimeOffset());
		assertEquals(expResult.get(0).getAt(OrderingComparators.MIN, DAILYDISCHARGE).get(0).getValue().getDisplay(), result.get(0).getAt(OrderingComparators.MIN, DAILYDISCHARGE).get(0).getValue().getDisplay());
		assertNotEquals(result.get(0).getAt(OrderingComparators.MIN, DAILYDISCHARGE).get(0).getValue().getDisplay(), result.get(0).get(OrderingComparators.MIN).get(0).getValue().getDisplay());
		assertEquals(expResult.get(0).getAt(OrderingComparators.MAX, DAILYDISCHARGE).get(1).getTimestamp().getDateTimeOffset(), result.get(0).getAt(OrderingComparators.MAX, DAILYDISCHARGE).get(1).getTimestamp().getDateTimeOffset());
		assertEquals(expResult.get(0).getAt(OrderingComparators.MAX, DAILYDISCHARGE).get(1).getValue().getDisplay(), result.get(0).getAt(OrderingComparators.MAX, DAILYDISCHARGE).get(1).getValue().getDisplay());
		assertNotEquals(result.get(0).getAt(OrderingComparators.MAX, DAILYDISCHARGE).get(1).getValue().getDisplay(), result.get(0).get(OrderingComparators.MAX).get(1).getValue().getDisplay());
		assertEquals(expResult.get(0).getAt(OrderingComparators.MAX, STAGE).get(0).getTimestamp().getDateTimeOffset(), result.get(0).getAt(OrderingComparators.MAX, STAGE).get(0).getTimestamp().getDateTimeOffset());
		assertEquals(expResult.get(0).getAt(OrderingComparators.MAX, STAGE).get(0).getValue().getDisplay(), result.get(0).getAt(OrderingComparators.MAX, STAGE).get(0).getValue().getDisplay());
		assertNotEquals(result.get(0).getAt(OrderingComparators.MAX, STAGE).get(0).getValue().getDisplay(), result.get(0).get(OrderingComparators.MAX).get(0).getValue().getDisplay());
		assertEquals(expResult.get(0).getAt(OrderingComparators.MIN, STAGE).get(0).getTimestamp().getDateTimeOffset(), result.get(0).getAt(OrderingComparators.MIN, STAGE).get(0).getTimestamp().getDateTimeOffset());
		assertEquals(expResult.get(0).getAt(OrderingComparators.MIN, STAGE).get(0).getValue().getDisplay(), result.get(0).getAt(OrderingComparators.MIN, STAGE).get(0).getValue().getDisplay());
		assertNotEquals(result.get(0).getAt(OrderingComparators.MIN, STAGE).get(0).getValue().getDisplay(), result.get(0).get(OrderingComparators.MIN).get(0).getValue().getDisplay());
		assertEquals(expResult.get(0).getAt(OrderingComparators.MAX, STAGE).get(1).getTimestamp().getDateTimeOffset(), result.get(0).getAt(OrderingComparators.MAX, STAGE).get(1).getTimestamp().getDateTimeOffset());
		assertEquals(expResult.get(0).getAt(OrderingComparators.MAX, STAGE).get(1).getValue().getDisplay(), result.get(0).getAt(OrderingComparators.MAX, STAGE).get(1).getValue().getDisplay());
		assertNotEquals(result.get(0).getAt(OrderingComparators.MAX, STAGE).get(1).getValue().getDisplay(), result.get(0).get(OrderingComparators.MAX).get(1).getValue().getDisplay());
	}
	
	/**
	 * Test of calculateSummaries method, of class TimeSeriesSummary.
	 */
	@Test
	public void testDefaultFilterQualifiers() {
		log.debug("testDefaultFilterQualifiers");
		qualifier1.setDateApplied(Instant.parse("2014-10-28T09:53:00Z"));
		qualifier1.setIdentifier("MIN_MAX");
		qualifier1.setStartTime(baseTime.minus(6, ChronoUnit.HOURS));
		qualifier1.setEndTime(baseTime.minus(2, ChronoUnit.HOURS));
		
		qualifier2.setDateApplied(Instant.parse("2013-10-28T09:53:00Z"));
		qualifier2.setIdentifier("OUTSIDE");
		qualifier2.setStartTime(baseTime.minus(1, ChronoUnit.HOURS));
		qualifier2.setEndTime(baseTime.minus(2, ChronoUnit.HOURS));
		
		qualifier3.setDateApplied(Instant.parse("2013-10-28T09:53:00Z"));
		qualifier3.setIdentifier("EQUAL_MIN");
		qualifier3.setStartTime(baseTime.minus(7, ChronoUnit.HOURS));
		qualifier3.setEndTime(baseTime.minus(5, ChronoUnit.HOURS));
		
		qualifiers.add(qualifier1);
		qualifiers.add(qualifier2);
		qualifiers.add(qualifier3);
		
		Map<OrderingComparators, List<TimeSeriesPoint>> extremePoints = incomingDefaultFilterQualifiersTimeSeriesPoints;
		List<Qualifier> result = TimeSeriesSummary.filterQualifiers(extremePoints, qualifiers);
		assertEquals(2, result.size());
		assertEquals("MIN_MAX", result.get(0).getIdentifier());
		assertEquals("EQUAL_MIN", result.get(1).getIdentifier());
	}
	
	/**
	 * Test of calculateSummaries method, of class TimeSeriesSummary.
	 */
	@Test
	public void testEdgeFilterQualifiers() {
		log.debug("testEdgeFilterQualifiers");
		qualifier1.setDateApplied(Instant.parse("2014-10-28T09:53:00Z"));
		qualifier1.setIdentifier("MIN_MAX");
		qualifier1.setStartTime(Instant.parse("2018-08-02T05:00:00Z"));
		qualifier1.setEndTime(Instant.parse("2018-08-02T05:00:00.000000100Z"));
		
		qualifiers.add(qualifier1);
		
		Map<OrderingComparators, List<TimeSeriesPoint>> extremePoints = incomingEdgeFilterQualifiersTimeSeriesPoints;
		List<Qualifier> result = TimeSeriesSummary.filterQualifiers(extremePoints, qualifiers);
		assertEquals(1, result.size());
		assertEquals("MIN_MAX", result.get(0).getIdentifier());
	}

}

package gov.usgs.aqcu.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.LocationDescription;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Qualifier;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.QualifierMetadata;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDataServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescription;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesPoint;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.DoubleWithDisplay;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.StatisticalDateTimeOffset;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import gov.usgs.aqcu.model.ExtremesMinMax;
import gov.usgs.aqcu.model.ExtremesPoint;
import gov.usgs.aqcu.model.ExtremesReport;
import gov.usgs.aqcu.model.ExtremesReportMetadata;
import gov.usgs.aqcu.parameter.ExtremesRequestParameters;
import gov.usgs.aqcu.retrieval.LocationDescriptionListService;
import gov.usgs.aqcu.retrieval.QualifierLookupService;
import gov.usgs.aqcu.retrieval.TimeSeriesDataService;
import gov.usgs.aqcu.retrieval.TimeSeriesDescriptionListService;
import gov.usgs.aqcu.util.AqcuTimeUtils;
import gov.usgs.aqcu.util.DoubleWithDisplayUtil;

@RunWith(SpringRunner.class)
public class ReportBuilderServiceTest {
	private ReportBuilderService service;
	private MinMaxBuilderService minMaxBuilderService;
	private ExtremesRequestParameters requestParameters;

	@MockBean
	LocationDescriptionListService locDescService;
	@MockBean
	TimeSeriesDescriptionListService tsDescService;
	@MockBean
	TimeSeriesDataService tsDataService;
	@MockBean
	QualifierLookupService qualLookupService;

	private TimeSeriesDescription primaryDesc = new TimeSeriesDescription()
		.setUniqueId("primaryTsId")
		.setUtcOffset(0.0D)
		.setLocationIdentifier("loc1")
		.setIdentifier("primary")
		.setParameter("primary-param")
		.setUnit("primary-unit")
		.setComputationPeriodIdentifier("realtime");
	private TimeSeriesDescription upchainDesc = new TimeSeriesDescription()
		.setUniqueId("upchainTsId")
		.setUtcOffset(0.0D)
		.setLocationIdentifier("loc1")
		.setIdentifier("upchain")
		.setParameter("upchain-param")
		.setUnit("upchain-unit")
		.setComputationPeriodIdentifier("realtime");
	private TimeSeriesDescription derivedDesc = new TimeSeriesDescription()
		.setUniqueId("derivedTsId")
		.setUtcOffset(0.0D)
		.setLocationIdentifier("loc1")
		.setIdentifier("derived")
		.setParameter("derived-param")
		.setUnit("derived-unit")
		.setComputationPeriodIdentifier("daily");
	private ArrayList<TimeSeriesPoint> primaryPoints = new ArrayList<>(Arrays.asList(
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
				.setDisplay("2.0")
				.setNumeric(2.0D)
			),
		new TimeSeriesPoint()
			.setTimestamp(new StatisticalDateTimeOffset()
				.setDateTimeOffset(Instant.parse("2018-01-04T00:00:00Z"))
				.setRepresentsEndOfTimePeriod(false)
			)
			.setValue(new DoubleWithDisplay()
				.setDisplay("0.5")
				.setNumeric(0.5D)
			)
	));
	private ArrayList<TimeSeriesPoint> upchainPoints = new ArrayList<>(Arrays.asList(
		new TimeSeriesPoint()
				.setTimestamp(new StatisticalDateTimeOffset()
					.setDateTimeOffset(Instant.parse("2018-01-01T00:00:00Z"))
					.setRepresentsEndOfTimePeriod(false)
				)
				.setValue(new DoubleWithDisplay()
					.setDisplay("0.5")
					.setNumeric(0.5D)
				),
		new TimeSeriesPoint()
			.setTimestamp(new StatisticalDateTimeOffset()
				.setDateTimeOffset(Instant.parse("2018-01-02T00:00:00Z"))
				.setRepresentsEndOfTimePeriod(false)
			)
			.setValue(new DoubleWithDisplay()
				.setDisplay("1.0")
				.setNumeric(1.0D)
			),
		new TimeSeriesPoint()
			.setTimestamp(new StatisticalDateTimeOffset()
				.setDateTimeOffset(Instant.parse("2018-01-03T00:00:00Z"))
				.setRepresentsEndOfTimePeriod(false)
			)
			.setValue(new DoubleWithDisplay()
				.setDisplay("3.0")
				.setNumeric(3.0D)
			)
	));
	private ArrayList<TimeSeriesPoint> derivedPoints = new ArrayList<>(Arrays.asList(
		new TimeSeriesPoint()
			.setTimestamp(new StatisticalDateTimeOffset()
				.setDateTimeOffset(Instant.parse("2018-01-01T00:00:00Z"))
				.setRepresentsEndOfTimePeriod(true)
			)
			.setValue(new DoubleWithDisplay()
				.setDisplay("1.0")
				.setNumeric(0.8D)
			),
		new TimeSeriesPoint()
			.setTimestamp(new StatisticalDateTimeOffset()
				.setDateTimeOffset(Instant.parse("2018-01-02T00:00:00Z"))
				.setRepresentsEndOfTimePeriod(true)
			)
			.setValue(new DoubleWithDisplay()
				.setDisplay("2.0")
				.setNumeric(2.0D)
			),
		new TimeSeriesPoint()
			.setTimestamp(new StatisticalDateTimeOffset()
				.setDateTimeOffset(Instant.parse("2018-01-03T00:00:00Z"))
				.setRepresentsEndOfTimePeriod(true)
			)
			.setValue(new DoubleWithDisplay()
				.setDisplay("2.0")
				.setNumeric(1.9D)
			)
	));
	private Qualifier qual1 = new Qualifier().setIdentifier("qual1");
	private Qualifier qual2 = new Qualifier().setIdentifier("qual2");
	private Qualifier qual3 = new Qualifier().setIdentifier("qual3");
	private ArrayList<Qualifier> quals1 = new ArrayList<>();
	private ArrayList<Qualifier> quals2 = new ArrayList<>();
	private ArrayList<Qualifier> quals3 = new ArrayList<>();
	private HashMap<String, QualifierMetadata> qualMetadata;

	@Before
	public void setup() {
		minMaxBuilderService = new MinMaxBuilderService();
		service = new ReportBuilderService(locDescService, minMaxBuilderService, tsDescService, tsDataService, qualLookupService);
		requestParameters = new ExtremesRequestParameters();
		requestParameters.setStartDate(LocalDate.parse("2018-01-01"));
		requestParameters.setEndDate(LocalDate.parse("2018-02-01"));
		requestParameters.setPrimaryTimeseriesIdentifier("primaryTsId");
		qual1.setEndTime(Instant.parse("2018-01-03T00:00:00Z"));
		qual2.setEndTime(Instant.parse("2018-02-04T00:00:00Z"));
		qual3.setEndTime(Instant.parse("2018-03-05T00:00:00Z"));
		quals1.addAll(Arrays.asList(qual1,qual2));
		quals2.addAll(Arrays.asList(qual1));
		quals3.addAll(Arrays.asList(qual3));
		qualMetadata = new HashMap<>();
		qualMetadata.put("qual1", new QualifierMetadata().setIdentifier("qual1"));
		qualMetadata.put("qual2", new QualifierMetadata().setIdentifier("qual2"));
		qualMetadata.put("qual3", new QualifierMetadata().setIdentifier("qual3"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void buildReportFullTest() {
		given(tsDescService.getTimeSeriesDescriptionList(any(List.class))).willReturn(
			Arrays.asList(primaryDesc, upchainDesc, derivedDesc)
		);
		given(tsDataService.get(eq("primaryTsId"), any(ExtremesRequestParameters.class), eq(ZoneOffset.UTC), eq(false), eq(false), eq(false), eq(null))).willReturn(
			new TimeSeriesDataServiceResponse()
				.setPoints(primaryPoints)
				.setQualifiers(quals1)
		);
		given(tsDataService.get(eq("upchainTsId"), any(ExtremesRequestParameters.class), eq(ZoneOffset.UTC), eq(false), eq(false), eq(false), eq(null))).willReturn(
			new TimeSeriesDataServiceResponse()
				.setPoints(upchainPoints)
				.setQualifiers(quals2)
		);
		given(tsDataService.get(eq("derivedTsId"), any(ExtremesRequestParameters.class), eq(ZoneOffset.UTC), eq(true), eq(false), eq(false), eq(null))).willReturn(
			new TimeSeriesDataServiceResponse()
				.setPoints(derivedPoints)
				.setQualifiers(quals3)
		);
		given(qualLookupService.getByQualifierList(any(List.class))).willReturn(
			qualMetadata
		);
		given(locDescService.getByLocationIdentifier(any(String.class))).willReturn(
			new LocationDescription()
				.setIdentifier("loc1")
				.setUniqueId("loc1")
				.setName("loc1")
		);
		requestParameters.setUpchainTimeseriesIdentifier("upchainTsId");
		requestParameters.setDerivedTimeseriesIdentifier("derivedTsId");

		ExtremesReport result = service.buildReport(requestParameters, "test-user");

		// Verify Primary Data
		assertEquals(result.getPrimary().getMax().keySet().size(), 2);
		assertEquals(result.getPrimary().getMin().keySet().size(), 1);
		assertEquals(result.getPrimary().getMax().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).size(), 2);
		assertEquals(result.getPrimary().getMax().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).get(0).getValue(), BigDecimal.valueOf(2.0D));
		assertEquals(result.getPrimary().getMin().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).size(), 1);
		assertEquals(result.getPrimary().getMin().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).get(0).getValue(), BigDecimal.valueOf(0.5D));
		assertEquals(result.getPrimary().getMax().get(ReportBuilderService.UPCHAIN_RELATED_KEY).size(), 2);
		assertEquals(result.getPrimary().getMax().get(ReportBuilderService.UPCHAIN_RELATED_KEY).get(0).getValue(), BigDecimal.valueOf(1.0D));
		assertEquals(result.getPrimary().getMax().get(ReportBuilderService.UPCHAIN_RELATED_KEY).get(1).getValue(), BigDecimal.valueOf(3.0D));
		assertNull(result.getPrimary().getMin().get(ReportBuilderService.UPCHAIN_RELATED_KEY));
		assertEquals(Instant.parse("2018-01-03T00:00:00Z"), result.getPrimary().getQualifiers().get(0).getEndTime());
		assertEquals(Instant.parse("2018-02-04T00:00:00Z"), result.getPrimary().getQualifiers().get(1).getEndTime());

		// Verify Upchain Data
		assertEquals(result.getUpchain().getMax().keySet().size(), 2);
		assertEquals(result.getUpchain().getMin().keySet().size(), 2);
		assertEquals(result.getUpchain().getMax().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).size(), 1);
		assertEquals(result.getUpchain().getMax().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).get(0).getValue(), BigDecimal.valueOf(3.0D));
		assertEquals(result.getUpchain().getMin().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).size(), 1);
		assertEquals(result.getUpchain().getMin().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).get(0).getValue(), BigDecimal.valueOf(0.5D));
		assertEquals(result.getUpchain().getMax().get(ReportBuilderService.PRIMARY_RELATED_KEY).size(), 1);
		assertEquals(result.getUpchain().getMax().get(ReportBuilderService.PRIMARY_RELATED_KEY).get(0).getValue(), BigDecimal.valueOf(2.0D));
		assertEquals(result.getUpchain().getMin().get(ReportBuilderService.PRIMARY_RELATED_KEY).size(), 1);
		assertEquals(result.getUpchain().getMin().get(ReportBuilderService.PRIMARY_RELATED_KEY).get(0).getValue(), BigDecimal.valueOf(1.0D));
		assertEquals(Instant.parse("2018-01-03T00:00:00Z"), result.getUpchain().getQualifiers().get(0).getEndTime());

		// Verify Derived Data
		assertEquals(result.getDv().getMax().keySet().size(), 1);
		assertEquals(result.getDv().getMin().keySet().size(), 1);
		assertEquals(result.getDv().getMax().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).size(), 1);
		assertEquals(result.getDv().getMax().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).get(0).getValue(), BigDecimal.valueOf(2.0D));
		assertEquals(result.getDv().getMax().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).get(0).getTime(), LocalDate.parse("2018-01-01"));
		assertEquals(result.getDv().getMin().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).size(), 1);
		assertEquals(result.getDv().getMin().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).get(0).getValue(), BigDecimal.valueOf(1.0D));
		assertEquals(result.getDv().getMin().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).get(0).getTime(), LocalDate.parse("2017-12-31"));
		assertEquals(LocalDate.parse("2018-03-05"), result.getDv().getQualifiers().get(0).getEndTime());
		assertTrue(result.getDv().getMultipleMaxFlag());
	

		// Verify Metadata
		assertEquals(result.getReportMetadata().getTitle(), ReportBuilderService.REPORT_TITLE);
		assertEquals(result.getReportMetadata().getStationName(), "loc1");
		assertEquals(result.getReportMetadata().getStationId(), "loc1");
		assertEquals(result.getReportMetadata().getPrimaryLabel(), primaryDesc.getIdentifier());
		assertEquals(result.getReportMetadata().getUpchainLabel(), upchainDesc.getIdentifier());
		assertEquals(result.getReportMetadata().getDvLabel(), derivedDesc.getIdentifier());
		assertEquals(result.getReportMetadata().getQualifierMetadata().size(), 3);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void buildReportNoPrimaryDataTest() {
		given(tsDescService.getTimeSeriesDescriptionList(any(List.class))).willReturn(
			Arrays.asList(primaryDesc, upchainDesc, derivedDesc)
		);
		given(tsDataService.get(eq("primaryTsId"), any(ExtremesRequestParameters.class), eq(ZoneOffset.UTC), eq(false), eq(false), eq(false), eq(null))).willReturn(
			new TimeSeriesDataServiceResponse()
				.setPoints(new ArrayList<>())
				.setQualifiers(quals1)
		);
		given(tsDataService.get(eq("upchainTsId"), any(ExtremesRequestParameters.class), eq(ZoneOffset.UTC), eq(false), eq(false), eq(false), eq(null))).willReturn(
			new TimeSeriesDataServiceResponse()
				.setPoints(upchainPoints)
				.setQualifiers(quals2)
		);
		given(tsDataService.get(eq("derivedTsId"), any(ExtremesRequestParameters.class), eq(ZoneOffset.UTC), eq(true), eq(false), eq(false), eq(null))).willReturn(
			new TimeSeriesDataServiceResponse()
				.setPoints(derivedPoints)
				.setQualifiers(quals3)
		);
		given(qualLookupService.getByQualifierList(any(List.class))).willReturn(
			qualMetadata
		);
		given(locDescService.getByLocationIdentifier(any(String.class))).willReturn(
			new LocationDescription()
				.setIdentifier("loc1")
				.setUniqueId("loc1")
				.setName("loc1")
		);
		requestParameters.setUpchainTimeseriesIdentifier("upchainTsId");
		requestParameters.setDerivedTimeseriesIdentifier("derivedTsId");

		ExtremesReport result = service.buildReport(requestParameters, "test-user");

		// Verify Primary Data
		assertNull(result.getPrimary().getMax());
		assertNull(result.getPrimary().getMin());

		// Verify Upchain Data
		assertEquals(result.getUpchain().getMax().keySet().size(), 1);
		assertEquals(result.getUpchain().getMin().keySet().size(), 1);
		assertEquals(result.getUpchain().getMax().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).size(), 1);
		assertEquals(result.getUpchain().getMax().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).get(0).getValue(), BigDecimal.valueOf(3.0D));
		assertEquals(result.getUpchain().getMin().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).size(), 1);
		assertEquals(result.getUpchain().getMin().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).get(0).getValue(), BigDecimal.valueOf(0.5D));
		assertNull(result.getUpchain().getMax().get(ReportBuilderService.PRIMARY_RELATED_KEY));
		assertNull(result.getUpchain().getMin().get(ReportBuilderService.PRIMARY_RELATED_KEY));

		// Verify Derived Data
		assertEquals(result.getDv().getMax().keySet().size(), 1);
		assertEquals(result.getDv().getMin().keySet().size(), 1);
		assertEquals(result.getDv().getMax().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).size(), 1);
		assertEquals(result.getDv().getMax().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).get(0).getValue(), BigDecimal.valueOf(2.0D));
		assertEquals(result.getDv().getMax().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).get(0).getTime(), LocalDate.parse("2018-01-01"));
		assertEquals(result.getDv().getMin().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).size(), 1);
		assertEquals(result.getDv().getMin().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).get(0).getValue(), BigDecimal.valueOf(1.0D));
		assertEquals(result.getDv().getMin().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).get(0).getTime(), LocalDate.parse("2017-12-31"));

		// Verify Metadata
		assertEquals(result.getReportMetadata().getTitle(), ReportBuilderService.REPORT_TITLE);
		assertEquals(result.getReportMetadata().getStationName(), "loc1");
		assertEquals(result.getReportMetadata().getStationId(), "loc1");
		assertEquals(result.getReportMetadata().getPrimaryLabel(), primaryDesc.getIdentifier());
		assertEquals(result.getReportMetadata().getUpchainLabel(), upchainDesc.getIdentifier());
		assertEquals(result.getReportMetadata().getDvLabel(), derivedDesc.getIdentifier());
		assertEquals(result.getReportMetadata().getQualifierMetadata().size(), 3);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void buildReportNoUpchainDataTest() {
		given(tsDescService.getTimeSeriesDescriptionList(any(List.class))).willReturn(
			Arrays.asList(primaryDesc, upchainDesc, derivedDesc)
		);
		given(tsDataService.get(eq("primaryTsId"), any(ExtremesRequestParameters.class), eq(ZoneOffset.UTC), eq(false), eq(false), eq(false), eq(null))).willReturn(
			new TimeSeriesDataServiceResponse()
				.setPoints(primaryPoints)
				.setQualifiers(quals1)
		);
		given(tsDataService.get(eq("upchainTsId"), any(ExtremesRequestParameters.class), eq(ZoneOffset.UTC), eq(false), eq(false), eq(false), eq(null))).willReturn(
			new TimeSeriesDataServiceResponse()
				.setPoints(new ArrayList<>())
				.setQualifiers(quals2)
		);
		given(tsDataService.get(eq("derivedTsId"), any(ExtremesRequestParameters.class), eq(ZoneOffset.UTC), eq(true), eq(false), eq(false), eq(null))).willReturn(
			new TimeSeriesDataServiceResponse()
				.setPoints(derivedPoints)
				.setQualifiers(quals3)
		);
		given(qualLookupService.getByQualifierList(any(List.class))).willReturn(
			qualMetadata
		);
		given(locDescService.getByLocationIdentifier(any(String.class))).willReturn(
			new LocationDescription()
				.setIdentifier("loc1")
				.setUniqueId("loc1")
				.setName("loc1")
		);
		requestParameters.setUpchainTimeseriesIdentifier("upchainTsId");
		requestParameters.setDerivedTimeseriesIdentifier("derivedTsId");

		ExtremesReport result = service.buildReport(requestParameters, "test-user");

		// Verify Primary Data
		assertEquals(result.getPrimary().getMax().keySet().size(), 1);
		assertEquals(result.getPrimary().getMin().keySet().size(), 1);
		assertEquals(result.getPrimary().getMax().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).size(), 2);
		assertEquals(result.getPrimary().getMax().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).get(0).getValue(), BigDecimal.valueOf(2.0D));
		assertEquals(result.getPrimary().getMax().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).get(1).getValue(), BigDecimal.valueOf(2.0D));
		assertEquals(result.getPrimary().getMin().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).size(), 1);
		assertEquals(result.getPrimary().getMin().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).get(0).getValue(), BigDecimal.valueOf(0.5D));
		assertNull(result.getPrimary().getMax().get(ReportBuilderService.UPCHAIN_RELATED_KEY));
		assertNull(result.getPrimary().getMin().get(ReportBuilderService.UPCHAIN_RELATED_KEY));

		// Verify Upchain Data
		assertNull(result.getUpchain().getMax());
		assertNull(result.getUpchain().getMin());

		// Verify Derived Data
		assertEquals(result.getDv().getMax().keySet().size(), 1);
		assertEquals(result.getDv().getMin().keySet().size(), 1);
		assertEquals(result.getDv().getMax().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).size(), 1);
		assertEquals(result.getDv().getMax().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).get(0).getValue(), BigDecimal.valueOf(2.0D));
		assertEquals(result.getDv().getMax().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).get(0).getTime(), LocalDate.parse("2018-01-01"));
		assertEquals(result.getDv().getMin().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).size(), 1);
		assertEquals(result.getDv().getMin().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).get(0).getValue(), BigDecimal.valueOf(1.0D));
		assertEquals(result.getDv().getMin().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).get(0).getTime(), LocalDate.parse("2017-12-31"));

		// Verify Metadata
		assertEquals(result.getReportMetadata().getTitle(), ReportBuilderService.REPORT_TITLE);
		assertEquals(result.getReportMetadata().getStationName(), "loc1");
		assertEquals(result.getReportMetadata().getStationId(), "loc1");
		assertEquals(result.getReportMetadata().getPrimaryLabel(), primaryDesc.getIdentifier());
		assertEquals(result.getReportMetadata().getUpchainLabel(), upchainDesc.getIdentifier());
		assertEquals(result.getReportMetadata().getDvLabel(), derivedDesc.getIdentifier());
		assertEquals(result.getReportMetadata().getQualifierMetadata().size(), 3);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void buildReportNoDerivedDataTest() {
		given(tsDescService.getTimeSeriesDescriptionList(any(List.class))).willReturn(
			Arrays.asList(primaryDesc, upchainDesc, derivedDesc)
		);
		given(tsDataService.get(eq("primaryTsId"), any(ExtremesRequestParameters.class), eq(ZoneOffset.UTC), eq(false), eq(false), eq(false), eq(null))).willReturn(
			new TimeSeriesDataServiceResponse()
				.setPoints(primaryPoints)
				.setQualifiers(quals1)
		);
		given(tsDataService.get(eq("upchainTsId"), any(ExtremesRequestParameters.class), eq(ZoneOffset.UTC), eq(false), eq(false), eq(false), eq(null))).willReturn(
			new TimeSeriesDataServiceResponse()
				.setPoints(upchainPoints)
				.setQualifiers(quals2)
		);
		given(tsDataService.get(eq("derivedTsId"), any(ExtremesRequestParameters.class), eq(ZoneOffset.UTC), eq(true), eq(false), eq(false), eq(null))).willReturn(
			new TimeSeriesDataServiceResponse()
				.setPoints(new ArrayList<>())
				.setQualifiers(quals3)
		);
		given(qualLookupService.getByQualifierList(any(List.class))).willReturn(
			qualMetadata
		);
		given(locDescService.getByLocationIdentifier(any(String.class))).willReturn(
			new LocationDescription()
				.setIdentifier("loc1")
				.setUniqueId("loc1")
				.setName("loc1")
		);
		requestParameters.setUpchainTimeseriesIdentifier("upchainTsId");
		requestParameters.setDerivedTimeseriesIdentifier("derivedTsId");

		ExtremesReport result = service.buildReport(requestParameters, "test-user");

		// Verify Primary Data
		assertEquals(result.getPrimary().getMax().keySet().size(), 2);
		assertEquals(result.getPrimary().getMin().keySet().size(), 1);
		assertEquals(result.getPrimary().getMax().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).size(), 2);
		assertEquals(result.getPrimary().getMax().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).get(0).getValue(), BigDecimal.valueOf(2.0D));
		assertEquals(result.getPrimary().getMax().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).get(1).getValue(), BigDecimal.valueOf(2.0D));
		assertEquals(result.getPrimary().getMin().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).size(), 1);
		assertEquals(result.getPrimary().getMin().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).get(0).getValue(), BigDecimal.valueOf(0.5D));
		assertEquals(result.getPrimary().getMax().get(ReportBuilderService.UPCHAIN_RELATED_KEY).size(), 2);
		assertEquals(result.getPrimary().getMax().get(ReportBuilderService.UPCHAIN_RELATED_KEY).get(0).getValue(), BigDecimal.valueOf(1.0D));
		assertEquals(result.getPrimary().getMax().get(ReportBuilderService.UPCHAIN_RELATED_KEY).get(1).getValue(), BigDecimal.valueOf(3.0D));
		assertNull(result.getPrimary().getMin().get(ReportBuilderService.UPCHAIN_RELATED_KEY));

		// Verify Upchain Data
		assertEquals(result.getUpchain().getMax().keySet().size(), 2);
		assertEquals(result.getUpchain().getMin().keySet().size(), 2);
		assertEquals(result.getUpchain().getMax().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).size(), 1);
		assertEquals(result.getUpchain().getMax().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).get(0).getValue(), BigDecimal.valueOf(3.0D));
		assertEquals(result.getUpchain().getMin().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).size(), 1);
		assertEquals(result.getUpchain().getMin().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).get(0).getValue(), BigDecimal.valueOf(0.5D));
		assertEquals(result.getUpchain().getMax().get(ReportBuilderService.PRIMARY_RELATED_KEY).size(), 1);
		assertEquals(result.getUpchain().getMax().get(ReportBuilderService.PRIMARY_RELATED_KEY).get(0).getValue(), BigDecimal.valueOf(2.0D));
		assertEquals(result.getUpchain().getMin().get(ReportBuilderService.PRIMARY_RELATED_KEY).size(), 1);
		assertEquals(result.getUpchain().getMin().get(ReportBuilderService.PRIMARY_RELATED_KEY).get(0).getValue(), BigDecimal.valueOf(1.0D));

		// Verify Derived Data
		assertNull(result.getDv().getMax());
		assertNull(result.getDv().getMin());

		// Verify Metadata
		assertEquals(result.getReportMetadata().getTitle(), ReportBuilderService.REPORT_TITLE);
		assertEquals(result.getReportMetadata().getStationName(), "loc1");
		assertEquals(result.getReportMetadata().getStationId(), "loc1");
		assertEquals(result.getReportMetadata().getPrimaryLabel(), primaryDesc.getIdentifier());
		assertEquals(result.getReportMetadata().getUpchainLabel(), upchainDesc.getIdentifier());
		assertEquals(result.getReportMetadata().getDvLabel(), derivedDesc.getIdentifier());
		assertEquals(result.getReportMetadata().getQualifierMetadata().size(), 3);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void buildReportNoDataTest() {
		given(tsDescService.getTimeSeriesDescriptionList(any(List.class))).willReturn(
			Arrays.asList(primaryDesc, upchainDesc, derivedDesc)
		);
		given(tsDataService.get(eq("primaryTsId"), any(ExtremesRequestParameters.class), eq(ZoneOffset.UTC), eq(false), eq(false), eq(false), eq(null))).willReturn(
			new TimeSeriesDataServiceResponse()
				.setPoints(new ArrayList<>())
				.setQualifiers(new ArrayList<>())
		);
		given(tsDataService.get(eq("upchainTsId"), any(ExtremesRequestParameters.class), eq(ZoneOffset.UTC), eq(false), eq(false), eq(false), eq(null))).willReturn(
			new TimeSeriesDataServiceResponse()
				.setPoints(new ArrayList<>())
				.setQualifiers(new ArrayList<>())
		);
		given(tsDataService.get(eq("derivedTsId"), any(ExtremesRequestParameters.class), eq(ZoneOffset.UTC), eq(true), eq(false), eq(false), eq(null))).willReturn(
			new TimeSeriesDataServiceResponse()
				.setPoints(new ArrayList<>())
				.setQualifiers(new ArrayList<>())
		);
		given(qualLookupService.getByQualifierList(any(List.class))).willReturn(
			qualMetadata
		);
		given(locDescService.getByLocationIdentifier(any(String.class))).willReturn(
			new LocationDescription()
				.setIdentifier("loc1")
				.setUniqueId("loc1")
				.setName("loc1")
		);
		requestParameters.setUpchainTimeseriesIdentifier("upchainTsId");
		requestParameters.setDerivedTimeseriesIdentifier("derivedTsId");

		ExtremesReport result = service.buildReport(requestParameters, "test-user");

		// Verify Primary Data
		assertNull(result.getPrimary().getMax());
		assertNull(result.getPrimary().getMin());

		// Verify Upchain Data
		assertNull(result.getUpchain().getMax());
		assertNull(result.getUpchain().getMin());

		// Verify Derived Data
		assertNull(result.getDv().getMax());
		assertNull(result.getDv().getMin());

		// Verify Metadata
		assertEquals(result.getReportMetadata().getTitle(), ReportBuilderService.REPORT_TITLE);
		assertEquals(result.getReportMetadata().getStationName(), "loc1");
		assertEquals(result.getReportMetadata().getStationId(), "loc1");
		assertEquals(result.getReportMetadata().getPrimaryLabel(), primaryDesc.getIdentifier());
		assertEquals(result.getReportMetadata().getUpchainLabel(), upchainDesc.getIdentifier());
		assertEquals(result.getReportMetadata().getDvLabel(), derivedDesc.getIdentifier());
		assertEquals(result.getReportMetadata().getQualifierMetadata().size(), 0);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void buildReportNoUpchainTest() {
		given(tsDescService.getTimeSeriesDescriptionList(any(List.class))).willReturn(
			Arrays.asList(primaryDesc, derivedDesc)
		);
		given(tsDataService.get(eq("primaryTsId"), any(ExtremesRequestParameters.class), eq(ZoneOffset.UTC), eq(false), eq(false), eq(false), eq(null))).willReturn(
			new TimeSeriesDataServiceResponse()
				.setPoints(primaryPoints)
				.setQualifiers(quals1)
		);
		given(tsDataService.get(eq("derivedTsId"), any(ExtremesRequestParameters.class), eq(ZoneOffset.UTC), eq(true), eq(false), eq(false), eq(null))).willReturn(
			new TimeSeriesDataServiceResponse()
				.setPoints(derivedPoints)
				.setQualifiers(quals3)
		);
		given(qualLookupService.getByQualifierList(any(List.class))).willReturn(
			qualMetadata
		);
		given(locDescService.getByLocationIdentifier(any(String.class))).willReturn(
			new LocationDescription()
				.setIdentifier("loc1")
				.setUniqueId("loc1")
				.setName("loc1")
		);
		requestParameters.setDerivedTimeseriesIdentifier("derivedTsId");

		ExtremesReport result = service.buildReport(requestParameters, "test-user");

		// Verify Primary Data
		assertEquals(result.getPrimary().getMax().keySet().size(), 1);
		assertEquals(result.getPrimary().getMin().keySet().size(), 1);
		assertEquals(result.getPrimary().getMax().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).size(), 2);
		assertEquals(result.getPrimary().getMax().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).get(0).getValue(), BigDecimal.valueOf(2.0D));
		assertEquals(result.getPrimary().getMax().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).get(1).getValue(), BigDecimal.valueOf(2.0D));
		assertEquals(result.getPrimary().getMin().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).size(), 1);
		assertEquals(result.getPrimary().getMin().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).get(0).getValue(), BigDecimal.valueOf(0.5D));
		assertNull(result.getPrimary().getMax().get(ReportBuilderService.UPCHAIN_RELATED_KEY));
		assertNull(result.getPrimary().getMin().get(ReportBuilderService.UPCHAIN_RELATED_KEY));

		// Verify Upchain Data
		assertNull(result.getUpchain().getMax());
		assertNull(result.getUpchain().getMin());

		// Verify Derived Data
		assertEquals(result.getDv().getMax().keySet().size(), 1);
		assertEquals(result.getDv().getMin().keySet().size(), 1);
		assertEquals(result.getDv().getMax().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).size(), 1);
		assertEquals(result.getDv().getMax().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).get(0).getValue(), BigDecimal.valueOf(2.0D));
		assertEquals(result.getDv().getMax().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).get(0).getTime(), LocalDate.parse("2018-01-01"));
		assertEquals(result.getDv().getMin().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).size(), 1);
		assertEquals(result.getDv().getMin().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).get(0).getValue(), BigDecimal.valueOf(1.0D));
		assertEquals(result.getDv().getMin().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).get(0).getTime(), LocalDate.parse("2017-12-31"));

		// Verify Metadata
		assertEquals(result.getReportMetadata().getTitle(), ReportBuilderService.REPORT_TITLE);
		assertEquals(result.getReportMetadata().getStationName(), "loc1");
		assertEquals(result.getReportMetadata().getStationId(), "loc1");
		assertEquals(result.getReportMetadata().getPrimaryLabel(), primaryDesc.getIdentifier());
		assertEquals(result.getReportMetadata().getDvLabel(), derivedDesc.getIdentifier());
		assertEquals(result.getReportMetadata().getQualifierMetadata().size(), 3);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void buildReportNoDerivedTest() {
		given(tsDescService.getTimeSeriesDescriptionList(any(List.class))).willReturn(
			Arrays.asList(primaryDesc, upchainDesc)
		);
		given(tsDataService.get(eq("primaryTsId"), any(ExtremesRequestParameters.class), eq(ZoneOffset.UTC), eq(false), eq(false), eq(false), eq(null))).willReturn(
			new TimeSeriesDataServiceResponse()
				.setPoints(primaryPoints)
				.setQualifiers(quals1)
		);
		given(tsDataService.get(eq("upchainTsId"), any(ExtremesRequestParameters.class), eq(ZoneOffset.UTC), eq(false), eq(false), eq(false), eq(null))).willReturn(
			new TimeSeriesDataServiceResponse()
				.setPoints(upchainPoints)
				.setQualifiers(quals2)
		);
		given(qualLookupService.getByQualifierList(any(List.class))).willReturn(
			qualMetadata
		);
		given(locDescService.getByLocationIdentifier(any(String.class))).willReturn(
			new LocationDescription()
				.setIdentifier("loc1")
				.setUniqueId("loc1")
				.setName("loc1")
		);
		requestParameters.setUpchainTimeseriesIdentifier("upchainTsId");

		ExtremesReport result = service.buildReport(requestParameters, "test-user");

		// Verify Primary Data
		assertEquals(result.getPrimary().getMax().keySet().size(), 2);
		assertEquals(result.getPrimary().getMin().keySet().size(), 1);
		assertEquals(result.getPrimary().getMax().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).size(), 2);
		assertEquals(result.getPrimary().getMax().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).get(0).getValue(), BigDecimal.valueOf(2.0D));
		assertEquals(result.getPrimary().getMax().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).get(1).getValue(), BigDecimal.valueOf(2.0D));
		assertEquals(result.getPrimary().getMin().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).size(), 1);
		assertEquals(result.getPrimary().getMin().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).get(0).getValue(), BigDecimal.valueOf(0.5D));
		assertEquals(result.getPrimary().getMax().get(ReportBuilderService.UPCHAIN_RELATED_KEY).size(), 2);
		assertEquals(result.getPrimary().getMax().get(ReportBuilderService.UPCHAIN_RELATED_KEY).get(0).getValue(), BigDecimal.valueOf(1.0D));
		assertEquals(result.getPrimary().getMax().get(ReportBuilderService.UPCHAIN_RELATED_KEY).get(1).getValue(), BigDecimal.valueOf(3.0D));
		assertNull(result.getPrimary().getMin().get(ReportBuilderService.UPCHAIN_RELATED_KEY));

		// Verify Upchain Data
		assertEquals(result.getUpchain().getMax().keySet().size(), 2);
		assertEquals(result.getUpchain().getMin().keySet().size(), 2);
		assertEquals(result.getUpchain().getMax().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).size(), 1);
		assertEquals(result.getUpchain().getMax().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).get(0).getValue(), BigDecimal.valueOf(3.0D));
		assertEquals(result.getUpchain().getMin().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).size(), 1);
		assertEquals(result.getUpchain().getMin().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).get(0).getValue(), BigDecimal.valueOf(0.5D));
		assertEquals(result.getUpchain().getMax().get(ReportBuilderService.PRIMARY_RELATED_KEY).size(), 1);
		assertEquals(result.getUpchain().getMax().get(ReportBuilderService.PRIMARY_RELATED_KEY).get(0).getValue(), BigDecimal.valueOf(2.0D));
		assertEquals(result.getUpchain().getMin().get(ReportBuilderService.PRIMARY_RELATED_KEY).size(), 1);
		assertEquals(result.getUpchain().getMin().get(ReportBuilderService.PRIMARY_RELATED_KEY).get(0).getValue(), BigDecimal.valueOf(1.0D));

		// Verify Derived Data
		assertNull(result.getDv().getMax());
		assertNull(result.getDv().getMin());

		// Verify Metadata
		assertEquals(result.getReportMetadata().getTitle(), ReportBuilderService.REPORT_TITLE);
		assertEquals(result.getReportMetadata().getStationName(), "loc1");
		assertEquals(result.getReportMetadata().getStationId(), "loc1");
		assertEquals(result.getReportMetadata().getPrimaryLabel(), primaryDesc.getIdentifier());
		assertEquals(result.getReportMetadata().getUpchainLabel(), upchainDesc.getIdentifier());
		assertEquals(result.getReportMetadata().getQualifierMetadata().size(), 3);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void buildReportNoUpchainOrDerivedTest() {
		given(tsDescService.getTimeSeriesDescriptionList(any(List.class))).willReturn(
			Arrays.asList(primaryDesc)
		);
		given(tsDataService.get(eq("primaryTsId"), any(ExtremesRequestParameters.class), eq(ZoneOffset.UTC), eq(false), eq(false), eq(false), eq(null))).willReturn(
			new TimeSeriesDataServiceResponse()
				.setPoints(primaryPoints)
				.setQualifiers(new ArrayList<>())
		);
		given(qualLookupService.getByQualifierList(any(List.class))).willReturn(
			qualMetadata
		);
		given(locDescService.getByLocationIdentifier(any(String.class))).willReturn(
			new LocationDescription()
				.setIdentifier("loc1")
				.setUniqueId("loc1")
				.setName("loc1")
		);

		ExtremesReport result = service.buildReport(requestParameters, "test-user");

		// Verify Primary Data
		assertEquals(result.getPrimary().getMax().keySet().size(), 1);
		assertEquals(result.getPrimary().getMin().keySet().size(), 1);
		assertEquals(result.getPrimary().getMax().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).size(), 2);
		assertEquals(result.getPrimary().getMax().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).get(0).getValue(), BigDecimal.valueOf(2.0D));
		assertEquals(result.getPrimary().getMax().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).get(1).getValue(), BigDecimal.valueOf(2.0D));
		assertEquals(result.getPrimary().getMin().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).size(), 1);
		assertEquals(result.getPrimary().getMin().get(ExtremesMinMax.MIN_MAX_POINTS_KEY).get(0).getValue(), BigDecimal.valueOf(0.5D));
		assertNull(result.getPrimary().getMax().get(ReportBuilderService.UPCHAIN_RELATED_KEY));
		assertNull(result.getPrimary().getMin().get(ReportBuilderService.UPCHAIN_RELATED_KEY));

		// Verify Upchain Data
		assertNull(result.getUpchain().getMax());
		assertNull(result.getUpchain().getMin());

		// Verify Derived Data
		assertNull(result.getDv().getMax());
		assertNull(result.getDv().getMin());

		// Verify Metadata
		assertEquals(result.getReportMetadata().getTitle(), ReportBuilderService.REPORT_TITLE);
		assertEquals(result.getReportMetadata().getStationName(), "loc1");
		assertEquals(result.getReportMetadata().getStationId(), "loc1");
		assertEquals(result.getReportMetadata().getPrimaryLabel(), primaryDesc.getIdentifier());
		assertEquals(result.getReportMetadata().getQualifierMetadata().size(), 0);
	}
	
	@Test
	public void getExtremesPointsTest() {
		List<ExtremesPoint> result = service.getExtremesPoints(primaryPoints, false, ZoneOffset.UTC);
		assertEquals(result.size(), 4);
		assertEquals(result.get(0).getValue(), BigDecimal.valueOf(primaryPoints.get(0).getValue().getNumeric()));
		assertEquals(result.get(0).getTime(), primaryPoints.get(0).getTimestamp().getDateTimeOffset());
		assertEquals(result.get(1).getValue(), BigDecimal.valueOf(primaryPoints.get(1).getValue().getNumeric()));
		assertEquals(result.get(1).getTime(), primaryPoints.get(1).getTimestamp().getDateTimeOffset());
		assertEquals(result.get(2).getValue(), BigDecimal.valueOf(primaryPoints.get(2).getValue().getNumeric()));
		assertEquals(result.get(2).getTime(), primaryPoints.get(2).getTimestamp().getDateTimeOffset());
		assertEquals(result.get(3).getValue(), BigDecimal.valueOf(primaryPoints.get(3).getValue().getNumeric()));
		assertEquals(result.get(3).getTime(), primaryPoints.get(3).getTimestamp().getDateTimeOffset());
		result = service.getExtremesPoints(upchainPoints, false, ZoneOffset.UTC);
		assertEquals(result.size(), 3);
		assertEquals(result.get(0).getValue(), BigDecimal.valueOf(upchainPoints.get(0).getValue().getNumeric()));
		assertEquals(result.get(0).getTime(), upchainPoints.get(0).getTimestamp().getDateTimeOffset());
		assertEquals(result.get(1).getValue(), BigDecimal.valueOf(upchainPoints.get(1).getValue().getNumeric()));
		assertEquals(result.get(1).getTime(), upchainPoints.get(1).getTimestamp().getDateTimeOffset());
		assertEquals(result.get(2).getValue(), BigDecimal.valueOf(upchainPoints.get(2).getValue().getNumeric()));
		assertEquals(result.get(2).getTime(), upchainPoints.get(2).getTimestamp().getDateTimeOffset());
		result = service.getExtremesPoints(derivedPoints, true, ZoneOffset.UTC);
		assertEquals(result.size(), 3);
		assertEquals(result.get(0).getValue(), DoubleWithDisplayUtil.getRoundedValue(derivedPoints.get(0).getValue()));
		assertEquals(result.get(1).getValue(), DoubleWithDisplayUtil.getRoundedValue(derivedPoints.get(1).getValue()));
		assertEquals(result.get(2).getValue(), DoubleWithDisplayUtil.getRoundedValue(derivedPoints.get(2).getValue()));
		assertEquals(result.get(0).getTime(), LocalDate.parse("2017-12-31"));
		assertEquals(result.get(1).getTime(), LocalDate.parse("2018-01-01"));
		assertEquals(result.get(2).getTime(), LocalDate.parse("2018-01-02"));
	}

	@Test
	public void getExtremesPointsEmptyTest() {
		List<ExtremesPoint> result = service.getExtremesPoints(new ArrayList<>(), false, ZoneOffset.UTC);
		assertEquals(result.size(), 0);
		result = service.getExtremesPoints(new ArrayList<>(), true, ZoneOffset.UTC);
		assertEquals(result.size(), 0);
	}

	@Test
	public void getExtremesPointsNullTest() {
		List<ExtremesPoint> result = service.getExtremesPoints(null, false, ZoneOffset.UTC);
		assertEquals(result.size(), 0);
		result = service.getExtremesPoints(new ArrayList<>(), true, ZoneOffset.UTC);
		assertEquals(result.size(), 0);
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void getReportMetadataFullTest() {
		given(qualLookupService.getByQualifierList(any(List.class))).willReturn(
			qualMetadata
		);
		given(locDescService.getByLocationIdentifier(any(String.class))).willReturn(
			new LocationDescription()
				.setIdentifier("loc1")
				.setUniqueId("loc1")
				.setName("loc1")
		);
		requestParameters.setUpchainTimeseriesIdentifier("upchainTsId");
		requestParameters.setDerivedTimeseriesIdentifier("derivedTsId");
		HashMap<String, TimeSeriesDescription> descMap = new HashMap<>();
		descMap.put("primaryTsId", primaryDesc);
		descMap.put("upchainTsId", upchainDesc);
		descMap.put("derivedTsId", derivedDesc);
		
		ExtremesReportMetadata result = service.getReportMetadata(requestParameters, descMap, primaryDesc, "test", quals1);
		assertEquals(result.getTitle(), ReportBuilderService.REPORT_TITLE);
		assertEquals(result.getRequestingUser(), "test");
		assertEquals(result.getRequestParameters(), requestParameters);
		assertEquals(result.getStationId(), "loc1");
		assertEquals(result.getStationName(), "loc1");
		assertEquals(result.getPrimaryParameter(), primaryDesc.getParameter());
		assertEquals(result.getPrimaryLabel(), primaryDesc.getIdentifier());
		assertEquals(result.getPrimaryUnit(), primaryDesc.getUnit());
		assertEquals(result.getTimezone(), AqcuTimeUtils.getTimezone(primaryDesc.getUtcOffset()));
		assertEquals(result.getDvParameter(), derivedDesc.getParameter());
		assertEquals(result.getDvLabel(), derivedDesc.getIdentifier());
		assertEquals(result.getDvUnit(), derivedDesc.getUnit());
		assertEquals(result.getDvComputation(), derivedDesc.getComputationIdentifier());
		assertEquals(result.getUpchainParameter(), upchainDesc.getParameter());
		assertEquals(result.getUpchainLabel(), upchainDesc.getIdentifier());
		assertEquals(result.getUpchainUnit(), upchainDesc.getUnit());
		assertEquals(result.getQualifierMetadata(), qualMetadata);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getReportMetadataNoQualsTest() {
		given(qualLookupService.getByQualifierList(any(List.class))).willReturn(
			qualMetadata
		);
		given(locDescService.getByLocationIdentifier(any(String.class))).willReturn(
			new LocationDescription()
				.setIdentifier("loc1")
				.setUniqueId("loc1")
				.setName("loc1")
		);
		requestParameters.setUpchainTimeseriesIdentifier("upchainTsId");
		requestParameters.setDerivedTimeseriesIdentifier("derivedTsId");
		HashMap<String, TimeSeriesDescription> descMap = new HashMap<>();
		descMap.put("primaryTsId", primaryDesc);
		descMap.put("upchainTsId", upchainDesc);
		descMap.put("derivedTsId", derivedDesc);
		
		ExtremesReportMetadata result = service.getReportMetadata(requestParameters, descMap, primaryDesc, "test", new ArrayList<>());
		assertEquals(result.getTitle(), ReportBuilderService.REPORT_TITLE);
		assertEquals(result.getRequestingUser(), "test");
		assertEquals(result.getRequestParameters(), requestParameters);
		assertEquals(result.getStationId(), "loc1");
		assertEquals(result.getStationName(), "loc1");
		assertEquals(result.getPrimaryParameter(), primaryDesc.getParameter());
		assertEquals(result.getPrimaryLabel(), primaryDesc.getIdentifier());
		assertEquals(result.getPrimaryUnit(), primaryDesc.getUnit());
		assertEquals(result.getTimezone(), AqcuTimeUtils.getTimezone(primaryDesc.getUtcOffset()));
		assertEquals(result.getDvParameter(), derivedDesc.getParameter());
		assertEquals(result.getDvLabel(), derivedDesc.getIdentifier());
		assertEquals(result.getDvUnit(), derivedDesc.getUnit());
		assertEquals(result.getDvComputation(), derivedDesc.getComputationIdentifier());
		assertEquals(result.getUpchainParameter(), upchainDesc.getParameter());
		assertEquals(result.getUpchainLabel(), upchainDesc.getIdentifier());
		assertEquals(result.getUpchainUnit(), upchainDesc.getUnit());
		assertEquals(result.getQualifierMetadata().size(), 0);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getReportMetadataNoUpchainTest() {
		given(qualLookupService.getByQualifierList(any(List.class))).willReturn(
			qualMetadata
		);
		given(locDescService.getByLocationIdentifier(any(String.class))).willReturn(
			new LocationDescription()
				.setIdentifier("loc1")
				.setUniqueId("loc1")
				.setName("loc1")
		);
		requestParameters.setDerivedTimeseriesIdentifier("derivedTsId");
		HashMap<String, TimeSeriesDescription> descMap = new HashMap<>();
		descMap.put("primaryTsId", primaryDesc);
		descMap.put("derivedTsId", derivedDesc);
		
		ExtremesReportMetadata result = service.getReportMetadata(requestParameters, descMap, primaryDesc, "test", quals1);
		assertEquals(result.getTitle(), ReportBuilderService.REPORT_TITLE);
		assertEquals(result.getRequestingUser(), "test");
		assertEquals(result.getRequestParameters(), requestParameters);
		assertEquals(result.getStationId(), "loc1");
		assertEquals(result.getStationName(), "loc1");
		assertEquals(result.getPrimaryParameter(), primaryDesc.getParameter());
		assertEquals(result.getPrimaryLabel(), primaryDesc.getIdentifier());
		assertEquals(result.getPrimaryUnit(), primaryDesc.getUnit());
		assertEquals(result.getTimezone(), AqcuTimeUtils.getTimezone(primaryDesc.getUtcOffset()));
		assertEquals(result.getDvParameter(), derivedDesc.getParameter());
		assertEquals(result.getDvLabel(), derivedDesc.getIdentifier());
		assertEquals(result.getDvUnit(), derivedDesc.getUnit());
		assertEquals(result.getDvComputation(), derivedDesc.getComputationIdentifier());
		assertNull(result.getUpchainParameter());
		assertNull(result.getUpchainLabel());
		assertNull(result.getUpchainUnit());
		assertEquals(result.getQualifierMetadata(), qualMetadata);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getReportMetadataNoDerivedTest() {
		given(qualLookupService.getByQualifierList(any(List.class))).willReturn(
			qualMetadata
		);
		given(locDescService.getByLocationIdentifier(any(String.class))).willReturn(
			new LocationDescription()
				.setIdentifier("loc1")
				.setUniqueId("loc1")
				.setName("loc1")
		);
		requestParameters.setUpchainTimeseriesIdentifier("upchainTsId");
		HashMap<String, TimeSeriesDescription> descMap = new HashMap<>();
		descMap.put("primaryTsId", primaryDesc);
		descMap.put("upchainTsId", upchainDesc);
		
		ExtremesReportMetadata result = service.getReportMetadata(requestParameters, descMap, primaryDesc, "test", quals1);
		assertEquals(result.getTitle(), ReportBuilderService.REPORT_TITLE);
		assertEquals(result.getRequestingUser(), "test");
		assertEquals(result.getRequestParameters(), requestParameters);
		assertEquals(result.getStationId(), "loc1");
		assertEquals(result.getStationName(), "loc1");
		assertEquals(result.getPrimaryParameter(), primaryDesc.getParameter());
		assertEquals(result.getPrimaryLabel(), primaryDesc.getIdentifier());
		assertEquals(result.getPrimaryUnit(), primaryDesc.getUnit());
		assertEquals(result.getTimezone(), AqcuTimeUtils.getTimezone(primaryDesc.getUtcOffset()));
		assertNull(result.getDvParameter());
		assertNull(result.getDvLabel());
		assertNull(result.getDvUnit());
		assertNull(result.getDvComputation());
		assertEquals(result.getUpchainParameter(), upchainDesc.getParameter());
		assertEquals(result.getUpchainLabel(), upchainDesc.getIdentifier());
		assertEquals(result.getUpchainUnit(), upchainDesc.getUnit());
		assertEquals(result.getQualifierMetadata(), qualMetadata);
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void getReportMetadataEmptyTest() {
		given(qualLookupService.getByQualifierList(any(List.class))).willReturn(
			qualMetadata
		);
		given(locDescService.getByLocationIdentifier(any(String.class))).willReturn(
			new LocationDescription()
				.setIdentifier("loc1")
				.setUniqueId("loc1")
				.setName("loc1")
		);
		HashMap<String, TimeSeriesDescription> descMap = new HashMap<>();
		descMap.put("primaryTsId", primaryDesc);
		
		ExtremesReportMetadata result = service.getReportMetadata(requestParameters, descMap, primaryDesc, "test", quals1);
		assertEquals(result.getTitle(), ReportBuilderService.REPORT_TITLE);
		assertEquals(result.getRequestingUser(), "test");
		assertEquals(result.getRequestParameters(), requestParameters);
		assertEquals(result.getStationId(), "loc1");
		assertEquals(result.getStationName(), "loc1");
		assertEquals(result.getPrimaryParameter(), primaryDesc.getParameter());
		assertEquals(result.getPrimaryLabel(), primaryDesc.getIdentifier());
		assertEquals(result.getPrimaryUnit(), primaryDesc.getUnit());
		assertEquals(result.getTimezone(), AqcuTimeUtils.getTimezone(primaryDesc.getUtcOffset()));
		assertNull(result.getDvParameter());
		assertNull(result.getDvLabel());
		assertNull(result.getDvUnit());
		assertNull(result.getDvComputation());
		assertNull(result.getUpchainParameter());
		assertNull(result.getUpchainLabel());
		assertNull(result.getUpchainUnit());
		assertEquals(result.getQualifierMetadata(), qualMetadata);
    }
}
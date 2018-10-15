package gov.usgs.aqcu.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class ExtremesMinMaxTest {

	private Map<String, List<ExtremesPoint>> minPoints;
	private Map<String, List<ExtremesPoint>> maxPoints;
	private List<ExtremesPoint> listPoints;
	private Temporal time1 = LocalDate.parse("2000-01-01");
	private Temporal time2 = LocalDate.parse("2010-01-01");
	private Temporal time3 = LocalDate.parse("2010-01-01");
	private Temporal time4 = LocalDate.parse("2018-01-01");
	private BigDecimal value1 = new BigDecimal("1.2");
	private BigDecimal value2 = new BigDecimal("3.2");
	private BigDecimal value3 = new BigDecimal("1745.62");
	private BigDecimal value4 = new BigDecimal("2343.212");
	private ExtremesMinMax extremesDailyValues = new ExtremesMinMax();


	@Before
	public void setup() {
		minPoints = new HashMap<>();
		maxPoints = new HashMap<>();
		listPoints = new ArrayList<>();
		listPoints.add(new ExtremesPoint().setTime(time1).setValue(value1));
		listPoints.add(new ExtremesPoint().setTime(time2).setValue(value2));
		minPoints.put("points", listPoints);
		
		listPoints = new ArrayList<>();
		listPoints.add(new ExtremesPoint().setTime(time3).setValue(value3));
		listPoints.add(new ExtremesPoint().setTime(time4).setValue(value4));
		maxPoints.put("points", listPoints);
	}

	@Test
	public void extremesMinMaxTest() {
		extremesDailyValues.setMin(minPoints);
		extremesDailyValues.setMax(maxPoints);
		assertEquals(extremesDailyValues.getMin().get("points").size(), 2);
		assertEquals(extremesDailyValues.getMax().get("points").size(), 2);
	}

}

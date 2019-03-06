package gov.usgs.aqcu.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Qualifier;

import org.junit.Test;

public class ExtremesQualifierTest {
    
	private Instant startTime = Instant.parse("2000-01-02T09:32:00Z");
	private Instant endTime = Instant.parse("2000-01-03T09:32:00Z");
    private Qualifier qualifier;

	@Test
	public void constructorTestFull() {
		qualifier = new Qualifier().setIdentifier("TESTQUALIFIER");
		qualifier.setStartTime(startTime);
		qualifier.setEndTime(endTime);
        ExtremesQualifier testQualifier = new ExtremesQualifier(qualifier, true, ZoneOffset.UTC);
        assertEquals(LocalDate.parse("2000-01-02"), testQualifier.getStartTime());
        assertEquals(LocalDate.parse("2000-01-03"), testQualifier.getEndTime());
        assertEquals("TESTQUALIFIER", testQualifier.getIdentifier());
        testQualifier = new ExtremesQualifier(qualifier, false, ZoneOffset.UTC);
        assertEquals(startTime, testQualifier.getStartTime());
        assertEquals(endTime, testQualifier.getEndTime());
        assertEquals("TESTQUALIFIER", testQualifier.getIdentifier());
    }

	@Test
	public void constructorTestNoTime() {
		qualifier = new Qualifier().setIdentifier("TESTQUALIFIER");
		ExtremesQualifier testQualifier = new ExtremesQualifier(qualifier, true, ZoneOffset.UTC);
        assertNull(testQualifier.getStartTime());
        assertNull(testQualifier.getEndTime());
        assertEquals("TESTQUALIFIER", testQualifier.getIdentifier());
    }

	@Test
	public void constructorTestNoIdentifier() {
		qualifier = new Qualifier();
		qualifier.setStartTime(startTime);
		qualifier.setEndTime(endTime);
		ExtremesQualifier testQualifier = new ExtremesQualifier(qualifier, true, ZoneOffset.UTC);
		assertEquals(LocalDate.parse("2000-01-02"), testQualifier.getStartTime());
        assertEquals(LocalDate.parse("2000-01-03"), testQualifier.getEndTime());
        assertNull(testQualifier.getIdentifier());
    }

	@Test
	public void constructorTestNoTimeOrValue() {
		qualifier = new Qualifier();
		ExtremesQualifier testQualifier = new ExtremesQualifier(qualifier, true, ZoneOffset.UTC);
        assertNull(testQualifier.getStartTime());
        assertNull(testQualifier.getEndTime());
        assertNull(testQualifier.getIdentifier());
    }
}
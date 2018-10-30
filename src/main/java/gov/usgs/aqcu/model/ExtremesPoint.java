package gov.usgs.aqcu.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.Temporal;

/** 
 * This class is a substitute for com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesPoint
 * Extremes requires Points that:
 * a) Have an optional time component to the temporal value;
 * b) Have values expressed as BigDecimal, rather than imprecise Double approximations.
 */
public class ExtremesPoint {
	private Temporal time;

	private BigDecimal value;

	public Temporal getTime() {
		return time;
	}

	public ExtremesPoint setTime(Temporal time) {
		this.time = time;
		return this;
	}

	public BigDecimal getValue() {
		return value;
	}

	public ExtremesPoint setValue(BigDecimal value) {
		this.value = value;
		return this;
	}
}

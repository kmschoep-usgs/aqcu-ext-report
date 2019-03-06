package gov.usgs.aqcu.model;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.Temporal;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Qualifier;

/** 
 * This class is a substitute for com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Qualifier
 * Extremes requires Qualifier that:
 * a) Have an optional time component to the temporal value;
 */
public class ExtremesQualifier {
	private String identifier;
	private Temporal startTime;
	private Temporal endTime;
	

	public ExtremesQualifier() {};

	public ExtremesQualifier(Qualifier source, Boolean isDaily, ZoneOffset zoneOffset) {
		if(source.getStartTime() != null) {
			if (isDaily) {
				setStartTime(LocalDateTime.ofInstant(source.getStartTime(), zoneOffset).toLocalDate());
			} else {
				setStartTime(source.getStartTime());
			}
		}
		if(source.getEndTime() != null) {
			if (isDaily) {
			setEndTime(LocalDateTime.ofInstant(source.getEndTime(), zoneOffset).toLocalDate());
			} else {
				setEndTime(source.getEndTime());
			}
		}

		if(source.getIdentifier() != null) {
			setIdentifier(source.getIdentifier());
		}
	}

	public Temporal getStartTime() {
		return startTime;
	}

	public ExtremesQualifier setStartTime(Temporal startTime) {
		this.startTime = startTime;
		return this;
	}

	public Temporal getEndTime() {
		return endTime;
	}

	public ExtremesQualifier setEndTime(Temporal endTime) {
		this.endTime = endTime;
		return this;
	}
	
	public String getIdentifier() {
		return identifier;
	}

	public ExtremesQualifier setIdentifier(String identifier) {
		this.identifier = identifier;
		return this;
	}
}

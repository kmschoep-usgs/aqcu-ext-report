package gov.usgs.aqcu.model;

import java.time.temporal.Temporal;
import java.util.List;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Qualifier;

public class TimeSeriesCorrectedData {

	private Temporal endTime;
	private boolean isVolumetricFlow;
	private String name; //not used according to doc, but required per R
	private List<ExtremesPoint> points;
	private List<Qualifier> qualifiers;
	private Temporal startTime;
	private String type;
	private String unit;

	public Temporal getEndTime() {
		return endTime;
	}
	public void setEndTime(Temporal endTime) {
		this.endTime = endTime;
	}
	public boolean isVolumetricFlow() {
		return isVolumetricFlow;
	}
	public void setVolumetricFlow(boolean isVolumetricFlow) {
		this.isVolumetricFlow = isVolumetricFlow;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<ExtremesPoint> getPoints() {
		return points;
	}
	public TimeSeriesCorrectedData setPoints(List<ExtremesPoint> points) {
		this.points = points;
		return this;
	}
	public List<Qualifier> getQualifiers() {
		return qualifiers;
	}
	public void setQualifiers(List<Qualifier> qualifiers) {
		this.qualifiers = qualifiers;
	}
	public Temporal getStartTime() {
		return startTime;
	}
	public void setStartTime(Temporal startTime) {
		this.startTime = startTime;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	
}

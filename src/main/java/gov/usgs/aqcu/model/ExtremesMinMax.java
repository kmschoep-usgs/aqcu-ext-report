package gov.usgs.aqcu.model;

import java.util.List;
import java.util.Map;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Qualifier;

public class ExtremesMinMax {	
	private Map<String, List<ExtremesPoint>> min;
	private Map<String, List<ExtremesPoint>> max;
	private List<Qualifier> qualifiers;
	
	public Map<String, List<ExtremesPoint>> getMin() {
		return min;
	}
	
	public void setMin(Map<String, List<ExtremesPoint>> val) {
		this.min = val;
	}
	
	public Map<String, List<ExtremesPoint>> getMax() {
		return max;
	}
	
	public void setMax(Map<String, List<ExtremesPoint>> val) {
		this.max = val;
	}
	
	public List<Qualifier> getQualifiers() {
		return qualifiers;
	}
	
	public void setQualifiers(List<Qualifier> val) {
		this.qualifiers = val;
	}
	
}
	

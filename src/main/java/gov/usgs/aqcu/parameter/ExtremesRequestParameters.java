package gov.usgs.aqcu.parameter;

import java.util.HashSet;
import java.util.Set;

public class ExtremesRequestParameters extends ReportRequestParameters {

	private String upchainTimeseriesIdentifier;
	private String derivedTimeseriesIdentifier;

	public String getUpchainTimeseriesIdentifier() {		
		return upchainTimeseriesIdentifier;
	}

	public void setUpchainTimeseriesIdentifier(String val) {
		this.upchainTimeseriesIdentifier = val;
	}

	public String getDerivedTimeseriesIdentifier() {
		return derivedTimeseriesIdentifier;
	}

	public void setDerivedTimeseriesIdentifier(String dvId) {
		this.derivedTimeseriesIdentifier = dvId;
	}

	public Set<String> getTsIdSet() {
		Set<String> result = new HashSet<>();

		if(getPrimaryTimeseriesIdentifier() != null && !getPrimaryTimeseriesIdentifier().isEmpty()) {
			result.add(getPrimaryTimeseriesIdentifier());
		}

		if(getUpchainTimeseriesIdentifier() != null && !getUpchainTimeseriesIdentifier().isEmpty()) {
			result.add(getUpchainTimeseriesIdentifier());
		}

		if(getDerivedTimeseriesIdentifier() != null && !getDerivedTimeseriesIdentifier().isEmpty()) {
			result.add(getDerivedTimeseriesIdentifier());
		}

		return result;
	}
}

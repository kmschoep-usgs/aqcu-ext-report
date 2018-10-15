package gov.usgs.aqcu.parameter;

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

}

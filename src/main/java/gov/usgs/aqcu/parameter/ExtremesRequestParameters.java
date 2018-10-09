package gov.usgs.aqcu.parameter;

public class ExtremesRequestParameters extends ReportRequestParameters {

	private String upchainId;
	private String dvId;

	public String getUpchainId() {		
		return upchainId;
	}

	public void setUpchainId(String val) {
		this.upchainId = val;
	}

	public String getDvId() {
		return dvId;
	}

	public void setDvId(String dvId) {
		this.dvId = dvId;
	}

}

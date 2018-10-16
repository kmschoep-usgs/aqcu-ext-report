package gov.usgs.aqcu.model;

public class ExtremesReport {	
	private ExtremesReportMetadata reportMetadata;
	private ExtremesMinMax dv;
	private ExtremesMinMax upchain;
	private ExtremesMinMax primary;
	
	public ExtremesReportMetadata getReportMetadata() {
		return reportMetadata;
	}
	
	public void setReportMetadata(ExtremesReportMetadata val) {
		reportMetadata = val;
	}

	public ExtremesMinMax getDv() {
		return dv;
	}

	public void setDv(ExtremesMinMax val) {
		this.dv = val;
	}

	public ExtremesMinMax getUpchain() {
		return upchain;
	}

	public void setUpchain(ExtremesMinMax val) {
		this.upchain = val;
	}

	public ExtremesMinMax getPrimary() {
		return primary;
	}

	public void setPrimary(ExtremesMinMax val) {
		this.primary = val;
	}
	
}
	

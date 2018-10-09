package gov.usgs.aqcu.model;

import java.time.ZoneOffset;

import gov.usgs.aqcu.parameter.ExtremesRequestParameters;

public class ExtremesReportMetadata extends ReportMetadata {
	private ExtremesRequestParameters requestParameters;
	private boolean isInverted;
	private String primaryParameter;
	private String primaryUnit;
	private String primaryId;
	private String primaryLabel;
	private String dvParameter;
	private String dvUnit;
	private String dvLabel;
	private String dvComputation;
	private String upchainParameter;
	private String upchainUnit;
	private String upchainLabel;
	private String requestingUser;

	public ExtremesRequestParameters getRequestParameters() {
		return requestParameters;
	}
	
	public boolean isInverted() {
		return isInverted;
	}

	public String getPrimaryUniqueId() {
		return primaryId;
	}
	
	public String getPrimaryLabel() {
		return primaryLabel;
	}
	
	public String getDvParameter() {
		return dvParameter;
	}

	public String getDvUnit() {
		return dvUnit;
	}

	public String getDvLabel() {
		return dvLabel;
	}

	public String getDvComputation() {
		return dvComputation;
	}

	public String getUpchainParameter() {
		return upchainParameter;
	}


	public String getUpchainUnit() {
		return upchainUnit;
	}


	public String getUpchainLabel() {
		return upchainLabel;
	}

	public String getPrimaryParameter() {
		return primaryParameter;
	}

	public String getPrimaryUnit() {
		return primaryUnit;
	}


	public String getRequestingUser() {
		return requestingUser;
	}
	
	public void setRequestParameters(ExtremesRequestParameters val) {
		requestParameters = val;
		//Report Period displayed should be exactly as received, so get as UTC
		setStartDate(val.getStartInstant(ZoneOffset.UTC));
		setEndDate(val.getEndInstant(ZoneOffset.UTC));
		setPrimaryUniqueId(val.getPrimaryTimeseriesIdentifier());
	}
	
	public void setInverted(boolean val) {
		this.isInverted = val;
	}
	
	public void setPrimaryUniqueId(String val) {
		primaryId = val;
	}

	public void setPrimaryParameter(String val) {
		primaryParameter = val;
	}
	
	public void setPrimaryUnit(String val) {
		this.primaryUnit = val;
	}
	
	public void setPrimaryLabel(String val) {
		this.primaryLabel = val;
	}
	
	public void setDvParameter(String val) {
		this.dvParameter = val;
	}
	
	public void setDvUnit(String val) {
		this.dvUnit = val;
	}
	
	public void setDvLabel(String val) {
		this.dvLabel = val;
	}
	
	public void setDvComputation(String val) {
		this.dvComputation = val;
	}
	
	public void setUpchainParameter(String val) {
		this.upchainParameter = val;
	}
	
	public void setUpchainUnit(String val) {
		this.upchainUnit = val;
	}
	
	public void setUpchainLabel(String val) {
		this.upchainLabel = val;
	}
	
	public void setRequestingUser(String val) {
		requestingUser = val;
	}
	

}
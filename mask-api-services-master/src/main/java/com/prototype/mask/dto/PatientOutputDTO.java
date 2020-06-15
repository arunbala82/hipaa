package com.prototype.mask.dto;

import java.io.Serializable;

public class PatientOutputDTO implements Serializable {

	private String age;
	private String zipCode;
	private String admissionYear;
	private String dischargeYear;
	private String notes;

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getAdmissionYear() {
		return admissionYear;
	}

	public void setAdmissionYear(String admissionYear) {
		this.admissionYear = admissionYear;
	}

	public String getDischargeYear() {
		return dischargeYear;
	}

	public void setDischargeYear(String dischargeYear) {
		this.dischargeYear = dischargeYear;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

}

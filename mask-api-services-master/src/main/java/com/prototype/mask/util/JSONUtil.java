package com.prototype.mask.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.core.io.ClassPathResource;

import com.prototype.mask.dto.PatientInputDTO;
import com.prototype.mask.dto.PatientOutputDTO;

public class JSONUtil {

	private static Map<String, String> propertyMap = new ConcurrentHashMap<>();
	private boolean isLoaded = false;

	public JSONUtil(boolean isLoaded) {
		this.isLoaded = isLoaded;
		checkIfLoadOrRefresh();
	}

	private void checkIfLoadOrRefresh() {
		if (isLoaded) {
			return;
		}

		synchronized (propertyMap) {
			if (!isLoaded) {

				propertyMap = preparePopulationData();

				/**
				 * Not adding to logs
				 */

				System.out.println(propertyMap);

				isLoaded = true;
			}
		}
	}

	  /**
	   * This is the  method which populates population_by_zcta_2010.csv file.
	   * @param args Unused.
	   * @return map.
	   * @exception Exception On input error.
	   * @see Exception
	   */	
	
	private static Map<String, String> preparePopulationData() {
		Map<String, String> map = new HashMap<String, String>();
		try {
			ClassPathResource classPathResource = new ClassPathResource("templates\\population_by_zcta_2010.csv");
			BufferedReader br = null;
			String line = "";
			String cvsSplitBy = "\\,";
			try {
				br = new BufferedReader(new FileReader(classPathResource.getFile()));
				boolean isNewLine = true;
				while ((line = br.readLine()) != null) {
					line = line.replaceAll("\\t", "").trim();
					if (isNewLine) {
						isNewLine = false;
						continue;
					}
					// use comma as separator
					String[] csvValues = line.split(cvsSplitBy);
					if (csvValues.length == 2)
						map.put(csvValues[0], csvValues[1]);
				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}


	  /**
	   * This is the  method which masks patiendOutputDTO objects.
	   * @param PatientInputDTO
	   * @return PatientInputDTO
	   * @exception
	   * @see
	   */	
	
	public PatientOutputDTO maskPatientDetails(PatientInputDTO patientInputDTO) {
		if (patientInputDTO != null) {
			PatientOutputDTO patientOutputDTO = new PatientOutputDTO();
			patientOutputDTO.setAge(calculateAge(patientInputDTO.getBirthDate()));
			patientOutputDTO.setZipCode(preparZipCode(patientInputDTO.getZipCode()));
			Integer admissionYear = getYear(getDate(patientInputDTO.getAdmissionDate()));
			patientOutputDTO.setAdmissionYear(admissionYear != null ? admissionYear + "" : null);
			Integer dischargeYear = getYear(getDate(patientInputDTO.getDischargeDate()));
			patientOutputDTO.setDischargeYear(dischargeYear != null ? dischargeYear + "" : null);
			patientOutputDTO.setNotes(prepareNotes(patientInputDTO.getNotes()));
			return patientOutputDTO;
		}
		return null;
	}

	  /**
	   * This is the  method which masks patiendOutputDTO objects.
	   * @param Date
	   * @return YEAR
	   * @exception
	   * @see
	   */		
	
	private static Integer getYear(Date date) {
		if (date != null) {
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(date);
			return calendar.get(Calendar.YEAR);
		}
		return null;
	}

	  /**
	   * This is the  method which prepare Notes field.
	   * @param notes
	   * @return maskedNote
	   * @exception
	   * @see
	   */		
	
	private static String prepareNotes(String notes) {
		if (notes != null && !notes.isEmpty()) {
			// check for url
			String maskedNote = checkCriteriaAndMask(notes,
					"\\(?\\b(http://|www[.])[-A-Za-z0-9+&amp;@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&amp;@#/%=~_()|]");
			if (maskedNote != null && !maskedNote.isEmpty()) {
				// check for number
				maskedNote = checkCriteriaAndMask(maskedNote, "-?\\d+(\\.\\d+)?");
				// check for email
				maskedNote = checkCriteriaAndMask(maskedNote,
						"^([A-Za-z0-9_\\-\\.])+\\@([A-Za-z0-9_\\-\\.])+\\.([A-Za-z]{2,4})");
				return maskedNote;
			}
		}
		return notes;
	}

	  /**
	   * This is the  method which check criteria and mask.
	   * @param notes, regEx
	   * @return maskedNote
	   * @exception
	   * @see
	   */		
	
	private static String checkCriteriaAndMask(String notes, String regEx) {
		String maskedNote = notes;
		String note[] = notes.split(" ");
		for (String data : note) {
			Matcher m = null;
			Pattern p = Pattern.compile(regEx);
			m = p.matcher(data);
			if (m.find()) {
				maskedNote = notes.replaceAll(data, getMaskedData(data));
			}
		}
		return maskedNote;
	}

	  /**
	   * This is the  method which returns masked data.
	   * @param data
	   * @return maskedData
	   * @exception
	   * @see
	   */	
	
	private static String getMaskedData(String data) {
		String maskedData = "";
		for (Character c : data.toCharArray()) {
			maskedData += "X";
		}
		return maskedData;
	}

	  /**
	   * This is the  method which prepares zip code.
	   * @param zipCode
	   * @return masked zipcode
	   * @exception
	   * @see
	   */	
	
	private static String preparZipCode(String zipCode) {
		String population = propertyMap.get(zipCode);
		Integer pop = population != null && !population.isEmpty() ? Integer.valueOf(population) : 0;
		if (pop >= 20000) {
			return "000" + zipCode.substring(population.length() - 2, zipCode.length());
		} else {
			return "00000";
		}
	}

	  /**
	   * This is the  method which calculates age.
	   * @param birthDate
	   * @return age
	   * @exception
	   * @see
	   */		
	private static String calculateAge(String birthDate) {
		Date bd = getDate(birthDate);
		if (bd != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(bd);
			System.out.println("Date is: " + bd);
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH) + 1;
			int date = c.get(Calendar.DATE);
			LocalDate l1 = LocalDate.of(year, month, date);
			LocalDate now1 = LocalDate.now();
			Period diff1 = Period.between(l1, now1);
			System.out.println("age:" + diff1.getYears() + "years");
			return diff1.getYears() + "";
		}
		return null;
	}

	  /**
	   * This is the  method which format date field.
	   * @param date
	   * @return date
	   * @exception
	   * @see
	   */		
	private static Date getDate(String date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return formatter.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}

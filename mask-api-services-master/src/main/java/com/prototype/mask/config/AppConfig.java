package com.prototype.mask.config;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class AppConfig {
	@Bean("populationData")
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

}

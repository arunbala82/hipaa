package com.prototype.mask.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.prototype.mask.dto.PatientInputDTO;
import com.prototype.mask.dto.PatientOutputDTO;
import com.prototype.mask.util.JSONUtil;

@RestController
@RequestMapping(value = "/rest/v1")
public class MaskController {
	private boolean isLoaded = false;

	@RequestMapping(value = "/json/mask", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<PatientOutputDTO> maskJson(
			@RequestParam(value = "secret_key", required = false) String secretKey,
			@RequestBody PatientInputDTO patientInputDTO, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws Exception {
		if (patientInputDTO != null) {
			PatientOutputDTO patientOutputDTO = new JSONUtil(isLoaded).maskPatientDetails(patientInputDTO);
			isLoaded = true;
			return new ResponseEntity<>(patientOutputDTO, HttpStatus.OK);
		} else
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

	}

}

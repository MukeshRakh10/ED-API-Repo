package in.mk.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import in.mk.ed.dto.ElibResponseDto;
import in.mk.ed.service.IEDService;

@RestController
@CrossOrigin("*")
public class EdRestController {

	@Autowired
	private IEDService edServices;
	
	@PostMapping("/eligibility/create/{caseId}")
	public ResponseEntity<ElibResponseDto> determine(@PathVariable Long caseId)	{
		ElibResponseDto  response = edServices.determineEligibility(caseId);
		return 	new ResponseEntity<ElibResponseDto>(response,HttpStatus.OK);
	}
}

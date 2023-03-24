package in.mk.ed.service;

import in.mk.ed.dto.ElibResponseDto;

public interface IEDService {
	
	public ElibResponseDto determineEligibility(Long caseId);
	

}

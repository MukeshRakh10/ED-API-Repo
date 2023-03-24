package in.mk.ed.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import in.mk.ed.entity.IncomeDetails;

public interface IncomeRepository extends JpaRepository<IncomeDetails, Long>{
	
	public IncomeDetails findByCaseId(Long caseId);

}

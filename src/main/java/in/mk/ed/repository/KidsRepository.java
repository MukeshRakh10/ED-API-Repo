package in.mk.ed.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import in.mk.ed.entity.KidsDetails;

public interface KidsRepository extends JpaRepository<KidsDetails, Long> {

	public List<KidsDetails> findByCaseId(Long caseId);
}

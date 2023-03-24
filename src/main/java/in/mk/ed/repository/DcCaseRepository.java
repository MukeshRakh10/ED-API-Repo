package in.mk.ed.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;

import in.mk.ed.entity.DcCase;

public interface DcCaseRepository extends JpaRepository<DcCase,Serializable> {

}

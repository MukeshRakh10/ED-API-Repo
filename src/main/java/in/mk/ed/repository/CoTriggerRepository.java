package in.mk.ed.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.mk.ed.entity.CoTrigger;

@Repository
public interface CoTriggerRepository extends JpaRepository<CoTrigger, Serializable> {

}

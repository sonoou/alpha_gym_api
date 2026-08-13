package com.sonoou.alphagym.repository;

import com.sonoou.alphagym.entity.MembershipPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MembershipPlanRepository extends JpaRepository<MembershipPlanEntity, Long> {
    List<MembershipPlanEntity> findByActiveTrue();
}

package com.sonoou.alphagym.repository;

import com.sonoou.alphagym.entity.CommunityPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityPostRepository extends JpaRepository<CommunityPostEntity, Long> {
    List<CommunityPostEntity> findAllByOrderByCreatedAtDesc();
}

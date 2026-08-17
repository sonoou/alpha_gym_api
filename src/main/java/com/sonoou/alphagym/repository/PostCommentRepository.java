package com.sonoou.alphagym.repository;

import com.sonoou.alphagym.entity.PostCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCommentRepository extends JpaRepository<PostCommentEntity, Long> {
    List<PostCommentEntity> findByPostIdOrderByCreatedAtAsc(Long postId);
}

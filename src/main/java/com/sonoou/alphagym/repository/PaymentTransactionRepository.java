package com.sonoou.alphagym.repository;

import com.sonoou.alphagym.entity.PaymentTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransactionEntity, Long> {

    List<PaymentTransactionEntity> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<PaymentTransactionEntity> findByRazorpayOrderId(String razorpayOrderId);
}

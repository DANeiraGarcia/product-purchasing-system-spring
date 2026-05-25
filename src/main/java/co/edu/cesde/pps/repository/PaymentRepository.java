package co.edu.cesde.pps.repository;

import co.edu.cesde.pps.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
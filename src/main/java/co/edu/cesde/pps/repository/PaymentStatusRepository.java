package co.edu.cesde.pps.repository;

import co.edu.cesde.pps.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PaymentStatusRepository extends JpaRepository<PaymentStatus, Long> {
    Optional<PaymentStatus> findByNameIgnoreCase(String name);
}
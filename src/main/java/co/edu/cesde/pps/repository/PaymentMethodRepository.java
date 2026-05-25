package co.edu.cesde.pps.repository;

import co.edu.cesde.pps.model.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
    Optional<PaymentMethod> findByNameIgnoreCase(String name);
}
package co.edu.cesde.pps.repository;


import co.edu.cesde.pps.enums.CartStatus;
import co.edu.cesde.pps.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
// en el servicio del cartservice marcara un error en indica que debe pertencer al repositorio.
    Optional<Cart> findByUser_UserIdAndStatus(Long userId, CartStatus status);
}

package co.edu.cesde.pps.repository;

import co.edu.cesde.pps.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/* springboot viene con los metodos implementados CRUD al extender Jpa repository, solo se declaran metodos propios como
   existByEmailIgnoreCase // ignorando mayusculas o minusculas
   si no se declaran metodos propios entonces iria vacio y por defecto tendria el CRUD
                                                   //se le pasa el usuario y Long que es el,id */
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmailIgnoreCase(String email);

    Optional<User> findByEmailIgnoreCase(String email);
}
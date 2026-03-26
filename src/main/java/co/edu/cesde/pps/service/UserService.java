package co.edu.cesde.pps.service;

import co.edu.cesde.pps.dto.UserDTO;
import co.edu.cesde.pps.exception.DuplicateEntityException;
import co.edu.cesde.pps.exception.EntityNotFoundException;
import co.edu.cesde.pps.mapper.UserMapper;
import co.edu.cesde.pps.model.Role;
import co.edu.cesde.pps.model.User;
import co.edu.cesde.pps.repository.RoleRepository;
import co.edu.cesde.pps.repository.UserRepository;
import co.edu.cesde.pps.util.ValidationUtils;
import co.edu.cesde.pps.config.AppConfig;
import co.edu.cesde.pps.enums.UserStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para gestión de usuarios.
 *
 * Responsabilidades:
 * - CRUD de usuarios
 * - Registro con validaciones
 * - Búsqueda por diferentes criterios
 * - Conversión Entity <-> DTO
 *
 * NOTA: En Etapa 06 se agregará:
 * - @Service annotation
 * - @Transactional
 * - Inyección de UserRepository
 * - Persistencia real
 */

// el controlador recibe la consulta del front y lo envia al service y el service valida
// el servicio valida la informacion y manda la informacion al frontend
    // @service, springboot identifica que la clase es un servicio.
    //@Transactional, dice que actividades van a hacer operaciones
// para que identificar el servicio a nivel de clase (unen la logica de la interface) y hace el puente el controllador(endpoint)
@Service
@Transactional(readOnly = true) // read only: no ejecuta las acciones desde la clase sino desde los metodos
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userMapper = new UserMapper();
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }


    //metodo de escritura
    @Transactional
    public UserDTO registerUser(String email, String passwordHash, String firstName,
                                String lastName, String phone) {
        ValidationUtils.validateEmail(email, "email");
        ValidationUtils.validateNotBlank(passwordHash, "passwordHash");
        ValidationUtils.validateMinLength(passwordHash, AppConfig.getMinPasswordLength(), "password");
        ValidationUtils.validateNotBlank(firstName, "firstName");
        ValidationUtils.validateNotBlank(lastName, "lastName");

        if (phone != null && !phone.isBlank()) {
            ValidationUtils.validatePhone(phone, "phone");
        }

        if (existsByEmail(email)) {
            throw new DuplicateEntityException("User", "email", email);
        }

        Role defaultRole = roleRepository.findByNameIgnoreCase("CUSTOMER")
                .orElseThrow(() -> new EntityNotFoundException("Role", "CUSTOMER"));

        User user = User.builder()
                .role(defaultRole)
                .email(email.toLowerCase().trim())
                .passwordHash(passwordHash)
                .firstName(firstName.trim())
                .lastName(lastName.trim())
                .phone(phone != null ? phone.trim() : null)
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        user = userRepository.save(user);

        return userMapper.toDTO(user);
    }


    public UserDTO findById(Long userId) {
        User user = findUserEntityOrThrow(userId);
        return userMapper.toDTO(user);
    }

    // los find de consulta no llevan @transactional
    public UserDTO findByEmail(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new EntityNotFoundException("User with email: " + email));

        return userMapper.toDTO(user);
    }


    public List<UserDTO> findAllUsers() {
        return userMapper.toDTOList(userRepository.findAll());
    }


    public UserDTO updateProfile(Long userId, String firstName, String lastName, String phone) {
        User user = findUserEntityOrThrow(userId);

        // Validaciones
        if (firstName != null) {
            ValidationUtils.validateNotBlank(firstName, "firstName");
            user.setFirstName(firstName.trim());
        }

        if (lastName != null) {
            ValidationUtils.validateNotBlank(lastName, "lastName");
            user.setLastName(lastName.trim());
        }

        if (phone != null) {
            if (!phone.isBlank()) {
                ValidationUtils.validatePhone(phone, "phone");
                user.setPhone(phone.trim());
            } else {
                user.setPhone(null);
            }
        }


        return userMapper.toDTO(user);
    }


    public void deleteUser(Long userId) {
        User user = findUserEntityOrThrow(userId);
        user.setStatus(UserStatus.INACTIVE);

    }


    public boolean existsByEmail(String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }



    public User findUserEntityOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));
    }


}


// metodos post put y delete, llevan el @transactional pero no son readonly (porque si hacen operaciones)

//metodos post (create)
//metodos update (put)
//metodos delete (delete)
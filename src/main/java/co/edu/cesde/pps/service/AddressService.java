package co.edu.cesde.pps.service;

import co.edu.cesde.pps.dto.AddressDTO;
import co.edu.cesde.pps.exception.EntityNotFoundException;
import co.edu.cesde.pps.exception.ValidationException;
import co.edu.cesde.pps.mapper.AddressMapper;
import co.edu.cesde.pps.model.Address;
import co.edu.cesde.pps.model.User;
import co.edu.cesde.pps.repository.AddressRepository;
import co.edu.cesde.pps.util.ValidationUtils;
import co.edu.cesde.pps.config.AppConfig;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de direcciones de usuarios.
 *
 * Responsabilidades:
 * - CRUD de direcciones
 * - Gestión bidireccional (User <-> Address)
 * - Validaciones de direcciones
 * - Gestión de dirección por defecto (solo una puede ser default)
 * - Conversión Entity <-> DTO
 *
 * NOTA: En Etapa 06 se agregará:
 * - @Service annotation
 * - @Transactional
 * - Inyección de AddressRepository
 * - Persistencia real
 */
@Service
@Transactional(readOnly = true)
public class AddressService {

    private final AddressMapper addressMapper;
    private final UserService userService;
    private final AddressRepository addressRepository;

    public AddressService(UserService userService, AddressRepository addressRepository) {
        this.addressMapper = new AddressMapper();
        this.userService = userService;
        this.addressRepository = addressRepository;
    }


    @Transactional
    public AddressDTO addAddress(Long userId, AddressDTO addressDTO) {
        User user = userService.findUserEntityOrThrow(userId);

        long currentCount = addressRepository.countByUser_UserId(userId);

        if (currentCount >= AppConfig.getMaxAddressesPerUser()) {
            throw new ValidationException("User has reached maximum number of addresses (" +
                    AppConfig.getMaxAddressesPerUser() + ")");
        }

        validateAddressData(addressDTO);

        Address address = addressMapper.toEntity(addressDTO);

        user.getAddresses().add(address);
        address.setUser(user);

        if (currentCount == 0) {
            address.setIsDefault(true);
        } else if (Boolean.TRUE.equals(address.getIsDefault())) {
            unsetOtherDefaultAddresses(userId);
        }

        address = addressRepository.save(address);

        return addressMapper.toDTO(address);
    }


    @Transactional
    public AddressDTO updateAddress(Long addressId, AddressDTO addressDTO) {
        Address address = findAddressEntityOrThrow(addressId);

        validateAddressData(addressDTO);

        address.setType(addressDTO.getType());
        address.setLine1(addressDTO.getLine1());
        address.setLine2(addressDTO.getLine2());
        address.setCity(addressDTO.getCity());
        address.setState(addressDTO.getState());
        address.setCountry(addressDTO.getCountry());
        address.setPostalCode(addressDTO.getPostalCode());

        if (Boolean.TRUE.equals(addressDTO.getIsDefault()) && !Boolean.TRUE.equals(address.getIsDefault())) {
            unsetOtherDefaultAddresses(address.getUser().getUserId());
            address.setIsDefault(true);
        }

        address = addressRepository.save(address);

        return addressMapper.toDTO(address);
    }


    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        User user = userService.findUserEntityOrThrow(userId);
        Address address = findAddressEntityOrThrow(addressId);

        if (!address.getUser().getUserId().equals(userId)) {
            throw new ValidationException("Address does not belong to user");
        }

        user.getAddresses().remove(address);
        addressRepository.delete(address);

        if (Boolean.TRUE.equals(address.getIsDefault())) {
            List<Address> remainingAddresses = addressRepository.findByUser_UserId(userId);
            if (!remainingAddresses.isEmpty()) {
                Address newDefault = remainingAddresses.get(0);
                newDefault.setIsDefault(true);
                addressRepository.save(newDefault);
            }
        }
    }


    public AddressDTO setDefaultAddress(Long userId, Long addressId) {
        userService.findUserEntityOrThrow(userId); // Validar que usuario existe
        Address address = findAddressEntityOrThrow(addressId);

        // Validar que la dirección pertenezca al usuario
        if (!address.getUser().getUserId().equals(userId)) {
            throw new ValidationException("Address does not belong to user");
        }

        // Desmarcar otras direcciones como default
        unsetOtherDefaultAddresses(userId);

        // Marcar esta como default
        address.setIsDefault(true);

        // TODO Etapa 06: addressRepository.save(address);

        return addressMapper.toDTO(address);
    }

    /**
     * Obtiene todas las direcciones de un usuario.
     *
     * @param userId ID del usuario
     * @return Lista de AddressDTO
     */
    public List<AddressDTO> findUserAddresses(Long userId) {
        userService.findUserEntityOrThrow(userId);
        return addressMapper.toDTOList(addressRepository.findByUser_UserId(userId));
    }


    public AddressDTO findById(Long addressId) {
        Address address = findAddressEntityOrThrow(addressId);
        return addressMapper.toDTO(address);
    }


    public Address findAddressEntityOrThrow(Long addressId) {
        return addressRepository.findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException("Address", addressId));
    }

    // Métodos privados auxiliares


    private void validateAddressData(AddressDTO dto) {
        ValidationUtils.validateNotNull(dto.getType(), "type");
        ValidationUtils.validateNotBlank(dto.getLine1(), "line1");
        ValidationUtils.validateNotBlank(dto.getCity(), "city");
        ValidationUtils.validateNotBlank(dto.getState(), "state");
        ValidationUtils.validateNotBlank(dto.getCountry(), "country");
        ValidationUtils.validateNotBlank(dto.getPostalCode(), "postalCode");
    }


    private void unsetOtherDefaultAddresses(Long userId) {
        List<Address> addresses = addressRepository.findByUser_UserId(userId);
        addresses.forEach(a -> a.setIsDefault(false));
    }

    // Método auxiliar para simular auto-increment en memoria
    private Long generateNextId() {
        return addressesInMemory.stream()
                .mapToLong(Address::getAddressId)
                .max()
                .orElse(0L) + 1;
    }
}

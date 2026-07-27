package com.FedericoFunes.app_service.services;

import com.FedericoFunes.app_service.dtos.users.RequestUsersDTO;
import com.FedericoFunes.app_service.dtos.users.ResetPasswordDTO;
import com.FedericoFunes.app_service.dtos.users.ResponseUsersDTO;
import com.FedericoFunes.app_service.entities.UsersEntity;
import com.FedericoFunes.app_service.entities.enums.Roles;
import com.FedericoFunes.app_service.repositories.UsersRepository;
import com.FedericoFunes.app_service.services.external.EmailService;
import com.FedericoFunes.app_service.services.impl.UsersServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsersServiceImplTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UsersServiceImpl usersService;

    private UsersEntity entity;
    private RequestUsersDTO request;

    @BeforeEach
    void setUp() {
        entity = UsersEntity.builder()
                .id(1L)
                .username("juanperez")
                .password("encoded_password_123")
                .email("juan@test.com")
                .phone("1234567890")
                .role(Roles.DONOR)
                .roleId(10L)
                .isActive(true)
                .build();

        request = RequestUsersDTO.builder()
                .username("juanperez")
                .password("raw_password")
                .email("juan@test.com")
                .phone("1234567890")
                .role(Roles.DONOR)
                .roleId(10L)
                .build();
    }

    // ── findByUsername ──

    @Test
    void findByUsername_shouldReturnDto_whenFound() {
        when(usersRepository.findByUsername("juanperez")).thenReturn(Optional.of(entity));

        ResponseUsersDTO result = usersService.findByUsername("juanperez");

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("juanperez", result.getUsername());
        assertEquals("juan@test.com", result.getEmail());
        assertEquals("1234567890", result.getPhone());
        assertEquals(Roles.DONOR, result.getRole());
        verify(usersRepository, times(1)).findByUsername("juanperez");
    }

    @Test
    void findByUsername_shouldThrowException_whenNotFound() {
        when(usersRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> usersService.findByUsername("ghost"));
    }

    // ── registerUser ──

    @Test
    void registerUser_shouldReturnCreatedDto() {
        when(passwordEncoder.encode("raw_password")).thenReturn("encoded_password_123");
        when(usersRepository.save(any(UsersEntity.class))).thenReturn(entity);

        ResponseUsersDTO result = usersService.registerUser(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("juanperez", result.getUsername());
        assertEquals("juan@test.com", result.getEmail());
        assertEquals(Roles.DONOR, result.getRole());

        verify(passwordEncoder, times(1)).encode("raw_password");
        verify(usersRepository, times(1)).save(any(UsersEntity.class));
        verify(emailService, times(1)).sendHtmlEmail(
                eq("juan@test.com"),
                contains("Bienvenido"),
                anyString()
        );
    }

    @Test
    void registerUser_shouldEncodePassword() {
        when(passwordEncoder.encode("my_secret")).thenReturn("BCRYPT_HASHED");
        UsersEntity saved = UsersEntity.builder()
                .id(2L).username("testuser").password("BCRYPT_HASHED")
                .email("test@test.com").phone("0000000000")
                .role(Roles.ORGANIZER).roleId(20L).isActive(true).build();
        when(usersRepository.save(any(UsersEntity.class))).thenReturn(saved);

        RequestUsersDTO req = RequestUsersDTO.builder()
                .username("testuser").password("my_secret")
                .email("test@test.com").phone("0000000000")
                .role(Roles.ORGANIZER).roleId(20L).build();

        usersService.registerUser(req);

        verify(passwordEncoder, times(1)).encode("my_secret");
        verify(usersRepository, times(1)).save(argThat(e ->
                e.getPassword().equals("BCRYPT_HASHED")
        ));
    }

    @Test
    void registerUser_shouldSendWelcomeEmail() {
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(usersRepository.save(any(UsersEntity.class))).thenReturn(entity);

        usersService.registerUser(request);

        verify(emailService, times(1)).sendHtmlEmail(
                eq("juan@test.com"),
                eq("Bienvenido a Bloodo.net"),
                anyString()
        );
    }

    @Test
    void registerUser_shouldSetRoleIdFromDto() {
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(usersRepository.save(any(UsersEntity.class))).thenReturn(entity);

        usersService.registerUser(request);

        verify(usersRepository).save(argThat(e ->
                e.getRoleId().equals(10L) && e.getRole() == Roles.DONOR
        ));
    }

    // ── resetPasswordFirstStep ──

    @Test
    void resetPasswordFirstStep_shouldReturnCodeAndSendEmail() {
        when(usersRepository.findByEmail("juan@test.com")).thenReturn(Optional.of(entity));

        String code = usersService.resetPasswordFirstStep("juan@test.com");

        assertNotNull(code);
        assertFalse(code.isEmpty());
        verify(emailService, times(1)).sendHtmlEmail(
                eq("juan@test.com"),
                contains("reseteo"),
                anyString()
        );
    }

    @Test
    void resetPasswordFirstStep_shouldReturnUuidFormat() {
        when(usersRepository.findByEmail("juan@test.com")).thenReturn(Optional.of(entity));

        String code = usersService.resetPasswordFirstStep("juan@test.com");

        assertDoesNotThrow(() -> java.util.UUID.fromString(code));
    }

    @Test
    void resetPasswordFirstStep_shouldThrowException_whenEmailNotFound() {
        when(usersRepository.findByEmail("ghost@test.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> usersService.resetPasswordFirstStep("ghost@test.com"));
    }

    @Test
    void resetPasswordFirstStep_shouldCallCreateHtmlWithResetTrue() {
        when(usersRepository.findByEmail("juan@test.com")).thenReturn(Optional.of(entity));

        usersService.resetPasswordFirstStep("juan@test.com");

        verify(emailService, times(1)).sendHtmlEmail(
                eq("juan@test.com"),
                eq("Pedido de reseteo de contraseña"),
                argThat(html -> html.contains("resetear tu contraseña") && html.contains("Código:"))
        );
    }

    @Test
    void resetPasswordFirstStep_shouldReturnDifferentCodesOnEachCall() {
        when(usersRepository.findByEmail("juan@test.com")).thenReturn(Optional.of(entity));

        String code1 = usersService.resetPasswordFirstStep("juan@test.com");
        String code2 = usersService.resetPasswordFirstStep("juan@test.com");

        assertNotEquals(code1, code2);
    }

    @Test
    void resetPasswordFirstStep_shouldThrowException_whenEmailIsNull() {
        when(usersRepository.findByEmail(null)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> usersService.resetPasswordFirstStep(null));
    }

    // ── resetPasswordSecondStep ──

    @Test
    void resetPasswordSecondStep_shouldReturnTrueOnSuccess() {
        ResetPasswordDTO dto = ResetPasswordDTO.builder()
                .email("juan@test.com")
                .password("new_password")
                .build();

        when(usersRepository.findByEmail("juan@test.com")).thenReturn(Optional.of(entity));
        when(passwordEncoder.encode("new_password")).thenReturn("new_encoded_password");
        when(usersRepository.save(any(UsersEntity.class))).thenReturn(entity);

        Boolean result = usersService.resetPasswordSecondStep(dto);

        assertTrue(result);
        verify(passwordEncoder, times(1)).encode("new_password");
        verify(usersRepository, times(1)).save(any(UsersEntity.class));
    }

    @Test
    void resetPasswordSecondStep_shouldUpdatePassword() {
        ResetPasswordDTO dto = ResetPasswordDTO.builder()
                .email("juan@test.com")
                .password("brand_new_pass")
                .build();

        when(usersRepository.findByEmail("juan@test.com")).thenReturn(Optional.of(entity));
        when(passwordEncoder.encode("brand_new_pass")).thenReturn("ENCODED_BRAND_NEW");
        when(usersRepository.save(any(UsersEntity.class))).thenReturn(entity);

        usersService.resetPasswordSecondStep(dto);

        verify(usersRepository).save(argThat(e ->
                e.getPassword().equals("ENCODED_BRAND_NEW")
        ));
    }

    @Test
    void resetPasswordSecondStep_shouldThrowException_whenEmailNotFound() {
        ResetPasswordDTO dto = ResetPasswordDTO.builder()
                .email("ghost@test.com")
                .password("new_password")
                .build();

        when(usersRepository.findByEmail("ghost@test.com")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> usersService.resetPasswordSecondStep(dto));
    }

    // ── EntityToDTO catch block ──

    @Test
    void entityToDTO_shouldThrowResponseStatusException_whenMappingFails() {
        UsersEntity badEntity = mock(UsersEntity.class);
        when(badEntity.getId()).thenThrow(new RuntimeException("DB connection error"));
        when(usersRepository.findByUsername("baduser")).thenReturn(Optional.of(badEntity));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> usersService.findByUsername("baduser"));

        assertEquals(500, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("Error mapping usersDTO"));
    }

    @Test
    void registerUser_shouldThrowException_whenEntityToDTOFails() {
        UsersEntity savedEntity = mock(UsersEntity.class);
        when(savedEntity.getId()).thenThrow(new RuntimeException("Serialization error"));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(usersRepository.save(any(UsersEntity.class))).thenReturn(savedEntity);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> usersService.registerUser(request));

        assertEquals(500, ex.getStatusCode().value());
    }

    // ── createHtml coverage (isReset=false via registerUser) ──

    @Test
    void registerUser_shouldCallCreateHtmlWithResetFalse() {
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(usersRepository.save(any(UsersEntity.class))).thenReturn(entity);

        usersService.registerUser(request);

        verify(emailService, times(1)).sendHtmlEmail(
                eq("juan@test.com"),
                eq("Bienvenido a Bloodo.net"),
                argThat(html -> html.contains("Nombre de usuario:") && html.contains("juanperez"))
        );
    }

    @Test
    void resetPasswordSecondStep_shouldThrowException_whenSaveFails() {
        ResetPasswordDTO dto = ResetPasswordDTO.builder()
                .email("juan@test.com")
                .password("new_password")
                .build();

        when(usersRepository.findByEmail("juan@test.com")).thenReturn(Optional.of(entity));
        when(passwordEncoder.encode("new_password")).thenReturn("new_encoded");
        when(usersRepository.save(any(UsersEntity.class))).thenThrow(new RuntimeException("DB write error"));

        assertThrows(ResponseStatusException.class,
                () -> usersService.resetPasswordSecondStep(dto));
    }
}

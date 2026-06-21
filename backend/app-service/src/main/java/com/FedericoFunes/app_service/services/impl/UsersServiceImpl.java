package com.FedericoFunes.app_service.services.impl;

import com.FedericoFunes.app_service.dtos.users.RequestUsersDTO;
import com.FedericoFunes.app_service.dtos.users.ResetPasswordDTO;
import com.FedericoFunes.app_service.dtos.users.ResponseUsersDTO;
import com.FedericoFunes.app_service.entities.UsersEntity;
import com.FedericoFunes.app_service.repositories.UsersRepository;
import com.FedericoFunes.app_service.services.UsersService;
import com.FedericoFunes.app_service.services.external.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {

    private final PasswordEncoder passwordEncoder;
    private final UsersRepository usersRepository;
    private final EmailService emailService;

    private ResponseUsersDTO EntityToDTO(UsersEntity usersEntity) {
        try {
            ResponseUsersDTO dto = new ResponseUsersDTO();
            dto.setId(usersEntity.getId());
            dto.setUsername(usersEntity.getUsername());
            dto.setEmail(usersEntity.getEmail());
            dto.setPhone(usersEntity.getPhone());
            dto.setRole(usersEntity.getRole());
            return dto;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(500),"Error mapping usersDTO: " + e.getMessage());
        }
    }
    private String createHtml (String code_title, Boolean isReset) {
        String titleHtml = isReset ? "Solicitud de reseteo de contraseña" : "Usuario creado con éxito";
        if (isReset) {
            return "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "  <style>\n" +
                    "    body { font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 20px; }\n" +
                    "    .card { background-color: #fff; border-radius: 8px; padding: 20px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }\n" +
                    "    h2 { color: #d32f2f; }\n" +
                    "    p { color: #333; }\n" +
                    "    .footer { margin-top: 20px; font-size: 12px; color: #777; }\n" +
                    "  </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "  <div class=\"card\">\n" +
                    "    <h2>"+titleHtml+"</h2>\n" +
                    "    <p>Utiliza el siguiente código en la aplicación para resetear tu contraseña</p>\n" +
                    "    <p><strong>Código:</strong> "+ code_title +"</p>\n" +
                    "    <p>Si no solicitaste este cambio, por favor ignora este correo.</p>\n" +
                    "    <p>¡Gracias por tu compromiso con salvar vidas!</p>\n" +
                    "    <div class=\"footer\">\n" +
                    "      Bloodo.net - Plataforma de donación de sangre\n" +
                    "    </div>\n" +
                    "  </div>\n" +
                    "</body>\n" +
                    "</html>\n";
        } else {
            return "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "  <style>\n" +
                    "    body { font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 20px; }\n" +
                    "    .card { background-color: #fff; border-radius: 8px; padding: 20px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }\n" +
                    "    h2 { color: #d32f2f; }\n" +
                    "    p { color: #333; }\n" +
                    "    .footer { margin-top: 20px; font-size: 12px; color: #777; }\n" +
                    "  </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "  <div class=\"card\">\n" +
                    "    <h2>"+titleHtml+"</h2>\n" +
                    "    <p><strong>Nombre de usuario:</strong> "+ code_title +"</p>\n" +
                    "    <p>¡Gracias por tu compromiso con salvar vidas!</p>\n" +
                    "    <div class=\"footer\">\n" +
                    "      Bloodo.net - Plataforma de donación de sangre\n" +
                    "    </div>\n" +
                    "  </div>\n" +
                    "</body>\n" +
                    "</html>\n";
        }
    }

    @Override
    public ResponseUsersDTO findByUsername(String username) {
        UsersEntity entity = usersRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        return EntityToDTO(entity);
    }

    @Override
    public ResponseUsersDTO registerUser(RequestUsersDTO dto) {
        UsersEntity entity = new UsersEntity();
        entity.setUsername(dto.getUsername());
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setRole(dto.getRole());
        entity.setRoleId(dto.getRoleId());

        UsersEntity savedEntity = usersRepository.save(entity);
        emailService.sendHtmlEmail(savedEntity.getEmail(), "Bienvenido a Bloodo.net", createHtml(savedEntity.getUsername(), false));
        return EntityToDTO(savedEntity);
    }

    @Override
    public String resetPasswordFirstStep(String email) {
        UsersEntity user = usersRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        String resetCode = UUID.randomUUID().toString();
        if (user.getEmail().equals(email)) {
            emailService.sendHtmlEmail(email, "Pedido de reseteo de contraseña", createHtml(resetCode, true));
        } else {
            throw new RuntimeException("Email not found");
        }
        return resetCode;
    }

    @Override
    public Boolean resetPasswordSecondStep(ResetPasswordDTO dto) {
        try {
            UsersEntity user = usersRepository.findByEmail(dto.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            usersRepository.save(user);
            return true;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(500), "Error resetting password: " + e.getMessage());
        }
    }
}

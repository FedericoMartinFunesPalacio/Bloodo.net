package com.FedericoFunes.app_service.controllers;

import com.FedericoFunes.app_service.dtos.users.RequestUsersDTO;
import com.FedericoFunes.app_service.dtos.users.ResponseUsersDTO;
import com.FedericoFunes.app_service.entities.UsersEntity;
import com.FedericoFunes.app_service.repositories.UsersRepository;
import com.FedericoFunes.app_service.security.JwtUtil;
import com.FedericoFunes.app_service.services.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UsersService usersService;
    private final AuthenticationManager authenticationManager;
    private final UsersRepository usersRepository;
    private final JwtUtil jwtUtil;

    @Operation(summary = "Registrar un nuevo usuario",
            description = "Crea un nuevo usuario en el sistema con los datos proporcionados.")
    @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente.")
    @PostMapping("/register")
    public ResponseEntity<ResponseUsersDTO> register(@RequestBody RequestUsersDTO request) {
        ResponseUsersDTO response = usersService.registerUser(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Login de usuario",
            description = "Valida las credenciales y devuelve un JWT para acceder a los endpoints protegidos.")
    @ApiResponse(responseCode = "200", description = "Login exitoso, se devuelve el token JWT.")
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody RequestUsersDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UsersEntity userDetails = usersRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        final String jwt = jwtUtil.generateToken(userDetails);

        Map<String, String> response = new HashMap<>();
        response.put("token", jwt);
        response.put("id", userDetails.getId().toString());
        return ResponseEntity.ok(response);
    }
}



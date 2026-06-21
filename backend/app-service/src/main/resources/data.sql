-- =============================================
-- Bloodo.net - Datos de prueba (perfil dev)
-- IDs 100+ para no molestar en desarrollo
-- =============================================

-- =============================================
-- 1. ORGANIZERS (base table - JOINED inheritance)
-- =============================================
INSERT INTO organizer (id, document, direction, latitude, longitude, email, phone_number, is_active)
VALUES
  (100, '30-71234567-9', 'Av. Córdoba 1234, CABA', -34.6037, -58.3816, 'contacto@hogenal.edu.ar', '+5491145678901', true),
  (101, '28345678', 'Av. Rivadavia 5678, CABA', -34.6092, -58.3789, 'carlos.mendez@gmail.com', '+5491156789012', true),
  (102, '30-71987654-3', 'San Martín 910, Vicente López, GBA', -34.5227, -58.4791, 'info@cruzroja-vl.org.ar', '+5491167890123', true);

-- =============================================
-- 2. ORGANIZER_EMP (inherits from organizer id=100, 102)
-- =============================================
INSERT INTO organizer_emp (id, full_name)
VALUES
  (100, 'Hospital General de Buenos Aires'),
  (102, 'Cruz Roja Argentina - Sucursal Vicente López');

-- =============================================
-- 3. ORGANIZER_PER (inherits from organizer id=101)
-- =============================================
INSERT INTO organizer_per (id, first_name, last_name, birthdate, gender)
VALUES
  (101, 'Carlos', 'Méndez', DATE '1978-05-15', 'MALE');

-- =============================================
-- 4. DONORS (10 donadores con variedad de tipos)
-- =============================================
INSERT INTO donor (id, first_name, last_name, birthdate, document, blood_factor, blood_group, gender, height, weight, email, phone_number, is_active)
VALUES
  (100, 'Juan', 'Pérez', DATE '1990-03-12', '34567890', 'POSITIVE', 'O', 'MALE', 1.78, 82.5, 'juan.perez@email.com', '+5491178901234', true),
  (101, 'María', 'García', DATE '1985-07-22', '35678901', 'NEGATIVE', 'A', 'FEMALE', 1.65, 58.0, 'maria.garcia@email.com', '+5491189012345', true),
  (102, 'Roberto', 'López', DATE '1992-11-08', '36789012', 'POSITIVE', 'B', 'MALE', 1.82, 90.0, 'roberto.lopez@email.com', '+5491190123456', true),
  (103, 'Ana', 'Martínez', DATE '1988-01-30', '37890123', 'POSITIVE', 'AB', 'FEMALE', 1.70, 63.5, 'ana.martinez@email.com', '+5491101234567', true),
  (104, 'Carlos', 'Rodríguez', DATE '1995-09-17', '38901234', 'NEGATIVE', 'O', 'MALE', 1.75, 75.0, 'carlos.rodriguez@email.com', '+5491112345678', true),
  (105, 'Laura', 'Fernández', DATE '1993-04-25', '39012345', 'POSITIVE', 'O', 'FEMALE', 1.68, 60.0, 'laura.fernandez@email.com', '+5491123456789', true),
  (106, 'Martín', 'González', DATE '1987-12-03', '40123456', 'NEGATIVE', 'A', 'MALE', 1.80, 85.0, 'martin.gonzalez@email.com', '+5491134567890', true),
  (107, 'Sofía', 'Torres', DATE '1991-06-18', '41234567', 'POSITIVE', 'O', 'FEMALE', 1.62, 55.0, 'sofia.torres@email.com', '+5491145678901', true),
  (108, 'Diego', 'Ramírez', DATE '1994-08-09', '42345678', 'NEGATIVE', 'B', 'MALE', 1.85, 88.0, 'diego.ramirez@email.com', '+5491156789012', true),
  (109, 'Valentina', 'Silva', DATE '1989-02-14', '43456789', 'POSITIVE', 'A', 'FEMALE', 1.72, 65.0, 'valentina.silva@email.com', '+5491167890123', true);

-- =============================================
-- 5. CAMPAIGNS (3 activas + 1 finalizada con suscriptores + 1 inactiva)
-- =============================================
INSERT INTO campaigns (id, title, description, start_date, end_date, start_time, direction, latitude, longitude, organizer_id, blood_factor_required, blood_group_required, is_active)
VALUES
  (100, 'Jornada de Donación - Hospital General',
   'Gran jornada de donación de sangre en el Hospital General. Se necesitan todos los tipos de sangre. ¡Tu donación salva vidas!',
   DATE '2026-06-15', NULL, TIME '09:00:00', 'Av. Córdoba 1234, CABA', -34.6037, -58.3816, 100, NULL, NULL, true),
  (101, 'Campaña Solidaria en San Martín',
   'Jornada de donación organizada por el Dr. Méndez en el centro cultural de San Martín. Se buscan donadores de sangre tipo O y A.',
   DATE '2026-06-20', NULL, TIME '10:00:00', 'Av. Rivadavia 5678, CABA', -34.6092, -58.3789, 101, 'POSITIVE', 'O', true),
  (102, 'Cruz Roja - Donación de Verano',
   'Campaña de verano de Cruz Roja. Se necesita sangre de todos los tipos para abastecer los hospitales de la zona norte del GBA.',
   DATE '2026-07-01', NULL, TIME '08:30:00', 'San Martín 910, Vicente López', -34.5227, -58.4791, 102, NULL, NULL, true),
  (103, 'Donación en Escuela Secundaria N°5',
   'Jornada de concientización y donación para estudiantes y vecinos del barrio. Se aceptan todos los tipos de sangre.',
   DATE '2025-12-01', DATE '2025-12-15', TIME '14:00:00', 'Av. Libertador 8000, Vicente López', -34.5310, -58.4750, 102, NULL, NULL, true),
  (104, 'Campaña de Invierno - Hospital General',
   'Campaña de invierno para reposición de bancos de sangre. Se necesitan todos los tipos sanguíneos.',
   DATE '2025-09-01', DATE '2025-09-30', TIME '09:00:00', 'Av. Córdoba 1234, CABA', -34.6037, -58.3816, 100, NULL, NULL, false);

-- =============================================
-- 6. CAMPAIGN_DONORS (suscripciones - más variedad)
-- =============================================
INSERT INTO campaign_donors (campaign_id, donor_id)
VALUES
  -- Campaña 100 (activa, sin requisito): 5 suscriptores
  (100, 100),
  (100, 101),
  (100, 103),
  (100, 105),
  (100, 107),
  -- Campaña 101 (activa, requiere O POSITIVE): 3 suscriptores
  (101, 102),
  (101, 104),
  (101, 108),
  -- Campaña 102 (activa, sin requisito): 4 suscriptores
  (102, 100),
  (102, 106),
  (102, 109),
  (102, 105),
  -- Campaña 103 (finalizada, sin requisito): 4 suscriptores
  (103, 100),
  (103, 101),
  (103, 104),
  (103, 107);

-- =============================================
-- 7. USERS (password: password123 hasheado con BCrypt)
-- =============================================
INSERT INTO users (username, password, email, phone, role, role_id, is_active)
VALUES
  ('admin',         '$2b$10$I0CooEyskeO0SfAunJakZe8jZ6ZPjmab5hWwPJFdKvvfI/Lkp2R/O', 'admin@bloodo.net',         '+5491100000000', 'ADMIN',     0, true),
  ('fede',      '$2b$10$I0CooEyskeO0SfAunJakZe8jZ6ZPjmab5hWwPJFdKvvfI/Lkp2R/O', 'juan.perez@email.com',     '+5491178901234', 'DONOR',     100,  true),
  ('drfede',       '$2b$10$I0CooEyskeO0SfAunJakZe8jZ6ZPjmab5hWwPJFdKvvfI/Lkp2R/O', 'carlos.mendez@gmail.com',  '+5491156789012', 'ORGANIZER', 101,  true);

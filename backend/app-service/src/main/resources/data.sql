-- =============================================
-- Bloodo.net - Datos de prueba (perfil dev)
-- IDs 100+ para no molestar en desarrollo
-- =============================================

-- =============================================
-- 1. ORGANIZERS (base table - JOINED inheritance)
-- =============================================
INSERT INTO organizer (id, document, direction, latitude, longitude, email, phone_number, is_active)
VALUES
  -- Existentes (Buenos Aires)
  (100, '30-71234567-9', 'Av. Córdoba 1234, CABA', -34.6037, -58.3816, 'contacto@hogenal.edu.ar', '+5491145678901', true),
  (101, '28345678', 'Av. Rivadavia 5678, CABA', -34.6092, -58.3789, 'carlos.mendez@gmail.com', '+5491156789012', true),
  (102, '30-71987654-3', 'San Martín 910, Vicente López, GBA', -34.5227, -58.4791, 'info@cruzroja-vl.org.ar', '+5491167890123', true),
  -- Nuevos Emp (Córdoba)
  (103, '30-72001111-1', 'Av. General Paz 500, Córdoba', -31.4201, -64.1848, 'contacto@hospitalITALiano.cba.ar', '+5493514567890', true),
  (104, '30-72002222-2', 'Bv. San Juan 1500, Córdoba', -31.4150, -64.1780, 'info@fundacion-sangre.cba.ar', '+5493514678901', true),
  (105, '30-72003333-3', 'Av. Vélez Sársfield 800, Córdoba', -31.4190, -64.1920, 'donaciones@cruzroja-cba.org.ar', '+5493514789012', true),
  (106, '30-72004444-4', 'Belgrano 1200, Córdoba', -31.4230, -64.1810, 'eventos@clinica-cba.com.ar', '+5493514890123', true),
  (107, '30-72005555-5', 'Obispo Trejo 450, Córdoba', -31.4170, -64.1870, 'solidaridad@ong-cba.org.ar', '+5493514901234', true),
  -- Nuevos Per (Córdoba)
  (108, '33123456', 'Caseros 300, Córdoba', -31.4130, -64.1830, 'lucia.fernandez@gmail.com', '+5493515012345', true),
  (109, '34234567', 'Mitre 780, Córdoba', -31.4200, -64.1750, 'marcos.lopez@gmail.com', '+5493515123456', true),
  (110, '35345678', 'San Martín 1100, Córdoba', -31.4180, -64.1900, 'elena.rodriguez@gmail.com', '+5493515234567', true),
  (111, '30-36456789-7', 'Entre Ríos 650, Córdoba', -31.4250, -64.1770, 'pablo.garcia@gmail.com', '+5493515345678', true),
  (112, '37567890', 'Duarte Quirós 900, Córdoba', -31.4160, -64.1950, 'camila.martinez@gmail.com', '+5493515456789', true);

-- =============================================
-- 2. ORGANIZER_EMP (inherits from organizer)
-- =============================================
INSERT INTO organizer_emp (id, full_name)
VALUES
  (100, 'Hospital General de Buenos Aires'),
  (102, 'Cruz Roja Argentina - Sucursal Vicente López'),
  (103, 'Hospital Italiano de Córdoba'),
  (104, 'Fundación Sangre Viva Córdoba'),
  (105, 'Cruz Roja Argentina - Sucursal Córdoba'),
  (106, 'Clínica Privada de Córdoba'),
  (107, 'ONG Solidaridad Córdoba');

-- =============================================
-- 3. ORGANIZER_PER (inherits from organizer)
-- =============================================
INSERT INTO organizer_per (id, first_name, last_name, birthdate, gender)
VALUES
  (101, 'Carlos', 'Méndez', DATE '1978-05-15', 'MALE'),
  (108, 'Lucía', 'Fernández', DATE '1985-08-20', 'FEMALE'),
  (109, 'Marcos', 'López', DATE '1990-03-12', 'MALE'),
  (110, 'Elena', 'Rodríguez', DATE '1982-11-25', 'FEMALE'),
  (111, 'Pablo', 'García', DATE '1975-06-30', 'MALE'),
  (112, 'Camila', 'Martínez', DATE '1993-01-18', 'FEMALE');

-- =============================================
-- 4. DONORS (10 existentes + 20 nuevos = 30 total)
-- =============================================
INSERT INTO donor (id, first_name, last_name, birthdate, document, blood_factor, blood_group, gender, height, weight, email, phone_number, is_active)
VALUES
  -- Existentes (Buenos Aires)
  (100, 'Juan', 'Pérez', DATE '1990-03-12', '34567890', 'POSITIVE', 'O', 'MALE', 1.78, 82.5, 'juan.perez@email.com', '+5491178901234', true),
  (101, 'María', 'García', DATE '1985-07-22', '35678901', 'NEGATIVE', 'A', 'FEMALE', 1.65, 58.0, 'maria.garcia@email.com', '+5491189012345', true),
  (102, 'Roberto', 'López', DATE '1992-11-08', '36789012', 'POSITIVE', 'B', 'MALE', 1.82, 90.0, 'roberto.lopez@email.com', '+5491190123456', true),
  (103, 'Ana', 'Martínez', DATE '1988-01-30', '37890123', 'POSITIVE', 'AB', 'FEMALE', 1.70, 63.5, 'ana.martinez@email.com', '+5491101234567', true),
  (104, 'Carlos', 'Rodríguez', DATE '1995-09-17', '38901234', 'NEGATIVE', 'O', 'MALE', 1.75, 75.0, 'carlos.rodriguez@email.com', '+5491112345678', true),
  (105, 'Laura', 'Fernández', DATE '1993-04-25', '39012345', 'POSITIVE', 'O', 'FEMALE', 1.68, 60.0, 'laura.fernandez@email.com', '+5491123456789', true),
  (106, 'Martín', 'González', DATE '1987-12-03', '40123456', 'NEGATIVE', 'A', 'MALE', 1.80, 85.0, 'martin.gonzalez@email.com', '+5491134567890', true),
  (107, 'Sofía', 'Torres', DATE '1991-06-18', '41234567', 'POSITIVE', 'O', 'FEMALE', 1.62, 55.0, 'sofia.torres@email.com', '+5491145678901', true),
  (108, 'Diego', 'Ramírez', DATE '1994-08-09', '42345678', 'NEGATIVE', 'B', 'MALE', 1.85, 88.0, 'diego.ramirez@email.com', '+5491156789012', true),
  (109, 'Valentina', 'Silva', DATE '1989-02-14', '43456789', 'POSITIVE', 'A', 'FEMALE', 1.72, 65.0, 'valentina.silva@email.com', '+5491167890123', true),
  -- Nuevos (Córdoba - 20 donadores)
  (110, 'Mateo', 'Díaz', DATE '1991-05-10', '50123456', 'POSITIVE', 'O', 'MALE', 1.80, 78.0, 'mateo.diaz@email.com', '+5493516012345', true),
  (111, 'Isabella', 'Morales', DATE '1988-09-22', '51234567', 'NEGATIVE', 'A', 'FEMALE', 1.67, 59.0, 'isabella.morales@email.com', '+5493516123456', true),
  (112, 'Santiago', 'Herrera', DATE '1993-01-05', '52345678', 'POSITIVE', 'B', 'MALE', 1.83, 85.0, 'santiago.herrera@email.com', '+5493516234567', true),
  (113, 'Camila', 'Vargas', DATE '1990-07-15', '53456789', 'POSITIVE', 'AB', 'FEMALE', 1.63, 54.0, 'camila.vargas@email.com', '+5493516345678', true),
  (114, 'Tomás', 'Castro', DATE '1986-12-28', '54567890', 'NEGATIVE', 'O', 'MALE', 1.77, 80.0, 'tomas.castro@email.com', '+5493516456789', true),
  (115, 'Valentina', 'Ruiz', DATE '1994-04-03', '55678901', 'POSITIVE', 'O', 'FEMALE', 1.70, 62.0, 'valentina.ruiz@email.com', '+5493516567890', true),
  (116, 'Lucas', 'Medina', DATE '1989-08-19', '56789012', 'NEGATIVE', 'A', 'MALE', 1.79, 82.0, 'lucas.medina@email.com', '+5493516678901', true),
  (117, 'Julieta', 'Romero', DATE '1992-02-11', '57890123', 'POSITIVE', 'B', 'FEMALE', 1.66, 57.0, 'julieta.romero@email.com', '+5493516789012', true),
  (118, 'Agustín', 'Alvarez', DATE '1987-06-25', '58901234', 'POSITIVE', 'O', 'MALE', 1.84, 90.0, 'agustin.alvarez@email.com', '+5493516890123', true),
  (119, 'Emilia', 'Sosa', DATE '1995-10-08', '59012345', 'NEGATIVE', 'AB', 'FEMALE', 1.60, 52.0, 'emilia.sosa@email.com', '+5493516901234', true),
  (120, 'Benjamín', 'Torres', DATE '1984-03-17', '60123456', 'POSITIVE', 'A', 'MALE', 1.81, 83.0, 'benjamin.torres@email.com', '+5493517012345', true),
  (121, 'Mía', 'Flores', DATE '1991-11-30', '61234567', 'NEGATIVE', 'O', 'FEMALE', 1.64, 56.0, 'mia.flores@email.com', '+5493517123456', true),
  (122, 'Daniel', 'Acosta', DATE '1983-07-14', '62345678', 'POSITIVE', 'B', 'MALE', 1.76, 77.0, 'daniel.acosta@email.com', '+5493517234567', true),
  (123, 'Sofía', 'Pereyra', DATE '1996-01-22', '63456789', 'POSITIVE', 'O', 'FEMALE', 1.69, 61.0, 'sofia.pereyra@email.com', '+5493517345678', true),
  (124, 'Nicolás', 'Giménez', DATE '1985-09-05', '64567890', 'NEGATIVE', 'A', 'MALE', 1.82, 86.0, 'nicolas.gimenez@email.com', '+5493517456789', true),
  (125, 'Martina', 'Ríos', DATE '1990-05-28', '65678901', 'POSITIVE', 'AB', 'FEMALE', 1.68, 59.5, 'martina.rios@email.com', '+5493517567890', true),
  (126, 'Facundo', 'Córdoba', DATE '1988-12-12', '66789012', 'POSITIVE', 'O', 'MALE', 1.79, 81.0, 'facundo.cordoba@email.com', '+5493517678901', true),
  (127, 'Julieta', 'Moreno', DATE '1993-08-07', '67890123', 'NEGATIVE', 'B', 'FEMALE', 1.65, 55.0, 'julieta.moreno@email.com', '+5493517789012', true),
  (128, 'Leonel', 'Paz', DATE '1986-04-20', '68901234', 'POSITIVE', 'A', 'MALE', 1.80, 79.0, 'leonel.paz@email.com', '+5493517890123', true),
  (129, 'Abril', 'Molina', DATE '1997-02-14', '69012345', 'POSITIVE', 'O', 'FEMALE', 1.62, 53.0, 'abril.molina@email.com', '+5493517901234', true);

-- =============================================
-- 5. CAMPAIGNS (5 existentes + 30 nuevas = 35 total)
--    20 finalizadas + 10 activas + 5 existentes
--    Todas con ubicaciones de Córdoba
-- =============================================
INSERT INTO campaigns (id, title, description, start_date, end_date, start_time, direction, latitude, longitude, organizer_id, blood_factor_required, blood_group_required, is_active)
VALUES
  -- Existentes
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
   DATE '2025-09-01', DATE '2025-09-30', TIME '09:00:00', 'Av. Córdoba 1234, CABA', -34.6037, -58.3816, 100, NULL, NULL, false),

  -- ══════════════════════════════════════════
  -- ACTIVAS EN CÓRDOBA (10 campañas)
  -- ══════════════════════════════════════════
  (105, 'Jornada de Donación - Hospital Italiano',
   'Campaña de donación en el Hospital Italiano de Córdoba. Se buscan todos los tipos sanguíneos para abastecer el banco de sangre.',
   DATE '2026-07-10', NULL, TIME '08:00:00', 'Av. General Paz 500, Córdoba', -31.4201, -64.1848, 103, NULL, NULL, true),
  (106, 'Donación en Plaza San Martín',
   'Jornada solidaria en Plaza San Martín. Acercáte con tu documento y ayudá a salvar vidas. ¡Cada donación cuenta!',
   DATE '2026-07-12', NULL, TIME '09:30:00', 'Plaza San Martín, Córdoba', -31.4175, -64.1850, 104, NULL, NULL, true),
  (107, 'Cruz Roja Córdoba - Maratón de Donación',
   'Maratón de donación organizada por Cruz Roja Córdoba. Habrá música, comida y mucho solidaridad.',
   DATE '2026-07-15', NULL, TIME '10:00:00', 'Bv. San Juan 1500, Córdoba', -31.4150, -64.1780, 105, NULL, NULL, true),
  (108, 'Donación en Clínica Privada',
   'Campaña de donación en la Clínica Privada de Córdoba. Se necesitan urgentemente tipos O y A.',
   DATE '2026-07-18', NULL, TIME '08:30:00', 'Belgrano 1200, Córdoba', -31.4230, -64.1810, 106, 'POSITIVE', 'O', true),
  (109, 'Jornada Solidaria - ONG Córdoba',
   'ONG Solidaridad Córdoba organiza una jornada de donación para pacientes oncológicos del hospital público.',
   DATE '2026-07-20', NULL, TIME '09:00:00', 'Obispo Trejo 450, Córdoba', -31.4170, -64.1870, 107, NULL, NULL, true),
  (110, 'Donación en la Facultad de Medicina',
   'Jornada de donación organizada por estudiantes de la Facultad de Medicina de la UNC. ¡Sumate!',
   DATE '2026-07-22', NULL, TIME '10:00:00', 'Av. Haya de la Torre s/n, Córdoba', -31.4225, -64.1890, 108, NULL, NULL, true),
  (111, 'Campaña en el Parque General Bustos',
   'Donación al aire libre en el Parque General Bustos. Traé tu documento y muchas ganas de ayudar.',
   DATE '2026-07-25', NULL, TIME '09:00:00', 'Parque General Bustos, Córdoba', -31.4120, -64.1790, 109, NULL, NULL, true),
  (112, 'Jornada de Donación - Zona Norte Córdoba',
   'Campaña en la zona norte de Córdoba. Se buscan donadores de todos los tipos sanguíneos.',
   DATE '2026-07-28', NULL, TIME '08:00:00', 'Av. Duarte Quirós 2000, Córdoba', -31.4100, -64.1920, 110, NULL, NULL, true),
  (113, 'Donación en el Teatro Rivera Indarte',
   'Jornada de donación en el histórico Teatro Rivera Indarte. Tu donación puede salvar hasta 3 vidas.',
   DATE '2026-08-01', NULL, TIME '10:30:00', 'Av. Vélez Sársfield 531, Córdoba', -31.4185, -64.1935, 111, NULL, NULL, true),
  (114, 'Campaña de Invierno - Centro Córdoba',
   'Campaña de invierno para reposición de bancos de sangre. ¡El invierno es momento de solidaridad!',
   DATE '2026-08-05', NULL, TIME '09:00:00', 'San Martín 850, Córdoba', -31.4195, -64.1815, 112, NULL, NULL, true),

  -- ══════════════════════════════════════════
  -- FINALIZADAS EN CÓRDOBA (20 campañas)
  -- ══════════════════════════════════════════
  (115, 'Campaña Fin de Año 2025 - Hospital Italiano',
   'Última jornada de donación del año en el Hospital Italiano. Gracias a todos los que participaron.',
   DATE '2025-11-15', DATE '2025-11-30', TIME '09:00:00', 'Av. General Paz 500, Córdoba', -31.4201, -64.1848, 103, NULL, NULL, true),
  (116, 'Donación Solidaria - Plaza Italia',
   'Campaña de donación en Plaza Italia. Se recolectaron más de 200 unidades de sangre.',
   DATE '2025-10-01', DATE '2025-10-15', TIME '10:00:00', 'Plaza Italia, Córdoba', -31.4210, -64.1860, 104, NULL, NULL, true),
  (117, 'Cruz Roja - Campaña de Primavera',
   'Campaña de primavera de Cruz Roja Córdoba. El resultado superó las expectativas.',
   DATE '2025-09-20', DATE '2025-10-05', TIME '08:30:00', 'Bv. San Juan 1500, Córdoba', -31.4150, -64.1780, 105, NULL, NULL, true),
  (118, 'Donación en el Hospital de Niños',
   'Campaña de donación para el Hospital de Niños de Córdoba. Se necesitaban tipos O y B.',
   DATE '2025-08-10', DATE '2025-08-25', TIME '09:00:00', 'Av. Loyola 1750, Córdoba', -31.4280, -64.1950, 106, 'POSITIVE', 'O', true),
  (119, 'Maratón de Donación - Universidad Nacional',
   'Maratón de donación organizada por la UNC. Participaron más de 150 donadores.',
   DATE '2025-07-05', DATE '2025-07-20', TIME '10:00:00', 'Av. Haya de la Torre s/n, Córdoba', -31.4225, -64.1890, 107, NULL, NULL, true),
  (120, 'Campaña de Invierno - ONG Solidaridad',
   'Campaña de invierno para pacientes con enfermedades hematológicas.',
   DATE '2025-06-15', DATE '2025-06-30', TIME '09:30:00', 'Obispo Trejo 450, Córdoba', -31.4170, -64.1870, 107, NULL, NULL, true),
  (121, 'Donación en el Parque Sarmiento',
   'Jornada de donación en el Parque Sarmiento. Se contó con la presencia de autoridades locales.',
   DATE '2025-05-10', DATE '2025-05-25', TIME '08:00:00', 'Parque Sarmiento, Córdoba', -31.4300, -64.1800, 108, NULL, NULL, true),
  (122, 'Campaña Solidaria - Barrio Güemes',
   'Donación en el barrio Güemes. La comunidad se volcó masivamente.',
   DATE '2025-04-01', DATE '2025-04-15', TIME '10:00:00', 'Güemes 1200, Córdoba', -31.4160, -64.1835, 109, NULL, NULL, true),
  (123, 'Jornada de Donación - Centro Cívico',
   'Campaña en el Centro Cívico de Córdoba. Se superaron las metas de donación.',
   DATE '2025-03-15', DATE '2025-03-30', TIME '09:00:00', 'Av. General Paz 150, Córdoba', -31.4205, -64.1845, 110, NULL, NULL, true),
  (124, 'Donación en Hospital Escuela',
   'Campaña de donación en el Hospital Escuela de Córdoba. Vital para abastecer las cirugías.',
   DATE '2025-02-20', DATE '2025-03-05', TIME '08:30:00', 'Av. Cáceres de Mones Cazón 1600, Córdoba', -31.4270, -64.1910, 111, NULL, NULL, true),
  (125, 'Campaña de Verano - Playa General Belgrano',
   'Donación en la playa General Belgrano durante el verano. Actividad al aire libre.',
   DATE '2025-01-10', DATE '2025-01-25', TIME '10:00:00', 'Playa General Belgrano, Córdoba', -31.4050, -64.1750, 112, NULL, NULL, true),
  (126, 'Campaña Fin de Año - Cruz Roja',
   'Campaña de fin de año de Cruz Roja Córdoba. Se logró recolectar sangre para más de 300 pacientes.',
   DATE '2024-12-01', DATE '2024-12-15', TIME '09:00:00', 'Bv. San Juan 1500, Córdoba', -31.4150, -64.1780, 105, NULL, NULL, true),
  (127, 'Donación en el Club Atlético Talleres',
   'Campaña de donación en el Club Talleres. La hinchada se sumó masivamente.',
   DATE '2024-11-05', DATE '2024-11-20', TIME '10:00:00', 'Av. Acceso a La Calera s/n, Córdoba', -31.3900, -64.2100, 103, NULL, NULL, true),
  (128, 'Jornada Solidaria - Unicen',
   'Donación en la sede de la Unicen de Córdoba. Participaron docentes y estudiantes.',
   DATE '2024-10-10', DATE '2024-10-25', TIME '09:00:00', 'Av. Vélez Sársfield 1200, Córdoba', -31.4195, -64.1915, 104, NULL, NULL, true),
  (129, 'Campaña de Primavera - Hospital Córdoba',
   'Campaña de primavera para el Hospital de Córdoba. Se abasteció el banco de sangre completo.',
   DATE '2024-09-15', DATE '2024-09-30', TIME '08:00:00', 'Av. Gral. Paz 789, Córdoba', -31.4215, -64.1855, 106, NULL, NULL, true),
  (130, 'Donación en la Rural',
   'Campaña de donación en la Predio Ferial de Córdoba durante la expo agropecuaria.',
   DATE '2024-08-20', DATE '2024-09-05', TIME '10:00:00', 'Predio Ferial, Córdoba', -31.4350, -64.1980, 107, NULL, NULL, true),
  (131, 'Maratón de Donación - La Cumbrecita',
   'Campaña itinerante que llegó a La Cumbrecita. Donación en plena sierra.',
   DATE '2024-07-01', DATE '2024-07-15', TIME '09:00:00', 'La Cumbrecita, Córdoba', -31.9833, -64.5833, 108, NULL, NULL, true),
  (132, 'Campaña de Invierno - Nueva Córdoba',
   'Campaña de invierno en el barrio Nueva Córdoba. Gran participación vecinal.',
   DATE '2024-06-10', DATE '2024-06-25', TIME '09:30:00', 'Av. General Paz 2000, Córdoba', -31.4240, -64.1875, 109, NULL, NULL, true),
  (133, 'Donación en el Parque de las Naciones',
   'Campaña en el Parque de las Naciones Unidas. Actividad familiar con food trucks.',
   DATE '2024-05-01', DATE '2024-05-15', TIME '10:00:00', 'Parque de las Naciones, Córdoba', -31.4000, -64.1700, 110, NULL, NULL, true),
  (134, 'Campaña de Solidaridad - Alta Córdoba',
   'Campaña de donación en el barrio Alta Córdoba. La comunidad organizó rifas y sorteos.',
   DATE '2024-04-05', DATE '2024-04-20', TIME '08:30:00', 'Av. Mario Ceballos 500, Córdoba', -31.4130, -64.1960, 111, NULL, NULL, true);

-- =============================================
-- 6. CAMPAIGN_DONORS (suscripciones variadas)
-- =============================================
INSERT INTO campaign_donors (campaign_id, donor_id)
VALUES
  -- ══ Campañas Activas en Córdoba ══
  -- Camp 105 (Hospital Italiano): 7 suscriptores
  (105, 110), (105, 111), (105, 112), (105, 113), (105, 115), (105, 118), (105, 120),
  -- Camp 106 (Plaza San Martín): 5 suscriptores
  (106, 114), (106, 116), (106, 119), (106, 121), (106, 123),
  -- Camp 107 (Cruz Roja Córdoba): 8 suscriptores
  (107, 110), (107, 112), (107, 114), (107, 117), (107, 118), (107, 122), (107, 125), (107, 126),
  -- Camp 108 (Clínica Privada): 4 suscriptores (requiere O POSITIVE)
  (108, 110), (108, 114), (108, 118), (108, 123),
  -- Camp 109 (ONG Córdoba): 6 suscriptores
  (109, 111), (109, 113), (109, 115), (109, 119), (109, 124), (109, 127),
  -- Camp 110 (Facultad Medicina): 5 suscriptores
  (110, 112), (110, 116), (110, 120), (110, 125), (110, 128),
  -- Camp 111 (Parque Bustos): 4 suscriptores
  (111, 113), (111, 117), (111, 121), (111, 129),
  -- Camp 112 (Zona Norte): 3 suscriptores
  (112, 114), (112, 122), (112, 126),
  -- Camp 113 (Teatro Rivera): 6 suscriptores
  (113, 110), (113, 115), (113, 118), (113, 120), (113, 124), (113, 128),
  -- Camp 114 (Centro Córdoba): 5 suscriptores
  (114, 111), (114, 116), (114, 119), (114, 123), (114, 127),

  -- ══ Campañas Existentes ══
  -- Camp 100 (Buenos Aires): 5 suscriptores
  (100, 100), (100, 101), (100, 103), (100, 105), (100, 107),
  -- Camp 101 (Buenos Aires): 3 suscriptores
  (101, 102), (101, 104), (101, 108),
  -- Camp 102 (Buenos Aires): 4 suscriptores
  (102, 100), (102, 106), (102, 109), (102, 105),
  -- Camp 103 (Finalizada): 4 suscriptores
  (103, 100), (103, 101), (103, 104), (103, 107),

  -- ══ Campañas Finalizadas en Córdoba ══
  -- Camp 115: 6 suscriptores
  (115, 110), (115, 112), (115, 114), (115, 116), (115, 120), (115, 124),
  -- Camp 116: 5 suscriptores
  (116, 111), (116, 113), (116, 117), (116, 121), (116, 125),
  -- Camp 117: 7 suscriptores
  (117, 110), (117, 112), (117, 115), (117, 118), (117, 122), (117, 126), (117, 128),
  -- Camp 118: 4 suscriptores
  (118, 113), (118, 119), (118, 123), (118, 127),
  -- Camp 119: 8 suscriptores
  (119, 110), (119, 111), (119, 112), (119, 113), (119, 114), (119, 115), (119, 120), (119, 125),
  -- Camp 120: 5 suscriptores
  (120, 116), (120, 117), (120, 121), (120, 124), (120, 129),
  -- Camp 121: 6 suscriptores
  (121, 110), (121, 114), (121, 118), (121, 122), (121, 126), (121, 128),
  -- Camp 122: 4 suscriptores
  (122, 111), (122, 115), (122, 119), (122, 123),
  -- Camp 123: 7 suscriptores
  (123, 112), (123, 113), (123, 116), (123, 117), (123, 120), (123, 124), (123, 127),
  -- Camp 124: 5 suscriptores
  (124, 110), (124, 114), (124, 118), (124, 121), (124, 125),
  -- Camp 125: 3 suscriptores
  (125, 111), (125, 119), (125, 129),
  -- Camp 126: 6 suscriptores
  (126, 112), (126, 115), (126, 120), (126, 122), (126, 126), (126, 128),
  -- Camp 127: 5 suscriptores
  (127, 113), (127, 116), (127, 121), (127, 124), (127, 127),
  -- Camp 128: 4 suscriptores
  (128, 110), (128, 114), (128, 118), (128, 123),
  -- Camp 129: 6 suscriptores
  (129, 111), (129, 115), (129, 117), (129, 120), (129, 125), (129, 129),
  -- Camp 130: 5 suscriptores
  (130, 112), (130, 116), (130, 122), (130, 126), (130, 128),
  -- Camp 131: 3 suscriptores
  (131, 113), (131, 119), (131, 127),
  -- Camp 132: 7 suscriptores
  (132, 110), (132, 111), (132, 114), (132, 117), (132, 120), (132, 124), (132, 129),
  -- Camp 133: 4 suscriptores
  (133, 115), (133, 118), (133, 121), (133, 125),
  -- Camp 134: 5 suscriptores
  (134, 110), (134, 112), (134, 116), (134, 122), (134, 128);

-- =============================================
-- 7. USERS (password: password123 hasheado con BCrypt)
-- =============================================
INSERT INTO users (username, password, email, phone, role, role_id, is_active)
VALUES
  -- Existentes
  ('admin',         '$2b$10$I0CooEyskeO0SfAunJakZe8jZ6ZPjmab5hWwPJFdKvvfI/Lkp2R/O', 'admin@bloodo.net',         '+5491100000000', 'ADMIN',     0, true),
  ('fede',          '$2b$10$I0CooEyskeO0SfAunJakZe8jZ6ZPjmab5hWwPJFdKvvfI/Lkp2R/O', 'juan.perez@email.com',     '+5491178901234', 'DONOR',     100, true),
  ('drfede',        '$2b$10$I0CooEyskeO0SfAunJakZe8jZ6ZPjmab5hWwPJFdKvvfI/Lkp2R/O', 'carlos.mendez@gmail.com',  '+5491156789012', 'ORGANIZER', 101, true),
  -- Nuevos Organizadores
  ('hosp_italiano', '$2b$10$I0CooEyskeO0SfAunJakZe8jZ6ZPjmab5hWwPJFdKvvfI/Lkp2R/O', 'contacto@hospitalITALiano.cba.ar', '+5493514567890', 'ORGANIZER', 103, true),
  ('fund_sangre',   '$2b$10$I0CooEyskeO0SfAunJakZe8jZ6ZPjmab5hWwPJFdKvvfI/Lkp2R/O', 'info@fundacion-sangre.cba.ar',     '+5493514678901', 'ORGANIZER', 104, true),
  ('cr_cordoba',    '$2b$10$I0CooEyskeO0SfAunJakZe8jZ6ZPjmab5hWwPJFdKvvfI/Lkp2R/O', 'donaciones@cruzroja-cba.org.ar',   '+5493514789012', 'ORGANIZER', 105, true),
  ('lucia_fer',     '$2b$10$I0CooEyskeO0SfAunJakZe8jZ6ZPjmab5hWwPJFdKvvfI/Lkp2R/O', 'lucia.fernandez@gmail.com',        '+5493515012345', 'ORGANIZER', 108, true),
  ('marcos_lo',     '$2b$10$I0CooEyskeO0SfAunJakZe8jZ6ZPjmab5hWwPJFdKvvfI/Lkp2R/O', 'marcos.lopez@gmail.com',           '+5493515123456', 'ORGANIZER', 109, true),
  -- Nuevos Donadores (usuarios de prueba)
  ('mateo_d',       '$2b$10$I0CooEyskeO0SfAunJakZe8jZ6ZPjmab5hWwPJFdKvvfI/Lkp2R/O', 'mateo.diaz@email.com',              '+5493516012345', 'DONOR',     110, true),
  ('isabella_m',    '$2b$10$I0CooEyskeO0SfAunJakZe8jZ6ZPjmab5hWwPJFdKvvfI/Lkp2R/O', 'isabella.morales@email.com',        '+5493516123456', 'DONOR',     111, true);

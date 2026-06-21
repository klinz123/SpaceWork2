/* ==============================================================================
 * SCRIPT DE INICIALIZACIÓN: ROLES Y USUARIOS BASE
 * ==============================================================================
 * Descripción: Crea los roles y usuarios iniciales (SuperAdmin y Cliente)
 *              necesarios para el arranque y pruebas del sistema SpaceWork.
 * Entorno:     Desarrollo / Testing
 * Proyecto:    SpaceWork - Sistema de Reservas Coworking
 * ============================================================================== */

USE SpaceWork;
GO

/* ------------------------------------------------------------------------------
 * 1. CONFIGURACIÓN DE ROLES
 * ------------------------------------------------------------------------------ */
-- Asegurar que existe el Rol SUPERADMIN (El rol CLIENTE ya existe por defecto)
IF NOT EXISTS (SELECT 1 FROM Usuarios.Roles WHERE NombreRol = 'SUPERADMIN')
BEGIN
    INSERT INTO Usuarios.Roles (NombreRol, Descripcion, Estado) 
    VALUES ('SUPERADMIN', 'Dueño o Gerente General del Sistema', 1);
END
GO

-- Obtener los IDs de los roles para futuras inserciones
DECLARE @RolSuperAdminID INT = (SELECT RolID FROM Usuarios.Roles WHERE NombreRol = 'SUPERADMIN');
DECLARE @RolClienteID INT = (SELECT RolID FROM Usuarios.Roles WHERE NombreRol = 'CLIENTE');

/* ------------------------------------------------------------------------------
 * 2. CONFIGURACIÓN DE PARÁMETROS BASE
 * ------------------------------------------------------------------------------ */
-- Obtener el Tipo de Documento por defecto (DNI) o usar 1 como fallback de seguridad
DECLARE @TipoDocID INT = (SELECT TOP 1 TipoDocumentoID FROM Usuarios.TiposDocumento WHERE Abreviatura = 'DNI');
IF @TipoDocID IS NULL 
BEGIN
    SET @TipoDocID = 1;
END

/* ------------------------------------------------------------------------------
 * 3. CREACIÓN DE USUARIOS DE PRUEBA
 * ------------------------------------------------------------------------------ */
-- 3.1. Insertar Usuario SUPERADMIN (Contraseña por defecto: 123456)
IF NOT EXISTS (SELECT 1 FROM Usuarios.Usuarios WHERE CorreoElectronico = 'superadmin@spacework.com')
BEGIN
    INSERT INTO Usuarios.Usuarios (
        TipoDocumentoID, NumeroDocumento, Nombre, Ap_Paterno, Ap_Materno, 
        CorreoElectronico, Telefono, Contrasena, RolID, FechaRegistro, 
        Estado, IntentosFallidos, Bloqueado
    ) VALUES (
        @TipoDocID, '99999999', 'Super', 'Admin', 'Root', 
        'superadmin@spacework.com', '999999999', 
        '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', -- BCrypt hash para '123456'
        @RolSuperAdminID, GETDATE(), 1, 0, 0
    );
END

-- 3.2. Insertar Usuario CLIENTE (Contraseña por defecto: 123456)
IF NOT EXISTS (SELECT 1 FROM Usuarios.Usuarios WHERE CorreoElectronico = 'cliente@spacework.com')
BEGIN
    INSERT INTO Usuarios.Usuarios (
        TipoDocumentoID, NumeroDocumento, Nombre, Ap_Paterno, Ap_Materno, 
        CorreoElectronico, Telefono, Contrasena, RolID, FechaRegistro, 
        Estado, IntentosFallidos, Bloqueado
    ) VALUES (
        @TipoDocID, '44444444', 'Juan', 'Cliente', 'Prueba', 
        'cliente@spacework.com', '944444444', 
        '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', -- BCrypt hash para '123456'
        @RolClienteID, GETDATE(), 1, 0, 0
    );
END
GO

/* ------------------------------------------------------------------------------
 * RESULTADO
 * ------------------------------------------------------------------------------ */
PRINT '==============================================================================';
PRINT '✅ SCRIPT EJECUTADO CON ÉXITO: Usuarios SUPERADMIN y CLIENTE creados o verificados.';
PRINT '🔑 Credenciales por defecto: Correo del usuario / Contraseña: 123456';
PRINT '==============================================================================';

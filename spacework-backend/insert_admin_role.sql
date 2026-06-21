-- ==========================================
-- SCRIPT DE PRUEBA: Agregar Rol ADMIN
-- ==========================================
USE SpaceWork;
GO

-- 1. Asegurar que existe el Rol ADMINISTRADOR
IF NOT EXISTS (SELECT * FROM Usuarios.Roles WHERE NombreRol = 'ADMIN' OR NombreRol = 'ADMINISTRADOR')
BEGIN
    INSERT INTO Usuarios.Roles (NombreRol, Descripcion, Estado) 
    VALUES ('ADMINISTRADOR', 'Administrador General del Sistema', 1);
END
GO

PRINT 'Rol ADMINISTRADOR asegurado en la BD.';

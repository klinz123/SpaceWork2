# Plan de Mejoras de Seguridad - SpaceWork

> Auditoría realizada el 2026-06-25. Basada en revisión manual del código fuente.

---

## Resumen Ejecutivo

| Métrica | Valor |
|---------|-------|
| Archivos revisados | ~130 (backend Java + frontend React/TS) |
| Líneas revisadas | ~12,550 |
| Hallazgos críticos | 4 |
| Hallazgos altos | 6 |
| Hallazgos medios | 12 |
| Hallazgos bajos | 8 |
| Validaciones faltantes | 6 |

---

## 🔴 CRÍTICOS

### C-01: Almacenamiento inseguro de datos de tarjeta de crédito
**Archivo:** `spacework-backend/.../billing/service/PagoService.java:78-80`
**Archivo:** `spacework-backend/.../billing/model/Pago.java:38-41`

**Problema:** Los datos de tarjeta se reciben como `String` plano en el request y se almacenan como `byte[]` en la columna `VARBINARY(MAX)` sin encriptación real, tokenización ni cumplimiento PCI DSS.

```java
// PagoService.java
String datosTarjeta = (String) body.get("datosTarjeta");
// ...
pago.setDatosTarjeta(datosTarjeta.getBytes()); // Almacenamiento en texto plano
```

**Riesgo:** Violación de PCI DSS. Si la BD es comprometida, los datos de tarjeta están expuestos. Multas regulatorias y responsabilidad legal.

**Solución:** Eliminar el almacenamiento de datos de tarjeta. Implementar un gateway de pagos real (Stripe, Mercado Pago, Culqi) que tokenice la información.

---

### C-02: Contraseña de BD y credenciales SMTP hardcodeadas en `.env.example`
**Archivo:** `spacework-backend/.env.example`

```properties
DB_PASSWORD=Klinzxd
MAIL_USERNAME=pruebas.spacework@gmail.com
MAIL_PASSWORD=password_smtp_app
JWT_SECRET=spaceworkSecretKeyForJWTSecurity2026Token!
```

**Riesgo:** Si este archivo se commitea a un repositorio público (o el `.env` real está en el repo), las credenciales quedan expuestas.

**Solución:**
- Reemplazar valores reales por placeholders (ej: `DB_PASSWORD=${DB_PASSWORD}`)
- Verificar que `.env` esté en `.gitignore`
- Rotar todas las credenciales existentes

---

### C-03: EmpresaController sin protección de acceso
**Archivo:** `spacework-backend/.../crm/controller/EmpresaController.java:22-41`

```java
@GetMapping   // Sin @PreAuthorize
public ResponseEntity<List<Empresa>> listarEmpresas() { ... }
@PostMapping  // Sin @PreAuthorize
public ResponseEntity<Empresa> crearEmpresa(@RequestBody Empresa empresa) { ... }
@PutMapping("/{id}")  // Sin @PreAuthorize
public ResponseEntity<Empresa> actualizarEmpresa(...) { ... }
@DeleteMapping("/{id}")  // Sin @PreAuthorize
public ResponseEntity<Void> eliminarEmpresa(...) { ... }
```

**Riesgo:** Cualquier usuario autenticado (incluso clientes) puede listar, crear, modificar y eliminar empresas.

**Solución:** Agregar `@PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")` a todos los endpoints CRUD de empresas.

---

### C-04: FacturaController sin protección de acceso
**Archivo:** `spacework-backend/.../billing/controller/FacturaController.java:22-25`

```java
@GetMapping  // Sin @PreAuthorize
public ResponseEntity<List<Factura>> listarFacturas() { ... }
```

**Riesgo:** Cualquier usuario autenticado puede listar todas las facturas del sistema, exponiendo datos financieros de todos los clientes.

**Solución:** Agregar `@PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")` o filtrar por usuario autenticado.

---

## 🟠 ALTOS

### A-01: PagoController expone todos los pagos sin filtro
**Archivo:** `spacework-backend/.../billing/controller/PagoController.java:32-43`

**Problema:** GET `/api/pagos` lista todos los pagos sin restricción de rol, solo con `authenticated()`.

**Solución:** Agregar `@PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")`.

---

### A-02: Stack trace expuesto en logs y posible información interna
**Archivo:** `spacework-backend/.../reservations/controller/EspacioController.java:285`

```java
e.printStackTrace(); // En producción revela estructura interna
```

**Solución:** Usar logger en vez de `printStackTrace()`.

---

### A-03: Rate limiting insuficiente
**Archivo:** `spacework-backend/.../core/security/LoginAttemptService.java`

**Problema:** Solo hay rate limiting en login. No hay límites en:
- Registro de usuarios (previene creación masiva de cuentas)
- Cambio de contraseña
- Procesamiento de pagos
- APIs públicas (espacios, ubicaciones)

**Solución:** Implementar rate limiting global con Spring o biblioteca dedicada (Bucket4j, Resilience4j).

---

### A-04: Validación de contraseña inconsistente entre capas
**Archivos múltiples:**

| Capa | Mínimo | Requisitos |
|------|--------|------------|
| `UsuarioRegistroRequestDTO.java` | `@Size(min=6)` | Solo longitud |
| `UsuarioService.java:validarContrasenaFuerte` | 8 chars | Mayúscula, número, especial |
| `Login.tsx` | `minLength={6}` | pattern con mayúscula, número, especial |

**Riesgo:** El DTO acepta contraseñas de 6 caracteres, el servicio exige 8. El frontend usa 6. Discrepancia que puede causar errores confusos.

**Solución:** Unificar en 8 caracteres mínimo en todas las capas y usar `@Pattern` en el DTO con los mismos requisitos del servicio.

---

### A-05: Sin endpoint de logout
**Problema:** No hay endpoint `/api/auth/logout` que invalide la cookie JWT del lado del servidor.

**Solución:** Implementar endpoint que limpie la cookie y, opcionalmente, mantenga una blacklist de tokens invalidados.

---

### A-06: User enumeration por mensajes de error diferentes
**Archivo:** `spacework-backend/.../core/security/AuthController.java:67-71`

```java
// Mensaje diferente para bloqueado vs credenciales incorrectas
"Cuenta bloqueada por seguridad."  // vs
"Credenciales incorrectas."
```

**Solución:** Usar mensaje genérico como "Credenciales inválidas" en ambos casos. Mover el detalle a logs internos.

---

## 🟡 MEDIOS

| ID | Hallazgo | Archivo | Solución |
|----|----------|---------|----------|
| M-01 | Cookie JWT sin `Secure` ni `SameSite` | `AuthController.java:81-84` | Agregar `cookie.setSecure(true)` y `cookie.setAttribute("SameSite", "Strict")` |
| M-02 | CORS demasiado permisivo (headers: `*`) | `SecurityConfig.java:54` | Restringir a headers específicos |
| M-03 | IDs secuenciales expuestos en URLs | Todos los controladores | Usar UUIDs en endpoints públicos o implementar autorización por recurso |
| M-04 | Sin headers de seguridad HTTP | `SecurityConfig.java` | Agregar CSP, HSTS, X-Content-Type-Options, X-Frame-Options |
| M-05 | Registro permite elegir rol (query param) | `Login.tsx:88` / `AuthController.java:43` | Aunque el backend fuerza CLIENTE, el parámetro `rol` es sospechoso. Remover del frontend. |
| M-06 | sessionStorage para datos de usuario | `AuthContext.tsx:30,44` | Usar cookies HttpOnly para sesión (ya hecho), evitar almacenar datos sensibles en sessionStorage |
| M-07 | Sin bloqueo de cuenta persistente en rate limiting de caché | `LoginAttemptService.java` | El bloqueo por 15 min es solo en caché, el de BD es permanente. Unificar. |
| M-08 | Sin logging de eventos de seguridad | Todo el proyecto | Agregar logger para intentos fallidos, cambios de rol, accesos denegados |
| M-09 | Sin política de expiración de contraseñas | `UsuarioService.java` | Agregar campo `fechaExpiracionContrasena` y validar en login |
| M-10 | ModelMapper mapea automáticamente sin DTOs explícitos | Varios controladores | Revisar que ModelMapper no exponga campos sensibles (ej: contraseña hash) |
| M-11 | Parámetro `tipoDoc` expuesto en registro público | `AuthController.java:43` | `@RequestParam(defaultValue = "DNI")` podría ser manipulado |
| M-12 | Servicio de email SMTP con contraseña default | `application.properties:21` | `MAIL_PASSWORD=password_smtp_app` como fallback |

---

## 🟢 BAJOS

| ID | Hallazgo | Archivo | Solución |
|----|----------|---------|----------|
| B-01 | Sin HTTPS configurado | `application.properties` | Agregar `server.ssl.*` properties o configurar en proxy reverso |
| B-02 | Sin Content Security Policy | Frontend | Agregar meta tag CSP o header |
| B-03 | Sin `X-Content-Type-Options: nosniff` | Backend | Configurar en SecurityConfig |
| B-04 | `server.error.include-message=never` correcto pero verificar | `application.properties:24` | ✅ Correcto. Verificar que no se revele en otros endpoints |
| B-05 | Sin límite de intentos en cambio de contraseña | `UsuarioController.java:56` | Agregar rate limiting |
| B-06 | `@Autowired` en campos (no constructor injection) | `UbicacionController.java`, `CaracteristicaController.java` | Usar constructor injection para testabilidad |
| B-07 | Sin validación de tipos de archivo en URLs de fotos | `EspacioRequestDTO.java` | Validar extensión de imagen |
| B-08 | Código de espacio generado con posible colisión | `EspacioController.java:114-128` | Usar secuencia BD en vez de búsqueda manual |

---

## 🧪 VALIDACIONES FALTANTES

| ID | Validación | Archivo | Descripción |
|----|-----------|---------|-------------|
| V-01 | Validar que monto de pago coincida con monto de reserva | `PagoService.java:40-82` | No se verifica que `monto` ≈ `reserva.montoTotal` |
| V-02 | Validar capacidad máxima de espacio en reserva | `ReservaService.java:53-148` | No se valida que asistentes ≤ capacidad del espacio |
| V-03 | Sanitización insuficiente en auditoría (regex parcial) | `AuditoriaAspect.java:34-37` | Los regex pueden no cubrir todos los formatos de datos sensibles |
| V-04 | Validación de superposición de reservas (edge cases) | `ReservaService.java:71-73` | Verificar边界条件: reservas contiguas, horario exacto |
| V-05 | Validación de que el espacio esté disponible (estado) | `ReservaService.java:53-148` | No se verifica `espacio.estadoEspacio` antes de crear reserva |
| V-06 | Validación de email duplicado en actualización de perfil | `UsuarioService.java:124-134` | `actualizarPerfil` no verifica si el nuevo email ya existe |

---

## Plan de Acción Priorizado

### Fase 1 - Inmediata (Semana 1)
1. Eliminar almacenamiento de datos de tarjeta (C-01) - Implementar gateway de pagos real
2. Rotar credenciales y limpiar `.env.example` (C-02)
3. Proteger `EmpresaController` con `@PreAuthorize` (C-03)
4. Proteger `FacturaController` con `@PreAuthorize` (C-04)
5. Proteger `PagoController` GET con `@PreAuthorize` (A-01)

### Fase 2 - Corto Plazo (Semana 2-3)
6. Unificar validación de contraseña en todas las capas (A-04)
7. Implementar endpoint de logout (A-05)
8. Corregir user enumeration en login (A-06)
9. Agregar Secure y SameSite a cookies JWT (M-01)
10. Agregar rate limiting global (A-03)
11. Reemplazar `printStackTrace()` por logger (A-02)

### Fase 3 - Mediano Plazo (Mes 1-2)
12. Endpoints protegidos con validación de propietario del recurso
13. Implementar logging de eventos de seguridad (M-08)
14. Agregar headers de seguridad HTTP (M-04, B-02, B-03)
15. Configurar HTTPS (B-01)
16. Validar monto de pago contra reserva (V-01)
17. Validar capacidad de espacio en reservas (V-02)
18. Mejorar sanitización de auditoría (V-03)

### Fase 4 - Largo Plazo (Mes 2-3)
19. Reemplazar IDs secuenciales por UUIDs (M-03)
20. Implementar política de expiración de contraseñas (M-09)
21. Migrar a constructor injection donde falte (B-06)
22. End-to-end testing de seguridad

---

## Pruebas de Seguridad Recomendadas

- [ ] **SAST** - Ejecutar SonarQube o SpotBugs contra el código
- [ ] **Pruebas de penetración manuales** en endpoints de pago y autenticación
- [ ] **Revisión de dependencias** - `mvn dependency-check` y `npm audit`
- [ ] **Pruebas de rate limiting** - Verificar que límites se apliquen correctamente
- [ ] **Pruebas de autorización** - Verificar que cada endpoint requiera el rol correcto

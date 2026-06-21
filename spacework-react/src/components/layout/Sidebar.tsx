import { NavLink } from 'react-router-dom';

const Sidebar = () => {
  // Obtener rol del usuario
  const usuarioString = sessionStorage.getItem('user');
  const usuarioInfo = usuarioString ? JSON.parse(usuarioString) : {};
  const rol = usuarioInfo.rol?.nombreRol || 'ADMIN';
  const isSuperAdmin = rol === 'SUPERADMIN';



  return (
    <div className="sidebar p-4 d-flex flex-column justify-content-between shadow-sm">
      <div>
        <h3 className="fw-bold text-gradient mb-5 text-center">
          <i className="bi bi-rocket-takeoff-fill me-2"></i>
          SpaceWork ERP
        </h3>

        <div className="d-flex flex-column gap-2">
          <NavLink to="/dashboard" className={({isActive}) => `sidebar-link rounded-3 ${isActive ? 'active' : ''}`}>
            <i className="bi bi-grid-1x2"></i> Dashboard
          </NavLink>
          
          <h6 className="text-muted text-uppercase fw-bold mt-4 mb-2 ms-3" style={{fontSize: '0.75rem'}}>Operaciones</h6>
          
          <NavLink to="/reservas" className={({isActive}) => `sidebar-link rounded-3 ${isActive ? 'active' : ''}`}>
            <i className="bi bi-calendar-check"></i> Reservas
          </NavLink>
          <NavLink to="/espacios" className={({isActive}) => `sidebar-link rounded-3 ${isActive ? 'active' : ''}`}>
            <i className="bi bi-box-seam"></i> Inv. Espacios
          </NavLink>
          
          {/* Sección de Administración solo visible para SUPERADMIN */}
          {isSuperAdmin && (
            <>
              <h6 className="text-muted text-uppercase fw-bold mt-4 mb-2 ms-3" style={{fontSize: '0.75rem'}}>Administración</h6>
              
              <NavLink to="/crm" className={({isActive}) => `sidebar-link rounded-3 ${isActive ? 'active' : ''}`}>
                <i className="bi bi-building"></i> CRM (Empresas)
              </NavLink>
              <NavLink to="/servicios" className={({isActive}) => `sidebar-link rounded-3 ${isActive ? 'active' : ''}`}>
                <i className="bi bi-tools"></i> Serv. Terceros
              </NavLink>
              <NavLink to="/facturacion" className={({isActive}) => `sidebar-link rounded-3 ${isActive ? 'active' : ''}`}>
                <i className="bi bi-receipt"></i> Facturación
              </NavLink>
              <NavLink to="/usuarios" className={({isActive}) => `sidebar-link rounded-3 ${isActive ? 'active' : ''}`}>
                <i className="bi bi-people"></i> Usuarios
              </NavLink>

              <h6 className="text-muted text-uppercase fw-bold mt-4 mb-2 ms-3" style={{fontSize: '0.75rem'}}>Configuración Catálogo</h6>
              <NavLink to="/sedes" className={({isActive}) => `sidebar-link rounded-3 ${isActive ? 'active' : ''}`}>
                <i className="bi bi-geo-alt"></i> Sedes y Mapas
              </NavLink>
              <NavLink to="/caracteristicas" className={({isActive}) => `sidebar-link rounded-3 ${isActive ? 'active' : ''}`}>
                <i className="bi bi-star"></i> Características
              </NavLink>
              
              <h6 className="text-muted text-uppercase fw-bold mt-4 mb-2 ms-3" style={{fontSize: '0.75rem'}}>Seguridad</h6>
              <NavLink to="/auditoria" className={({isActive}) => `sidebar-link rounded-3 ${isActive ? 'active' : ''}`}>
                <i className="bi bi-shield-lock"></i> Auditoría
              </NavLink>
            </>
          )}
        </div>
      </div>

    </div>
  );
};

export default Sidebar;


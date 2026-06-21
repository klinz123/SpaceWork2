import { Outlet, useNavigate } from 'react-router-dom';
import { useState, useEffect } from 'react';
import Sidebar from './Sidebar';
import { useAuth } from '../../context/AuthContext';

const AdminLayout = () => {
  const navigate = useNavigate();
  const { user: usuario, logout } = useAuth();
  const [theme, setTheme] = useState(localStorage.getItem('theme') || 'light');

  const toggleTheme = () => {
    const newTheme = theme === 'light' ? 'dark' : 'light';
    setTheme(newTheme);
    localStorage.setItem('theme', newTheme);
    document.documentElement.setAttribute('data-theme', newTheme);
  };

  useEffect(() => {
    document.documentElement.setAttribute('data-theme', theme);
  }, [theme]);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="app-container">
      <Sidebar />
      <div className="main-content">
        <header className="app-header shadow-sm bg-white">
          <div className="d-flex align-items-center">
            <h5 className="mb-0 fw-semibold text-dark">Portal de Administración ERP</h5>
          </div>
          <div className="d-flex align-items-center gap-3">
            <button 
              onClick={() => navigate('/catalogo')} 
              className="btn btn-outline-primary btn-sm d-flex align-items-center gap-2 fw-semibold px-3 rounded-pill" 
              title="Ir a la Vista Pública"
            >
              <i className="bi bi-shop"></i> Ir al Catálogo
            </button>
            <div className="vr d-none d-sm-block text-muted opacity-25"></div>
            <button onClick={toggleTheme} className="btn btn-light rounded-circle d-flex align-items-center justify-content-center border-0 shadow-sm" style={{ width: '40px', height: '40px' }} title={theme === 'light' ? 'Modo Oscuro' : 'Modo Claro'}>
              {theme === 'light' ? <i className="bi bi-moon-stars-fill text-dark"></i> : <i className="bi bi-sun-fill text-warning"></i>}
            </button>
            <button className="btn btn-light rounded-circle p-2 d-flex align-items-center justify-content-center border-0 shadow-sm" style={{width:'40px', height:'40px'}}>
              <i className="bi bi-bell fs-5 text-muted"></i>
            </button>
            <div className="dropdown">
              <div 
                className="d-flex align-items-center gap-2 cursor-pointer dropdown-toggle" 
                data-bs-toggle="dropdown" 
                aria-expanded="false"
                style={{ cursor: 'pointer' }}
              >
                <div className="bg-primary text-white rounded-circle d-flex align-items-center justify-content-center fw-bold" style={{width:'40px', height:'40px'}}>
                  {usuario?.nombre?.charAt(0) || 'U'}
                </div>
                <div>
                  <p className="mb-0 fw-semibold" style={{fontSize: '0.9rem'}}>{usuario?.nombre || 'Usuario'} {usuario?.apellidoPaterno}</p>
                  <p className="mb-0 text-muted" style={{fontSize: '0.75rem'}}>{usuario?.rol?.nombreRol || 'Rol Desconocido'}</p>
                </div>
              </div>
              <ul className="dropdown-menu dropdown-menu-end shadow border-0 mt-2">
                <li>
                  <button className="dropdown-item py-2" onClick={() => navigate('/configuracion')}>
                    <i className="bi bi-person-circle me-2 text-primary"></i>Mi Perfil
                  </button>
                </li>
                <li><hr className="dropdown-divider" /></li>
                <li>
                  <button className="dropdown-item py-2 text-danger" onClick={handleLogout}>
                    <i className="bi bi-box-arrow-right me-2"></i>Cerrar Sesión
                  </button>
                </li>
              </ul>
            </div>
          </div>
        </header>
        
        {/* Contenido Principal (Rutas anidadas) */}
        <main className="p-4" style={{ backgroundColor: '#f8fafc', minHeight: 'calc(100vh - 70px)' }}>
          <Outlet />
        </main>
      </div>
    </div>
  );
};

export default AdminLayout;


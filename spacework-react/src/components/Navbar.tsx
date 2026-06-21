import React, { useEffect, useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Navbar: React.FC = () => {
  const location = useLocation();
  const { user: usuario, role, logout } = useAuth();
  const [isAdmin, setIsAdmin] = useState(false);
  const [theme, setTheme] = useState(localStorage.getItem('theme') || 'light');

  const toggleTheme = () => {
    const newTheme = theme === 'light' ? 'dark' : 'light';
    setTheme(newTheme);
    localStorage.setItem('theme', newTheme);
    document.documentElement.setAttribute('data-theme', newTheme);
  };

  useEffect(() => {
    if (usuario && role) {
      setIsAdmin(['ADMIN', 'ADMINISTRADOR', 'SUPERADMIN'].includes(role));
    }
    document.documentElement.setAttribute('data-theme', theme);
  }, [location.pathname, theme, usuario, role]);

  // no renderizar el Navbar en la pantalla de login
  if (location.pathname === '/login' || location.pathname === '/') {
    return null;
  }

  const handleLogout = () => {
    logout();
    window.location.href = '/login';
  };

  return (
    <nav className="navbar navbar-expand-lg navbar-light bg-white shadow-sm sticky-top">
      <div className="container">
        <Link className="navbar-brand fw-bold text-gradient" style={{ fontSize: '1.4rem' }} to="/catalogo">
          <i className="bi bi-rocket-takeoff-fill"></i> SpaceWork
        </Link>
        
        <button className="navbar-toggler border-0" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
          <span className="navbar-toggler-icon"></span>
        </button>

        <div className="collapse navbar-collapse" id="navbarNav">
          <ul className="navbar-nav me-auto mb-2 mb-lg-0">
            <li className="nav-item">
              <Link className={`nav-link ${location.pathname.includes('/catalogo') ? 'active fw-bold' : ''}`} to="/catalogo">Catálogo</Link>
            </li>
            {usuario && !isAdmin && (
              <li className="nav-item">
                <Link className={`nav-link ${location.pathname.includes('/mis-reservas') ? 'active fw-bold' : ''}`} to="/mis-reservas">Mis Reservas</Link>
              </li>
            )}
            
            {usuario && isAdmin && (
              <>
                <li className="nav-item">
                  <Link className={`nav-link ${location.pathname.includes('/dashboard') ? 'active fw-bold text-primary-custom' : ''}`} to="/dashboard">Panel Administrativo (ERP)</Link>
                </li>
              </>
            )}
          </ul>

          <div className="d-flex align-items-center gap-3">
            <button onClick={toggleTheme} className="btn btn-light rounded-circle d-flex align-items-center justify-content-center border-0 shadow-sm" style={{ width: '40px', height: '40px' }} title={theme === 'light' ? 'Modo Oscuro' : 'Modo Claro'}>
              {theme === 'light' ? <i className="bi bi-moon-stars-fill text-dark"></i> : <i className="bi bi-sun-fill text-warning"></i>}
            </button>
            
            {!usuario ? (
              <>
                <Link to="/" className="btn btn-outline-secondary rounded-pill fw-bold hover-lift">
                  <i className="bi bi-house me-2"></i>Inicio
                </Link>
                <Link to="/login" className="btn btn-primary-custom rounded-pill px-4 fw-bold shadow-sm hover-lift">
                  Iniciar Sesión
                </Link>
              </>
            ) : (
              <>
                <span className="text-muted small d-none d-lg-block">Hola, {usuario?.nombre}</span>
                <div className="dropdown">
                  <button className="btn btn-light rounded-circle p-0 d-flex justify-content-center align-items-center" type="button" id="profileDropdown" data-bs-toggle="dropdown" aria-expanded="false" style={{ width: '40px', height: '40px', border: '1px solid #e5e7eb' }}>
                    <i className="bi bi-person-fill text-secondary fs-5"></i>
                  </button>
                  <ul className="dropdown-menu dropdown-menu-end shadow border-0 mt-2" aria-labelledby="profileDropdown">
                    <li><Link className="dropdown-item py-2" to="/configuracion"><i className="bi bi-person-circle me-2 text-primary"></i>Mi Perfil</Link></li>
                    <li><hr className="dropdown-divider" /></li>
                    <li><button className="dropdown-item py-2 text-danger" onClick={handleLogout}><i className="bi bi-box-arrow-right me-2"></i>Cerrar Sesión</button></li>
                  </ul>
                </div>
              </>
            )}
          </div>
        </div>
      </div>
      <style>{`
        .navbar .nav-link {
          font-weight: 500;
          color: #4b5563;
        }
        .navbar .nav-link.active {
          color: #0ea5e9 !important;
        }
        .text-primary-custom {
          color: #0ea5e9 !important;
        }
      `}</style>
    </nav>
  );
};

export default Navbar;

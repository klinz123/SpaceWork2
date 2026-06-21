import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

interface ProtectedRouteProps {
  children: React.ReactNode;
  allowedRoles?: string[];
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ children, allowedRoles }) => {
  const { user, role } = useAuth();

  // 1. Si no hay usuario en context, pero sí en sessionStorage (refresh de página), AuthContext se actualizará
  // pero momentáneamente será null. Si estamos seguros de que no hay login en absoluto:
  if (!user && !sessionStorage.getItem('user')) {
    return <Navigate to="/login" replace />;
  }

  // 2. Si el AuthContext todavía está inicializándose y sessionStorage tiene algo, renderizamos nada momentáneamente o dejamos pasar
  const storedUserStr = sessionStorage.getItem('user');
  const currentRole = role || (storedUserStr ? JSON.parse(storedUserStr).rol?.nombreRol : null);

  if (allowedRoles && currentRole && !allowedRoles.includes(currentRole)) {
    // Si no tiene el rol, mandarlo a una página segura para él (ej. catalogo si es cliente, dashboard si es admin)
    return <Navigate to="/catalogo" replace />;
  }

  return <>{children}</>;
};

export default ProtectedRoute;

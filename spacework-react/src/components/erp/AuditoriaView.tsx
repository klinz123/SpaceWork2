import React, { useEffect, useState } from 'react';
import { apiService } from '../../services/api';
import { format } from 'date-fns';

interface RegistroAuditoria {
  id: number;
  usuarioId: number | null;
  accion: string;
  entidad: string;
  ipOrigen: string;
  userAgent: string;
  detalles: string;
  fechaHora: string;
}

const AuditoriaView: React.FC = () => {
  const [registros, setRegistros] = useState<RegistroAuditoria[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    cargarAuditoria();
  }, []);

  const cargarAuditoria = async () => {
    try {
      setLoading(true);
      const response = await apiService.getAuditoria();
      // Asumiendo que el backend nos devuelve una lista, ordenamos por fecha descendente
      const dataOrdenada = response.data.sort((a: any, b: any) => 
        new Date(b.fechaHora).getTime() - new Date(a.fechaHora).getTime()
      );
      setRegistros(dataOrdenada);
      setError('');
    } catch (err: any) {
      console.error(err);
      setError('Error al cargar los registros de auditoría.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container-fluid py-4 animate-fade-in">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="h3 mb-1 text-primary-custom fw-bold">Registro de Auditoría</h2>
          <p className="text-muted mb-0">Monitoreo de acciones y seguridad del sistema</p>
        </div>
        <button onClick={cargarAuditoria} className="btn btn-outline-primary rounded-pill px-4">
          <i className="bi bi-arrow-clockwise me-2"></i>
          Actualizar
        </button>
      </div>

      {error && (
        <div className="alert alert-danger rounded-4 shadow-sm border-0 mb-4" role="alert">
          <i className="bi bi-exclamation-triangle-fill me-2"></i>
          {error}
        </div>
      )}

      <div className="card shadow-sm border-0 rounded-4 overflow-hidden">
        <div className="table-responsive">
          <table className="table table-hover align-middle mb-0">
            <thead className="table-light">
              <tr>
                <th className="px-4 py-3">Fecha/Hora</th>
                <th className="py-3">Usuario ID</th>
                <th className="py-3">Acción</th>
                <th className="py-3">Módulo</th>
                <th className="py-3">Origen (IP)</th>
                <th className="px-4 py-3">Detalles</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr>
                  <td colSpan={6} className="text-center py-5">
                    <div className="spinner-border text-primary-custom" role="status">
                      <span className="visually-hidden">Cargando...</span>
                    </div>
                  </td>
                </tr>
              ) : registros.length === 0 ? (
                <tr>
                  <td colSpan={6} className="text-center py-5 text-muted">
                    No hay registros de auditoría disponibles.
                  </td>
                </tr>
              ) : (
                registros.map((registro) => (
                  <tr key={registro.id}>
                    <td className="px-4 py-3">
                      <div className="fw-semibold">
                        {format(new Date(registro.fechaHora), 'dd/MM/yyyy')}
                      </div>
                      <div className="small text-muted">
                        {format(new Date(registro.fechaHora), 'HH:mm:ss')}
                      </div>
                    </td>
                    <td className="py-3">
                      <span className="badge bg-secondary rounded-pill">
                        {registro.usuarioId ? `#${registro.usuarioId}` : 'Anónimo/Sistema'}
                      </span>
                    </td>
                    <td className="py-3 fw-bold text-dark">{registro.accion}</td>
                    <td className="py-3">{registro.entidad}</td>
                    <td className="py-3">
                      <div className="d-flex align-items-center">
                        <i className="bi bi-globe text-muted me-2"></i>
                        <span className="small">{registro.ipOrigen}</span>
                      </div>
                    </td>
                    <td className="px-4 py-3">
                      <p className="mb-1 small text-truncate" style={{ maxWidth: '250px' }} title={registro.detalles}>
                        {registro.detalles}
                      </p>
                      <small className="text-muted" title={registro.userAgent}>
                        <i className="bi bi-laptop me-1"></i> 
                        {registro.userAgent ? registro.userAgent.substring(0, 30) + '...' : 'N/A'}
                      </small>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default AuditoriaView;

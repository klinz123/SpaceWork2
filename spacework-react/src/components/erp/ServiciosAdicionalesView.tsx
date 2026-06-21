import { useState, useEffect } from 'react';
import { apiService } from '../../services/api';

export default function ServiciosAdicionalesView() {
  const [servicios, setServicios] = useState<any[]>([]);
  const [empresas, setEmpresas] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  
  const [isEditing, setIsEditing] = useState(false);
  const [selectedServicioId, setSelectedServicioId] = useState<number | null>(null);

  const [newServicio, setNewServicio] = useState({
    nombre: '',
    descripcion: '',
    precioBase: 0,
    tipoServicio: 'INTERNO',
    stockTotal: null as number | null,
    proveedor: { id: '' },
    estado: true,
    imagenUrl: '',
    caracteristicasDetalle: '',
    advertenciasDevolucion: ''
  });

  useEffect(() => {
    fetchServicios();
    fetchEmpresas();
  }, []);

  const fetchServicios = async () => {
    try {
      const response = await apiService.getServiciosAdicionales();
      setServicios(Array.isArray(response.data) ? response.data : []);
      setLoading(false);
    } catch (error) {
      console.error('Error fetching servicios:', error);
      setServicios([]);
      setLoading(false);
    }
  };

  const fetchEmpresas = async () => {
    try {
      const response = await apiService.getEmpresas();
      setEmpresas(Array.isArray(response.data) ? response.data : []);
    } catch (error) {
      console.error('Error fetching empresas:', error);
      setEmpresas([]);
    }
  };

  const handleOpenCreateModal = () => {
    setIsEditing(false);
    setSelectedServicioId(null);
    setNewServicio({
      nombre: '',
      descripcion: '',
      precioBase: 0,
      tipoServicio: 'INTERNO',
      stockTotal: null,
      proveedor: { id: '' },
      estado: true,
      imagenUrl: '',
      caracteristicasDetalle: '',
      advertenciasDevolucion: ''
    });
    setShowModal(true);
  };

  const handleEdit = (servicio: any) => {
    setIsEditing(true);
    setSelectedServicioId(servicio.id);
    setNewServicio({
      nombre: servicio.nombre || '',
      descripcion: servicio.descripcion || '',
      precioBase: servicio.precioBase || 0,
      tipoServicio: servicio.tipoServicio || 'INTERNO',
      stockTotal: servicio.stockTotal || null,
      proveedor: servicio.proveedor ? { id: servicio.proveedor.id.toString() } : { id: '' },
      estado: servicio.estado,
      imagenUrl: servicio.imagenUrl || '',
      caracteristicasDetalle: servicio.caracteristicasDetalle || '',
      advertenciasDevolucion: servicio.advertenciasDevolucion || ''
    });
    setShowModal(true);
  };

  const handleDelete = async (id: number) => {
    if (window.confirm('¿Estás seguro de eliminar este servicio?')) {
      try {
        await apiService.deleteServicioAdicional(id);
        fetchServicios();
      } catch (error) {
        console.error('Error deleting servicio:', error);
      }
    }
  };

  const handleSubmit = async () => {
    try {
      const payload = {
        ...newServicio,
        proveedor: newServicio.tipoServicio === 'TERCERO' && newServicio.proveedor.id ? { id: parseInt(newServicio.proveedor.id) } : null,
        stockTotal: newServicio.tipoServicio === 'INTERNO' && newServicio.stockTotal ? parseInt(newServicio.stockTotal as any) : null
      };

      if (isEditing && selectedServicioId) {
        await apiService.updateServicioAdicional(selectedServicioId, payload);
      } else {
        await apiService.crearServicioAdicional(payload);
      }

      setShowModal(false);
      fetchServicios();
    } catch (error) {
      console.error('Error saving servicio:', error);
    }
  };

  const filteredServicios = servicios.filter(s => 
    (s.nombre || '').toLowerCase().includes(searchTerm.toLowerCase()) ||
    (s.proveedor?.razonSocial || '').toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="container-fluid py-4">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="mb-0 fw-bold" style={{ color: '#1a1a1a' }}>Servicios de Terceros</h2>
          <p className="text-muted mb-0">Gestiona los servicios adicionales y sus proveedores.</p>
        </div>
        <button className="btn btn-primary-custom shadow-sm px-4 rounded-pill" onClick={handleOpenCreateModal}>
          <i className="bi bi-plus-lg me-2"></i> Nuevo Servicio
        </button>
      </div>

      <div className="card border-0 shadow-sm rounded-4 overflow-hidden">
        <div className="card-header bg-white border-bottom-0 py-4 px-4">
          <div className="row g-3 align-items-center">
            <div className="col-md-6">
              <div className="input-group">
                <span className="input-group-text bg-light border-0"><i className="bi bi-search text-muted"></i></span>
                <input 
                  type="text" 
                  className="form-control bg-light border-0 shadow-none" 
                  placeholder="Buscar servicio o proveedor..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                />
              </div>
            </div>
          </div>
        </div>

        <div className="table-responsive">
          <table className="table table-hover align-middle mb-0">
            <thead className="bg-light text-muted small text-uppercase">
              <tr>
                <th className="px-4 py-3">Servicio</th>
                <th className="py-3">Descripción</th>
                <th className="py-3">Precio Base</th>
                <th className="py-3">Tipo / Stock</th>
                <th className="py-3">Proveedor</th>
                <th className="py-3">Estado</th>
                <th className="px-4 py-3 text-end">Acciones</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr><td colSpan={6} className="text-center py-4">Cargando servicios...</td></tr>
              ) : (
                filteredServicios.map((srv) => (
                  <tr key={srv.id}>
                    <td className="px-4 py-3 fw-bold text-dark">{srv.nombre}</td>
                    <td className="py-3 text-muted">{srv.descripcion || '-'}</td>
                    <td className="py-3 text-success fw-bold">S/. {srv.precioBase?.toFixed(2) || '0.00'}</td>
                    <td className="py-3">
                      <span className={`badge ${srv.tipoServicio === 'INTERNO' ? 'bg-primary' : 'bg-info'} bg-opacity-10 text-dark`}>
                        {srv.tipoServicio}
                      </span>
                      {srv.tipoServicio === 'INTERNO' && (
                         <div className="small text-muted mt-1">Stock: {srv.stockTotal || 'Ilimitado'}</div>
                      )}
                    </td>
                    <td className="py-3">
                      <div className="fw-semibold text-dark">{srv.proveedor?.razonSocial || (srv.tipoServicio === 'INTERNO' ? 'Propio' : 'Sin Proveedor')}</div>
                      <div className="small text-muted">{srv.proveedor?.documentoFiscal || ''}</div>
                    </td>
                    <td className="py-3">
                      <span className={`badge px-3 py-2 rounded-pill ${srv.estado ? 'bg-success bg-opacity-10 text-success' : 'bg-danger bg-opacity-10 text-danger'}`}>
                        {srv.estado ? 'Activo' : 'Inactivo'}
                      </span>
                    </td>
                    <td className="px-4 py-3 text-end">
                      <button onClick={() => handleEdit(srv)} className="btn btn-sm btn-light rounded-circle p-2 me-2 text-primary" title="Editar"><i className="bi bi-pencil"></i></button>
                      <button onClick={() => handleDelete(srv.id)} className="btn btn-sm btn-light rounded-circle p-2 text-danger" title="Eliminar"><i className="bi bi-trash"></i></button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {showModal && (
        <div className="modal show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
          <div className="modal-dialog modal-dialog-centered modal-lg">
            <div className="modal-content border-0 rounded-4 shadow">
              <div className="modal-header border-bottom-0 py-4 px-5">
                <h5 className="modal-title fw-bold fs-4">{isEditing ? 'Editar Servicio' : 'Nuevo Servicio Adicional'}</h5>
                <button type="button" className="btn-close" onClick={() => setShowModal(false)}></button>
              </div>
              <div className="modal-body px-5 pb-5">
                <div className="row g-4">
                  <div className="col-md-8">
                    <label className="form-label text-muted small fw-bold">Nombre del Servicio</label>
                    <input 
                      type="text" 
                      className="form-control form-control-lg bg-light border-0" 
                      value={newServicio.nombre}
                      onChange={(e) => setNewServicio({...newServicio, nombre: e.target.value})}
                      placeholder="Ej: Catering 10 Personas"
                    />
                  </div>
                  <div className="col-md-4">
                    <label className="form-label text-muted small fw-bold">Precio Base (S/.)</label>
                    <input 
                      type="number" 
                      className="form-control form-control-lg bg-light border-0 text-success fw-bold" 
                      value={newServicio.precioBase}
                      onChange={(e) => setNewServicio({...newServicio, precioBase: parseFloat(e.target.value) || 0})}
                    />
                  </div>
                  <div className="col-md-12">
                    <label className="form-label text-muted small fw-bold">Descripción</label>
                    <textarea 
                      className="form-control bg-light border-0" 
                      rows={3}
                      value={newServicio.descripcion}
                      onChange={(e) => setNewServicio({...newServicio, descripcion: e.target.value})}
                      placeholder="Detalles sobre el servicio..."
                    ></textarea>
                  </div>
                  <div className="col-md-6">
                    <label className="form-label text-muted small fw-bold">Tipo de Servicio</label>
                    <select 
                      className="form-select form-control-lg bg-light border-0"
                      value={newServicio.tipoServicio}
                      onChange={(e) => setNewServicio({...newServicio, tipoServicio: e.target.value, proveedor: { id: '' }})}
                    >
                      <option value="INTERNO">Inventario Propio (Interno)</option>
                      <option value="TERCERO">Servicio de Terceros (Externo)</option>
                    </select>
                  </div>
                  
                  {newServicio.tipoServicio === 'INTERNO' ? (
                    <div className="col-md-6">
                      <label className="form-label text-muted small fw-bold">Stock Máximo Simultáneo</label>
                      <input 
                        type="number" 
                        className="form-control form-control-lg bg-light border-0" 
                        value={newServicio.stockTotal || ''}
                        onChange={(e) => setNewServicio({...newServicio, stockTotal: e.target.value ? parseInt(e.target.value) : null})}
                        placeholder="Dejar vacío si es ilimitado"
                      />
                    </div>
                  ) : (
                    <div className="col-md-6">
                      <label className="form-label text-muted small fw-bold">Proveedor Asociado</label>
                      <select 
                        className="form-select form-control-lg bg-light border-0"
                        value={newServicio.proveedor.id}
                        onChange={(e) => setNewServicio({...newServicio, proveedor: { id: e.target.value }})}
                      >
                        <option value="">-- Seleccionar Empresa --</option>
                        {empresas.filter(emp => emp.estado).map(emp => (
                          <option key={emp.id} value={emp.id}>{emp.razonSocial} ({emp.documentoFiscal})</option>
                        ))}
                      </select>
                    </div>
                  )}

                  <div className="col-md-12 border-top pt-3 mt-4">
                    <h6 className="fw-bold text-dark mb-3"><i className="bi bi-info-circle-fill text-primary me-2"></i>Detalles Extra para el Cliente</h6>
                  </div>
                  <div className="col-md-12">
                    <label className="form-label text-muted small fw-bold">URL de la Imagen (Referencial)</label>
                    <input 
                      type="text" 
                      className="form-control bg-light border-0" 
                      value={newServicio.imagenUrl || ''}
                      onChange={(e) => setNewServicio({...newServicio, imagenUrl: e.target.value})}
                      placeholder="https://ejemplo.com/imagen.jpg"
                    />
                  </div>
                  <div className="col-md-6">
                    <label className="form-label text-muted small fw-bold">Características (Opcional)</label>
                    <textarea 
                      className="form-control bg-light border-0" 
                      rows={4}
                      value={newServicio.caracteristicasDetalle || ''}
                      onChange={(e) => setNewServicio({...newServicio, caracteristicasDetalle: e.target.value})}
                      placeholder="Ej: - Silla Ergonómica \n- Soporte lumbar ajustable"
                    ></textarea>
                  </div>
                  <div className="col-md-6">
                    <label className="form-label text-muted small fw-bold">Advertencias / Devolución (Opcional)</label>
                    <textarea 
                      className="form-control bg-light border-0" 
                      rows={4}
                      value={newServicio.advertenciasDevolucion || ''}
                      onChange={(e) => setNewServicio({...newServicio, advertenciasDevolucion: e.target.value})}
                      placeholder="Ej: Devolver sin manchas o roturas. Penalidad de S/. 50."
                    ></textarea>
                  </div>

                  <div className="col-md-12 d-flex align-items-end mt-4">
                    <div className="form-check form-switch mb-2">
                      <input 
                        className="form-check-input" 
                        type="checkbox" 
                        role="switch" 
                        id="estadoSwitch"
                        checked={newServicio.estado}
                        onChange={(e) => setNewServicio({...newServicio, estado: e.target.checked})}
                      />
                      <label className="form-check-label fw-bold text-muted" htmlFor="estadoSwitch">
                        Servicio Activo
                      </label>
                    </div>
                  </div>
                </div>
              </div>
              <div className="modal-footer border-top-0 px-5 pb-4">
                <button className="btn btn-light px-4 rounded-pill fw-bold" onClick={() => setShowModal(false)}>Cancelar</button>
                <button className="btn btn-primary-custom px-4 rounded-pill fw-bold" onClick={handleSubmit}>Guardar Servicio</button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

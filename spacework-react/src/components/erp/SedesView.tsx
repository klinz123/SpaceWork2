import { useState, useEffect } from 'react';
import { apiService } from '../../services/api';

export default function SedesView() {
  const [sedes, setSedes] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  
  const [isEditing, setIsEditing] = useState(false);
  const [selectedSedeId, setSelectedSedeId] = useState<number | null>(null);

  const initialFormState = {
    nombreUbicacion: '',
    direccion: '',
    ciudad: '',
    pais: '',
    latitud: '',
    longitud: '',
    urlGoogleMaps: '',
    estado: true
  };

  const [newSede, setNewSede] = useState(initialFormState);

  useEffect(() => {
    fetchSedes();
  }, []);

  const fetchSedes = async () => {
    try {
      const response = await apiService.getUbicaciones();
      setSedes(Array.isArray(response.data) ? response.data : []);
    } catch (error) {
      console.error('Error fetching sedes:', error);
      setSedes([]);
    } finally {
      setLoading(false);
    }
  };

  const handleOpenCreateModal = () => {
    setIsEditing(false);
    setSelectedSedeId(null);
    setNewSede(initialFormState);
    setShowModal(true);
  };

  const handleEdit = (sede: any) => {
    setIsEditing(true);
    setSelectedSedeId(sede.id);
    setNewSede({
      nombreUbicacion: sede.nombreUbicacion || '',
      direccion: sede.direccion || '',
      ciudad: sede.ciudad || '',
      pais: sede.pais || '',
      latitud: sede.latitud || '',
      longitud: sede.longitud || '',
      urlGoogleMaps: sede.urlGoogleMaps || '',
      estado: sede.estado
    });
    setShowModal(true);
  };

  const handleDelete = async (id: number) => {
    if (window.confirm('¿Estás seguro de eliminar (desactivar) esta sede?')) {
      try {
        await apiService.deleteUbicacion(id);
        fetchSedes();
      } catch (error) {
        console.error('Error deleting sede:', error);
      }
    }
  };

  const handleSubmit = async () => {
    try {
      const payload = {
        ...newSede,
        latitud: newSede.latitud ? parseFloat(newSede.latitud) : null,
        longitud: newSede.longitud ? parseFloat(newSede.longitud) : null,
      };

      if (isEditing && selectedSedeId) {
        await apiService.updateUbicacion(selectedSedeId, payload);
      } else {
        await apiService.crearUbicacion(payload);
      }
      setShowModal(false);
      fetchSedes();
    } catch (error) {
      console.error('Error saving sede:', error);
      alert('Error al guardar. Asegúrese de que el nombre de la sede no esté duplicado y los campos sean válidos.');
    }
  };

  const filteredSedes = sedes.filter(s => 
    (s.nombreUbicacion || '').toLowerCase().includes(searchTerm.toLowerCase()) ||
    (s.ciudad || '').toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="container-fluid py-4">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="mb-0 fw-bold text-dark">Sedes y Mapas</h2>
          <p className="text-muted mb-0">Gestiona las ubicaciones físicas de tus espacios (Coworking).</p>
        </div>
        <button className="btn btn-primary-custom shadow-sm px-4 rounded-pill" onClick={handleOpenCreateModal}>
          <i className="bi bi-geo-alt-fill me-2"></i> Nueva Sede
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
                  placeholder="Buscar sede por nombre o ciudad..."
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
                <th className="px-4 py-3">Nombre Sede</th>
                <th className="py-3">Ciudad / País</th>
                <th className="py-3">Dirección Exacta</th>
                <th className="py-3">Mapa (Google Maps)</th>
                <th className="py-3">Estado</th>
                <th className="px-4 py-3 text-end">Acciones</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr><td colSpan={6} className="text-center py-4">Cargando sedes...</td></tr>
              ) : filteredSedes.length === 0 ? (
                <tr><td colSpan={6} className="text-center py-5 text-muted">No se encontraron sedes.</td></tr>
              ) : (
                filteredSedes.map((sede) => (
                  <tr key={sede.id}>
                    <td className="px-4 py-3 fw-bold text-dark">{sede.nombreUbicacion}</td>
                    <td className="py-3">
                      <div className="fw-semibold text-dark">{sede.ciudad}</div>
                      <div className="small text-muted">{sede.pais}</div>
                    </td>
                    <td className="py-3 text-muted" style={{ maxWidth: '200px' }}>
                      <span className="d-inline-block text-truncate w-100" title={sede.direccion}>{sede.direccion}</span>
                    </td>
                    <td className="py-3">
                      {sede.urlGoogleMaps ? (
                        <a href={sede.urlGoogleMaps} target="_blank" rel="noopener noreferrer" className="btn btn-sm btn-outline-info rounded-pill px-3">
                          <i className="bi bi-map-fill me-1"></i> Ver Mapa
                        </a>
                      ) : (
                        <span className="text-muted small">Sin Mapa</span>
                      )}
                    </td>
                    <td className="py-3">
                      <span className={`badge px-3 py-2 rounded-pill ${sede.estado ? 'bg-success bg-opacity-10 text-success' : 'bg-danger bg-opacity-10 text-danger'}`}>
                        {sede.estado ? 'Activa' : 'Inactiva'}
                      </span>
                    </td>
                    <td className="px-4 py-3 text-end">
                      <button onClick={() => handleEdit(sede)} className="btn btn-sm btn-light rounded-circle p-2 me-2 text-primary" title="Editar"><i className="bi bi-pencil"></i></button>
                      <button onClick={() => handleDelete(sede.id)} className="btn btn-sm btn-light rounded-circle p-2 text-danger" title="Desactivar"><i className="bi bi-trash"></i></button>
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
                <h5 className="modal-title fw-bold fs-4">{isEditing ? 'Editar Sede' : 'Nueva Sede'}</h5>
                <button type="button" className="btn-close" onClick={() => setShowModal(false)}></button>
              </div>
              <div className="modal-body px-5 pb-5">
                <div className="row g-4">
                  <div className="col-md-12">
                    <label className="form-label text-muted small fw-bold">Nombre de la Sede</label>
                    <input 
                      type="text" 
                      className="form-control form-control-lg bg-light border-0" 
                      value={newSede.nombreUbicacion}
                      onChange={(e) => setNewSede({...newSede, nombreUbicacion: e.target.value})}
                      placeholder="Ej: Sede Miraflores"
                    />
                  </div>
                  
                  <div className="col-md-6">
                    <label className="form-label text-muted small fw-bold">Ciudad</label>
                    <input 
                      type="text" 
                      className="form-control bg-light border-0" 
                      value={newSede.ciudad}
                      onChange={(e) => setNewSede({...newSede, ciudad: e.target.value})}
                      placeholder="Ej: Lima"
                    />
                  </div>
                  <div className="col-md-6">
                    <label className="form-label text-muted small fw-bold">País</label>
                    <input 
                      type="text" 
                      className="form-control bg-light border-0" 
                      value={newSede.pais}
                      onChange={(e) => setNewSede({...newSede, pais: e.target.value})}
                      placeholder="Ej: Perú"
                    />
                  </div>

                  <div className="col-md-12">
                    <label className="form-label text-muted small fw-bold">Dirección Exacta</label>
                    <textarea 
                      className="form-control bg-light border-0" 
                      rows={2}
                      value={newSede.direccion}
                      onChange={(e) => setNewSede({...newSede, direccion: e.target.value})}
                      placeholder="Ej: Av. Pardo 123, Miraflores..."
                    ></textarea>
                  </div>

                  <div className="col-md-6">
                    <label className="form-label text-muted small fw-bold">Latitud (Opcional)</label>
                    <input 
                      type="number" 
                      step="any"
                      className="form-control bg-light border-0" 
                      value={newSede.latitud}
                      onChange={(e) => setNewSede({...newSede, latitud: e.target.value})}
                      placeholder="Ej: -12.1221"
                    />
                  </div>
                  <div className="col-md-6">
                    <label className="form-label text-muted small fw-bold">Longitud (Opcional)</label>
                    <input 
                      type="number" 
                      step="any"
                      className="form-control bg-light border-0" 
                      value={newSede.longitud}
                      onChange={(e) => setNewSede({...newSede, longitud: e.target.value})}
                      placeholder="Ej: -77.0298"
                    />
                  </div>

                  <div className="col-md-12">
                    <label className="form-label text-muted small fw-bold">URL de Google Maps</label>
                    <input 
                      type="text" 
                      className="form-control bg-light border-0" 
                      value={newSede.urlGoogleMaps}
                      onChange={(e) => setNewSede({...newSede, urlGoogleMaps: e.target.value})}
                      placeholder="https://maps.app.goo.gl/..."
                    />
                    <small className="text-muted d-block mt-2">Puedes pegar el enlace para compartir de Google Maps para que los clientes ubiquen la sede.</small>
                  </div>

                  {newSede.urlGoogleMaps && newSede.urlGoogleMaps.includes('<iframe') && (
                    <div className="col-md-12">
                      <div className="p-3 bg-light rounded text-center">
                        <small className="text-danger d-block fw-bold"><i className="bi bi-exclamation-triangle-fill"></i> Nota: Pega un ENLACE DIRECTO (URL), no el código HTML del iframe.</small>
                      </div>
                    </div>
                  )}

                  <div className="col-md-12">
                    <div className="form-check form-switch mt-2">
                      <input 
                        className="form-check-input" 
                        type="checkbox" 
                        role="switch" 
                        id="estadoSede"
                        checked={newSede.estado}
                        onChange={(e) => setNewSede({...newSede, estado: e.target.checked})}
                      />
                      <label className="form-check-label fw-bold text-muted" htmlFor="estadoSede">
                        Sede Activa y Visible
                      </label>
                    </div>
                  </div>
                </div>
              </div>
              <div className="modal-footer border-top-0 px-5 pb-4">
                <button className="btn btn-light px-4 rounded-pill fw-bold" onClick={() => setShowModal(false)}>Cancelar</button>
                <button className="btn btn-primary-custom px-4 rounded-pill fw-bold" onClick={handleSubmit}>Guardar Sede</button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

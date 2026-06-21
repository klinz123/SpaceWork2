import { useState, useEffect } from 'react';
import { apiService } from '../../services/api';

export default function CaracteristicasView() {
  const [caracteristicas, setCaracteristicas] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  
  const [isEditing, setIsEditing] = useState(false);
  const [selectedId, setSelectedId] = useState<number | null>(null);

  const initialFormState = {
    nombreCaracteristica: '',
    descripcion: '',
    tipo: 'AMENITY',
    estado: true
  };

  const [newCaracteristica, setNewCaracteristica] = useState(initialFormState);

  useEffect(() => {
    fetchCaracteristicas();
  }, []);

  const fetchCaracteristicas = async () => {
    try {
      const response = await apiService.getCaracteristicas();
      setCaracteristicas(Array.isArray(response.data) ? response.data : []);
    } catch (error) {
      console.error('Error fetching características:', error);
      setCaracteristicas([]);
    } finally {
      setLoading(false);
    }
  };

  const handleOpenCreateModal = () => {
    setIsEditing(false);
    setSelectedId(null);
    setNewCaracteristica(initialFormState);
    setShowModal(true);
  };

  const handleEdit = (carac: any) => {
    setIsEditing(true);
    setSelectedId(carac.id);
    setNewCaracteristica({
      nombreCaracteristica: carac.nombreCaracteristica || '',
      descripcion: carac.descripcion || '',
      tipo: carac.tipo || 'AMENITY',
      estado: carac.estado
    });
    setShowModal(true);
  };

  const handleDelete = async (id: number) => {
    if (window.confirm('¿Estás seguro de desactivar esta característica? No aparecerá para futuros espacios.')) {
      try {
        await apiService.deleteCaracteristica(id);
        fetchCaracteristicas();
      } catch (error) {
        console.error('Error deleting:', error);
      }
    }
  };

  const handleSubmit = async () => {
    try {
      if (isEditing && selectedId) {
        await apiService.updateCaracteristica(selectedId, newCaracteristica);
      } else {
        await apiService.crearCaracteristica(newCaracteristica);
      }
      setShowModal(false);
      fetchCaracteristicas();
    } catch (error) {
      console.error('Error saving:', error);
      alert('Error al guardar. Verifique que el nombre no esté duplicado.');
    }
  };

  const filtered = caracteristicas.filter(c => 
    (c.nombreCaracteristica || '').toLowerCase().includes(searchTerm.toLowerCase()) ||
    (c.tipo || '').toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="container-fluid py-4">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="mb-0 fw-bold text-dark">Características y Amenities</h2>
          <p className="text-muted mb-0">Gestiona las comodidades disponibles para asignar a los espacios.</p>
        </div>
        <button className="btn btn-primary-custom shadow-sm px-4 rounded-pill" onClick={handleOpenCreateModal}>
          <i className="bi bi-star-fill me-2"></i> Nueva Característica
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
                  placeholder="Buscar por nombre o tipo..."
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
                <th className="px-4 py-3">Nombre</th>
                <th className="py-3">Descripción</th>
                <th className="py-3">Categoría / Tipo</th>
                <th className="py-3">Estado</th>
                <th className="px-4 py-3 text-end">Acciones</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr><td colSpan={5} className="text-center py-4">Cargando...</td></tr>
              ) : filtered.length === 0 ? (
                <tr><td colSpan={5} className="text-center py-5 text-muted">No se encontraron características.</td></tr>
              ) : (
                filtered.map((carac) => (
                  <tr key={carac.id}>
                    <td className="px-4 py-3 fw-bold text-dark">{carac.nombreCaracteristica}</td>
                    <td className="py-3 text-muted">{carac.descripcion || <span className="text-black-50 fst-italic">Sin descripción</span>}</td>
                    <td className="py-3">
                      <span className="badge bg-secondary rounded-pill fw-normal px-3">{carac.tipo}</span>
                    </td>
                    <td className="py-3">
                      <span className={`badge px-3 py-2 rounded-pill ${carac.estado ? 'bg-success bg-opacity-10 text-success' : 'bg-danger bg-opacity-10 text-danger'}`}>
                        {carac.estado ? 'Activo' : 'Inactivo'}
                      </span>
                    </td>
                    <td className="px-4 py-3 text-end">
                      <button onClick={() => handleEdit(carac)} className="btn btn-sm btn-light rounded-circle p-2 me-2 text-primary" title="Editar"><i className="bi bi-pencil"></i></button>
                      <button onClick={() => handleDelete(carac.id)} className="btn btn-sm btn-light rounded-circle p-2 text-danger" title="Desactivar"><i className="bi bi-trash"></i></button>
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
          <div className="modal-dialog modal-dialog-centered">
            <div className="modal-content border-0 rounded-4 shadow">
              <div className="modal-header border-bottom-0 py-4 px-5">
                <h5 className="modal-title fw-bold fs-4">{isEditing ? 'Editar Característica' : 'Nueva Característica'}</h5>
                <button type="button" className="btn-close" onClick={() => setShowModal(false)}></button>
              </div>
              <div className="modal-body px-5 pb-5">
                <div className="row g-4">
                  <div className="col-md-12">
                    <label className="form-label text-muted small fw-bold">Nombre</label>
                    <input 
                      type="text" 
                      className="form-control form-control-lg bg-light border-0" 
                      value={newCaracteristica.nombreCaracteristica}
                      onChange={(e) => setNewCaracteristica({...newCaracteristica, nombreCaracteristica: e.target.value})}
                      placeholder="Ej: Proyector 4K"
                    />
                  </div>

                  <div className="col-md-12">
                    <label className="form-label text-muted small fw-bold">Categoría / Tipo</label>
                    <select 
                      className="form-select bg-light border-0"
                      value={newCaracteristica.tipo}
                      onChange={(e) => setNewCaracteristica({...newCaracteristica, tipo: e.target.value})}
                    >
                      <option value="AMENITY">Comodidad (Amenity)</option>
                      <option value="EQUIPAMIENTO">Equipamiento</option>
                      <option value="SEGURIDAD">Seguridad</option>
                      <option value="ACCESIBILIDAD">Accesibilidad</option>
                      <option value="OTRO">Otro</option>
                    </select>
                  </div>

                  <div className="col-md-12">
                    <label className="form-label text-muted small fw-bold">Descripción (Opcional)</label>
                    <textarea 
                      className="form-control bg-light border-0" 
                      rows={3}
                      value={newCaracteristica.descripcion}
                      onChange={(e) => setNewCaracteristica({...newCaracteristica, descripcion: e.target.value})}
                      placeholder="Breve descripción de la característica..."
                    ></textarea>
                  </div>

                  <div className="col-md-12">
                    <div className="form-check form-switch mt-2">
                      <input 
                        className="form-check-input" 
                        type="checkbox" 
                        role="switch" 
                        id="estadoCarac"
                        checked={newCaracteristica.estado}
                        onChange={(e) => setNewCaracteristica({...newCaracteristica, estado: e.target.checked})}
                      />
                      <label className="form-check-label fw-bold text-muted" htmlFor="estadoCarac">
                        Característica Activa
                      </label>
                    </div>
                  </div>
                </div>
              </div>
              <div className="modal-footer border-top-0 px-5 pb-4">
                <button className="btn btn-light px-4 rounded-pill fw-bold" onClick={() => setShowModal(false)}>Cancelar</button>
                <button className="btn btn-primary-custom px-4 rounded-pill fw-bold" onClick={handleSubmit}>Guardar</button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

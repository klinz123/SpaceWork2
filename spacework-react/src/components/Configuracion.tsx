import React, { useState, useEffect } from 'react';
import { apiService } from '../services/api';

const Configuracion: React.FC = () => {
  const [usuario, setUsuario] = useState<any>(null);


  // states for Profile Editing
  const [editandoPerfil, setEditandoPerfil] = useState(false);
  const [perfilForm, setPerfilForm] = useState({
    nombre: '',
    apellidoPaterno: '',
    apellidoMaterno: '',
    telefono: ''
  });
  const [perfilMensaje, setPerfilMensaje] = useState({ tipo: '', texto: '' });

  // states for Password Changing
  const [cambiandoContrasena, setCambiandoContrasena] = useState(false);
  const [pwdForm, setPwdForm] = useState({
    contrasenaActual: '',
    nuevaContrasena: '',
    confirmarContrasena: ''
  });
  const [pwdMensaje, setPwdMensaje] = useState({ tipo: '', texto: '' });

  useEffect(() => {
    cargarUsuario();
  }, []);

  const cargarUsuario = () => {
    const userStr = sessionStorage.getItem('user');
    if (userStr) {
      const u = JSON.parse(userStr);
      setUsuario(u);

      setPerfilForm({
        nombre: u.nombre || '',
        apellidoPaterno: u.apellidoPaterno || '',
        apellidoMaterno: u.apellidoMaterno || '',
        telefono: u.telefono || ''
      });
    }
  };

  const getNombreCompleto = (): string => {
    if (!usuario) return 'Usuario';
    return `${usuario.nombre} ${usuario.apellidoPaterno} ${usuario.apellidoMaterno || ''}`;
  };

  const getRolLargo = (): string => {
    if (!usuario) return 'Usuario del Sistema';
    return ['ADMIN', 'ADMINISTRADOR', 'SUPERADMIN'].includes(usuario.rol?.nombreRol) ? 'Administrador del Sistema' : 'Usuario del Sistema';
  };

  const getFechaRegistro = (): Date => {
    if (!usuario || !usuario.fechaRegistro) return new Date();
    return new Date(usuario.fechaRegistro);
  };

  const getUltimoAcceso = (): Date | null => {
    if (!usuario || !usuario.ultimoAcceso) return null;
    return new Date(usuario.ultimoAcceso);
  };




  const handlePerfilSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setPerfilMensaje({ tipo: '', texto: '' });
    
    try {
      const res = await apiService.actualizarPerfil(usuario.id, perfilForm);
      // actualizamos sessionStorage con el nuevo usuario
      const updatedUser = { ...usuario, ...res.data };
      sessionStorage.setItem('user', JSON.stringify(updatedUser));
      setUsuario(updatedUser);
      setPerfilMensaje({ tipo: 'success', texto: 'Perfil actualizado correctamente.' });
      setTimeout(() => {
        setEditandoPerfil(false);
        setPerfilMensaje({ tipo: '', texto: '' });
      }, 2000);
    } catch (err: any) {
      setPerfilMensaje({ tipo: 'danger', texto: err.response?.data || 'Error al actualizar el perfil.' });
    }
  };

  const handlePwdSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setPwdMensaje({ tipo: '', texto: '' });

    if (pwdForm.nuevaContrasena !== pwdForm.confirmarContrasena) {
      setPwdMensaje({ tipo: 'danger', texto: 'Las contraseñas nuevas no coinciden.' });
      return;
    }

    try {
      await apiService.cambiarContrasena(usuario.id, pwdForm.contrasenaActual, pwdForm.nuevaContrasena);
      setPwdMensaje({ tipo: 'success', texto: 'Contraseña actualizada correctamente.' });
      setTimeout(() => {
        setCambiandoContrasena(false);
        setPwdForm({ contrasenaActual: '', nuevaContrasena: '', confirmarContrasena: '' });
        setPwdMensaje({ tipo: '', texto: '' });
      }, 2000);
    } catch (err: any) {
      setPwdMensaje({ tipo: 'danger', texto: err.response?.data?.error || 'Error al cambiar la contraseña.' });
    }
  };

  const contactarSoporte = () => {
    alert('Redireccionando al canal de soporte del sistema SpaceWork.');
  };

  return (
    <div className="container py-3 animate-fade-in">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="text-dark fw-bold mb-0">Mi Perfil</h2>
          <p className="text-muted small">Gestiona tu información de cuenta y configuraciones de seguridad</p>
        </div>
      </div>

      <div className="row justify-content-center">
        <div className="col-lg-10">
          
          <div className="card glass-panel p-4 mb-4">
            <div className="d-flex flex-column flex-sm-row align-items-center gap-4">
              <div className="position-relative d-flex justify-content-center align-items-center bg-light rounded-circle shadow-sm border border-4 border-white" style={{ width: '120px', height: '120px' }}>
                <i className="bi bi-person-fill text-secondary" style={{ fontSize: '5rem' }}></i>
              </div>
              <div className="flex-grow-1 text-center text-sm-start">
                <h2 className="text-dark fw-bold mb-1">{getNombreCompleto()}</h2>
                <p className="text-primary fw-medium mb-2">{getRolLargo()}</p>
                <div className="d-flex flex-wrap justify-content-center justify-content-sm-start gap-3 text-muted small">
                  <span><i className="bi bi-calendar3 me-1"></i> Miembro desde: {getFechaRegistro().toLocaleDateString()}</span>
                  {getUltimoAcceso() && (
                    <span><i className="bi bi-clock-history me-1"></i> Último acceso: {getUltimoAcceso()?.toLocaleString()}</span>
                  )}
                  <span className="text-success"><i className="bi bi-patch-check-fill me-1"></i> Verificado</span>
                </div>
              </div>
              <div>
                <button className="btn btn-outline-secondary btn-sm px-3" onClick={() => {
                  setEditandoPerfil(!editandoPerfil);
                  setCambiandoContrasena(false);
                  setPerfilMensaje({ tipo: '', texto: '' });
                }}>
                  {editandoPerfil ? 'Cancelar Edición' : 'Editar Perfil'}
                </button>
              </div>
            </div>
          </div>

          <div className="card glass-panel p-4 mb-4">
            <div className="d-flex justify-content-between align-items-center mb-4 border-bottom pb-2">
              <h5 className="text-dark fw-bold mb-0">Información Personal</h5>
            </div>
            
            {perfilMensaje.texto && (
              <div className={`alert alert-${perfilMensaje.tipo} py-2 small`} role="alert">
                {perfilMensaje.texto}
              </div>
            )}

            {!editandoPerfil ? (
              <div className="row g-3">
                <div className="col-sm-6">
                  <span className="text-muted small d-block text-uppercase fw-semibold">Correo Electrónico</span>
                  <span className="text-dark d-flex align-items-center gap-2 mt-1">
                    <i className="bi bi-envelope text-muted"></i> {usuario?.correoElectronico}
                  </span>
                </div>
                <div className="col-sm-6">
                  <span className="text-muted small d-block text-uppercase fw-semibold">Teléfono</span>
                  <span className="text-dark d-flex align-items-center gap-2 mt-1">
                    <i className="bi bi-telephone text-muted"></i> {usuario?.telefono || 'No registrado'}
                  </span>
                </div>
                <div className="col-sm-6">
                  <span className="text-muted small d-block text-uppercase fw-semibold">Tipo de Documento</span>
                  <span className="text-dark d-flex align-items-center gap-2 mt-1">
                    <i className="bi bi-card-text text-muted"></i> {usuario?.tipoDocumento?.nombreTipo || 'No registrado'}
                  </span>
                </div>
                <div className="col-sm-6">
                  <span className="text-muted small d-block text-uppercase fw-semibold">Número de Documento</span>
                  <span className="text-dark d-flex align-items-center gap-2 mt-1">
                    <i className="bi bi-hash text-muted"></i> {usuario?.numeroDocumento || 'No registrado'}
                  </span>
                </div>
              </div>
            ) : (
              <form onSubmit={handlePerfilSubmit} className="animate-fade-in">
                <div className="row g-3">
                  <div className="col-sm-4">
                    <label className="form-label small text-muted">Nombre</label>
                    <input type="text" className="form-control" value={perfilForm.nombre} onChange={e => setPerfilForm({...perfilForm, nombre: e.target.value})} required />
                  </div>
                  <div className="col-sm-4">
                    <label className="form-label small text-muted">Apellido Paterno</label>
                    <input type="text" className="form-control" value={perfilForm.apellidoPaterno} onChange={e => setPerfilForm({...perfilForm, apellidoPaterno: e.target.value})} required />
                  </div>
                  <div className="col-sm-4">
                    <label className="form-label small text-muted">Apellido Materno</label>
                    <input type="text" className="form-control" value={perfilForm.apellidoMaterno} onChange={e => setPerfilForm({...perfilForm, apellidoMaterno: e.target.value})} required />
                  </div>
                  <div className="col-sm-6">
                    <label className="form-label small text-muted">Teléfono</label>
                    <input type="text" className="form-control" value={perfilForm.telefono} onChange={e => setPerfilForm({...perfilForm, telefono: e.target.value})} />
                  </div>
                  <div className="col-12 mt-4 text-end">
                    <button type="submit" className="btn btn-primary-custom px-4">Guardar Cambios</button>
                  </div>
                </div>
              </form>
            )}
          </div>

          <div className="card glass-panel p-4 mb-4">
            <h5 className="text-dark fw-bold mb-4 border-bottom pb-2">Seguridad</h5>
            
            {pwdMensaje.texto && (
              <div className={`alert alert-${pwdMensaje.tipo} py-2 small`} role="alert">
                {pwdMensaje.texto}
              </div>
            )}

            {!cambiandoContrasena ? (
              <div className="d-flex align-items-center justify-content-between p-3 bg-light rounded-3">
                <div className="d-flex align-items-center gap-3">
                  <div className="p-2 bg-white rounded-circle border text-primary" style={{ width: '40px', height: '40px', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                    <i className="bi bi-key-fill fs-5"></i>
                  </div>
                  <div>
                    <strong className="text-dark d-block">Contraseña</strong>
                    <span className="text-muted small">Cambiar tu clave de acceso</span>
                  </div>
                </div>
                <button className="btn btn-outline-secondary btn-sm" onClick={() => {
                  setCambiandoContrasena(true);
                  setEditandoPerfil(false);
                  setPwdMensaje({ tipo: '', texto: '' });
                }}>
                  Cambiar
                </button>
              </div>
            ) : (
              <form onSubmit={handlePwdSubmit} className="p-3 bg-light rounded-3 animate-fade-in">
                <div className="row g-3">
                  <div className="col-md-12">
                    <label className="form-label small text-muted">Contraseña Actual</label>
                    <input type="password" className="form-control" value={pwdForm.contrasenaActual} onChange={e => setPwdForm({...pwdForm, contrasenaActual: e.target.value})} required />
                  </div>
                  <div className="col-md-6">
                    <label className="form-label small text-muted">Nueva Contraseña</label>
                    <input type="password" className="form-control" value={pwdForm.nuevaContrasena} onChange={e => setPwdForm({...pwdForm, nuevaContrasena: e.target.value})} required minLength={6} />
                  </div>
                  <div className="col-md-6">
                    <label className="form-label small text-muted">Confirmar Nueva Contraseña</label>
                    <input type="password" className="form-control" value={pwdForm.confirmarContrasena} onChange={e => setPwdForm({...pwdForm, confirmarContrasena: e.target.value})} required minLength={6} />
                  </div>
                  <div className="col-12 mt-3 d-flex gap-2 justify-content-end">
                    <button type="button" className="btn btn-outline-secondary btn-sm px-3" onClick={() => setCambiandoContrasena(false)}>Cancelar</button>
                    <button type="submit" className="btn btn-primary-custom btn-sm px-4">Actualizar Contraseña</button>
                  </div>
                </div>
              </form>
            )}
          </div>

          <div className="alert alert-secondary d-flex justify-content-between align-items-center p-3 border-0 rounded-3 shadow-sm" style={{ backgroundColor: '#f8fafc' }}>
            <div className="d-flex align-items-center gap-3">
              <i className="bi bi-headset fs-3 text-primary"></i>
              <div>
                <strong className="text-dark d-block" style={{ fontSize: '0.9rem' }}>¿Necesitas ayuda?</strong>
                <span className="text-muted" style={{ fontSize: '0.8rem' }}>Soporte disponible 24/7</span>
              </div>
            </div>
            <button className="btn btn-outline-primary btn-sm" onClick={contactarSoporte}>Contactar</button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Configuracion;

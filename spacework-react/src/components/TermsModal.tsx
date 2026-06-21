import React, { useState } from 'react';

interface TermsModalProps {
  show: boolean;
  onClose: () => void;
  onAccept: (tipoComprobante: string, documento: string, razonSocial: string, direccion: string) => void;
}

const TermsModal: React.FC<TermsModalProps> = ({ show, onClose, onAccept }) => {
  const [aceptaTerms, setAceptaTerms] = useState(false);
  const [tipoComprobante, setTipoComprobante] = useState('BOLETA');
  const [documento, setDocumento] = useState('');
  const [razonSocial, setRazonSocial] = useState('');
  const [direccion, setDireccion] = useState('');
  const [error, setError] = useState('');

  if (!show) return null;

  const handleAccept = () => {
    if (!aceptaTerms) {
      setError('Debes aceptar los términos y condiciones para continuar.');
      return;
    }
    if (!documento) {
      setError('Debes ingresar tu documento de identidad o RUC.');
      return;
    }
    if (tipoComprobante === 'FACTURA' && !razonSocial) {
      setError('Debes ingresar la Razón Social para la factura.');
      return;
    }
    if (tipoComprobante === 'FACTURA' && !direccion) {
      setError('Debes ingresar la Dirección Fiscal para la factura.');
      return;
    }
    setError('');
    onAccept(tipoComprobante, documento, razonSocial, direccion);
  };

  return (
    <div className="modal-overlay d-flex justify-content-center align-items-center animate-fade-in" style={{ zIndex: 2500 }}>
      <div className="glass-panel p-4 col-md-8 col-lg-5 max-h-90 overflow-auto border-0 position-relative bg-white shadow-lg rounded-4">
        <button className="btn-close-custom btn btn-light rounded-circle position-absolute" style={{ top: '15px', right: '15px' }} onClick={onClose}>
          <i className="bi bi-x-lg"></i>
        </button>
        
        <h4 className="text-primary fw-bold mb-3"><i className="bi bi-shield-check me-2"></i>Términos y Condiciones</h4>
        
        <div className="bg-light p-3 rounded-3 mb-3 text-muted small" style={{ maxHeight: '200px', overflowY: 'auto', border: '1px solid #dee2e6' }}>
          <strong>Políticas de Convivencia y Cancelación</strong><br/><br/>
          1. <strong>Uso del Espacio:</strong> El cliente se compromete a hacer un uso adecuado de las instalaciones, manteniendo un volumen de ruido respetuoso para los demás miembros del coworking.<br/>
          2. <strong>Política de Escritorio Limpio:</strong> Al finalizar su reserva, el espacio debe quedar limpio y desocupado.<br/>
          3. <strong>Tiempos de Espera (Reservas sin pagar):</strong> Las reservas que no sean pagadas en un plazo de <strong>2 horas</strong> serán canceladas automáticamente por el sistema para liberar el espacio.<br/>
          4. <strong>Daños y Perjuicios:</strong> Cualquier daño al mobiliario o equipamiento tecnológico será responsabilidad absoluta del titular de la reserva.<br/>
          5. <strong>Horarios:</strong> El tiempo de gracia en el check-out es de 15 minutos. Superado este tiempo se cobrará la fracción correspondiente o tarifa diaria.
        </div>

        <div className="form-check mb-4">
          <input className="form-check-input" type="checkbox" id="aceptaTerms" checked={aceptaTerms} onChange={(e) => {
            setAceptaTerms(e.target.checked);
            if (e.target.checked) setError('');
          }} />
          <label className="form-check-label fw-bold text-dark" htmlFor="aceptaTerms">
            He leído y acepto los Términos y Condiciones
          </label>
        </div>

        <h5 className="text-dark fw-bold mb-3 border-top pt-3">Datos de Facturación</h5>
        
        <div className="mb-3">
          <label className="form-label text-muted small">Tipo de Comprobante</label>
          <select className="form-select bg-light border-0 py-2" value={tipoComprobante} onChange={(e) => {
            setTipoComprobante(e.target.value);
            setDocumento('');
            setRazonSocial('');
            setDireccion('');
            setError('');
          }}>
            <option value="BOLETA">Boleta de Venta</option>
            <option value="FACTURA">Factura</option>
          </select>
        </div>

        <div className="mb-3">
          <label className="form-label text-muted small">{tipoComprobante === 'FACTURA' ? 'RUC' : 'DNI / Cédula'}</label>
          <input 
            type="text" 
            className="form-control bg-light border-0 py-2" 
            value={documento} 
            onChange={(e) => setDocumento(e.target.value.replace(/\D/g, ''))} 
            maxLength={tipoComprobante === 'FACTURA' ? 11 : 15}
            placeholder={tipoComprobante === 'FACTURA' ? 'Ingrese RUC (11 dígitos)' : 'Ingrese DNI'} 
          />
        </div>

        {tipoComprobante === 'FACTURA' && (
          <>
            <div className="mb-3">
              <label className="form-label text-muted small">Razón Social</label>
              <input type="text" className="form-control bg-light border-0 py-2" value={razonSocial} onChange={(e) => setRazonSocial(e.target.value)} placeholder="Nombre de la empresa" />
            </div>
            <div className="mb-3">
              <label className="form-label text-muted small">Dirección Fiscal</label>
              <input type="text" className="form-control bg-light border-0 py-2" value={direccion} onChange={(e) => setDireccion(e.target.value)} placeholder="Dirección completa" />
            </div>
          </>
        )}

        {error && <div className="alert alert-danger py-2 small mb-3">{error}</div>}

        <div className="d-flex gap-2 mt-4">
          <button className="btn btn-outline-secondary w-50 fw-semibold py-2" onClick={onClose}>Cancelar</button>
          <button className="btn btn-primary w-50 fw-semibold py-2" onClick={handleAccept}>Continuar</button>
        </div>
      </div>
    </div>
  );
};

export default TermsModal;

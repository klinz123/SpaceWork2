const fs = require('fs');

function fixReserva() {
    let content = fs.readFileSync('src/components/Reserva.tsx', 'utf8');

    // Add import
    content = content.replace(/import TermsModal from '\.\/TermsModal';/, "import TermsModal from './TermsModal';\nimport type { Espacio, ServicioAdicional } from '../types';");

    // Replace types
    content = content.replace(/const \[espacio, setEspacio\] = useState<any>\(null\);/, 'const [espacio, setEspacio] = useState<Espacio | null>(null);');
    content = content.replace(/const \[serviciosDisponibles, setServiciosDisponibles\] = useState<any\[\]>\(\[\]\);/, 'const [serviciosDisponibles, setServiciosDisponibles] = useState<ServicioAdicional[]>([]);');
    content = content.replace(/const \[servicioDetalleModal, setServicioDetalleModal\] = useState<any>\(null\);/, 'const [servicioDetalleModal, setServicioDetalleModal] = useState<ServicioAdicional | null>(null);');
    content = content.replace(/const \[datosFacturacion, setDatosFacturacion\] = useState<any>\(null\);/, 'const [datosFacturacion, setDatosFacturacion] = useState<Record<string, string> | null>(null);');

    // Replace map functions
    content = content.replace(/\(e: any\) => e.id === espId/, '(e: Espacio) => e.id === espId');
    content = content.replace(/\(s: any\) => s.estado === true/, '(s: ServicioAdicional) => s.estado === true');

    // Replace crearReservaPendiente
    content = content.replace(/const crearReservaPendiente = async \(facturacion: any\) => {/, 'const crearReservaPendiente = async (facturacion: Record<string, string>) => {');
    
    // Replace err.response
    content = content.replace(/setErrorMessage\(err\.response\?\.data\?\.error/g, 'setErrorMessage((err as any).response?.data?.error');
    content = content.replace(/setErrorMessage\('Reserva creada pero ' \+ \(err\.response\?\.data\?\.error/g, "setErrorMessage('Reserva creada pero ' + ((err as any).response?.data?.error");
    content = content.replace(/catch \(err: any\)/g, 'catch (err: unknown)');

    // Optional chaining for espacio
    content = content.replace(/espacio\.nombreEspacio/g, 'espacio?.nombreEspacio');
    content = content.replace(/espacio\.capacidadEquipos/g, 'espacio?.capacidadEquipos');
    content = content.replace(/espacio\.ubicacion\.ciudad/g, 'espacio?.ubicacion?.ciudad');
    content = content.replace(/espacio\.ubicacion\.pais/g, 'espacio?.ubicacion?.pais');

    fs.writeFileSync('src/components/Reserva.tsx', content);
}

fixReserva();
console.log('Fixed Reserva.tsx');

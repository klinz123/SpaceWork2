const fs = require('fs');

function fixMisReservas() {
    let content = fs.readFileSync('src/components/MisReservas.tsx', 'utf8');
    content = content.replace(/import autoTable from 'jspdf-autotable';/, 
        "import autoTable from 'jspdf-autotable';\nimport { Reserva, Espacio, Ubicacion, ServicioAdicional } from '../types';");
    
    // States
    content = content.replace(/const \[reservas, setReservas\] = useState<any\[\]>\(\[\]\);/, 'const [reservas, setReservas] = useState<Reserva[]>([]);');
    content = content.replace(/const \[reservaSeleccionada, setReservaSeleccionada\] = useState<any>\(null\);/, 'const [reservaSeleccionada, setReservaSeleccionada] = useState<Reserva | null>(null);');
    content = content.replace(/const \[reservaParaResena, setReservaParaResena\] = useState<any>\(null\);/, 'const [reservaParaResena, setReservaParaResena] = useState<Reserva | null>(null);');
    content = content.replace(/const \[detalleReserva, setDetalleReserva\] = useState<any>\(null\);/, 'const [detalleReserva, setDetalleReserva] = useState<Reserva | null>(null);');
    content = content.replace(/const \[sedeSeleccionada, setSedeSeleccionada\] = useState<any>\(null\);/, 'const [sedeSeleccionada, setSedeSeleccionada] = useState<Ubicacion | null>(null);');
    content = content.replace(/const \[reservaParaExtra, setReservaParaExtra\] = useState<any>\(null\);/, 'const [reservaParaExtra, setReservaParaExtra] = useState<Reserva | null>(null);');
    content = content.replace(/const \[serviciosAdicionales, setServiciosAdicionales\] = useState<any\[\]>\(\[\]\);/, 'const [serviciosAdicionales, setServiciosAdicionales] = useState<ServicioAdicional[]>([]);');
    content = content.replace(/const \[reservaParaFacturas, setReservaParaFacturas\] = useState<any>\(null\);/, 'const [reservaParaFacturas, setReservaParaFacturas] = useState<Reserva | null>(null);');

    // Functions
    content = content.replace(/abrirMapa = \(ubicacion: any\)/, 'abrirMapa = (ubicacion: Ubicacion)');
    content = content.replace(/obtenerImagenEspacio = \(espacio: any\)/, 'obtenerImagenEspacio = (espacio: Espacio)');
    content = content.replace(/verDetalle = \(reserva: any\)/, 'verDetalle = (reserva: Reserva)');
    content = content.replace(/abrirModalFacturas = \(r: any\)/, 'abrirModalFacturas = (r: Reserva)');
    content = content.replace(/generarPDF = \(titulo: string, items: any\[\]/, 'generarPDF = (titulo: string, items: Record<string, unknown>[]');
    content = content.replace(/descargarBoletaExtra = \(rs: any, idx: number\)/, 'descargarBoletaExtra = (rs: Record<string, unknown>, idx: number)');
    content = content.replace(/abrirModalPago = \(reserva: any\)/, 'abrirModalPago = (reserva: Reserva)');
    content = content.replace(/abrirModalResena = async \(reserva: any\)/, 'abrirModalResena = async (reserva: Reserva)');
    content = content.replace(/abrirModalExtra = async \(reserva: any\)/, 'abrirModalExtra = async (reserva: Reserva)');
    content = content.replace(/const payload: any\[\] = \[\];/, 'const payload: Record<string, unknown>[] = [];');

    content = content.replace(/\(rs: any\) => acc \+ rs.subtotal/g, '(rs: Record<string, unknown>) => acc + (rs.subtotal as number)');
    content = content.replace(/s: any/g, 's: ServicioAdicional');
    content = content.replace(/catch \(error: any\)/g, 'catch (error: unknown)');
    content = content.replace(/catch \(err: any\)/g, 'catch (err: unknown)');
    content = content.replace(/\(rs: any, idx: number\)/g, '(rs: Record<string, unknown>, idx: number)');
    
    // err.response fix
    content = content.replace(/setErrorMessage\(err\.response\?\.data\?\.error/g, 'setErrorMessage((err as any).response?.data?.error');
    content = content.replace(/setErrorMessage\('Reserva creada pero ' \+ \(err\.response\?\.data\?\.error/g, "setErrorMessage('Reserva creada pero ' + ((err as any).response?.data?.error");

    // doc as any
    content = content.replace(/\(doc as any\)\.lastAutoTable/g, '(doc as { lastAutoTable: { finalY: number } }).lastAutoTable');

    fs.writeFileSync('src/components/MisReservas.tsx', content);
}

function fixPagos() {
    let content = fs.readFileSync('src/components/Pagos.tsx', 'utf8');
    content = content.replace(/import { apiService } from '\.\.\/services\/api';/, "import { apiService } from '../services/api';\nimport { Pago } from '../types';");
    content = content.replace(/useState<any\[\]>\(\[\]\);/, 'useState<Pago[]>([]);');
    content = content.replace(/descargarPDF = \(p: any\)/, 'descargarPDF = (p: Pago)');
    content = content.replace(/\(a: any, b: any\)/g, '(a: Pago, b: Pago)');
    content = content.replace(/\(p: any\)/g, '(p: Pago)');
    content = content.replace(/catch \(err: any\)/g, 'catch (err: unknown)');
    content = content.replace(/\(doc as any\)\.lastAutoTable/g, '(doc as { lastAutoTable: { finalY: number } }).lastAutoTable');
    content = content.replace(/err\.response/g, '(err as any).response');
    fs.writeFileSync('src/components/Pagos.tsx', content);
}

function fixCatalogo() {
    let content = fs.readFileSync('src/components/Catalogo.tsx', 'utf8');
    content = content.replace(/import DOMPurify from 'dompurify';/, "import DOMPurify from 'dompurify';\nimport { Espacio, Ubicacion, TipoEspacio, Caracteristica } from '../types';");
    content = content.replace(/useState<any\[\]>\(\[\]\);/g, 'useState<Espacio[]>([]);');
    // some are not Espacio:
    content = content.replace(/const \[tipos, setTipos\] = useState<Espacio\[\]>\(\[\]\);/, 'const [tipos, setTipos] = useState<TipoEspacio[]>([]);');
    content = content.replace(/const \[ubicaciones, setUbicaciones\] = useState<Espacio\[\]>\(\[\]\);/, 'const [ubicaciones, setUbicaciones] = useState<Ubicacion[]>([]);');
    content = content.replace(/const \[caracteristicas, setCaracteristicas\] = useState<Espacio\[\]>\(\[\]\);/, 'const [caracteristicas, setCaracteristicas] = useState<Caracteristica[]>([]);');
    content = content.replace(/useState<any>\(null\);/, 'useState<Ubicacion | null>(null);'); // sedeSeleccionada
    content = content.replace(/\(t: any\)/g, '(t: TipoEspacio)');
    content = content.replace(/\(u: any\)/g, '(u: Ubicacion)');
    content = content.replace(/\(c: any\)/g, '(c: Caracteristica)');
    content = content.replace(/\(esp: any\)/g, '(esp: Espacio)');
    content = content.replace(/abrirMapa = \(ubicacion: any\)/, 'abrirMapa = (ubicacion: Ubicacion)');
    content = content.replace(/catch \(err: any\)/g, 'catch (err: unknown)');
    fs.writeFileSync('src/components/Catalogo.tsx', content);
}

fixMisReservas();
fixPagos();
fixCatalogo();
console.log('Fixed any typings.');

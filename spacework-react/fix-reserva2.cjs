const fs = require('fs');
let content = fs.readFileSync('src/components/Reserva.tsx', 'utf8');

// Fix the null checks
content = content.replace(/espacio\.descuento/g, 'espacio?.descuento');
content = content.replace(/espacio\.precioPersonaExtra/g, 'espacio?.precioPersonaExtra');

// Fix the disabled boolean checks if they are still using && 
content = content.replace(/disabled=\{cargando \|\| totalCalculado <= 0 \|\| \(espacio && asistentes > espacio\.capacidad\)\}/g, 
    'disabled={cargando || totalCalculado <= 0 || (espacio ? asistentes > espacio.capacidad : false)}');

fs.writeFileSync('src/components/Reserva.tsx', content);

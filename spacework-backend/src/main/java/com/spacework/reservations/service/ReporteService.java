package com.spacework.reservations.service;
import com.spacework.reservations.model.Reserva;
import com.spacework.reservations.repository.ReservaRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ReporteService {

    private final ReservaRepository reservaRepository;

    @Autowired
    public ReporteService(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    public byte[] generarReporteExcel() throws IOException {
        List<Reserva> reservas = reservaRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Reservas SpaceWork");

            // Armamos la primera fila (los títulos de las columnas)
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID Reserva", "Código", "Cliente", "Espacio", "Inicio", "Fin", "Monto Total", "Estado"};

            // Le metemos un poco de diseño a la cabecera 
            CellStyle headerCellStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            for (int col = 0; col < columns.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(columns[col]);
                cell.setCellStyle(headerCellStyle);
            }

            // Poblando el Excel fila por fila (cuidado con la memoria si la BD crece mucho)
            int rowIdx = 1;
            for (Reserva reserva : reservas) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(reserva.getId() != null ? reserva.getId() : 0);
                row.createCell(1).setCellValue(reserva.getCodigoReserva());
                row.createCell(2).setCellValue(reserva.getUsuario().getNombre() + " " + reserva.getUsuario().getApellidoPaterno());
                row.createCell(3).setCellValue(reserva.getEspacio().getNombreEspacio());
                row.createCell(4).setCellValue(reserva.getFechaInicioReserva().toString());
                row.createCell(5).setCellValue(reserva.getFechaFinReserva().toString());
                row.createCell(6).setCellValue(reserva.getMontoTotal().doubleValue());
                row.createCell(7).setCellValue(reserva.getEstadoReserva().getNombreEstado());
            }

            // Magia de POI: autoajustar el ancho de las columnas para que no se vea feo
            for (int col = 0; col < columns.length; col++) {
                sheet.autoSizeColumn(col);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }
}

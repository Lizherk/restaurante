package isa.restaurante.restaurante.service.reporte;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import isa.restaurante.modelo.DetallePedido;
import isa.restaurante.modelo.Pedido;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class GeneradorTicketService {
    
    // Formateadores necesarios
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,##0.00");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void exportar(Pedido pedido, HttpServletResponse response) throws IOException {
        
        // 1. Configuración inicial del documento (simulando un ticket estrecho)
        Document document = new Document(PageSize.A6.rotate()); 
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        
        // --- DEFINICIÓN DE FUENTES ---
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 8);
        Font fontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
        
        // --- TICKET HEADER ---
        document.add(new Paragraph("RESTAURANTE PARRILLA ISA", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        document.add(new Paragraph("TICKET DE VENTA #" + pedido.getId(), fontTitle));
        document.add(new Paragraph("Fecha: " + pedido.getFecha().format(DATE_FORMAT), fontNormal));
        
        // Información del Cliente
        String clienteNombre = (pedido.getCliente().getNombre() + " " + pedido.getCliente().getApellido()).trim();
        document.add(new Paragraph("Cliente: " + clienteNombre, fontNormal));
        
        // Información opcional de Reserva
        if (pedido.getReserva() != null) {
            document.add(new Paragraph("Reserva: #" + pedido.getReserva().getId(), fontNormal));
        }
        document.add(new Paragraph("\n"));
        
        // --- DETALLES DEL PEDIDO (Tabla) ---
        PdfPTable table = new PdfPTable(3); // Cantidad, Producto, Subtotal
        table.setWidths(new int[]{1, 5, 2}); // Proporción de columnas
        table.setWidthPercentage(100);
        table.setSpacingBefore(5);
        
        // Encabezados
        table.addCell(new Phrase("Cant.", fontBold));
        table.addCell(new Phrase("Producto", fontBold));
        table.addCell(new Phrase("Subtotal", fontBold));

        // Filas
        List<DetallePedido> detalles = pedido.getDetalles();
        if (detalles != null) {
            for (DetallePedido detalle : detalles) {
                table.addCell(new Phrase(String.valueOf(detalle.getCantidad()), fontNormal));
                table.addCell(new Phrase(detalle.getProducto().getNombre(), fontNormal));
                table.addCell(new Phrase("$" + DECIMAL_FORMAT.format(detalle.getSubtotal()), fontNormal));
            }
        }
        
        document.add(table);

        // --- TOTAL ---
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("GRACIAS POR SU COMPRA", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8)));
        document.add(new Paragraph("TOTAL: $" + DECIMAL_FORMAT.format(pedido.getTotal()), fontTitle));
        
        document.close();
    }
}
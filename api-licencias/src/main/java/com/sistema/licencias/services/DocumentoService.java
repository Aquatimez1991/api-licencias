package com.sistema.licencias.services;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.sistema.licencias.entities.Licencia;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class DocumentoService {

    public byte[] generarLicenciaPdf(Licencia licencia) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph titulo = new Paragraph("LICENCIA DE FUNCIONAMIENTO", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);
            document.add(Chunk.NEWLINE);

            document.add(new Paragraph("Número de Licencia: " + licencia.getNumeroLicencia()));
            document.add(new Paragraph("Expediente N°: " + licencia.getTramite().getNumeroExpediente()));
            document.add(new Paragraph("Administrado: " + licencia.getTramite().getAdministrado().getNombreCompleto()));
            document.add(new Paragraph("Fecha de Emisión: " + licencia.getFechaEmision()));
            document.add(new Paragraph("Válido hasta: " + licencia.getFechaVencimiento()));
            document.add(Chunk.NEWLINE);

            QRCodeWriter qrCodeWriter = new QRCodeWriter();

            String urlValidacion = "http://192.168.0.239:8080/api/validar/" + licencia.getNumeroLicencia();
            BitMatrix bitMatrix = qrCodeWriter.encode(urlValidacion, BarcodeFormat.QR_CODE, 200, 200);

            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            byte[] qrBytes = pngOutputStream.toByteArray();


            Image qrImage = Image.getInstance(qrBytes);
            qrImage.setAlignment(Element.ALIGN_CENTER);
            document.add(qrImage);

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF de la Licencia", e);
        }
    }
}
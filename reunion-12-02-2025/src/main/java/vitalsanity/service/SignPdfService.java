package vitalsanity.service;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class SignPdfService {

    public byte[] generatePdf(String nombre, String email, String observaciones) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            PdfWriter pdfWriter = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(pdfWriter);
            Document document = new Document(pdfDoc);

            document.add(new Paragraph("DATOS DEL FORMULARIO"));
            document.add(new Paragraph("Nombre: " + nombre));
            document.add(new Paragraph("Email: " + email));
            document.add(new Paragraph("Observaciones: " + observaciones));

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF", e);
        }
    }

}

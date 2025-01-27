package vitalsanity.service;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.stereotype.Service;
import vitalsanity.model.FormData;

@Service
public class SignatureService {

    // Almacén temporal para la DEMO (en memoria)
    private byte[] lastGeneratedPdf;
    private byte[] lastSignedPdf;

    /**
     * Genera un PDF con los datos del formulario.
     */
    public byte[] generatePdf(FormData formData) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);

        try (Document document = new Document(pdfDoc)) {
            document.add(new Paragraph("DATOS DEL FORMULARIO"));
            document.add(new Paragraph("Nombre: " + formData.getName()));
            document.add(new Paragraph("Email: " + formData.getEmail()));
            document.add(new Paragraph(""));
            document.add(new Paragraph("Este PDF será firmado con AutoFirma."));
        }

        this.lastGeneratedPdf = baos.toByteArray();
        return this.lastGeneratedPdf;
    }

    public byte[] getLastGeneratedPdf() {
        return lastGeneratedPdf;
    }

    public void setLastSignedPdf(byte[] signedPdf) {
        this.lastSignedPdf = signedPdf;
    }

    public byte[] getLastSignedPdf() {
        return lastSignedPdf;
    }
}

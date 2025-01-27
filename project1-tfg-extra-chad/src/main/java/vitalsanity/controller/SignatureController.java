package vitalsanity.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vitalsanity.model.FormData;
import vitalsanity.service.SignatureService;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Controller
@RequestMapping("/sign")
public class SignatureController {

    @Autowired
    private SignatureService signatureService;

    /**
     * Mapa en memoria donde almacenamos los PDFs en proceso de firma (por ID).
     * "fileId" -> bytes del PDF (sin firmar o firmado).
     */
    private ConcurrentMap<String, byte[]> documentsMap = new ConcurrentHashMap<>();

    /**
     * Muestra el formulario para introducir datos (nombre, email, etc.).
     */
    @GetMapping
    public String showForm(Model model) {
        model.addAttribute("formData", new FormData());
        return "sign-form"; // Plantilla Thymeleaf
    }

    /**
     * Procesa el formulario, genera un PDF y prepara la trifásica:
     * - Creamos un ID único (máximo 20 caracteres)
     * - Guardamos el PDF en documentsMap
     * - Mostramos la página de resultado con el botón "FIRMAR"
     */
    @PostMapping("/submit")
    public String submitForm(@ModelAttribute FormData formData, Model model) {
        try {
            byte[] pdfBytes = signatureService.generatePdf(formData);

            // Generamos un ID único (máximo 20 caracteres)
            String fileId = generateUniqueId(20);

            // Guardamos el PDF en memoria, sin firmar
            documentsMap.put(fileId, pdfBytes);

            // Obtenemos la IP LAN de la máquina
            String localIp = getLocalIpAddress();

            // Verificamos que la IP no sea localhost o 127.0.0.1
            if (localIp.equals("127.0.0.1") || localIp.equals("0:0:0:0:0:0:0:1")) {
                throw new RuntimeException("No se pudo obtener una IP LAN válida. Asegúrate de que tu máquina está conectada a una red.");
            }

            // Construimos la URL del StorageServlet usando la IP LAN
            String storageServletUrl = "http://" + localIp + ":" + getServerPort() + "/vital-sanity/sign/storage";

            // Pasamos a la vista
            model.addAttribute("fileId", fileId);
            model.addAttribute("fileName", "documento-sin-firma.pdf");
            model.addAttribute("storageServletUrl", storageServletUrl);

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error generando el PDF: " + e.getMessage());
        }
        return "sign-result"; // Plantilla con el botón "FIRMAR"
    }

    /**
     * Servlet trifásico que requiere AutoFirma:
     *   - op=get, fileId=xxx => Devolvemos el PDF sin firmar en Base64
     *   - op=put, fileId=xxx, dat=Base64 => Guardamos el PDF firmado
     *
     * AutoFirma internamente lo llama con GET, pero en otras versiones puede usar POST.
     */
    @RequestMapping("/storage")
    @ResponseBody
    public String storageServlet(
            @RequestParam("op") String operation,
            @RequestParam("fileId") String fileId,
            @RequestParam(name = "dat", required = false) String base64Data) {

        // Verificamos que exista ese fileId en nuestro mapa
        if (!documentsMap.containsKey(fileId)) {
            return "ERR-00: No existe ese fileId en el servidor";
        }

        // OBTENER (op=get): AutoFirma quiere el PDF sin firmar
        if ("get".equalsIgnoreCase(operation)) {
            byte[] unsignedPdf = documentsMap.get(fileId);
            // Devolvemos en Base64
            return Base64.getEncoder().encodeToString(unsignedPdf);
        }
        // GUARDAR (op=put): AutoFirma envía el PDF firmado
        else if ("put".equalsIgnoreCase(operation)) {
            if (base64Data == null || base64Data.isEmpty()) {
                return "ERR-01: Falta el parámetro 'dat' con el PDF firmado";
            }
            // Decodificamos y guardamos
            byte[] signedPdf = Base64.getDecoder().decode(base64Data);
            documentsMap.put(fileId, signedPdf);
            // AutoFirma espera una cadena que NO sea un error para OK
            return "OK";
        }
        else {
            return "ERR-02: Operación no soportada";
        }
    }

    /**
     * Descarga el PDF (firmado o no) desde el servidor.
     * Realmente, si se ha completado la firma, tendrás el PDF firmado.
     */
    @GetMapping("/download")
    @ResponseBody
    public byte[] download(@RequestParam("fileId") String fileId) {
        return documentsMap.getOrDefault(fileId, null);
    }

    /**
     * Genera un ID único alfanumérico de longitud especificada.
     *
     * @param length Longitud del ID a generar.
     * @return Cadena alfanumérica única.
     */
    private String generateUniqueId(int length) {
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for(int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    /**
     * Obtiene la dirección IP LAN de la máquina.
     *
     * @return IP LAN como String.
     */
    private String getLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
            while(nics.hasMoreElements()) {
                NetworkInterface ni = nics.nextElement();
                if (ni.isUp() && !ni.isLoopback()) {
                    Enumeration<InetAddress> addresses = ni.getInetAddresses();
                    while(addresses.hasMoreElements()) {
                        InetAddress addr = addresses.nextElement();
                        if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                            return addr.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "127.0.0.1"; // Fallback
    }

    /**
     * Obtiene el puerto en el que está corriendo el servidor.
     *
     * @return Puerto como int.
     */
    private int getServerPort() {
        // Por simplicidad, devuelve 8058 como en application.properties
        // Para un manejo dinámico, podrías inyectar el puerto usando @Value
        return 8058;
    }
}

package vitalsanity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.out.println("Aplicacion ejecutada correctamente y corriendo en: http://localhost:8002/vital-sanity/login");
    }
}

package vitalsanity.initializer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
@Profile("docker")  // Only run when the 'docker' profile is active
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        // Load the SQL file from the classpath
        ClassPathResource resource = new ClassPathResource("sql/database-script/seed-develop-db.sql");

        // Read the file content into a string using InputStream
        try (InputStream inputStream = resource.getInputStream()) {
            String sql = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            // Execute the SQL script
            jdbcTemplate.execute(sql);
            System.out.println("SQL script executed successfully!");
        } catch (IOException e) {
            System.err.println("Failed to read the SQL script: " + e.getMessage());
        }
    }
}


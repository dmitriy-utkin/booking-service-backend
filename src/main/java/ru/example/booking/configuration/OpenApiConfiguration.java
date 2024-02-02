package ru.example.booking.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI openApiDescription() {

        Server server = new Server();
        server.setUrl("http://localhost:8080");
        server.setDescription("Local host server");

        Contact devContacts = new Contact();
        devContacts.setEmail("dm.utkin@icloud.com");
        devContacts.setName("Dmitriy UTKIN");
        devContacts.setUrl("https://github.com/dmitriy-utkin");

        Info info = new Info();
        info.setTitle("Spring course final project: backend server for booking management");
        info.setVersion("1.0.0");
        info.setContact(devContacts);
        info.setDescription("""
                Booking service for manage hotel operation work:
                -> Manger can create a hotel, rooms
                -> Users can find the rooms by needed parameters and reserve the exact room
                """);

        return new OpenAPI().servers(List.of(server)).info(info);
    }
}

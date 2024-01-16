package ru.example.booking.abstracts;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class HotelAbstractTest extends AbstractMainTest {

    @BeforeEach
    public void beforeEach() {

        try (Connection connection = DriverManager.getConnection(postgreSQLContainer.getJdbcUrl(),
                postgreSQLContainer.getUsername(),
                postgreSQLContainer.getPassword())) {
            String resetSql = "ALTER SEQUENCE booking_schema.hotels_id_seq RESTART WITH 1;";
            try (Statement statement = connection.createStatement()) {
                statement.execute(resetSql);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        for (int i = 1; i <= 5; i++) {
            hotelService.save(createDefaultHotel(i));
        }
    }

    @AfterEach
    public void afterEach() {
        hotelRepository.deleteAll();
    }

}

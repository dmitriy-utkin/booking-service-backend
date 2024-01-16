package ru.example.booking.abstracts;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.example.booking.mapper.HotelMapper;
import ru.example.booking.model.Hotel;
import ru.example.booking.repository.HotelRepository;
import ru.example.booking.service.HotelService;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@Testcontainers
public class AbstractMainTest {

    @Autowired
    protected HotelRepository hotelRepository;

    @Autowired
    protected HotelService hotelService;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext context;

    @Autowired
    protected HotelMapper hotelMapper;

    @Autowired
    protected ObjectMapper objectMapper;

    protected static PostgreSQLContainer postgreSQLContainer;

    static {
        DockerImageName psqlImage = DockerImageName.parse("postgres:12.3-alpine");
        postgreSQLContainer = (PostgreSQLContainer) new PostgreSQLContainer(psqlImage).withReuse(true);
        postgreSQLContainer.start();
    }

    @DynamicPropertySource
    public static void register(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    protected Hotel createDefaultHotel(int hotelNum) {
        return Hotel.builder()
                .name("Hotel " + hotelNum)
                .headline("Hotel headline " + hotelNum)
                .city("Hotel city location " + hotelNum)
                .address("Hotel address " + hotelNum)
                .distance((float) hotelNum)
                .rating((float) hotelNum)
                .numberOfRatings(hotelNum)
                .build();
    }

    protected List<Hotel> createDefaultHotelList(int count) {
        List<Hotel> result = new ArrayList<>(count);
        for (int i = 1; i <= count; i++) {
            result.add(createDefaultHotel(i));
        }
        return result;
    }

}

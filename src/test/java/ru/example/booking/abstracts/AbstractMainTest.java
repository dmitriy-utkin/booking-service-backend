package ru.example.booking.abstracts;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.Arguments;
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
import ru.example.booking.mapper.RoomMapper;
import ru.example.booking.model.Hotel;
import ru.example.booking.model.Room;
import ru.example.booking.model.RoomDescription;
import ru.example.booking.repository.HotelRepository;
import ru.example.booking.repository.RoomRepository;
import ru.example.booking.service.HotelService;
import ru.example.booking.service.RoomService;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

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
    protected RoomService roomService;

    @Autowired
    protected RoomMapper roomMapper;

    @Autowired
    protected RoomRepository roomRepository;

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

    @AfterEach
    public void afterEach() {
        roomRepository.deleteAll();
        hotelRepository.deleteAll();
    }

    private static Stream<Arguments> invalidInputStringsTwoValues() {
        return Stream.of(
                Arguments.of(RandomString.make(1)),
                Arguments.of(RandomString.make(161))
        );
    }

    protected Hotel createDefaultHotel(int hotelNum) {
        return Hotel.builder()
                .id((long) hotelNum)
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

    protected List<Room> createDefaultRoomList(boolean withHotelSaving) {
        return List.of(
                createStandardRoomWithoutBookedDates(1, withHotelSaving),
                createStandardRoomWithoutBookedDates(2, withHotelSaving),
                createPresidentRoomWithoutBookedDates(3, withHotelSaving),
                createSuperiorRoomWithoutBookedDates(4, withHotelSaving),
                createSuiteRoomWithoutBookedDates(5, withHotelSaving)
        );
    }

    protected Room createStandardRoomWithoutBookedDates(int roomNum, boolean withHotelSaving) {
        return createDefaultRoomWithoutBookedDates(roomNum, RoomDescription.STANDARD, withHotelSaving);
    }

    protected Room createPresidentRoomWithoutBookedDates(int roomNum, boolean withHotelSaving) {
        return createDefaultRoomWithoutBookedDates(roomNum, RoomDescription.PRESIDENT, withHotelSaving);
    }

    protected Room createSuperiorRoomWithoutBookedDates(int roomNum, boolean withHotelSaving) {
        return createDefaultRoomWithoutBookedDates(roomNum, RoomDescription.SUPERIOR, withHotelSaving);
    }

    protected Room createSuiteRoomWithoutBookedDates(int roomNum, boolean withHotelSaving) {
        return createDefaultRoomWithoutBookedDates(roomNum, RoomDescription.SUITE, withHotelSaving);
    }

    protected Room createDefaultRoomWithoutBookedDates(int roomNum, RoomDescription description, boolean withHotelSaving) {

        Hotel hotel = withHotelSaving ? hotelService.save(createDefaultHotel(roomNum)) : createDefaultHotel(roomNum);

        return Room.builder()
                .id((long) roomNum)
                .name("Room " + roomNum)
                .description(description)
                .number(roomNum)
                .price(BigDecimal.valueOf(roomNum))
                .capacity(roomNum)
                .hotel(hotel)
                .bookedDates(new HashSet<>())
                .build();
    }

    protected void resetSequence() {
        try (Connection connection = DriverManager.getConnection(postgreSQLContainer.getJdbcUrl(),
                postgreSQLContainer.getUsername(),
                postgreSQLContainer.getPassword())) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("ALTER SEQUENCE booking_schema.rooms_id_seq RESTART WITH 1;");
                statement.execute("ALTER SEQUENCE booking_schema.hotels_id_seq RESTART WITH 1;");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}

package ru.example.booking.abstracts;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.example.booking.controller.ReservationController;
import ru.example.booking.dao.postrgres.*;
import ru.example.booking.dto.defaults.ErrorResponse;
import ru.example.booking.dto.reservation.UpsertReservationRequest;
import ru.example.booking.mapper.HotelMapper;
import ru.example.booking.mapper.ReservationMapper;
import ru.example.booking.mapper.RoomMapper;
import ru.example.booking.mapper.UserMapper;
import ru.example.booking.repository.mongo.StatisticReservationRepository;
import ru.example.booking.repository.mongo.StatisticUserRepository;
import ru.example.booking.repository.postgres.HotelRepository;
import ru.example.booking.repository.postgres.ReservationRepository;
import ru.example.booking.repository.postgres.RoomRepository;
import ru.example.booking.repository.postgres.UserRepository;
import ru.example.booking.service.*;
import ru.example.booking.util.LocalDatesUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@Testcontainers
public class AbstractMainTest {

    protected static final String DATE_PATTERN = "dd/MM/yyyy";

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
    protected UserService userService;

    @Autowired
    protected ValidationService validationService;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected UserMapper userMapper;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected HotelMapper hotelMapper;

    @Autowired
    protected ReservationRepository reservationRepository;

    @Autowired
    protected ReservationService reservationService;

    @Autowired
    protected ReservationController reservationController;

    @Autowired
    protected ReservationMapper reservationMapper;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected StatisticService statisticService;

    @Autowired
    protected StatisticUserRepository statisticUserRepository;

    @Autowired
    protected StatisticReservationRepository statisticReservationRepository;

    @Container
    protected static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0.8")
            .withReuse(true);

    protected static PostgreSQLContainer postgreSQLContainer;

    static {
        DockerImageName psqlImage = DockerImageName.parse("postgres:12.3-alpine");
        postgreSQLContainer = (PostgreSQLContainer) new PostgreSQLContainer(psqlImage).withReuse(true);
        postgreSQLContainer.start();
    }

    @Container
    protected static final KafkaContainer kafkaContainer = new KafkaContainer(
        DockerImageName.parse("confluentinc/cp-kafka:7.3.3")
    ).withReuse(true).withEmbeddedZookeeper();

    @DynamicPropertySource
    public static void register(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);

        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);

        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);

        registry.add("app.dateFormat", () -> DATE_PATTERN);
    }

    @AfterEach
    public void afterEach() {
        roomRepository.deleteAll();
        hotelRepository.deleteAll();
        userRepository.deleteAll();
    }

    private static Stream<Arguments> invalidInputStringsTwoValues() {
        return Stream.of(
                Arguments.of(RandomString.make(1)),
                Arguments.of(RandomString.make(161))
        );
    }

    protected List<User> createDefaultUserList(int roleUserCount, int roleAdminCount) {
        int count = 1;
        List<User> users = new ArrayList<>();
        for (int i = 1; i <= roleUserCount; i++) {
            users.add(createUserWithUserRole(count));
            count++;
        }
        for (int i = 1; i <= roleAdminCount; i++) {
            users.add(createUserWithAdminRole(count));
            count++;
        }
        return users;
    }

    protected User createUserWithUserRole(int userNum) {
        return createDefaultUser(userNum, RoleType.ROLE_USER);
    }

    protected User createUserWithAdminRole(int userNum) {
        return createDefaultUser(userNum, RoleType.ROLE_ADMIN);
    }

    protected User createDefaultUser(int userNum, RoleType role) {
        return User.builder()
                .id((long) userNum)
                .username("user" + userNum)
                .email("user" + userNum + "@email.com")
                .password("pass")
                .roles(Set.of(role))
                .build();
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

    protected List<Room> createDefaultRoomListWithBookedDates(boolean withHotelSaving) {
        var rooms = List.of(
                createStandardRoomWithoutBookedDates(1, withHotelSaving),
                createStandardRoomWithoutBookedDates(2, withHotelSaving),
                createPresidentRoomWithoutBookedDates(3, withHotelSaving),
                createSuperiorRoomWithoutBookedDates(4, withHotelSaving),
                createSuiteRoomWithoutBookedDates(5, withHotelSaving)
        );

        rooms.forEach(room -> {
            var reservation = Reservation.builder()
                    .id(room.getId())
                    .checkInDate(LocalDate.now())
                    .checkOutDate(LocalDate.now().plusDays(room.getId()))
                    .user(createDefaultUser(1, RoleType.ROLE_USER))
                    .build();
            room.setReservations(List.of(reservation));
            room.setBookedDates(roomService.getDateList(reservation.getCheckInDate(), reservation.getCheckOutDate()));
        });
        return rooms;
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

        Hotel hotel = withHotelSaving ? hotelRepository.save(createDefaultHotel(roomNum)) : createDefaultHotel(roomNum);

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
                statement.execute("ALTER SEQUENCE booking_schema.users_id_seq RESTART WITH 1;");
                statement.execute("ALTER SEQUENCE booking_schema.reservations_id_seq RESTART WITH 1;");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected Room createDefaultRoomWithBookingDatesTodayAndTomorrow(RoomDescription description) {
        int roomNum = (int) (roomRepository.count() + 1);
        var room = createDefaultRoomWithoutBookedDates(roomNum, description, true);
        Set<LocalDate> dates = Set.of(
                LocalDate.now(),
                LocalDate.now().plusDays(1)
        );

        room.setBookedDates(dates);
        return room;
    }

    protected List<Reservation> createDefaultReservationListWithStepByCounter(int count, boolean withHotelSaving) {
        List<Reservation> reservations = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            reservations.add(
                    createDefaultReservationWithRoomHotelUser(i,
                            LocalDate.now(),
                            LocalDate.now().plusDays(i),
                            withHotelSaving)
            );
        }
        return reservations;
    }

    protected Reservation createDefaultReservationWithRoomHotelUser(int num,
                                                                    LocalDate from,
                                                                    LocalDate to,
                                                                    boolean withHotelSaving) {
        var room = roomRepository.save(
                createDefaultRoomWithoutBookedDates(num, RoomDescription.STANDARD, withHotelSaving)
        );
        User user = num == 5 ? userRepository.save(createDefaultUser(num, RoleType.ROLE_ADMIN)) :
                userRepository.save(createDefaultUser(num, RoleType.ROLE_USER));
        return createDefaultReservation(num, room, user, from, to);
    }

    protected Reservation createDefaultReservation(int num, Room room, User user, LocalDate from, LocalDate to) {
        return Reservation.builder()
                .id((long) num)
                .user(user)
                .room(room)
                .checkInDate(from)
                .checkOutDate(to)
                .build();
    }

    protected ErrorResponse getAccessErrorResponse() {
        return new ErrorResponse("Access denied, please contact administrator");
    }

    protected UpsertReservationRequest createUpsertReservationRequest(Long roomId, LocalDate from, LocalDate to) {
        return UpsertReservationRequest.builder()
                .roomId(roomId)
                .checkInDate(LocalDatesUtil.localDateToStr(from, DATE_PATTERN))
                .checkOutDate(LocalDatesUtil.localDateToStr(to, DATE_PATTERN))
                .build();
    }

}

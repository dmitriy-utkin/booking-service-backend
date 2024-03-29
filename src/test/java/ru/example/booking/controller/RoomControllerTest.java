package ru.example.booking.controller;

import net.javacrumbs.jsonunit.JsonAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import ru.example.booking.abstracts.RoomAbstractTest;
import ru.example.booking.dao.postrgres.RoleType;
import ru.example.booking.dao.postrgres.Room;
import ru.example.booking.dao.postrgres.RoomDescription;
import ru.example.booking.dto.defaults.ErrorResponse;
import ru.example.booking.dto.defaults.FindAllSettings;
import ru.example.booking.dto.defaults.RoomFilter;
import ru.example.booking.dto.reservation.UpsertReservationRequest;
import ru.example.booking.dto.room.RoomResponseList;
import ru.example.booking.dto.room.SimpleRoomResponse;
import ru.example.booking.dto.room.UpsertRoomRequest;
import ru.example.booking.dto.user.CreateUserRequest;
import ru.example.booking.util.LocalDatesUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RoomControllerTest extends RoomAbstractTest {

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenFindAllRooms_thenReturnAllRooms() throws Exception {

        JsonAssert.assertJsonEquals(5L, roomRepository.count());

        JsonAssert.assertJsonEquals(5L, roomService.findAll().getRooms().size());

        var expectedResponse = roomMapper.roomListToResponseList(createDefaultRoomList(false));

        var actualResponse = mockMvc.perform(get("/api/room"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenFindRoomById_thenReturnRoom() throws Exception {
        JsonAssert.assertJsonEquals(true, roomRepository.existsById(1L));

        var expectedResponse = roomMapper.roomToSimpleResponse(
                createStandardRoomWithoutBookedDates(1, false)
        );

        var actualResponse = mockMvc.perform(get("/api/room/1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenDeleteRoomById_thenReturnNoContentAndDecreasedRepository() throws Exception {
        JsonAssert.assertJsonEquals(5L, roomRepository.count());

        mockMvc.perform(delete("/api/room/1"))
                .andExpect(status().isNoContent());

        JsonAssert.assertJsonEquals(false, roomRepository.existsById(1L));
        JsonAssert.assertJsonEquals(4L, roomRepository.count());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenSaveRoom_thenReturnRoomAndIncreasedRepository() throws Exception {
        JsonAssert.assertJsonEquals(5L, roomRepository.count());

        var request = UpsertRoomRequest.builder()
                .name("New room")
                .description(RoomDescription.STANDARD)
                .hotelId(1L)
                .capacity(1)
                .price(BigDecimal.valueOf(1))
                .number(1)
                .build();

        mockMvc.perform(post("/api/room")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        JsonAssert.assertJsonEquals(6L, roomRepository.count());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenUpdateRoomById_thenReturnUpdatedRoom() throws Exception {

        String newName = "UpdatedRoom";
        String newName2 = "UpdatedRoom2";
        RoomDescription newDescription = RoomDescription.PRESIDENT;
        RoomDescription newDescription2 = RoomDescription.STANDARD;
        Long newHotelId = 3L;
        Integer newCapacity = 5;
        Integer newCapacity2 = 10;
        BigDecimal newPrice = BigDecimal.valueOf(10000);
        Integer newNumber = 1001;

        var request1 = UpsertRoomRequest.builder()
                .name(newName)
                .build();

        var request2 = UpsertRoomRequest.builder()
                .description(newDescription)
                .capacity(newCapacity)
                .build();

        var request3 = UpsertRoomRequest.builder()
                .name(newName2)
                .description(newDescription2)
                .hotelId(newHotelId)
                .capacity(newCapacity2)
                .price(newPrice)
                .number(newNumber)
                .build();

        JsonAssert.assertJsonEquals(5L, roomRepository.count());

        var room1 = createStandardRoomWithoutBookedDates(1, false);
        room1.setName(newName);

        var room2 = createStandardRoomWithoutBookedDates(2, false);
        room2.setDescription(newDescription);
        room2.setCapacity(newCapacity);

        var room3 = createPresidentRoomWithoutBookedDates(3, false);
        room3.setName(newName2);
        room3.setDescription(newDescription2);
        room3.setHotel(hotelRepository.findById(newHotelId).orElse(null));
        room3.setCapacity(newCapacity2);
        room3.setPrice(newPrice);
        room3.setNumber(newNumber);

        var expectedResponse1 = roomMapper.roomToSimpleResponse(room1);
        var expectedResponse2 = roomMapper.roomToSimpleResponse(room2);
        var expectedResponse3 = roomMapper.roomToSimpleResponse(room3);

        var actualResponse1 = mockMvc.perform(put("/api/room/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var actualResponse2 = mockMvc.perform(put("/api/room/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var actualResponse3 = mockMvc.perform(put("/api/room/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request3)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(expectedResponse1, actualResponse1);
        JsonAssert.assertJsonEquals(expectedResponse2, actualResponse2);
        JsonAssert.assertJsonEquals(expectedResponse3, actualResponse3);
        JsonAssert.assertJsonEquals(5L, roomRepository.count());
    }

    @ParameterizedTest
    @MethodSource("invalidInputStringsTwoValues")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenSaveRoomWithIncorrectNameLength_thenReturnError(String name) throws Exception {

        var request = UpsertRoomRequest.builder()
                .name(name)
                .description(RoomDescription.STANDARD)
                .hotelId(1L)
                .capacity(1)
                .price(BigDecimal.valueOf(1))
                .number(1)
                .build();

        var expectedResponse = new ErrorResponse("Room name length should be between 2 and 100 characters");

        var actualResponse = mockMvc.perform(post("/api/room")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
        JsonAssert.assertJsonEquals(5L, roomRepository.count());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenSaveRoomWithNegativeRoomNumber_thenReturnError() throws Exception {

        var request = UpsertRoomRequest.builder()
                .name("New name")
                .description(RoomDescription.STANDARD)
                .hotelId(1L)
                .capacity(1)
                .price(BigDecimal.valueOf(1))
                .number(-1)
                .build();

        var expectedResponse = new ErrorResponse("Room number can not be negative value");

        var actualResponse = mockMvc.perform(post("/api/room")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
        JsonAssert.assertJsonEquals(5L, roomRepository.count());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenSaveRoomWithNegativePrice_thenReturnError() throws Exception {

        var request = UpsertRoomRequest.builder()
                .name("New name")
                .description(RoomDescription.STANDARD)
                .hotelId(1L)
                .capacity(1)
                .price(BigDecimal.valueOf(-1))
                .number(1)
                .build();

        var expectedResponse = new ErrorResponse("Room price can not be negative value");

        var actualResponse = mockMvc.perform(post("/api/room")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
        JsonAssert.assertJsonEquals(5L, roomRepository.count());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenSaveRoomWithZeroCapacity_thenReturnError() throws Exception {

        var request = UpsertRoomRequest.builder()
                .name("New name")
                .description(RoomDescription.STANDARD)
                .hotelId(1L)
                .capacity(0)
                .price(BigDecimal.valueOf(1))
                .number(1)
                .build();

        var expectedResponse = new ErrorResponse("Minimal room capacity is 1");

        var actualResponse = mockMvc.perform(post("/api/room")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
        JsonAssert.assertJsonEquals(5L, roomRepository.count());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenSaveRoomWithNegativeCapacity_thenReturnError() throws Exception {

        var request = UpsertRoomRequest.builder()
                .name("New name")
                .description(RoomDescription.STANDARD)
                .hotelId(1L)
                .capacity(-1)
                .price(BigDecimal.valueOf(1))
                .number(1)
                .build();

        var expectedResponse = new ErrorResponse("Minimal room capacity is 1");

        var actualResponse = mockMvc.perform(post("/api/room")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
        JsonAssert.assertJsonEquals(5L, roomRepository.count());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenSaveRoomWithCapacityMoreThen15_thenReturnError() throws Exception {

        var request = UpsertRoomRequest.builder()
                .name("New name")
                .description(RoomDescription.STANDARD)
                .hotelId(1L)
                .capacity(16)
                .price(BigDecimal.valueOf(1))
                .number(1)
                .build();

        var expectedResponse = new ErrorResponse("Maximal room capacity is 15");

        var actualResponse = mockMvc.perform(post("/api/room")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
        JsonAssert.assertJsonEquals(5L, roomRepository.count());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenFindRoomByNotExistedId_thenReturnError() throws Exception {

        var expectedResponse = new ErrorResponse("Room not found, ID is 100");

        var actualResponse = mockMvc.perform(get("/api/room/100"))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenUpdateRoomByNotExistedId_thenReturnError() throws Exception {

        var request = UpsertRoomRequest.builder()
                .name("New name")
                .build();

        var expectedResponse = new ErrorResponse("Room not found, ID is 100");

        var actualResponse = mockMvc.perform(put("/api/room/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenSaveRoomWithExistedName_thenReturnError() throws Exception {

        var request = UpsertRoomRequest.builder()
                .name("Room 1")
                .description(RoomDescription.STANDARD)
                .hotelId(1L)
                .capacity(1)
                .price(BigDecimal.valueOf(1))
                .number(1)
                .build();

        var expectedResponse = new ErrorResponse("Room with name \"Room 1\" is already exists");

        var actualResponse = mockMvc.perform(post("/api/room")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAlreadyReported())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenDeleteRoomByNotExistedId_thenReturnError() throws Exception {

        JsonAssert.assertJsonEquals(5L, roomRepository.count());

        var expectedResponse = new ErrorResponse("Room not found, ID is 100");

        var actualResponse = mockMvc.perform(delete("/api/room/100"))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
        JsonAssert.assertJsonEquals(5L, roomRepository.count());
    }

    @Test
    public void whenFindAllWithFilterWithoutLogging_thenReturnError() throws Exception {

        var settings = FindAllSettings.builder()
                .pageNum(0)
                .pageSize(100)
                .roomFilter(RoomFilter.builder()
                        .capacity(5)
                        .build())
                .build();

        mockMvc.perform(get("/api/room/filter")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(settings)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenFindAllWithFilterWithAdminRole_thenReturnOk() throws Exception {

        var settings = FindAllSettings.builder()
                .pageNum(0)
                .pageSize(100)
                .roomFilter(RoomFilter.builder()
                        .capacity(5)
                        .build())
                .build();

        mockMvc.perform(get("/api/room/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(settings)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = {"user"})
    public void whenFindAllWithFilterWithUserRole_thenReturnOk() throws Exception {

        var settings = FindAllSettings.builder()
                .pageNum(0)
                .pageSize(100)
                .roomFilter(RoomFilter.builder()
                        .capacity(5)
                        .build())
                .build();

        mockMvc.perform(get("/api/room/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(settings)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void whenFindAllWithFilterByCapacity_thenReturnCorrectList() throws Exception {

        saveAdditionalRooms(20);

        var settings = FindAllSettings.builder()
                .pageNum(0)
                .pageSize(100)
                .roomFilter(RoomFilter.builder()
                        .capacity(6)
                        .build())
                .build();

        var expectedResponse = roomMapper.roomListToResponseList(
                List.of(createDefaultRoomWithoutBookedDates(6, RoomDescription.STANDARD, false))
        );

        var actualResponse = mockMvc.perform(get("/api/room/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(settings)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void whenFindAllWithFilterById_thenReturnCorrectList() throws Exception {

        saveAdditionalRooms(20);

        var settings = FindAllSettings.builder()
                .pageNum(0)
                .pageSize(100)
                .roomFilter(RoomFilter.builder()
                        .id(1L)
                        .build())
                .build();

        var expectedResponse = roomMapper.roomListToResponseList(
                List.of(createDefaultRoomWithoutBookedDates(1, RoomDescription.STANDARD, false))
        );

        var actualResponse = mockMvc.perform(get("/api/room/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(settings)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void whenFindAllWithFilterByMinPrice_thenReturnCorrectList() throws Exception {

        saveAdditionalRooms(20);

        var settings = FindAllSettings.builder()
                .pageNum(0)
                .pageSize(100)
                .roomFilter(RoomFilter.builder()
                        .minPrice(BigDecimal.valueOf(6))
                        .build())
                .build();

        var expectedResponse = roomMapper.roomListToResponseList(
                createAdditionalRooms(6, 20)
        );

        var actualResponse = mockMvc.perform(get("/api/room/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(settings)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void whenFindAllWithFilterByMaxPrice_thenReturnCorrectList() throws Exception {

        saveAdditionalRooms(20);

        var settings = FindAllSettings.builder()
                .pageNum(0)
                .pageSize(100)
                .roomFilter(RoomFilter.builder()
                        .maxPrice(BigDecimal.valueOf(5))
                        .build())
                .build();

        var expectedResponse = roomMapper.roomListToResponseList(
                createDefaultRoomList(false)
        );

        var actualResponse = mockMvc.perform(get("/api/room/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(settings)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void whenFindAllWithFilterByMaxAndMinPrice_thenReturnCorrectList() throws Exception {

        saveAdditionalRooms(20);

        var settings = FindAllSettings.builder()
                .pageNum(0)
                .pageSize(100)
                .roomFilter(RoomFilter.builder()
                        .minPrice(BigDecimal.valueOf(6))
                        .maxPrice(BigDecimal.valueOf(10))
                        .build())
                .build();

        var expectedResponse = roomMapper.roomListToResponseList(
                createAdditionalRooms(6, 4)
        );

        var actualResponse = mockMvc.perform(get("/api/room/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(settings)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void whenFindAllWithFilterByRoomDescriptionStandard_thenReturnCorrectList() throws Exception {

        saveAdditionalRooms(20);

        var settings = FindAllSettings.builder()
                .pageNum(0)
                .pageSize(100)
                .roomFilter(RoomFilter.builder()
                        .description(RoomDescription.STANDARD)
                        .build())
                .build();

        List<Room> roomList = roomRepository.findAll().stream()
                .filter(room -> room.getDescription().equals(RoomDescription.STANDARD))
                .toList();

        var expectedResponse = roomMapper.roomListToResponseList(roomList);

        var actualResponse = mockMvc.perform(get("/api/room/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(settings)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void whenFindAllWithFilterByRoomDescriptionSuite_thenReturnCorrectList() throws Exception {

        saveAdditionalRooms(20);

        var settings = FindAllSettings.builder()
                .pageNum(0)
                .pageSize(100)
                .roomFilter(RoomFilter.builder()
                        .description(RoomDescription.SUITE)
                        .build())
                .build();

        List<Room> roomList = roomRepository.findAll().stream()
                .filter(room -> room.getDescription().equals(RoomDescription.SUITE))
                .toList();

        var expectedResponse = roomMapper.roomListToResponseList(roomList);

        var actualResponse = mockMvc.perform(get("/api/room/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(settings)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void whenFindAllWithFilterByRoomDescriptionPresident_thenReturnCorrectList() throws Exception {

        saveAdditionalRooms(20);

        var settings = FindAllSettings.builder()
                .pageNum(0)
                .pageSize(100)
                .roomFilter(RoomFilter.builder()
                        .description(RoomDescription.PRESIDENT)
                        .build())
                .build();

        List<Room> roomList = roomRepository.findAll().stream()
                .filter(room -> room.getDescription().equals(RoomDescription.PRESIDENT))
                .toList();

        var expectedResponse = roomMapper.roomListToResponseList(roomList);

        var actualResponse = mockMvc.perform(get("/api/room/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(settings)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void whenFindAllWithFilterByRoomDescriptionSuperior_thenReturnCorrectList() throws Exception {

        saveAdditionalRooms(20);

        var settings = FindAllSettings.builder()
                .pageNum(0)
                .pageSize(100)
                .roomFilter(RoomFilter.builder()
                        .description(RoomDescription.SUPERIOR)
                        .build())
                .build();

        List<Room> roomList = roomRepository.findAll().stream()
                .filter(room -> room.getDescription().equals(RoomDescription.SUPERIOR))
                .toList();

        var expectedResponse = roomMapper.roomListToResponseList(roomList);

        var actualResponse = mockMvc.perform(get("/api/room/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(settings)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void whenFindAllWithFilterByHotelId_thenReturnCorrectList() throws Exception {

        saveAdditionalRooms(20);

        var settings = FindAllSettings.builder()
                .pageNum(0)
                .pageSize(100)
                .roomFilter(RoomFilter.builder()
                        .hotelId(1L)
                        .build())
                .build();

        var expectedResponse = roomMapper.roomListToResponseList(
                List.of(createDefaultRoomWithoutBookedDates(1, RoomDescription.STANDARD, false))
        );

        var actualResponse = mockMvc.perform(get("/api/room/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(settings)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void whenFindAllWithByCheckInOutDate1_thenReturnCorrectList() throws Exception {

        userService.save(RoleType.ROLE_USER, CreateUserRequest.builder()
                        .email("email@email.com")
                        .password("pass")
                        .username("user1")
                        .build());

        roomRepository.findAll().forEach(
                room -> reservationService.booking(
                        createUpsertReservationRequest(
                                room.getId(), LocalDate.now(), LocalDate.now().plusDays(Math.toIntExact(room.getId()))
                        ),
                        "user1"
                )
        );

        var settings = FindAllSettings.builder()
                .pageNum(0)
                .pageSize(100)
                .roomFilter(RoomFilter.builder()
                        .checkInDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(5), DATE_PATTERN))
                        .checkOutDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(10), DATE_PATTERN))
                        .build())
                .build();


        var response = mockMvc.perform(get("/api/room/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(settings)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var roomResponse = objectMapper.readValue(response, RoomResponseList.class);

        JsonAssert.assertJsonEquals(4, roomResponse.getRooms().size());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void whenFindAllWithByCheckInOutDate2_thenReturnEmptyList() throws Exception {

        userService.save(RoleType.ROLE_USER, CreateUserRequest.builder()
                .email("email@email.com")
                .password("pass")
                .username("user1")
                .build());

        roomRepository.findAll().forEach(
                room -> reservationService.booking(
                        createUpsertReservationRequest(
                                room.getId(), LocalDate.now(), LocalDate.now().plusDays(Math.toIntExact(room.getId()))
                        ),
                        "user1"
                )
        );

        var settings = FindAllSettings.builder()
                .pageNum(0)
                .pageSize(100)
                .roomFilter(RoomFilter.builder()
                        .checkInDate(LocalDatesUtil.localDateToStr(LocalDate.now(), DATE_PATTERN))
                        .checkOutDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(10), DATE_PATTERN))
                        .build())
                .build();


        var response = mockMvc.perform(get("/api/room/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(settings)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var roomResponse = objectMapper.readValue(response, RoomResponseList.class);

        JsonAssert.assertJsonEquals(0, roomResponse.getRooms().size());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void whenFindAllWithByCheckInOutDate3_thenReturnCorrectList() throws Exception {


        userService.save(RoleType.ROLE_USER, CreateUserRequest.builder()
                .email("email@email.com")
                .password("pass")
                .username("user1")
                .build());

        // + room1
        reservationService.booking(
                UpsertReservationRequest.builder()
                        .roomId(1L)
                        .checkInDate(LocalDatesUtil.localDateToStr(LocalDate.now(), DATE_PATTERN))
                        .checkOutDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(1), DATE_PATTERN))
                        .build(),
                "user1"
        );
        // -
        reservationService.booking(
                UpsertReservationRequest.builder()
                        .roomId(2L)
                        .checkInDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(1), DATE_PATTERN))
                        .checkOutDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(5), DATE_PATTERN))
                        .build(),
                "user1"
        );
        // -
        reservationService.booking(
                UpsertReservationRequest.builder()
                        .roomId(3L)
                        .checkInDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(2), DATE_PATTERN))
                        .checkOutDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(2), DATE_PATTERN))
                        .build(),
                "user1"
        );

        // -
        reservationService.booking(
                UpsertReservationRequest.builder()
                        .roomId(4L)
                        .checkInDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(3), DATE_PATTERN))
                        .checkOutDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(10), DATE_PATTERN))
                        .build(),
                "user1"
        );
        // + room5
        reservationService.booking(
                UpsertReservationRequest.builder()
                        .roomId(5L)
                        .checkInDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(11), DATE_PATTERN))
                        .checkOutDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(15), DATE_PATTERN))
                        .build(),
                "user1"
        );


        var settings = FindAllSettings.builder()
                .pageNum(0)
                .pageSize(100)
                .roomFilter(RoomFilter.builder()
                        .checkInDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(2), DATE_PATTERN))
                        .checkOutDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(10), DATE_PATTERN))
                        .build())
                .build();


        var response = mockMvc.perform(get("/api/room/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(settings)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var actualResponse = objectMapper.readValue(response, RoomResponseList.class).getRooms().size();

        JsonAssert.assertJsonEquals(2, actualResponse);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void whenFindAllWithByCheckInOutDate4_thenReturnCorrectList() throws Exception {


        userService.save(RoleType.ROLE_USER, CreateUserRequest.builder()
                .email("email@email.com")
                .password("pass")
                .username("user1")
                .build());

        // -
        reservationService.booking(
                UpsertReservationRequest.builder()
                        .roomId(1L)
                        .checkInDate(LocalDatesUtil.localDateToStr(LocalDate.now(), DATE_PATTERN))
                        .checkOutDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(1), DATE_PATTERN))
                        .build(),
                "user1"
        );
        // -
        reservationService.booking(
                UpsertReservationRequest.builder()
                        .roomId(2L)
                        .checkInDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(1), DATE_PATTERN))
                        .checkOutDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(5), DATE_PATTERN))
                        .build(),
                "user1"
        );
        // -
        reservationService.booking(
                UpsertReservationRequest.builder()
                        .roomId(3L)
                        .checkInDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(2), DATE_PATTERN))
                        .checkOutDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(2), DATE_PATTERN))
                        .build(),
                "user1"
        );

        // -
        reservationService.booking(
                UpsertReservationRequest.builder()
                        .roomId(4L)
                        .checkInDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(3), DATE_PATTERN))
                        .checkOutDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(10), DATE_PATTERN))
                        .build(),
                "user1"
        );
        // + room5
        reservationService.booking(
                UpsertReservationRequest.builder()
                        .roomId(5L)
                        .checkInDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(11), DATE_PATTERN))
                        .checkOutDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(15), DATE_PATTERN))
                        .build(),
                "user1"
        );

        reservationService.booking(
                UpsertReservationRequest.builder()
                        .roomId(1L)
                        .checkInDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(2), DATE_PATTERN))
                        .checkOutDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(3), DATE_PATTERN))
                        .build(),
                "user1"
        );

        reservationService.booking(
                UpsertReservationRequest.builder()
                        .roomId(5L)
                        .checkInDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(3), DATE_PATTERN))
                        .checkOutDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(5), DATE_PATTERN))
                        .build(),
                "user1"
        );


        var settings = FindAllSettings.builder()
                .pageNum(0)
                .pageSize(100)
                .roomFilter(RoomFilter.builder()
                        .checkInDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(2), DATE_PATTERN))
                        .checkOutDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(10), DATE_PATTERN))
                        .build())
                .build();


        var response = mockMvc.perform(get("/api/room/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(settings)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var actualResponse = objectMapper.readValue(response, RoomResponseList.class).getRooms().size();

        JsonAssert.assertJsonEquals(0, actualResponse);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void whenFindAllWithByCheckInOutDate5_thenReturnCorrectList() throws Exception {


        userService.save(RoleType.ROLE_USER, CreateUserRequest.builder()
                .email("email@email.com")
                .password("pass")
                .username("user1")
                .build());

        // + room1
        reservationService.booking(
                UpsertReservationRequest.builder()
                        .roomId(1L)
                        .checkInDate(LocalDatesUtil.localDateToStr(LocalDate.now(), DATE_PATTERN))
                        .checkOutDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(1), DATE_PATTERN))
                        .build(),
                "user1"
        );
        // -
        reservationService.booking(
                UpsertReservationRequest.builder()
                        .roomId(2L)
                        .checkInDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(1), DATE_PATTERN))
                        .checkOutDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(5), DATE_PATTERN))
                        .build(),
                "user1"
        );
        // -
        reservationService.booking(
                UpsertReservationRequest.builder()
                        .roomId(3L)
                        .checkInDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(2), DATE_PATTERN))
                        .checkOutDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(2), DATE_PATTERN))
                        .build(),
                "user1"
        );

        // -
        reservationService.booking(
                UpsertReservationRequest.builder()
                        .roomId(4L)
                        .checkInDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(3), DATE_PATTERN))
                        .checkOutDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(10), DATE_PATTERN))
                        .build(),
                "user1"
        );
        // + room5
        reservationService.booking(
                UpsertReservationRequest.builder()
                        .roomId(5L)
                        .checkInDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(11), DATE_PATTERN))
                        .checkOutDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(15), DATE_PATTERN))
                        .build(),
                "user1"
        );

        var settings = FindAllSettings.builder()
                .pageNum(0)
                .pageSize(100)
                .roomFilter(RoomFilter.builder()
                        .checkInDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(2), DATE_PATTERN))
                        .checkOutDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(10), DATE_PATTERN))
                        .build())
                .build();

        var expectedResponse = RoomResponseList.builder()
                .rooms(List.of(
                        SimpleRoomResponse.builder()
                                .id(1L)
                                .hotelId(1L)
                                .description(RoomDescription.STANDARD)
                                .name("Room 1")
                                .number(1)
                                .price(BigDecimal.valueOf(1))
                                .capacity(1)
                                .bookedDatesSize(2)
                                .build(),
                        SimpleRoomResponse.builder()
                                .id(5L)
                                .hotelId(5L)
                                .description(RoomDescription.SUITE)
                                .name("Room 5")
                                .number(5)
                                .price(BigDecimal.valueOf(5))
                                .capacity(5)
                                .bookedDatesSize(5)
                                .build()
                        )
                )
                .build();


        var actualResponse = mockMvc.perform(get("/api/room/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(settings)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void whenFindAllWithByCheckInOutDate6_thenReturnCorrectList() throws Exception {


        userService.save(RoleType.ROLE_USER, CreateUserRequest.builder()
                .email("email@email.com")
                .password("pass")
                .username("user1")
                .build());

        // + room1
        reservationService.booking(
                UpsertReservationRequest.builder()
                        .roomId(1L)
                        .checkInDate(LocalDatesUtil.localDateToStr(LocalDate.now(), DATE_PATTERN))
                        .checkOutDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(1), DATE_PATTERN))
                        .build(),
                "user1"
        );
        // + room2
        reservationService.booking(
                UpsertReservationRequest.builder()
                        .roomId(2L)
                        .checkInDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(1), DATE_PATTERN))
                        .checkOutDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(5), DATE_PATTERN))
                        .build(),
                "user1"
        );
        // + room3
        reservationService.booking(
                UpsertReservationRequest.builder()
                        .roomId(3L)
                        .checkInDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(2), DATE_PATTERN))
                        .checkOutDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(2), DATE_PATTERN))
                        .build(),
                "user1"
        );
        // + room4
        reservationService.booking(
                UpsertReservationRequest.builder()
                        .roomId(4L)
                        .checkInDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(3), DATE_PATTERN))
                        .checkOutDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(10), DATE_PATTERN))
                        .build(),
                "user1"
        );
        // + room5
        reservationService.booking(
                UpsertReservationRequest.builder()
                        .roomId(5L)
                        .checkInDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(21), DATE_PATTERN))
                        .checkOutDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(22), DATE_PATTERN))
                        .build(),
                "user1"
        );

        var settings = FindAllSettings.builder()
                .pageNum(0)
                .pageSize(100)
                .roomFilter(RoomFilter.builder()
                        .checkInDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(11), DATE_PATTERN))
                        .checkOutDate(LocalDatesUtil.localDateToStr(LocalDate.now().plusDays(20), DATE_PATTERN))
                        .build())
                .build();

        var response = mockMvc.perform(get("/api/room/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(settings)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var actualResponse = objectMapper.readValue(response, RoomResponseList.class).getRooms().size();

        JsonAssert.assertJsonEquals(5, actualResponse);
    }

}

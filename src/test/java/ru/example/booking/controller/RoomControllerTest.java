package ru.example.booking.controller;

import net.javacrumbs.jsonunit.JsonAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import ru.example.booking.abstracts.RoomAbstractTest;
import ru.example.booking.dao.RoomDescription;
import ru.example.booking.dto.defaults.ErrorResponse;
import ru.example.booking.dto.room.UpsertRoomRequest;

import java.math.BigDecimal;

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

        var expectedResponse = roomMapper.roomToResponse(
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

        var expectedResponse = roomMapper.requestToRoom(request);
        expectedResponse.setId(6L);

        var actualResponse = mockMvc.perform(post("/api/room")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
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

        var expectedResponse1 = roomMapper.roomToResponse(room1);
        var expectedResponse2 = roomMapper.roomToResponse(room2);
        var expectedResponse3 = roomMapper.roomToResponse(room3);

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
}

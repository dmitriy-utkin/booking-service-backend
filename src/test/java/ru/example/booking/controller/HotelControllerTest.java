package ru.example.booking.controller;

import net.javacrumbs.jsonunit.JsonAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import ru.example.booking.abstracts.HotelAbstractTest;
import ru.example.booking.dto.defaults.ErrorResponse;
import ru.example.booking.dto.hotel.CreateHotelRequest;
import ru.example.booking.dto.hotel.UpdateHotelRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class HotelControllerTest extends HotelAbstractTest {

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenFindHotelById_thenReturnHotel() throws Exception {

        var expectedResponse = hotelMapper.hotelToResponse(createDefaultHotel(1));

        var actualResponse = mockMvc.perform(get("/api/hotel/1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenFindAllHotels_thenReturnAllHotels() throws Exception {
        var expectedResponse = hotelMapper.hotelListToResponseList(createDefaultHotelList(5));

        var actualResponse = mockMvc.perform(get("/api/hotel"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenSaveHotel_thenReturnHotelAndIncreaseRepositoryCount() throws Exception {

        JsonAssert.assertJsonEquals(5L, hotelRepository.count());

        var createRequest = CreateHotelRequest.builder()
                .name("New hotel name 1")
                .headline("New hotel headline 1")
                .address("New hotel address 1")
                .city("New hotel city 1")
                .distance(1F)
                .build();

        var expectedResponse = hotelMapper.hotelToResponse(hotelMapper.createRequestToHotel(createRequest));
        expectedResponse.setId(6L);

        var actualResponse = mockMvc.perform(post("/api/hotel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(6L, hotelRepository.count());
        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenUpdateHotelById_thenReturnUpdatedHotelAndNoIncreaseOfRepository() throws Exception {
        JsonAssert.assertJsonEquals(5L, hotelRepository.count());

        var updateRequest1 = UpdateHotelRequest.builder().name("New hotel name 1").build();
        var updateRequest2 = UpdateHotelRequest.builder()
                .name("New hotel name 2")
                .address("New hotel address 2")
                .build();
        var updateRequest3 = UpdateHotelRequest.builder()
                .name("Full update: name")
                .headline("Full update: headline")
                .city("Full update: city")
                .address("Full update: address")
                .build();
        var hotel1 = createDefaultHotel(1);
        hotel1.setName("New hotel name 1");

        var hotel2 = createDefaultHotel(2);
        hotel2.setName("New hotel name 2");
        hotel2.setAddress("New hotel address 2");

        var hotel3 = createDefaultHotel(3);
        hotel3.setName("Full update: name");
        hotel3.setHeadline("Full update: headline");
        hotel3.setCity("Full update: city");
        hotel3.setAddress("Full update: address");

        var expectedResponse1 = hotelMapper.hotelToResponse(hotel1);
        var expectedResponse2 = hotelMapper.hotelToResponse(hotel2);
        var expectedResponse3 = hotelMapper.hotelToResponse(hotel3);

        var actualResponse1 = mockMvc.perform(put("/api/hotel/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest1)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        var actualResponse2 = mockMvc.perform(put("/api/hotel/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest2)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        var actualResponse3 = mockMvc.perform(put("/api/hotel/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest3)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(5L, hotelRepository.count());
        JsonAssert.assertJsonEquals(expectedResponse1, actualResponse1);
        JsonAssert.assertJsonEquals(expectedResponse2, actualResponse2);
        JsonAssert.assertJsonEquals(expectedResponse3, actualResponse3);

    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenDeleteHotelById_thenReturnNoContentAndDecreaseRepositoryCount() throws Exception {

        JsonAssert.assertJsonEquals(5L, hotelRepository.count());

        mockMvc.perform(delete("/api/hotel/1"))
                .andExpect(status().isNoContent());

        JsonAssert.assertJsonEquals(4L, hotelRepository.count());

        mockMvc.perform(delete("/api/hotel/2"))
                .andExpect(status().isNoContent());

        JsonAssert.assertJsonEquals(3L, hotelRepository.count());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenFindHotelByNotExistsId_thenReturnError() throws Exception {

        var expectedResponse = new ErrorResponse("Hotel not found, ID is " + 100);

        var actualResponse = mockMvc.perform(get("/api/hotel/100"))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenSaveHotelWithNullName_thenReturnValidationError() throws Exception {

        var createRequest = CreateHotelRequest.builder()
                .headline("New hotel headline 1")
                .address("New hotel address 1")
                .city("New hotel city 1")
                .distance(1F)
                .build();

        var expectedResponse = new ErrorResponse("Hotel name is required");

        var actualResponse = mockMvc.perform(post("/api/hotel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(5L, hotelRepository.count());
        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenSaveHotelWithNullHeadline_thenReturnValidationError() throws Exception {

        var createRequest = CreateHotelRequest.builder()
                .name("New hotel name 1")
                .address("New hotel address 1")
                .city("New hotel city 1")
                .distance(1F)
                .build();

        var expectedResponse = new ErrorResponse("Hotel headline is required");

        var actualResponse = mockMvc.perform(post("/api/hotel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(5L, hotelRepository.count());
        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenSaveHotelWithNullCity_thenReturnValidationError() throws Exception {

        var createRequest = CreateHotelRequest.builder()
                .name("New hotel name 1")
                .headline("New hotel headline 1")
                .address("New hotel address 1")
                .distance(1F)
                .build();

        var expectedResponse = new ErrorResponse("City of hotel location is required");

        var actualResponse = mockMvc.perform(post("/api/hotel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(5L, hotelRepository.count());
        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenSaveHotelWithNullAddress_thenReturnValidationError() throws Exception {

        var createRequest = CreateHotelRequest.builder()
                .name("New hotel name 1")
                .headline("New hotel headline 1")
                .city("New hotel city 1")
                .distance(1F)
                .build();

        var expectedResponse = new ErrorResponse("The hotel address is required");

        var actualResponse = mockMvc.perform(post("/api/hotel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(5L, hotelRepository.count());
        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenSaveHotelWithNullDistance_thenReturnValidationError() throws Exception {

        var createRequest = CreateHotelRequest.builder()
                .name("New hotel name 1")
                .headline("New hotel headline 1")
                .address("New hotel address 1")
                .city("New hotel city 1")
                .build();

        var expectedResponse = new ErrorResponse("The distance of hotel to city center is required");

        var actualResponse = mockMvc.perform(post("/api/hotel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(5L, hotelRepository.count());
        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenUpdateHotelByNotExistsId_thenReturnError() throws Exception {

        var updateRequest = UpdateHotelRequest.builder().name("New hotel name 1").build();

        var expectedResponse = new ErrorResponse("Hotel not found, ID is " + 100);

        var actualResponse = mockMvc.perform(put("/api/hotel/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);

    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenDeleteHotelByNotExistsId_thenReturnError() throws Exception {

        var expectedResponse = new ErrorResponse("Hotel not found, ID is " + 100);

        var actualResponse = mockMvc.perform(delete("/api/hotel/100"))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);

    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenSaveHotelWithExistsName_thenReturnException() throws Exception {

        JsonAssert.assertJsonEquals(5L, hotelRepository.count());

        var defaultHotel = createDefaultHotel(1);

        var createRequest = CreateHotelRequest.builder()
                .name(defaultHotel.getName())
                .headline(defaultHotel.getHeadline())
                .address(defaultHotel.getAddress())
                .city(defaultHotel.getCity())
                .distance(defaultHotel.getDistance())
                .build();

        var expectedResponse = new ErrorResponse(
                "Hotel with name \"" + defaultHotel.getName() + "\" is already exists"
        );

        var actualResponse = mockMvc.perform(post("/api/hotel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isAlreadyReported())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(5L, hotelRepository.count());
        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @ParameterizedTest
    @MethodSource("invalidInputStringsTwoValues")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenSaveHotelWithIncorrectHeadlineLength_thenReturnValidationError(String headline) throws Exception {

        JsonAssert.assertJsonEquals(5L, hotelRepository.count());

        var createRequest = CreateHotelRequest.builder()
                .name("New hotel name 1")
                .headline(headline)
                .address("New hotel address 1")
                .city("New hotel city 1")
                .distance(1F)
                .build();

        var expectedResponse = new ErrorResponse(
                "Hotel headline length should be between 15 and 160 characters"
        );

        var actualResponse = mockMvc.perform(post("/api/hotel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(5L, hotelRepository.count());
        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);

    }

    @ParameterizedTest
    @MethodSource("invalidInputStringsTwoValues")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenSaveHotelWithIncorrectNameLength_thenReturnValidationError(String name) throws Exception {

        JsonAssert.assertJsonEquals(5L, hotelRepository.count());

        var createRequest = CreateHotelRequest.builder()
                .name(name)
                .headline("New hotel headline 1")
                .address("New hotel address 1")
                .city("New hotel city 1")
                .distance(1F)
                .build();

        var expectedResponse = new ErrorResponse(
                "Hotel name length should be between 5 and 60 characters"
        );

        var actualResponse = mockMvc.perform(post("/api/hotel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(5L, hotelRepository.count());
        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);

    }

    @ParameterizedTest
    @MethodSource("invalidInputStringsTwoValues")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenSaveHotelWithIncorrectCityLength_thenReturnValidationError(String city) throws Exception {

        JsonAssert.assertJsonEquals(5L, hotelRepository.count());

        var createRequest = CreateHotelRequest.builder()
                .name("New hotel name 1")
                .headline("New hotel headline 1")
                .address("New hotel address 1")
                .city(city)
                .distance(1F)
                .build();

        var expectedResponse = new ErrorResponse(
                "City of hotel location length should be between 2 and 60 characters"
        );

        var actualResponse = mockMvc.perform(post("/api/hotel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(5L, hotelRepository.count());
        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);

    }

    @ParameterizedTest
    @MethodSource("invalidInputStringsTwoValues")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenSaveHotelWithIncorrectAddressLength_thenReturnValidationError(String address) throws Exception {

        JsonAssert.assertJsonEquals(5L, hotelRepository.count());

        var createRequest = CreateHotelRequest.builder()
                .name("New hotel name 1")
                .headline("New hotel headline 1")
                .address(address)
                .city("New hotel city 1")
                .distance(1F)
                .build();

        var expectedResponse = new ErrorResponse(
                "The hotel address length should be between 5 and 160 characters"
        );

        var actualResponse = mockMvc.perform(post("/api/hotel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(5L, hotelRepository.count());
        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    @WithMockUser(username = "user1")
    public void whenUpdateHotelRating_thenReturnUpdatedHotel1() throws Exception {
        var hotel = createDefaultHotel(1);
        hotel.setNumberOfRatings(2);
        hotel.setRating(3.0F);

        var expectedResponse = hotelMapper.hotelToResponse(hotel);

        var actualResponse = mockMvc.perform(put("/api/hotel/rate/1?newRating=5"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(5L, hotelRepository.count());
        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    @WithMockUser(username = "user1")
    public void whenUpdateHotelRating_thenReturnUpdatedHotel2() throws Exception {
        var hotel = createDefaultHotel(1);
        hotel.setNumberOfRatings(2);
        hotel.setRating(2.5F);

        var expectedResponse = hotelMapper.hotelToResponse(hotel);

        var actualResponse = mockMvc.perform(put("/api/hotel/rate/1?newRating=4"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(5L, hotelRepository.count());
        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    @WithMockUser(username = "user1")
    public void whenUpdateHotelRating_thenReturnUpdatedHotel3() throws Exception {
        var hotel = createDefaultHotel(5);
        hotel.setNumberOfRatings(6);
        hotel.setRating(4.8F);

        var expectedResponse = hotelMapper.hotelToResponse(hotel);

        var actualResponse = mockMvc.perform(put("/api/hotel/rate/5?newRating=4"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(5L, hotelRepository.count());
        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    @WithMockUser(username = "user1")
    public void whenUpdateHotelRatingWithValueMoreThan5_thenReturnError() throws Exception {
        mockMvc.perform(put("/api/hotel/rate/2?newRating=6"))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    @WithMockUser(username = "user1")
    public void whenUpdateHotelRatingWithValueLessThan1_thenReturnError() throws Exception {
        mockMvc.perform(put("/api/hotel/rate/2?newRating=0"))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }


    @Test
    public void whenUpdateHotelRatingWithoutLogging_thenReturnError() throws Exception {
        mockMvc.perform(put("/api/hotel/rate/1?newRating=5"))
                .andExpect(status().isUnauthorized());
    }
}
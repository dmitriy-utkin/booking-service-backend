package ru.example.booking.web.controller;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import ru.example.booking.AbstractTest;
import net.javacrumbs.jsonunit.JsonAssert;
import ru.example.booking.web.model.defaults.ErrorResponse;
import ru.example.booking.web.model.hotel.CreateHotelRequest;
import ru.example.booking.web.model.hotel.UpdateHotelRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

public class HotelControllerTest extends AbstractTest {

    @Test
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
    public void whenFindHotelByNotExistsId_thenReturnError() throws Exception {

        var expectedResponse = new ErrorResponse("Hotel not found, ID is " + 100);

        var actualResponse = mockMvc.perform(get("/api/hotel/100"))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

//    @Test
    public void whenSaveHotelWithIncorrectNameLength_thenReturnValidationError() throws Exception {

    }

    @Test
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

//    @Test
    public void whenSaveHotelWithIncorrectHeadlineLength_thenReturnValidationError() throws Exception {

    }

    @Test
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

//    @Test
    public void whenSaveHotelWithIncorrectCityLength_thenReturnValidationError() throws Exception {

    }

    @Test
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

//    @Test
    public void whenSaveHotelWithIncorrectAddressLength_thenReturnValidationError() throws Exception {

    }

    @Test
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

//    @Test
    public void whenUpdateHotelByNotExistsId_thenReturnError() throws Exception {

    }

//    @Test
    public void whenDeleteHotelByNotExistsId_thenReturnError() throws Exception {

    }

//    @Test
    public void whenSaveHotelWithExistsName_thenReturnException() throws Exception {

    }

}
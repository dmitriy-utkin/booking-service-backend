package ru.example.booking.service;

import net.javacrumbs.jsonunit.JsonAssert;
import org.junit.jupiter.api.Test;
import ru.example.booking.abstracts.HotelAbstractTest;

public class HotelServiceTest extends HotelAbstractTest {

    @Test
    public void whenUpdateHotelRating_thenReturnRating3() {

        var hotelResult = createDefaultHotel(1);
        hotelResult.setRating(3F);
        hotelResult.setNumberOfRatings(2);

        var expectedResult = hotelMapper.hotelToResponse(hotelResult);

        var actualResult = hotelService.updateRating(1L, 5);

        JsonAssert.assertJsonEquals(expectedResult, actualResult);
    }

    @Test
    public void whenUpdateHotelRating_thenReturnRating2_5() {

        var hotelResult = createDefaultHotel(1);
        hotelResult.setRating(2.5F);
        hotelResult.setNumberOfRatings(2);
        var expectedResult = hotelMapper.hotelToResponse(hotelResult);

        var actualResult = hotelService.updateRating(1L, 4);

        JsonAssert.assertJsonEquals(expectedResult, actualResult);
    }
}

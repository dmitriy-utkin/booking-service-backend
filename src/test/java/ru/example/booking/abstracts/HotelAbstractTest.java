package ru.example.booking.abstracts;

import org.junit.jupiter.api.BeforeEach;
import ru.example.booking.dao.postrgres.Hotel;

import java.util.ArrayList;
import java.util.List;

public class HotelAbstractTest extends AbstractMainTest {

    protected void saveAdditionalHotels(int count) {
        hotelRepository.saveAll(createAdditionalHotels(count, 6));
    }

    protected List<Hotel> createAdditionalHotels(int count, int startIndex) {
        List<Hotel> hotels = new ArrayList<>();
        for (int i = startIndex; i < count + startIndex; i++) {
            hotels.add(createDefaultHotel(i));
        }
        return hotels;
    }

    @BeforeEach
    public void beforeEach() {

        resetSequence();

        for (int i = 1; i <= 5; i++) {
            hotelRepository.save(createDefaultHotel(i));
        }
    }
}

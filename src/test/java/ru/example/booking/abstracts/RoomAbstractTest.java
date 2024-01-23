package ru.example.booking.abstracts;

import org.junit.jupiter.api.BeforeEach;
import ru.example.booking.dao.Room;
import ru.example.booking.dao.RoomDescription;

import java.util.ArrayList;
import java.util.List;

public class RoomAbstractTest extends AbstractMainTest {

    protected List<Room> createAdditionalRooms(int start, int count) {
        List<Room> rooms = new ArrayList<>();
        for (int i = start; i <= start + count; i++) {
            rooms.add(createDefaultRoomWithoutBookedDates(i, RoomDescription.STANDARD, true));
        }
        return rooms;
    }

    protected void saveAdditionalRooms(int count) {
        roomRepository.saveAll(createAdditionalRooms((int) (roomRepository.count() + 1), count));
    }

    @BeforeEach
    public void beforeEach() {


        resetSequence();

        createDefaultRoomList(true).forEach(
                roomRepository::save
        );
    }
}

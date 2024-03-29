package ru.example.booking.dto.room;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomResponseList {

    @Builder.Default
    private List<SimpleRoomResponse> rooms = new ArrayList<>();
}

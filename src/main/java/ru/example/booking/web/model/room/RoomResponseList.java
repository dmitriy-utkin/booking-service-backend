package ru.example.booking.web.model.room;

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
    private List<RoomResponse> rooms = new ArrayList<>();
}

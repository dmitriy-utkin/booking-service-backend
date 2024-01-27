package ru.example.booking.dto.defaults;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.example.booking.dao.postrgres.Hotel;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadObject {

    private List<Hotel> hotels;

    private List<String> dates;

    private Set<String> roomNames;
}

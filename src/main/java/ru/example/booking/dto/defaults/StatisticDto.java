package ru.example.booking.dto.defaults;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatisticDto {

    private HttpHeaders headers;

    private ByteArrayResource body;
}

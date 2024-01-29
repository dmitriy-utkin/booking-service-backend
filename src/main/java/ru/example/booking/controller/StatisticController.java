package ru.example.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.example.booking.service.StatisticService;

@RestController
@RequestMapping("/api/statistic")
@RequiredArgsConstructor
public class StatisticController {

    private final StatisticService statisticService;


    @GetMapping("/user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ByteArrayResource> downloadUserStatistic() {

        var statistic = statisticService.prepareUserStatistic();

        return ResponseEntity.ok()
                .headers(statistic.getHeaders())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(statistic.getBody());
    }

    @GetMapping("/reservation")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ByteArrayResource> downloadReservationStatistic() {

        var statistic = statisticService.prepareReservationStatistic();

        return ResponseEntity.ok()
                .headers(statistic.getHeaders())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(statistic.getBody());
    }
}

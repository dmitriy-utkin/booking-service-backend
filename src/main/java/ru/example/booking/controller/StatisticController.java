package ru.example.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.example.booking.dto.statistic.StatisticType;
import ru.example.booking.service.StatisticService;

@RestController
@RequestMapping("/api/statistic")
@RequiredArgsConstructor
public class StatisticController {

    private final StatisticService statisticService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Resource> downloadStatistic(@RequestParam StatisticType type) {
        var statistic = statisticService.prepareStatistic(type);
        return ResponseEntity.ok().headers(statistic.getHeaders()).body(statistic.getBody());
    }
}

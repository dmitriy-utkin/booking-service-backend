package ru.example.booking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import ru.example.booking.dto.defaults.ErrorResponse;
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

    @Operation(
            summary = "Download statistic",
            description = "To download statistic by user registration, reservation in the service",
            security = @SecurityRequirement(name = "ADMIN"),
            tags = {"user", "reservation", "statistic", "GET"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    content = {
                            @Content(schema = @Schema(implementation = Resource.class),
                                    mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)
                    }
            ),
            @ApiResponse(
                    responseCode = "403",
                    content = {
                            @Content(schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    }
            )
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Resource> downloadStatistic(@RequestParam StatisticType type) {
        var statistic = statisticService.prepareStatistic(type);
        return ResponseEntity.ok().headers(statistic.getHeaders()).body(statistic.getBody());
    }
}

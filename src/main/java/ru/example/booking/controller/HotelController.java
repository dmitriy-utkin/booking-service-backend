package ru.example.booking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.example.booking.dto.defaults.ErrorResponse;
import ru.example.booking.dto.defaults.FindAllSettings;
import ru.example.booking.dto.hotel.CreateHotelRequest;
import ru.example.booking.dto.hotel.HotelResponse;
import ru.example.booking.dto.hotel.HotelResponseList;
import ru.example.booking.dto.hotel.UpdateHotelRequest;
import ru.example.booking.service.HotelService;

@RestController
@RequestMapping("/api/hotel")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;

    @Operation(
            summary = "Find all hotels",
            description = "To find all hotels without filter and pagination",
            security = @SecurityRequirement(name = "ADMIN, USER"),
            tags = {"hotel", "GET"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = {
                            @Content(schema = @Schema(implementation = HotelResponseList.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
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
    public HotelResponseList findAll() {
        return hotelService.findAll();
    }

    @Operation(
            summary = "Find all hotels",
            description = "To find all hotels with filter and pagination",
            security = @SecurityRequirement(name = "ADMIN, USER"),
            tags = {"hotel", "GET"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = {
                            @Content(schema = @Schema(implementation = HotelResponseList.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
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
    @GetMapping("/filter")
    public HotelResponseList findAllWithFilter(@RequestBody FindAllSettings settings) {
        return hotelService.findAll(settings);
    }

    @Operation(
            summary = "Find hotel",
            description = "To find hotel by ID",
            security = @SecurityRequirement(name = "ADMIN, USER"),
            tags = {"hotel", "GET"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = {
                            @Content(schema = @Schema(implementation = HotelResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    }
            ),
            @ApiResponse(
                    responseCode = "403",
                    content = {
                            @Content(schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = {
                            @Content(schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    }
            )
    })
    @GetMapping("/{id}")
    public HotelResponse findById(@PathVariable("id") Long id) {
        return hotelService.findById(id);
    }

    @Operation(
            summary = "Create new hotel",
            description = "To create new hotel",
            security = @SecurityRequirement(name = "ADMIN"),
            tags = {"hotel", "POST"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode="201"
            ),
            @ApiResponse(
                    responseCode = "403",
                    content = {
                            @Content(schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    }
            ),
            @ApiResponse(
                    responseCode = "400",
                    content = {
                            @Content(schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    }
            ),
            @ApiResponse(
                    responseCode = "208",
                    content = {
                            @Content(schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = {
                            @Content(schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    }
            )
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@Valid @RequestBody CreateHotelRequest request) {
        hotelService.save(request);
    }

    @Operation(
            summary = "Update hotel",
            description = "To update existed hotel",
            security = @SecurityRequirement(name = "ADMIN"),
            tags = {"hotel", "PUT"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    content = {
                            @Content(schema = @Schema(implementation = HotelResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    }
            ),
            @ApiResponse(
                    responseCode = "403",
                    content = {
                            @Content(schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    }
            ),
            @ApiResponse(
                    responseCode = "400",
                    content = {
                            @Content(schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    }
            ),
            @ApiResponse(
                    responseCode = "208",
                    content = {
                            @Content(schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = {
                            @Content(schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    }
            )
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public HotelResponse update(@PathVariable("id") Long id,
                                @Valid @RequestBody UpdateHotelRequest request) {
        return hotelService.updateById(id, request);
    }

    @Operation(
            summary = "Delete hotel",
            description = "To delete existed hotel by ID",
            security = @SecurityRequirement(name = "ADMIN"),
            tags = {"hotel", "DELETE"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode="204"
            ),
            @ApiResponse(
                    responseCode = "403",
                    content = {
                            @Content(schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = {
                            @Content(schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    }
            )
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable("id") Long id) {
        hotelService.deleteById(id);
    }

    @Operation(
            summary = "Update hotel rating",
            description = "To update rating for existed hotel",
            security = @SecurityRequirement(name = "ADMIN, USER"),
            tags = {"hotel", "PUT"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    content = {
                            @Content(schema = @Schema(implementation = HotelResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    }
            ),
            @ApiResponse(
                    responseCode = "403",
                    content = {
                            @Content(schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    }
            ),
            @ApiResponse(
                    responseCode = "400",
                    content = {
                            @Content(schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = {
                            @Content(schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    }
            )
    })
    @PutMapping("/rate/{id}")
    public HotelResponse updateRating(@PathVariable("id") Long id,
                                      @RequestParam @Min(1) @Max(5) Integer newRating) {
        return hotelService.updateRating(id, newRating);
    }
}

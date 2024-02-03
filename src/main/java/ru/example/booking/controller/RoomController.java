package ru.example.booking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import ru.example.booking.dto.defaults.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import ru.example.booking.dto.defaults.FindAllSettings;
import ru.example.booking.dto.room.RoomResponseList;
import ru.example.booking.dto.room.SimpleRoomResponse;
import ru.example.booking.dto.room.UpsertRoomRequest;
import ru.example.booking.dto.user.UserResponseList;
import ru.example.booking.service.RoomService;

@RestController
@RequestMapping("/api/room")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @Operation(
            summary = "Find all rooms",
            description = "To find rooms without filter and pagination",
            security = @SecurityRequirement(name = "ADMIN, USER"),
            tags = {"room", "GET"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    content = {
                            @Content(schema = @Schema(implementation = RoomResponseList.class),
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
    })
    @GetMapping
    public RoomResponseList findAll() {
        return roomService.findAll();
    }

    @Operation(
            summary = "Find all rooms",
            description = "To find rooms with filter and pagination",
            security = @SecurityRequirement(name = "ADMIN, USER"),
            tags = {"room", "GET"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    content = {
                            @Content(schema = @Schema(implementation = RoomResponseList.class),
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
    })
    @GetMapping("/filter")
    public RoomResponseList findAllWithFilter(@RequestBody FindAllSettings settings) {
        return roomService.findAll(settings);
    }

    @Operation(
            summary = "Find room",
            description = "To find room by id",
            security = @SecurityRequirement(name = "ADMIN, USER"),
            tags = {"room", "GET"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    content = {
                            @Content(schema = @Schema(implementation = SimpleRoomResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = {
                            @Content(schema = @Schema(implementation = SimpleRoomResponse.class),
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
    })
    @GetMapping("/{id}")
    public SimpleRoomResponse findById(@PathVariable("id") Long id) {
        return roomService.findById(id);
    }

    @Operation(
            summary = "Create new room",
            description = "To create new room with link to the existed hotel",
            security = @SecurityRequirement(name = "ADMIN"),
            tags = {"room", "POST"}
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
    public void save(@Valid @RequestBody UpsertRoomRequest request) {
        roomService.save(request);
    }

    @Operation(
            summary = "Update existed room",
            description = "To update information about existed room",
            security = @SecurityRequirement(name = "ADMIN"),
            tags = {"room", "PUT"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    content = {
                            @Content(schema = @Schema(implementation = SimpleRoomResponse.class),
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
    public SimpleRoomResponse update(@PathVariable("id") Long id, @RequestBody UpsertRoomRequest request) {
        return roomService.updateById(id, request);
    }

    @Operation(
            summary = "Delete room",
            description = "To delete existed room by ID",
            security = @SecurityRequirement(name = "ADMIN"),
            tags = {"room", "DELETE"}
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
    public void delete(@PathVariable("id") Long id) {
        roomService.deleteById(id);
    }
}

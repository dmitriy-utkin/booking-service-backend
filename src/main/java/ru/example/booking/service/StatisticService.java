package ru.example.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import ru.example.booking.dao.mongo.ReservationsStatistic;
import ru.example.booking.dao.mongo.UserStatistic;
import ru.example.booking.dto.statistic.StatisticDto;
import ru.example.booking.dto.statistic.StatisticType;
import ru.example.booking.exception.IllegalArguments;
import ru.example.booking.repository.mongo.StatisticReservationRepository;
import ru.example.booking.repository.mongo.StatisticUserRepository;
import ru.example.booking.util.IOUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class StatisticService {

    private final StatisticReservationRepository statisticReservationRepository;

    private final StatisticUserRepository statisticUserRepository;

    public StatisticDto prepareStatistic(StatisticType type) {
        Resource resource;
        HttpHeaders headers;

        switch (type) {
            case USER -> {
                resource = new ByteArrayResource(IOUtils.generateCsvByteArray(statisticUserRepository.findAll()));
                headers = getHeaders(true, false);
            }
            case RESERVATION -> {
                resource = new ByteArrayResource(IOUtils.generateCsvByteArray((statisticReservationRepository.findAll())));
                headers = getHeaders(false, true);
            }
            default -> throw new IllegalArguments("Statistic type is not defined");
        }

        return StatisticDto.builder().headers(headers).body(resource).build();
    }

    public void saveUser(UserStatistic userStatistic) {
        userStatistic.setRegistrationDate(Instant.now());
        statisticUserRepository.save(userStatistic);
    }

    public void saveReservation(ReservationsStatistic reservationsStatistic) {
        reservationsStatistic.setReservationDate(Instant.now());
        statisticReservationRepository.save(reservationsStatistic);
    }

    private HttpHeaders getHeaders(boolean user, boolean reservation) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm");

        String actualDateTime = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()).format(formatter);

        String filename = "";

        if (user) {
            filename = "user_stat_" + actualDateTime + ".csv";
        } else if (reservation) {
            filename = "reservation_stat_" + actualDateTime + ".csv";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return headers;
    }
}

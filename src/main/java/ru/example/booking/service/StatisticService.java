package ru.example.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import ru.example.booking.dao.mongo.ReservationsStatistic;
import ru.example.booking.dao.mongo.UserStatistic;
import ru.example.booking.dto.defaults.StatisticDto;
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

    public StatisticDto prepareUserStatistic() {
        HttpHeaders headers = getHeaders(true, false);

        ByteArrayResource resource = new ByteArrayResource(IOUtils.generateCsvFile(statisticUserRepository.findAll()));

        return StatisticDto.builder().headers(headers).body(resource).build();
    }

    public StatisticDto prepareReservationStatistic() {
        HttpHeaders headers = getHeaders(false, true);

        var i = statisticReservationRepository.count();

        ByteArrayResource resource = new ByteArrayResource(IOUtils.generateCsvFile(
                statisticReservationRepository.findAll()));

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

        String headervalue = "";

        if (user) {
            headervalue = "attachment; filename=user_stat_" + actualDateTime + ".csv";
        } else if (reservation) {
            headervalue = "attachment; filename=reservation_stat_" + actualDateTime + ".csv";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, headervalue);

        return headers;
    }
}

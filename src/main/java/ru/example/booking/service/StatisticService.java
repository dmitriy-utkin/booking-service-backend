package ru.example.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.example.booking.dao.mongo.ReservationsStatistic;
import ru.example.booking.dao.mongo.UserStatistic;
import ru.example.booking.repository.mongo.StatisticReservationRepository;
import ru.example.booking.repository.mongo.StatisticUserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticService {

    private final StatisticReservationRepository statisticReservationRepository;

    private final StatisticUserRepository statisticUserRepository;

    public void downloadStatistics() {

    }

    public void saveUser(UserStatistic userStatistic) {
        statisticUserRepository.save(userStatistic);
    }

    public void saveReservation(ReservationsStatistic reservationsStatistic) {
        statisticReservationRepository.save(reservationsStatistic);
    }
}

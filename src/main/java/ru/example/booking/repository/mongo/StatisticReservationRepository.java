package ru.example.booking.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.example.booking.dao.mongo.ReservationsStatistic;

@Repository
public interface StatisticReservationRepository extends MongoRepository<ReservationsStatistic, String> {
}

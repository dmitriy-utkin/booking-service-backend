package ru.example.booking.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.example.booking.dao.mongo.UserStatistic;

@Repository
public interface StatisticUserRepository extends MongoRepository<UserStatistic, String> {
}

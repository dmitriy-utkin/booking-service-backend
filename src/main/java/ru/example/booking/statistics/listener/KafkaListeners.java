package ru.example.booking.statistics.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.example.booking.mapper.ReservationMapper;
import ru.example.booking.mapper.UserMapper;
import ru.example.booking.statistics.event.ReservationEvent;
import ru.example.booking.statistics.event.UserEvent;
import ru.example.booking.service.StatisticService;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaListeners {

    private final UserMapper userMapper;

    private final ReservationMapper reservationMapper;

    private final StatisticService statisticService;

    @KafkaListener(
            topics = "${app.kafka.userTopic}",
            groupId = "${app.kafka.kafkaGroupId}",
            containerFactory = "concurrentKafkaListenerContainerFactory"
    )
    public void listenUserCreation(@Payload UserEvent userEvent) {
        log.info("Received user event");
        userEvent.setId(UUID.randomUUID().toString());
        statisticService.saveUser(userMapper.eventToStatistic(userEvent));
    }

    @KafkaListener(
            topics = "${app.kafka.reservationTopic}",
            groupId = "${app.kafka.kafkaGroupId}",
            containerFactory = "concurrentKafkaListenerContainerFactory"
    )
    public void listenReservationCreation(@Payload ReservationEvent reservationEvent) {
        log.info("Received reservation event");
        reservationEvent.setId(UUID.randomUUID().toString());
        statisticService.saveReservation(reservationMapper.eventRoStatistic(reservationEvent));
    }
}

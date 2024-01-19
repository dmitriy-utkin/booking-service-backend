package ru.example.booking.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;

//@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "spring.profiles", name = "active", havingValue = "dev")
public class DevListener {

//    private final HotelRepository hotelRepository;

    @Value("${app.uploading.mockHotelPath}")
    private String mockHotelPath;

    @EventListener(ApplicationStartedEvent.class)
    @Order(1)
    public void uploadHotel() {
//        if (hotelRepository.count() > 0) {
//            return;
//        }

    }
}

package ru.example.booking.repository.postgres;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.example.booking.dao.postrgres.Hotel;

import java.util.Optional;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long>, JpaSpecificationExecutor<Hotel> {

    Optional<Hotel> findByName(String name);

    boolean existsByName(String name);
}

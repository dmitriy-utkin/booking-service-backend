package ru.example.booking.repository.postgres;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.example.booking.dao.postrgres.Room;

import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long>, JpaSpecificationExecutor<Room> {

    Optional<Room> findByName(String name);

    boolean existsByName(String name);
}

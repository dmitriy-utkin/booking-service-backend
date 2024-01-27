package ru.example.booking.dao.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldNameConstants
@Document(collection = "users_stat")
public class UserStatistic {

    @Id
    private String id;

    private Instant registrationDate;

    private Long userId;
}

package ru.example.booking.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import ru.example.booking.dto.defaults.UploadObject;

import java.io.File;

@UtilityClass
public class UploadUtils {

    @SneakyThrows
    public static UploadObject readValues(String path) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File(path), UploadObject.class);
    }

}

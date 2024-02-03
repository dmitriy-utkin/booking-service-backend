package ru.example.booking.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import ru.example.booking.dto.defaults.UploadObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.util.List;

@UtilityClass
public class IOUtils {

    @SneakyThrows
    public static UploadObject readValues(String path) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File(path), UploadObject.class);
    }

    @SneakyThrows
    public static <T> byte[] generateCsvByteArray(List<T> data) {
        if (data.isEmpty()) {
            return new byte[0];
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CSVWriter writer = new CSVWriter(new OutputStreamWriter(outputStream));

        T objectType = data.get(0);
        Field[] fields = objectType.getClass().getDeclaredFields();
        String[] fieldNames = new String[fields.length];

        for (int i = 0; i < fields.length; i++) {
            fieldNames[i] = fields[i].getName();
        }

        writer.writeNext(fieldNames);

        for (T part : data) {
            String[] row = new String[fields.length];
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                Object value = fields[i].get(part);
                row[i] = value == null ? "" : value.toString();
            }
            writer.writeNext(row);
        }
        writer.close();
        return outputStream.toByteArray();
    }
}

package ru.example.booking.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import ru.example.booking.dto.defaults.UploadObject;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@UtilityClass
public class IOUtils {

    @SneakyThrows
    public static UploadObject readValues(String path) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File(path), UploadObject.class);
    }

    @SneakyThrows
    public static <T> void generateCsvFile(List<T> data, String filePathWithoutDataStamp) {
        String finalFileOutputName = filePathWithoutDataStamp + "_" +
                LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        File file = new File(finalFileOutputName);
        FileWriter outputFile = new FileWriter(file);
        CSVWriter csvWriter = new CSVWriter(outputFile);

        T objectType = data.get(0);
        Field[] fields = objectType.getClass().getDeclaredFields();
        String[] fieldNames = new String[fields.length];

        for (int i = 0; i < fields.length; i++) {
            fieldNames[i] = fields[i].getName();
        }

        csvWriter.writeNext(fieldNames);

        for (T part : data) {
            String[] row = new String[fields.length];
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                Object value = fields[i].get(part);
                row[i] = value == null ? "" : value.toString();
            }
            csvWriter.writeNext(row);
        }
        csvWriter.close();
    }
}

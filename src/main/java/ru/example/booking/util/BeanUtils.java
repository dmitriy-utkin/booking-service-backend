package ru.example.booking.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@UtilityClass
public class BeanUtils {

    @SneakyThrows
    public static void copyNonNullProperties(Object from, Object to) {
        Class<?> clazz = from.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field f : fields) {
            f.setAccessible(true);
            Object value = f.get(from);

            if (value != null) {
                f.set(to, value);
            }
        }
    }
}

package zed.utils;

import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Array;

public final class Reflections {

    private Reflections() {
    }

    @SuppressWarnings("unchecked") // Array#newInstance return Object
    public static <T> Class<T[]> classOfArrayOfClass(Class<T> clazz) {
        return (Class<T[]>) Array.newInstance(clazz, 0).getClass();
    }

    public static void writeField(Object object, String field, Object value) {
        try {
            FieldUtils.writeField(object, field, value, true);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}

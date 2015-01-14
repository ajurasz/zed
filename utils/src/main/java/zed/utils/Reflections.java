package zed.utils;

import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Map;

import static java.lang.Character.toLowerCase;
import static java.lang.String.format;
import static org.apache.commons.lang3.reflect.FieldUtils.getField;

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

    public static <T> T readField(Object object, String field, Class<T> type) {
        try {
            Field actualField = getField(object.getClass(), field, true);
            if (!isInstanceOfOrWrappable(actualField.getType(), type)) {
                String message = format("Field %s is a type of %s instead of %s.", field, actualField.getType(), type);
                throw new IllegalStateException(message);
            }
            return (T) FieldUtils.readField(actualField, object, true);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static final Map<Class<?>, Class<?>> wrapperClasses = Maps.immutableMapOf(
            int.class, Integer.class,
            long.class, Long.class,
            short.class, Short.class,
            byte.class, Byte.class,
            char.class, Character.class,
            float.class, Float.class,
            double.class, Double.class);

    public static boolean isInstanceOfOrWrappable(Class<?> type, Class<?> instanceOf) {
        if (instanceOf.isAssignableFrom(type)) {
            return true;
        } else {
            type = wrapperClasses.get(type);
            return instanceOf.isAssignableFrom(type);
        }
    }

    public static String classNameToCamelCase(Class<?> clazz) {
        String simpleName = clazz.getSimpleName();
        return toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
    }

}

package zed.service.jsoncrud.mongo.crossstore.sql;

import static org.apache.commons.lang3.reflect.FieldUtils.readField;

public class Property<T> {

    private final String name;

    private final Class<T> type;

    public Property(String name, Class<T> type) {
        this.name = name;
        this.type = type;
    }


    public String name() {
        return name;
    }

    public Class<T> type() {
        return type;
    }

    public T readFrom(Object from) {
        try {
            return (T) readField(from, name, true);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}

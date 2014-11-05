package zed.service.jsoncrud.mongo.crossstore.sql;

public class Property {

    private final String name;

    private final Class<?> type;

    public Property(String name, Class<?> type) {
        this.name = name;
        this.type = type;
    }


    public String name() {
        return name;
    }

    public Class<?> type() {
        return type;
    }

}

package zed.service.jsoncrud.mongo.crossstore.sql;

import java.util.List;

public interface PropertiesResolver {

    <T> T readFrom(Object from, Property<T> property);

    List<Property> resolveProperties(Class<?> pojoClass);

    List<Property> resolveBasicProperties(Class<?> pojoClass);

    List<Property> resolvePojoProperties(Class<?> pojoClass);

}

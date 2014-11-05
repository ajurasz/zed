package zed.service.jsoncrud.mongo.sqlmirror;

import java.util.List;

public interface PropertiesResolver {

    List<Property> resolveProperties(Class<?> pojoClass);

    List<Property> resolveBasicProperties(Class<?> pojoClass);

    List<Property> resolvePojoProperties(Class<?> pojoClass);

}

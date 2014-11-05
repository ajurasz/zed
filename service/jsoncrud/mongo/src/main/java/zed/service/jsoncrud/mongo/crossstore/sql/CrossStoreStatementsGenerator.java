package zed.service.jsoncrud.mongo.crossstore.sql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class CrossStoreStatementsGenerator {

    private final DynamicSchemaExpander dynamicSchemaExpander;

    private final PropertiesResolver propertiesResolver;

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CrossStoreStatementsGenerator(DynamicSchemaExpander dynamicSchemaExpander, PropertiesResolver propertiesResolver, JdbcTemplate jdbcTemplate) {
        this.dynamicSchemaExpander = dynamicSchemaExpander;
        this.propertiesResolver = propertiesResolver;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(Object pojo) {
        dynamicSchemaExpander.expandPojoSchema(pojo.getClass());

        String insertStatement = "INSERT INTO " + pojo.getClass().getSimpleName() + " (";
        for (Property property : propertiesResolver.resolveBasicProperties(pojo.getClass())) {
            insertStatement += property.name() + ", ";
        }
        insertStatement = insertStatement.substring(0, insertStatement.length() - 2) + ") VALUES (";
        for (Property property : propertiesResolver.resolveBasicProperties(pojo.getClass())) {
            try {
                Field f = pojo.getClass().getDeclaredField(property.name());
                f.setAccessible(true);
                if (f.getType() == String.class) {
                    insertStatement += "\'" + f.get(pojo) + "\'";
                } else {
                    insertStatement += f.get(pojo);
                }
                insertStatement += ", ";
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        insertStatement = insertStatement.substring(0, insertStatement.length() - 2);
        insertStatement += ")";
        jdbcTemplate.execute(insertStatement);
    }

}

package zed.service.document.mongo.query;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.List;
import java.util.Map;

import static java.lang.Boolean.parseBoolean;

public class MongoQueryBuilder {

    public DBObject jsonToMongoQuery(DBObject jsonQuery) {
        BasicDBObject mongoQuery = new BasicDBObject();
        for (String key : jsonQuery.keySet()) {
            if (key.endsWith("Contains")) {
                addRestriction(mongoQuery, key, "Contains", "$regex", ".*" + jsonQuery.get(key) + ".*");
            } else if (key.endsWith("GreaterThan")) {
                addRestriction(mongoQuery, key, "GreaterThan", "$gt", jsonQuery.get(key));
            } else if (key.endsWith("GreaterThanEqual")) {
                addRestriction(mongoQuery, key, "GreaterThanEqual", "$gte", jsonQuery.get(key));
            } else if (key.endsWith("LessThan")) {
                addRestriction(mongoQuery, key, "LessThan", "$lt", jsonQuery.get(key));
            } else if (key.endsWith("LessThanEqual")) {
                addRestriction(mongoQuery, key, "LessThanEqual", "$lte", jsonQuery.get(key));
            } else {
                mongoQuery.put(key, new BasicDBObject("$eq", jsonQuery.get(key)));
            }
        }
        return mongoQuery;
    }

    public DBObject queryBuilderToSortConditions(Map<String, Object> queryBuilder) {
        int order = parseBoolean(queryBuilder.get("sortAscending").toString()) ? 1 : -1;
        List<String> orderBy = (List<String>) queryBuilder.get("orderBy");
        if (orderBy.size() == 0) {
            return new BasicDBObject("$natural", order);
        } else {
            BasicDBObject sort = new BasicDBObject();
            for (String by : orderBy) {
                sort.put(by, order);
            }
            return sort;
        }
    }

    private void addRestriction(BasicDBObject query, String propertyWithOperator, String propertyOperator, String operator, Object value) {
        String property = propertyWithOperator.replaceAll(propertyOperator + "$", "");
        if (query.containsField(property)) {
            BasicDBObject existingRestriction = (BasicDBObject) query.get(property);
            existingRestriction.put(operator, value);
        } else {
            query.put(property, new BasicDBObject(operator, value));
        }
    }

}

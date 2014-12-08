package zed.service.document.mongo.query;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

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

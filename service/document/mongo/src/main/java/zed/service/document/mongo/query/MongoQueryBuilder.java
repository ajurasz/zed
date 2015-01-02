package zed.service.document.mongo.query;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.List;
import java.util.Map;

import static java.lang.Boolean.parseBoolean;
import static zed.utils.Maps.immutableMapOf;

public class MongoQueryBuilder {

    private static final Map<String, String> SIMPLE_SUFFIX_OPERATORS = immutableMapOf(
            "GreaterThan", "$gt",
            "GreaterThanEqual", "$gte",
            "LessThan", "$lt",
            "LessThanEqual", "$lte",
            "NotIn", "$nin",
            "In", "$in");

    public DBObject jsonToMongoQuery(DBObject jsonQuery) {
        BasicDBObject mongoQuery = new BasicDBObject();
        keyLoop:
        for (String key : jsonQuery.keySet()) {
            String compoundKey = key.replaceAll("_", ".");
            for (String suffixOperator : SIMPLE_SUFFIX_OPERATORS.keySet()) {
                if (key.endsWith(suffixOperator)) {
                    addRestriction(mongoQuery, compoundKey, suffixOperator, SIMPLE_SUFFIX_OPERATORS.get(suffixOperator), jsonQuery.get(key));
                    continue keyLoop;
                }
            }
            if (key.endsWith("Contains")) {
                addRestriction(mongoQuery, compoundKey, "Contains", "$regex", ".*" + jsonQuery.get(key) + ".*");
            } else {
                mongoQuery.put(compoundKey, new BasicDBObject("$eq", jsonQuery.get(key)));
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

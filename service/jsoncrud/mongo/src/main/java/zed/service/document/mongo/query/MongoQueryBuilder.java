package zed.service.document.mongo.query;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class MongoQueryBuilder {

    public DBObject jsonToMongoQuery(DBObject jsonQuery) {
        DBObject mongoQuery = new BasicDBObject(jsonQuery.toMap());
        for (String key : mongoQuery.keySet()) {
            if (key.endsWith("Contains")) {
                mongoQuery.put(key.replaceAll("Contains$", ""), new BasicDBObject("$regex", ".*" + mongoQuery.get(key) + ".*"));
                mongoQuery.removeField(key);
            }
        }
        return mongoQuery;
    }

}

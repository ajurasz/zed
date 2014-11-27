package zed.service.jsoncrud.mongo.routing;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class BsonMapper {

    public DBObject bsonToJson(DBObject bson) {
        DBObject json = new BasicDBObject(bson.toMap());
        Object id = json.get("_id");
        if (id != null) {
            json.removeField("_id");
            json.put("_id", id.toString());
        }
        return json;
    }

}

package zed.service.jsoncrud.mongo.routing;

import com.google.common.collect.Lists;
import com.mongodb.DBObject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.List;

public class BsonToJsonMapperProcessor implements Processor {

    private final BsonMapper bsonMapper;

    public BsonToJsonMapperProcessor(BsonMapper bsonMapper) {
        this.bsonMapper = bsonMapper;
    }

    public BsonToJsonMapperProcessor() {
        this(new BsonMapper());
    }

    public static BsonToJsonMapperProcessor mapBsonToJson() {
        return new BsonToJsonMapperProcessor();
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        DBObject bson = exchange.getIn().getBody(DBObject.class);
        if (bson != null) {
            DBObject json = bsonMapper.bsonToJson(bson);
            exchange.getIn().setBody(json);
        } else {
            List<DBObject> bsons = exchange.getIn().getBody(List.class);
            if (bsons != null) {
                List<DBObject> resultBsons = Lists.newArrayListWithExpectedSize(bsons.size());
                for (DBObject bs : bsons) {
                    resultBsons.add(bsonMapper.bsonToJson(bs));
                }
                exchange.getIn().setBody(resultBsons);
            }
        }
    }

}

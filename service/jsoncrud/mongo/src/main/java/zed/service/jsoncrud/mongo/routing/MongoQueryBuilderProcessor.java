package zed.service.jsoncrud.mongo.routing;

import com.mongodb.DBObject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class MongoQueryBuilderProcessor implements Processor {

    private final MongoQueryBuilder mongoQueryBuilder = new MongoQueryBuilder();

    public static MongoQueryBuilderProcessor queryBuilder() {
        return new MongoQueryBuilderProcessor();
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        DBObject jsonQuery = exchange.getIn().getBody(DBObject.class);
        DBObject mongoQuery = mongoQueryBuilder.jsonToMongoQuery(jsonQuery);
        exchange.getIn().setBody(mongoQuery);
    }

}
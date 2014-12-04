package zed.service.document.mongo.routing;

public class FindOneOperation {

    private final String collection;

    private final String oid;

    public FindOneOperation(String collection, String oid) {
        this.collection = collection;
        this.oid = oid;
    }

    public String collection() {
        return collection;
    }

    public String oid() {
        return oid;
    }

}

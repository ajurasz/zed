package zed.service.jsoncrud.api.client;

public final class Pojos {

    private Pojos() {
    }

    public static String pojoClassToCollection(Class<?> pojoClass) {
        return pojoClass.getSimpleName();
    }

}

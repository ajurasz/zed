package zed.org.apache.camel.rest.annotations;

public interface BeanToExpose {

    @RestOperation
    String someOperation(String arg1, String arg2);

    String operationWithDifferentTypes(int arg1, float arg2);

    int operationReturningInteger(String arg1);

    PojoWithValue operationReturningPojoWithValue(String arg1);

}

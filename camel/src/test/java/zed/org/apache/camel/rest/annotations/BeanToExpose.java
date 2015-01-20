package zed.org.apache.camel.rest.annotations;

public class BeanToExpose {

    @RestOperation
    public String someOperation(String arg1, String arg2) {
        return arg1 + arg2;
    }

    @RestOperation
    public String operationWithDifferentTypes(int arg1, float arg2) {
        return (arg1 * arg2) + "";
    }

    @RestOperation
    public int operationReturningInteger(String arg1) {
        return Integer.parseInt(arg1);
    }

    @RestOperation
    public PojoWithValue operationReturningPojoWithValue(String arg1) {
        return new PojoWithValue(arg1);
    }

}

class PojoWithValue {

    private String value;

    public PojoWithValue() {
    }

    public PojoWithValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
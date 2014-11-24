package zed.quickstarts.fuse61.blueprint.camel.dozer;

public class Bar {

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Bar:" + value;
    }
}

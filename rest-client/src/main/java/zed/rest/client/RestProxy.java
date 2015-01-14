package zed.rest.client;

public interface RestProxy<T> {

    T post(Header... headers);

    T get(Header... headers);

}
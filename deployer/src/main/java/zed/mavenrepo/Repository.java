package zed.mavenrepo;

public class Repository {

    private final String id;

    private final String url;

    public Repository(String id, String url) {
        this.id = id;
        this.url = url;
    }

    public static Repository mavenCentral() {
        return new Repository("mavenCentral", "https://repo1.maven.org/maven2");
    }

    public String id() {
        return id;
    }

    public String url() {
        return url;
    }

}

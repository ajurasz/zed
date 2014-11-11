package zed.service.jsoncrud.api;

public class QueryBuilder<CLASS, QUERY> {

    private final Class<CLASS> classifier;

    private final QUERY query;

    private int page = 0;

    private int size = 25;

    private boolean sortAscending = true;

    private String[] orderBy = new String[0];

    public QueryBuilder(Class<CLASS> classifier, QUERY query) {
        this.classifier = classifier;
        this.query = query;
    }

    public Class<CLASS> classifier() {
        return classifier;
    }

    public QUERY query() {
        return query;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isSortAscending() {
        return sortAscending;
    }

    public void setSortAscending(boolean sortAscending) {
        this.sortAscending = sortAscending;
    }

    public String[] getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String[] orderBy) {
        this.orderBy = orderBy;
    }

}
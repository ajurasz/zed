package zed.service.document.sdk;

public class QueryBuilder<QUERY> {

    private final QUERY query;

    private int page = 0;

    private int size = 25;

    private boolean sortAscending = true;

    private String[] orderBy = new String[0];

    public QueryBuilder(QUERY query) {
        this.query = query;
    }

    public QUERY query() {
        return query;
    }

    public QUERY getQuery() {
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
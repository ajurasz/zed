package zed.camel.rpi.benchmark.statistic;

public interface Statistic {
    void updateCreated();
    void updateConsumed();
    Details details();
}

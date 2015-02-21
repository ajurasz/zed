package zed.camel.rpi.benchmark.statistic;

public interface Statistic {
    void update();
    Details details();
}

package zed.camel.rpi.benchmark.statistic;

import java.util.List;

public interface Statistic {
    void updateCreated();
    void updateConsumed();
    Details details();
    List<Details> list();
}

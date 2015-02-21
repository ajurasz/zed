package zed.camel.rpi.benchmark.statistic;

import org.apache.camel.util.StopWatch;

public class StatisticImpl implements Statistic {

    private StopWatch stopWatch;
    private Long counter = 0L;

    @Override
    public void call() {
        if (stopWatch == null) {
            stopWatch = new StopWatch();
        }
        counter++;
    }

    @Override
    public Details details() {
        long sec = (stopWatch.taken() / 1000);
        return new Details(
                counter,
                sec,                    // in seconds
                counter / sec           // messages / seconds
        );
    }
}

package zed.camel.rpi.benchmark.statistic;

import org.apache.camel.util.StopWatch;

import java.util.concurrent.atomic.AtomicLong;

public class StatisticImpl implements Statistic {

    private final StopWatch stopWatch = new StopWatch();
    private AtomicLong counter = new AtomicLong(0L);

    @Override
    public void update() {
        counter.incrementAndGet();
    }

    @Override
    public Details details() {
        long sec = (stopWatch.taken() / 1000);
        long currentCount = counter.get();
        return new Details(
                currentCount,
                sec,                    // in seconds
                currentCount / sec      // messages / seconds
        );
    }
}

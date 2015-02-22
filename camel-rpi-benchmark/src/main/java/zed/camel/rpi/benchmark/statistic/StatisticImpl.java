package zed.camel.rpi.benchmark.statistic;

import org.apache.camel.util.StopWatch;

import java.util.concurrent.atomic.AtomicLong;

public class StatisticImpl implements Statistic {

    private final StopWatch stopWatch = new StopWatch();
    private AtomicLong counterCreated = new AtomicLong(0L);
    private AtomicLong counterConsumed = new AtomicLong(0L);

    @Override
    public void updateCreated() {
        counterCreated.incrementAndGet();
    }

    @Override
    public void updateConsumed() {
        counterConsumed.incrementAndGet();
    }

    @Override
    public Details details() {
        long sec = (stopWatch.taken() / 1000);
        long createdCount = counterCreated.get();
        long consumedCount = counterConsumed.get();
        return new Details(
                createdCount,
                consumedCount,
                sec,                    // in seconds
                createdCount / sec,     // in messages / seconds
                consumedCount / sec     // out messages / seconds
        );
    }
}

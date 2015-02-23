package zed.camel.rpi.benchmark.statistic;

import org.apache.camel.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

public class StatisticImpl extends TimerTask implements Statistic {

    private final List<Details> list = new ArrayList<>();
    private final StopWatch stopWatch = new StopWatch();
    private AtomicLong counterCreated = new AtomicLong(0L);
    private AtomicLong counterConsumed = new AtomicLong(0L);

    public StatisticImpl(int period) {
        Timer timer = new Timer();
        timer.schedule(this, period * 1000, period * 1000);     // every x sec
    }

    @Override
    public void run() {
        list.add(details());
    }

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

    @Override
    public List<Details> list() {
        return list;
    }
}

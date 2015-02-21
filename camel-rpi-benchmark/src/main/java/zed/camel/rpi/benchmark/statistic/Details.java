package zed.camel.rpi.benchmark.statistic;

public class Details {
    private Long counter;
    private Long duration;
    private Long performance;

    public Details(Long counter, Long duration, Long performance) {
        this.counter = counter;
        this.duration = duration;
        this.performance = performance;
    }

    public Long getCounter() {
        return counter;
    }

    public Long getDuration() {
        return duration;
    }

    public Long getPerformance() {
        return performance;
    }
}

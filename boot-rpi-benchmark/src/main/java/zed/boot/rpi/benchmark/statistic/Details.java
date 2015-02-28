package zed.boot.rpi.benchmark.statistic;

public class Details {

    private long created;
    private Long consumed;
    private Long duration;
    private Long creationPerformance;
    private Long consumptionPerformance;

    public Details(long created, Long consumed, Long duration, Long creationPerformance, Long consumptionPerformance) {
        this.created = created;
        this.consumed = consumed;
        this.duration = duration;
        this.creationPerformance = creationPerformance;
        this.consumptionPerformance = consumptionPerformance;
    }

    public long getCreated() {
        return created;
    }

    public Long getConsumed() {
        return consumed;
    }

    public long getCurrentQueueSize() {
        return created - consumed;
    }

    public Long getDuration() {
        return duration;
    }

    public Long getCreationPerformance() {
        return creationPerformance;
    }

    public Long getConsumptionPerformance() {
        return consumptionPerformance;
    }
}

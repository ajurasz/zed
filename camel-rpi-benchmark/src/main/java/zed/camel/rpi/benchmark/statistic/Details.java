package zed.camel.rpi.benchmark.statistic;

public class Details {
    private Long created;
    private Long consumed;
    private Long duration;
    private Long creationPerformance;
    private Long consumptionPerformance;

    public Details(Long created, Long consumed, Long duration, Long creationPerformance, Long consumptionPerformance) {
        this.created = created;
        this.consumed = consumed;
        this.duration = duration;
        this.creationPerformance = creationPerformance;
        this.consumptionPerformance = consumptionPerformance;
    }

    public Long getCreated() {
        return created;
    }

    public Long getConsumed() {
        return consumed;
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

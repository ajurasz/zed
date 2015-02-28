package zed.boot.rpi.benchmark;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import zed.boot.rpi.benchmark.statistic.Statistic;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class Runner implements CommandLineRunner {

    @Autowired
    Statistic statistic;

    private Queue<String> queue = new ConcurrentLinkedQueue<>();

    @Override
    public void run(String... strings) throws Exception {
        // Producer
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                queue.add(UUID.randomUUID().toString());
                statistic.updateCreated();
            }
        }).start();

        // Consumer
        new Thread(() -> {
            while (true) {
                if (queue.poll() != null) {
                    statistic.updateConsumed();
                }
            }
        }).start();
    }
}

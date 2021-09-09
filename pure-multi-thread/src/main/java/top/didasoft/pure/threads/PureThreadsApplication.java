package top.didasoft.pure.threads;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.config.Task;
import org.springframework.util.concurrent.ListenableFuture;
import top.didasoft.pure.threads.config.TaskProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@SpringBootConfiguration
@EnableConfigurationProperties(value = {TaskProperties.class})
public class PureThreadsApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(PureThreadsApplication.class.getCanonicalName());

    public static void main(String[] args) {
        SpringApplication.run(PureThreadsApplication.class, args);

    }

    @Autowired
    private TaskProperties taskProperties;

    @Override
    public void run(String... args) throws Exception {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();

        threadPoolTaskExecutor.setCorePoolSize(taskProperties.getNoOfThreads());
        threadPoolTaskExecutor.setMaxPoolSize(taskProperties.getNoOfThreads());
        //threadPoolTaskExecutor.setQueueCapacity(1);
        threadPoolTaskExecutor.afterPropertiesSet();

        threadPoolTaskExecutor.setThreadNamePrefix("pure-multi");
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        threadPoolTaskExecutor.setAwaitTerminationSeconds(30);

        ArrayList<ListenableFuture<?>> futures = new ArrayList<>();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        for (int i = 0; i < taskProperties.getNoOfTasks(); i++) {
            ListenableFuture<?> listenable = threadPoolTaskExecutor.submitListenable(() -> {
                //log.info("Requesting...");
                try {
//                    Thread.sleep(taskProperties.getDelayMs());
                    busyWaitMicros(taskProperties.getDelayMs());
                } catch (Exception e) {
                    log.error("interrupted", e);
                }
            });
            futures.add(listenable);
        }

        log.info("Waiting all tasks to completed.");
        CompletableFuture<?> completableFuture = new CompletableFuture<>();
        List<? extends CompletableFuture<?>> completableFutures = futures.stream().map(listenableFuture -> listenableFuture.completable()).collect(Collectors.toList());
        completableFuture.allOf(completableFutures.toArray(new CompletableFuture[completableFutures.size()])).get();

        log.info("All tasks completed.");
        stopWatch.stop();
        //long stopWatchTime = stopWatch.getTime();
        String formatTime = stopWatch.formatTime();
        log.info("Tasks completed in {}", formatTime);

        threadPoolTaskExecutor.shutdown();


    }

    public static void busyWaitMicros(long micros){
        long waitUntil = System.nanoTime() + (micros * 1_000 * 1_000);
        while(waitUntil > System.nanoTime()){
//            log.info("time {}", waitUntil);
            ;
        }
    }
}

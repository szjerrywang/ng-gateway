package top.didasoft.core.ibm.mq;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class ScheduledCompletable {

    public static <T> CompletableFuture<T> schedule(
            ScheduledExecutorService executor,
            Supplier<T> command,
            long delay,
            TimeUnit unit
    ) {
        CompletableFuture<T> completableFuture = new CompletableFuture<>();
        executor.schedule(
                (() -> {
                    try {
                        return completableFuture.complete(command.get());
                    } catch (Throwable t) {
                        return completableFuture.completeExceptionally(t);
                    }
                }),
                delay,
                unit
        );
        return completableFuture;
    }

    public static <T> CompletableFuture<T> scheduleAsync(
            ScheduledExecutorService executor,
            Supplier<CompletableFuture<T>> command,
            long delay,
            TimeUnit unit
    ) {
        CompletableFuture<T> completableFuture = new CompletableFuture<>();
        executor.schedule(
                (() -> {
                    command.get().thenAccept(
                            t -> {completableFuture.complete(t);}
                    )
                            .exceptionally(
                                    t -> {completableFuture.completeExceptionally(t);return null;}
                            );
                }),
                delay,
                unit
        );
        return completableFuture;
    }
}

package top.didasoft.pure.websocket;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ReactiveTests {

    @Test
    public void testFibonacci() {
        Flux<Long> fibonacciGenerator = Flux.generate(
                () -> Tuples.<Long, Long>of(0L, 1L),
                (state, sink) -> {
                    sink.next(state.getT1());
                    return Tuples.of(state.getT2(), state.getT1() + state.getT2());
                });
        List<Long> fibonacciSeries = new LinkedList<>();
        int size = 50;
        fibonacciGenerator.take(size).subscribe(t -> {
            fibonacciSeries.add(t);
        });
        System.out.println(fibonacciSeries);
        assertEquals( 7778742049L, fibonacciSeries.get(size-1).longValue());
    }

    @Test
    public void testFibonacciFluxSink() {
        Flux<Long> fibonacciGenerator = Flux.create(e -> {
            long current = 1, prev = 0;
            AtomicBoolean stop = new AtomicBoolean(false);
            e.onDispose(()->{
                stop.set(true);
                System.out.println("******* Stop Received ****** ");
            });
            while (current > 0) {
                e.next(current);
                System.out.println("generated " + current);
                long next = current + prev;
                prev = current;
                current = next;
            }
            e.complete();
        });
        List<Long> fibonacciSeries = new LinkedList<>();
        fibonacciGenerator.take(50).subscribe(t -> {
            System.out.println("consuming " + t);
            fibonacciSeries.add(t);
        });
        System.out.println(fibonacciSeries);
    }

    @Test
    public void testThrownException() {
        Flux<Long> fibonacciGenerator = Flux.generate(() -> Tuples.<Long,
                Long>of(0L, 1L), (state, sink) -> {
            if (state.getT1() < 0)
                sink.error(new RuntimeException("Value out of bounds"));
            else
                sink.next(state.getT1());
            return Tuples.of(state.getT2(), state.getT1() + state.getT2());
        });
//        fibonacciGenerator
//                .subscribe(x -> {throw new RuntimeException("Subscriber threw error");});
        fibonacciGenerator
                .onErrorReturn(0L)
                .subscribe(System.out::println); //, System.out::println);
    }

    @Test
    public void testReactorThread() throws Exception {
        Flux<Long> fibonacciGenerator = Flux.generate(() -> Tuples.<Long,
                Long>of(0L, 1L), (state, sink) -> {
            if (state.getT1() < 0)
                sink.complete();
            else
                sink.next(state.getT1());
            print("Generating next of " + state.getT2());
            return Tuples.of(state.getT2(), state.getT1() + state.getT2());
        });
        fibonacciGenerator
                .filter(x -> {

                    print("Executing Filter");
                    return x < 100;
                })
                .doOnNext(x -> print("Next value is  " + x))
                .doFinally(x -> print("Closing "))
                .subscribe(x -> print("Sub received : " + x));
    }

    @Test
    public void testReactorDelayThread() throws Exception{
        Flux<Long> fibonacciGenerator = Flux.generate(() -> Tuples.<Long,
                Long>of(0L, 1L), (state, sink) -> {
            if (state.getT1() < 0)
                sink.complete();
            else
                sink.next(state.getT1());
            print("Generating next of " + state.getT2());
            return Tuples.of(state.getT2(), state.getT1() + state.getT2());
        });
        // Removed for brevity
        fibonacciGenerator
                .filter(x -> {
                    print("Executing Filter");
                    return x < 100;
                }).delayElements(Duration.ZERO)
                .doOnNext(x -> print("Next value is  "+x))
                .doFinally(x -> print("Closing "))
                .subscribe(x -> print("Sub received : "+x));
        Thread.sleep(500);
    }

    static void print(String text){
        System.out.println("["+Thread.currentThread().getName()+"] "+text);
    }
}

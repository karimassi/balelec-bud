package ch.epfl.balelecbud.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public class TaskToCompletableFutureAdapterTest {

    @Test
    public void taskToFutureAdapterSuccess() throws ExecutionException, InterruptedException {
        TestTask task = new TestTask();
        CompletableFuture<String> f = new TaskToCompletableFutureAdapter<>(task);
        task.complete();
        // check that the listener is triggered properly (MAY BE LATER)
        f.thenApply(new Function<String, String>() {
            @Override
            public String apply(String s) {
                return s;
            }
        });
        // explicitly wait because we want the test to actually check the future termination
        String result = f.get();
        Assert.assertEquals(task.value, result);
    }

    @Test
    public void taskToFutureAdapterFailure() throws InterruptedException {
        TestTask task = new TestTask();
        CompletableFuture<String> f = new TaskToCompletableFutureAdapter<>(task);
        task.fail();
        // check that listener is triggered properly (MAY BE LATER)
        f.exceptionally(new Function<Throwable, String>() {
            @Override
            public String apply(Throwable throwable) {
                return throwable.getCause().toString();
            }
        });
        // explicitly wait because we want the test to actually check the future termination
        try {
            f.get();
        } catch (ExecutionException e) {
            Assert.assertEquals(task.exception, e.getCause());
        }
    }
}

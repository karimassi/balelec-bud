package ch.epfl.balelecbud.utility;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class TaskToCompletableFutureAdapterTest {

    @Test
    public void taskToFutureAdapterSuccess() throws ExecutionException, InterruptedException {
        TestTask task = new TestTask();
        CompletableFuture<String> f = new TaskToCompletableFutureAdapter<>(task);
        task.complete();
        // check that the listener is triggered properly (MAY BE LATER)
        f.thenApply(s -> s);
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
        f.exceptionally(throwable -> throwable.getCause().toString());
        // explicitly wait because we want the test to actually check the future termination
        try {
            f.get();
        } catch (ExecutionException e) {
            Assert.assertEquals(task.exception, e.getCause());
        }
    }
}

package ch.epfl.balelecbud.testUtils;

import android.util.Log;

import junit.framework.AssertionFailedError;

import org.hamcrest.Matcher;
import org.junit.Assert;

import java.util.Objects;

import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;

public class TestAsyncUtils {
    private static final String TAG = TestAsyncUtils.class.getSimpleName();
    private int called = 0;
    private boolean hasFailed = false;

    public static void runOnUIThreadAndWait(Runnable runnable) throws Throwable {
        TestAsyncUtils sync = new TestAsyncUtils();
        runOnUiThread(() -> {
            runnable.run();
            Log.v(TAG, "runnable ran");
            sync.call();
        });
        sync.waitCall(1);
    }

    public void waitCall(int shouldBe) throws InterruptedException {
        synchronized (this) {
            this.wait(100);
            Log.d(TAG, "waitCall() called with: shouldBe = [" + shouldBe + "] and called = [" + called + "]");
            if (shouldBe != called) {
                this.wait(1000);
                Assert.assertEquals("Was not freed by a call", shouldBe, called);
            }
        }
    }

    public void call() {
        synchronized (this) {
            Log.d(TAG, "call() called with: called = [" + called + "]");
            ++called;
            this.notify();
        }
    }

    public void assertCalled(int nTimes) {
        synchronized (this) {
            Assert.assertEquals(nTimes, called);
        }
    }

    public void assertEquals(Object expected, Object actual) {
        if (!Objects.equals(expected, actual)) {
            hasFailed = true;
            Log.wtf(TAG, "assertEquals: test failed" +
                            "\n\texpected = " + expected.toString() +
                            "\n\tactual = " + actual.toString(),
                    new AssertionFailedError());
        }
    }

    public void assertNotNull(Object o) {
        if (o == null) {
            hasFailed = true;
            Log.wtf(TAG, "assertNotNull: test failed should be null but was",
                    new AssertionFailedError());
        }
    }

    public void fail() {
        hasFailed = true;
        Log.wtf(TAG, "fail: test failed", new AssertionFailedError());
    }

    public void fail(Throwable throwable) {
        hasFailed = true;
        Log.wtf(TAG, "fail: test failed with error", throwable);
    }

    public void assertTrue(boolean bool) {
        if (!bool) {
            hasFailed = true;
            Log.wtf(TAG, "assertTrue: test failed, should be true", new AssertionFailedError());
        }
    }

    public void assertFalse(boolean bool) {
        if (bool) {
            hasFailed = true;
            Log.wtf(TAG, "assertTrue: test failed, should be false", new AssertionFailedError());
        }
    }

    public void assertNoFailedTests() {
        Assert.assertFalse("Some tests failed, check logcat for details", this.hasFailed);
    }

    public <T> void assertThat(T actual, Matcher<? super T> matcher) {
        if (!matcher.matches(actual)) {
            hasFailed = true;
            Log.wtf(TAG, "assertThat: match failed" +
                    "\n\texpected = " + matcher.toString() +
                    "\n\tactual = " + actual.toString(), new AssertionFailedError());
        }
    }
}

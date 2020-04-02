package ch.epfl.balelecbud.util;

import android.util.Log;

import junit.framework.AssertionFailedError;

import org.junit.Assert;

import java.util.Objects;

public class TestAsyncUtils {
    private static final String TAG = TestAsyncUtils.class.getSimpleName();
    private int called = 0;
    private boolean hasFailed = false;

    public void waitCall(int shouldBe) throws InterruptedException {
        synchronized (this) {
            Log.d(TAG, "waitCall: " + called);
            if (shouldBe != called) {
                this.wait(1000);
                Assert.assertEquals("Was not freed by a call", shouldBe, called);
            }
        }
    }

    public void call() {
        synchronized (this) {
            Log.d(TAG, "call: " + called);
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

    public void assertNoFailedTests() {
        Assert.assertFalse("Some tests failed, check logcat for details", this.hasFailed);
    }
}

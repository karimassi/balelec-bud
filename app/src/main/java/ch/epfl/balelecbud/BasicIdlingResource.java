package ch.epfl.balelecbud;

import android.app.Activity;

import androidx.test.espresso.IdlingResource;

import java.util.concurrent.atomic.AtomicBoolean;

public class BasicIdlingResource implements androidx.test.espresso.IdlingResource {


    private ResourceCallback mCallBack;

    private AtomicBoolean isIdleNow = new AtomicBoolean(true);


    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public boolean isIdleNow() {
        return isIdleNow.get();
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        mCallBack = callback;
    }

    public void setIdleState(boolean state) {
        isIdleNow.set(state);
        if (isIdleNow() && mCallBack != null) {
            mCallBack.onTransitionToIdle();
        }
    }




}

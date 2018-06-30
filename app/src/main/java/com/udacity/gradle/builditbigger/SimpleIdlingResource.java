package com.udacity.gradle.builditbigger;

import android.support.test.espresso.IdlingResource;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nullable;


public class SimpleIdlingResource implements IdlingResource{

    @Nullable private volatile ResourceCallback idlingResourceCallback;
    private AtomicBoolean isIdle = new AtomicBoolean(true);

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public boolean isIdleNow() {
        return isIdle.get();
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        idlingResourceCallback = callback;
    }

    public void setIdleState(boolean idle){
        isIdle.set(idle);
        if(isIdle.get() && (idlingResourceCallback != null)){
            idlingResourceCallback.onTransitionToIdle();
        }
    }
}

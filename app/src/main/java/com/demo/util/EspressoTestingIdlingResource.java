package com.demo.util;


import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.idling.CountingIdlingResource;

/**
 * A counter for network delay testing purpose
 */
public class EspressoTestingIdlingResource {
    private static final String RESOURCE = "GLOBAL";

    private static CountingIdlingResource mCountingIdlingResource =
            new CountingIdlingResource(RESOURCE);

    public static void increment() {
        mCountingIdlingResource.increment();
    }

    public static void decrement() {
        mCountingIdlingResource.decrement();
    }

    public static IdlingResource getIdlingResource() {
        return mCountingIdlingResource;
    }

}
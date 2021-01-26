package com.example.florim.thirremjeshtrin;

/**
 * Created by Florim on 11/12/2016.
 */


import android.support.test.espresso.intent.rule.IntentsTestRule;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class MainActivityInstrumentationTest {

    @Rule
    public IntentsTestRule<MainActivity> mActivityRule = new IntentsTestRule<>(MainActivity.class);


    @Test
    public void shouldBeAbleToDisplay() {
        onView(withId(R.id.imgPlumber)).check(matches(isDisplayed()));
        onView(withId(R.id.imgElectrician)).check(matches(isDisplayed()));
        onView(withId(R.id.imgAuto)).check(matches(isDisplayed()));
    }

    @Test
    public void shouldBeClickable() {
        onView(withId(R.id.imgPlumber)).check(matches(isClickable()));
        onView(withId(R.id.imgElectrician)).check(matches(isClickable()));
        onView(withId(R.id.imgAuto)).check(matches(isClickable()));
    }

}

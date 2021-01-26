package com.example.florim.thirremjeshtrin;

/**
 * Created by Florim on 11/12/2016.
 */
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.IsNull.notNullValue;

@RunWith(AndroidJUnit4.class)
public class LoginActivityInstrumentationTest {

    @Rule
    public IntentsTestRule<Login> mActivityRule = new IntentsTestRule<>(Login.class);

    @Test
    public void triggerIntentTestButtonLogin() {
        // check that the button is there
        onView(withId(R.id.btnLogin)).check(matches(isClickable()));
        onView(withId(R.id.btnLinkToRegisterScreen)).check(matches(notNullValue()));
    }

    @Test
    public void triggerIntentTestButtonToRegister() {
        // check that the button is there
        onView(withId(R.id.btnLinkToRegisterScreen)).check(matches(isClickable()));
        onView(withId(R.id.btnLinkToRegisterScreen)).check(matches(notNullValue()));
    }
}
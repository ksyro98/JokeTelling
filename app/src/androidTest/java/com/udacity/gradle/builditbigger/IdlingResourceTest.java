package com.udacity.gradle.builditbigger;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.example.jokecontainer.JokeActivity;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static android.support.test.espresso.intent.Intents.intending;


@RunWith(AndroidJUnit4.class)
public class IdlingResourceTest {
    @Rule
    public IntentsTestRule activityTestRule = new IntentsTestRule<>(MainActivity.class);

    private IdlingResource idlingResource;
    private IdlingRegistry idlingRegistry;

    @Before
    public void registerIdlingResource(){
        MainActivityFragment fragment = getFragment();
        onView(ViewMatchers.withId(R.id.tell_joke_button)).perform(click());

        idlingResource = fragment.getIdlingResource();
        idlingRegistry = IdlingRegistry.getInstance();
        idlingRegistry.register(idlingResource);
    }

    @Before
    public void stubAllExternalIntents(){
        intending(not(isInternal())).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
    }

    @Test
    public void idlingResourceTest(){
        intended(allOf(hasExtraWithKey(JokeActivity.JOKE)));
    }

    @After
    public void unregisterIdlingResource(){
        idlingRegistry.unregister(idlingResource);
    }


    private MainActivityFragment getFragment(){
        MainActivity mainActivity = (MainActivity) activityTestRule.getActivity();
        MainActivityFragment fragment = new MainActivityFragment();
        android.support.v4.app.FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(fragment, "MainActivityFragment")
                .commit();
        return fragment;
    }
}

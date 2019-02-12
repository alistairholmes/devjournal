package com.alistairholmes.devjournal;

import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.alistairholmes.devjournal.activities.SignUpActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

/**
 * Testing for user authentication when signing into the app
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginScreenTest {

    @Rule
    public ActivityTestRule<SignUpActivity> mSignUpActivityTestRule =
            new ActivityTestRule<>(SignUpActivity.class);

    @Test
    public void openLoginActivity() {
        // Click on the sign in text view
        onView(withId(R.id.tv_sign_in)).perform(click());

        // Verify that Login Screen is displayed
        onView(withId(R.id.emailSignInButton)).check(matches(isDisplayed()));
    }

    @Test
    public void signInUserWithEmail() {
        String userEmail = "janedoe@email.com";
        String userPassword = "1234567";

        // Click on the sign in text view
        onView(withId(R.id.tv_sign_in)).perform(click());

        // Verify that Login Screen is displayed
        onView(withId(R.id.emailSignInButton)).check(matches(isDisplayed()));

        // Add user email and password
        onView(withId(R.id.fieldEmail)).perform(typeText(userEmail), closeSoftKeyboard());
        onView(withId(R.id.fieldPassword)).perform(typeText(userPassword),
                closeSoftKeyboard()); // Type new note description and close the keyboard

        // Click on the sign in button
        onView(withId(R.id.emailSignInButton)).perform(click());

        // Wait 15 seconds to connect to firebase auth server
        try {
            Thread.sleep(15000);
        } catch(InterruptedException e) {
            System.out.println("got interrupted!");
        }

        // Verify that journal activity is displayed
        onView(withId(R.id.fab)).check(matches(isDisplayed()));
    }


}

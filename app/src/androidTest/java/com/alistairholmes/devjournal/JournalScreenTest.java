package com.alistairholmes.devjournal;

import android.text.TextUtils;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.alistairholmes.devjournal.activities.JournalActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollTo;
import static androidx.test.espresso.intent.Checks.checkArgument;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

/**
 * Tests for the journal screen, the main screen which contains a list of all note entries.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class JournalScreenTest {

    /**
     * A custom Matcher which matches an item in a RecyclerView by its text.
     * View constraints:
     * View must be a child of a RecyclerView
     *
     * @param itemText the text to match
     * @return Matcher that matches text in the given view
     */
    private Matcher<View> withItemText(final String itemText) {
        checkArgument(!TextUtils.isEmpty(itemText), "itemText cannot be null or empty");
        return new TypeSafeMatcher<View>() {
            @Override
            public boolean matchesSafely(View item) {
                return allOf(
                        isDescendantOfA(isAssignableFrom(RecyclerView.class)),
                        withText(itemText)).matches(item);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is isDescendantOfA RV with text " + itemText);
            }
        };
    }

    /**
     * ActivityTestRule is a JUnit Rule (@Rule) to launch your activity under test.
     * Rules are interceptors which are executed for each test method and are important building
     * blocks of Junit tests.
     */
    @Rule
    public ActivityTestRule<JournalActivity> mJournalActivityTestRule =
            new ActivityTestRule<>(JournalActivity.class);

    // Tests to see if FAB button open AddEntry screen
    @Test
    public void clickAddNoteButton_opensAddEntryUi() throws Exception {
        // Click on the add note button
        onView(withId(R.id.fab)).perform(click());

        // Check if the add note screen is displayed
        onView(withId(R.id.editTextTitle)).check(matches(isDisplayed()));
    }

    @Test
    public void addNoteToNotesList() throws Exception {
        String newNoteTitle = "Espresso";
        String newNoteDescription = "UI testing for Android";

        // Click on the add note button
        onView(withId(R.id.fab)).perform(click());

        // Add note title and description
        // Type new note title
        onView(withId(R.id.editTextTitle)).perform(typeText(newNoteTitle), closeSoftKeyboard());
        onView(withId(R.id.editTextEntryDescription)).perform(typeText(newNoteDescription),
                closeSoftKeyboard()); // Type new note description and close the keyboard

        // Save the note
        onView(withId(R.id.fab_update)).perform(click());

        // Scroll notes list to added note, by finding its description
        onView(withId(R.id.recyclerView_journalEntries)).perform(
                scrollTo(hasDescendant(withText(newNoteDescription))));

        // Verify note is displayed on screen
        onView(withItemText(newNoteDescription)).check(matches(isDisplayed()));
    }
}

package com.alistairholmes.devjournal;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.alistairholmes.devjournal.customViewAsserts.RecyclerViewMatcher;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddNoteTest {

    @Rule
    public ActivityTestRule<JournalActivity> mActivityRule =
            new ActivityTestRule<>(JournalActivity.class);

    @Test
    public void addNewNote() {
        String noteTitle = "Test Note " + System.currentTimeMillis();
        noteTitle = noteTitle.substring(0, 19); // max length of title is 20
        String noteDescription = "Test Description " + System.currentTimeMillis();

        // Opens AddEntryActivity
        onView(withId(R.id.fab)).perform(click());

        // Key in information in AddEntryActivity
        onView(withId(R.id.editTextTitle)).perform(typeText(noteTitle));
        onView(withId(R.id.editTextEntryDescription)).perform(typeText(noteDescription)).perform(closeSoftKeyboard());
        onView(withId(R.id.fab_update)).perform(click());

       onView(withRecyclerView(R.id.recyclerView_journalEntries).atPositionOnView(0, R.id.entry_title))
               .check(matches(withText(noteTitle)));
        onView(withRecyclerView(R.id.recyclerView_journalEntries).atPositionOnView(0, R.id.entry_description))
                .check(matches(withText(noteDescription)));
    }

    public static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
        return new RecyclerViewMatcher(recyclerViewId);
    }
}
package com.wipro.datafeedapp;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.ListView;

import com.wipro.datafeedapp.model.DataFeed;
import com.wipro.datafeedapp.service.DataFeedService;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * UI test cases
 * Created by Hariharan on 18/02/18.
 */
@RunWith(AndroidJUnit4.class)
public class DataFeedAppUITest {

    ActivityTestRule<DataFeedActivity> rule = new ActivityTestRule<>(DataFeedActivity.class, false, true);

    @Test
    public void testLoadedData() {
        rule.launchActivity(new Intent());
        //wait till it loads data (quick hack)
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String title = (String) rule.getActivity().getSupportActionBar().getTitle();
        assertEquals("About Canada", title);
        ListView feedsList = rule.getActivity().getFeedsList();
        assertTrue("Feed count should be more than 1 but was only " + feedsList.getCount(), feedsList.getCount() > 1);
        DataFeed itemAtPosition = (DataFeed) feedsList.getItemAtPosition(0);
        assertEquals(itemAtPosition.getTitle(), "Beavers");
    }

    @Test
    public void testErrorData() {
        DataFeedService.setFetchUrl("");
        rule.launchActivity(new Intent());
        //wait till it loads data (quick hack)
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String title = (String) rule.getActivity().getSupportActionBar().getTitle();
        assertEquals("Data Feed App", title);
        ListView feedsList = rule.getActivity().getFeedsList();
        assertTrue("Feed count should be only 1 " + feedsList.getCount(), feedsList.getCount() == 1);
        DataFeed feed = (DataFeed) feedsList.getItemAtPosition(0);
        assertEquals(feed.getTitle(), "Error while fetching data!");
        assertTrue(feed.getDescription().contains("no protocol"));
    }
}

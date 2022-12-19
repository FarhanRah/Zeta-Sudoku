package com.example.sudoku;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import android.content.Context;
import android.widget.TextView;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class GameActivityTest {
    // int function
    @Test
    public void translateDpToPixel_int_returnsCorrectValue() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        int actual = GameActivity.translateDpToPixel(213, appContext);

        assertEquals(585, actual);
    }

    @Test
    public void translateDpToPixel_int_returnsWrongValue() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        int actual = GameActivity.translateDpToPixel(213, appContext);

        assertNotEquals(585585, actual);
    }

    // float function
    @Test
    public void translateDpToPixel_float_returnsCorrectValue() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        int actual = GameActivity.translateDpToPixel(213.0f, appContext);

        assertEquals(585, actual);
    }

    @Test
    public void translateDpToPixel_float_returnsWrongValue() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        int actual = GameActivity.translateDpToPixel(213.0f, appContext);

        assertNotEquals(585585, actual);
    }

    @Test
    public void getRow_returnsCorrectValue() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        TextView textView = new TextView(appContext);
        textView.setTag(13);

        int actual = GameActivity.getRow(textView);
        assertEquals(1, actual);
    }

    @Test
    public void getRow_returnsWrongValue() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        TextView textView = new TextView(appContext);
        textView.setTag(13);

        int actual = GameActivity.getRow(textView);
        assertNotEquals(3, actual);
    }

    @Test
    public void getColumn_returnsCorrectValue() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        TextView textView = new TextView(appContext);
        textView.setTag(13);

        int actual = GameActivity.getColumn(textView);
        assertEquals(3, actual);
    }

    @Test
    public void getColumn_returnsWrongValue() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        TextView textView = new TextView(appContext);
        textView.setTag(13);

        int actual = GameActivity.getColumn(textView);
        assertNotEquals(1, actual);
    }
}



package com.perpedus.android.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Util class that handles keyboard changes
 */
public class KeyboardUtils {

    /**
     * Closes the keyboard
     *
     * @param activity
     */
    public static void closeKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(activity.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText() && activity.getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * Focuses an EditText and opens the keyboard
     *
     * @param context
     * @param editText
     */
    public static void focusEditText(Context context, EditText editText) {
        editText.requestFocus(View.FOCUS_RIGHT);
        editText.setSelection(editText.getText().length());
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

}

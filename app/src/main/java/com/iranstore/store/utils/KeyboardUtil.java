package com.iranstore.store.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
public class KeyboardUtil {

    public static void closeKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}

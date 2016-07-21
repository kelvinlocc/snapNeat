package com.accordhk.SnapNEat.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by jm on 29/1/16.
 */
public class CustomFontTextView extends TextView {
    public CustomFontTextView(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public CustomFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public CustomFontTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont(context);
    }

//    public CustomFontTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface("HiraginoSansGBW3.ttf", context);
        setTypeface(customFont);
    }
}

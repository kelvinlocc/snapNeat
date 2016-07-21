package com.accordhk.SnapNEat.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by jm on 29/1/16.
 */
public class CustomFontButton extends Button {
    public CustomFontButton(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public CustomFontButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public CustomFontButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont(context);
    }

//    public CustomFontButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        applyCustomFont(context);
//    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface("HiraginoSansGBW3.ttf", context);
        setTypeface(customFont);
    }
}

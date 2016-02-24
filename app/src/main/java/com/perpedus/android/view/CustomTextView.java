package com.perpedus.android.view;


import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.perpedus.android.util.Constants;
import com.perpedus.android.util.Typefaces;


/**
 * Custom text view
 */
public class CustomTextView extends TextView {

    /**
     * Constructor
     *
     * @param context
     */
    public CustomTextView(Context context) {
        super(context);
    }


    /**
     * Constructor
     *
     * @param context
     * @param attrs
     */
    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    /**
     * Constructor
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public void setTypeface(Typeface tf) {
        Typeface typeface = Typefaces.get(Constants.CUSTOM_FONT);
        if (typeface != null) {
            super.setTypeface(typeface);
            return;
        }
        super.setTypeface(tf);
    }

    /*@Override
    public void setTypeface(Typeface tf, int style) {

        switch (style) {
            case Typeface.BOLD_ITALIC:
                super.setTypeface(Typefaces.get(Constants.ROBOTO_REGULAR_BOLD_ITALIC), style);
                break;
            case Typeface.BOLD:
                super.setTypeface(Typefaces.get(Constants.ROBOTO_REGULAR_BOLD), style);
                break;
            case Typeface.ITALIC:
                super.setTypeface(Typefaces.get(Constants.ROBOTO_REGULAR_ITALIC), style);
                break;
            default:
                super.setTypeface(Typefaces.get(Constants.ROBOTO_REGULAR), style);
                break;
        }
    }*/

}

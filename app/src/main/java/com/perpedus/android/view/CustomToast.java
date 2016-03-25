package com.perpedus.android.view;


import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.widget.Toast;


public class CustomToast {

    private volatile static CustomToast	globalToast	= null;


    /**
     * Make a standard toast
     *
     * @param context
     * @param text
     * @return
     */
    public static CustomToast makeText(Context context, CharSequence text) {
        return new CustomToast(context, text.toString(), Toast.LENGTH_SHORT);
    }


    /**
     * Make a standard toast
     *
     * @param context
     * @param text
     * @param duration
     * @return
     */
    public static CustomToast makeText(Context context, CharSequence text, int duration) {
        return new CustomToast(context, text.toString(), duration);
    }


    /**
     * Make a standard toast
     *
     * @param context
     * @param resId
     * @return
     * @throws Resources.NotFoundException
     */
    public static CustomToast makeText(Context context, int resId) throws Resources.NotFoundException {
        return new CustomToast(context, context.getString(resId), Toast.LENGTH_SHORT);
    }


    /**
     * Make a standard toast
     *
     * @param context
     * @param resId
     * @param duration
     * @return
     * @throws Resources.NotFoundException
     */
    public static CustomToast makeText(Context context, int resId, int duration) throws Resources.NotFoundException {
        return new CustomToast(context, context.getString(resId), duration);
    }

    private LayoutInflater inflater;

    private Toast internalToast;


    private CustomToast(Context context, String text, int duration) {
        internalToast = Toast.makeText(context, text, duration);
    }


    /**
     * Cancel the toast
     */
    public void cancel() {
        internalToast.cancel();
    }


    /**
     * Show the toast
     */
    public void show() {
        show(true);
    }


    /**
     * Show the toast
     *
     * @param cancelCurrent
     */
    public void show(boolean cancelCurrent) {
        // cancel current
        if (cancelCurrent && (globalToast != null)) {
            globalToast.cancel();
        }

        // save an instance of this current notification
        globalToast = this;

        internalToast.show();
    }

}
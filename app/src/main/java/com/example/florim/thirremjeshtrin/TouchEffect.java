package com.example.florim.thirremjeshtrin;

/**
 * * Created by GentR on 03-Dec-16
 **/

import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * The Class TouchEffect is the implementation of OnTouchListener interface. You
 * can apply this to views mostly Buttons to provide Touch effect and that view
 * must have a valid background. The current implementation simply set Alpha
 * value of View background.
 */

public class TouchEffect implements OnTouchListener
{
    /* (non-Javadoc)
     * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
     */
    @Override
    public boolean onTouch(View v, MotionEvent event){
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            Drawable d = v.getBackground();
            d.mutate();
            d.setAlpha(150);
            v.setBackground(d);
        }
        else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL){
            Drawable d = v.getBackground();
            d.setAlpha(255);
            v.setBackground(d);
        }
        return false;
    }
}
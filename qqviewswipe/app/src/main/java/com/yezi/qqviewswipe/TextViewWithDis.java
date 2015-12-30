package com.yezi.qqviewswipe;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewWithDis extends TextView {

    private int distance = 0;

    public TextViewWithDis(Context context) {
        this(context, null);
    }

    public TextViewWithDis(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextViewWithDis(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int dis) {
        distance = dis;
    }
}

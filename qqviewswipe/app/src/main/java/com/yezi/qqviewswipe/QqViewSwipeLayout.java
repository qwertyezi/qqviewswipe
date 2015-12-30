package com.yezi.qqviewswipe;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class QqViewSwipeLayout extends FrameLayout {
    private View mContentView;
    private List<View> mItemView;
    private ViewDragHelper mDragHelper;
    private int mDragDistance;
    private float downX;
    private float upX;
    private onItemViewClickListener mListener;
    private View mClickItemView;
    private boolean afterClickClose = true;
    private int sensitivity;
    private Context mContext;

    public QqViewSwipeLayout(Context context) {
        this(context, null);
    }

    public QqViewSwipeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QqViewSwipeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        mDragHelper = ViewDragHelper.create(this, 1.0f, mCallback);
        sensitivity = (int) (5 * mContext.getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mItemView = getItemViewList();
        mContentView = getContentView();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        setTextViewDistance();
        layoutViewToParent();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isInContentView(event)) {
                    mDragHelper.captureChildView(mContentView, event.getPointerId(0));
                    mClickItemView = whichItemView(event);
                } else {
                    mClickItemView = mContentView;
                }
                break;
            case MotionEvent.ACTION_UP:
                upX = event.getX();
                break;
        }
        mDragHelper.processTouchEvent(event);
        return true;
    }

    private boolean isInContentView(MotionEvent event) {
        Rect rect = new Rect();
        mContentView.getHitRect(rect);
        if (rect.contains((int) event.getX(), (int) event.getY())) {
            return true;
        } else {
            return false;
        }
    }

    private View whichItemView(MotionEvent event) {
        Rect rect = new Rect();
        for (View view : mItemView) {
            view.getHitRect(rect);
            if (rect.contains((int) event.getX(), (int) event.getY())) {
                return view;
            }
            rect.setEmpty();
        }
        return null;
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private void setTextViewDistance() {
        for (int i = 0; i < mItemView.size(); i++) {
            TextViewWithDis textViewWithDis = (TextViewWithDis) mItemView.get(i);
            if (i == 0) {
                textViewWithDis.setDistance(0);
            } else {
                textViewWithDis.setDistance(((TextViewWithDis) mItemView.get(i - 1)).getMeasuredWidth()
                        + ((TextViewWithDis) mItemView.get(i - 1)).getDistance());

                if (i == mItemView.size() - 1) {
                    mDragDistance = textViewWithDis.getDistance()
                            + textViewWithDis.getMeasuredWidth();
                }
            }
        }
    }

    private void layoutViewToParent() {
        if (mContentView != null) {
            mContentView.layout(0, 0, mContentView.getMeasuredWidth(), mContentView.getMeasuredHeight());
        }
        for (View view : mItemView) {
            TextViewWithDis textViewWithDis = (TextViewWithDis) view;
            view.layout(mContentView.getMeasuredWidth() + textViewWithDis.getDistance(),
                    0,
                    mContentView.getMeasuredWidth() + textViewWithDis.getDistance() + textViewWithDis.getMeasuredWidth(),
                    mContentView.getMeasuredHeight());
        }
    }

    private View getContentView() {
        if (getChildCount() == 0)
            return null;
        return getChildAt(0);
    }

    private List<View> getItemViewList() {
        mItemView = new ArrayList<>();
        for (int i = 1; i < getChildCount(); i++) {
            mItemView.add((TextViewWithDis) getChildAt(i));
        }
        return mItemView;
    }

    private ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == mContentView;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return Math.min(Math.max(left, -mDragDistance), 0);
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return mDragDistance;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if (Math.abs(upX - downX) < sensitivity && mClickItemView != null) {
                if (mClickItemView == mContentView) {
                    if (isOpen()) {
                        smoothClose();
                    } else {
                        mListener.onItemViewClick(mClickItemView);
                    }
                } else {
                    mListener.onItemViewClick(mClickItemView);
                    if (afterClickClose) {
                        smoothClose();
                    }
                }
                mClickItemView = null;
                return;
            }
            float minSpeed = mDragHelper.getMinVelocity();
            int minSlop = mDragHelper.getTouchSlop();
            float distanceX = upX - downX;
            if (distanceX > 0) {
                if (Math.abs(distanceX) > mDragDistance / 2 || Math.abs(xvel) > minSpeed)
                    smoothClose();
                else
                    smoothOpen();
            } else {
                if (Math.abs(distanceX) > mDragDistance / 2 || Math.abs(xvel) > minSpeed)
                    smoothOpen();
                else
                    smoothClose();
            }
//            float distanceX = Math.abs(releasedChild.getX());
//            if (xvel < -minSpeed || distanceX >= mDragDistance / 2) {
//                smoothOpen();
//            } else if (xvel > minSpeed || distanceX < mDragDistance / 2) {
//                smoothClose();
//            }
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            int width= mContentView.getWidth();
            int i = left / width;
            if (changedView == mContentView) {
                adjustItemView(dx);
            }
            ViewCompat.postInvalidateOnAnimation(QqViewSwipeLayout.this);
        }
    };

    private void smoothOpen() {
        mDragHelper.smoothSlideViewTo(mContentView, -mDragDistance, 0);
        ViewCompat.postInvalidateOnAnimation(QqViewSwipeLayout.this);
    }

    private void smoothClose() {
        mDragHelper.smoothSlideViewTo(mContentView, 0, 0);
        ViewCompat.postInvalidateOnAnimation(QqViewSwipeLayout.this);
    }

    private void adjustItemView(int dx) {
        for (View view : mItemView) {
            view.setX(view.getX() + dx);
        }
    }

    private boolean isOpen() {
        if (mContentView.getX() == 0) {
            return false;
        } else {
            return true;
        }
    }

    public interface onItemViewClickListener {
        void onItemViewClick(View view);
    }

    public void setOnItemViewClickListener(onItemViewClickListener listener) {
        mListener = listener;
    }

    public void setAfterClickClose(boolean flag) {
        afterClickClose = flag;
    }
}

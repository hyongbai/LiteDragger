package me.yourbay.litelistdragger;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;import java.lang.Math;import java.lang.Override;import java.lang.String;

public class DragListView extends ListView {

    private final String TAG = "DragList";
    //
    private int mDragPosition;
    private int mStartPosition;
    //
    private int mLayoutYOffset;
    private int mDragPointToTop;
    private DragListener mDragListener;
    private DragHelper mDragHelper;

    private static final int ANIMATION_DURATION = 200;

    public DragListView(Context context) {
        this(context, null);
    }

    public DragListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDragHelper = new DragHelper(context);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        if (adapter instanceof DragListener) {
            mDragListener = (DragListener) adapter;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            final int x = (int) ev.getX();
            final int y = (int) ev.getY();
            final int position = pointToPosition(x, y);
            if (position == AdapterView.INVALID_POSITION) {
                return super.onInterceptTouchEvent(ev);
            }
            final View child = getChildAt(position - getFirstVisiblePosition());
            mLayoutYOffset = (int) (ev.getRawY() - y);
            mDragPointToTop = y - child.getTop();
            if (mDragListener != null) {
                View trigger = mDragListener.getTriggerView(child);
                if (trigger != null && trigger.getLeft() < x && x < trigger.getRight()) {
                    child.setDrawingCacheEnabled(true);
                    Bitmap bm = Bitmap.createBitmap(child.getDrawingCache(true));// 根据cache创建一个新的bitmap对象.
                    child.setDrawingCacheEnabled(false);
                    startDrag(bm, y);
                    return false;
                }
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mDragHelper.isDragging()) {
            final int act = ev.getAction();
            if (act == MotionEvent.ACTION_UP) {
                onDrop();
            } else if (act == MotionEvent.ACTION_MOVE) {
                onDrag((int) ev.getY());
            }
            return true;
        }
        return super.onTouchEvent(ev);
    }

    private void startDrag(Bitmap bm, int y) {
        mDragHelper.showDrag(bm, getPaddingLeft(), y - mDragPointToTop + mLayoutYOffset);
        mStartPosition = mDragPosition = pointToPosition(0, y);
        if (mDragListener != null) {
            mDragListener.onDrag(mStartPosition);
        }
    }

    public void onDrag(int y) {
        if (mDragHelper.isDragging() && y >= mDragPointToTop) {
            mDragHelper.showDrag(null, getPaddingLeft(), y - mDragPointToTop + mLayoutYOffset);
        }
        animChild(y);
        doScroll(y);
    }

    private void onDrop() {
        mDragHelper.dismiss();
        if (mDragListener != null) {
            mDragListener.onDrop(mDragPosition);
        }
    }

    private void doScroll(int y) {
        final int autoScrollOffset = 20;
        final int h = getMeasuredHeight();
        final int upScrollBounce = h >> 2;
        final int downScrollBounce = h - upScrollBounce;
        final int step;
        if (y < upScrollBounce) {
            step = autoScrollOffset;
        } else if (y > downScrollBounce) {
            step = -autoScrollOffset;
        } else {
            return;
        }
        View view = getChildAt(mDragPosition - getFirstVisiblePosition());
        setSelectionFromTop(mDragPosition, view.getTop() + step);
//        Log.d(TAG, "doScroll    top=" + view.getTop() + "    step=" + step);
    }

    private void animChild(int y) {
        int touchPosition = pointToPosition(0, y);
        if (touchPosition == INVALID_POSITION || touchPosition == mDragPosition) {
            return;
        }
        final int last = mDragPosition;
        final boolean isDown = last < touchPosition;
        final boolean isStart = mStartPosition == touchPosition;
        final boolean isDownFromStart = mStartPosition < touchPosition;
        //
        final int param = (isDown ? 1 : -1);
        final int positionOffset = isStart ? -param : (isDownFromStart == isDown ? 0 : -param);
        final int animPosition = touchPosition + positionOffset;
        //
        View child = getChildAt(animPosition - getFirstVisiblePosition());
        final boolean isReturn = last == animPosition;
        final int yOffset = child.getHeight() + getDividerHeight();
        final int byY = (isReturn ? param : -param) * Math.abs(yOffset);
        Log.d(TAG, "animChild    last=" + last + "    to=" + touchPosition + "  anim=" + animPosition + " " + isReturn);
        //
        child.startAnimation(isReturn ? getToSelfAnimation(byY) : getFromSelfAnimation(byY));
        mDragPosition = touchPosition;
    }

    public static final Animation getFromSelfAnimation(int y) {
        final TranslateAnimation go = new TranslateAnimation(
                Animation.ABSOLUTE, 0, Animation.ABSOLUTE, 0,
                Animation.ABSOLUTE, 0, Animation.ABSOLUTE, y);
        go.setInterpolator(new AccelerateDecelerateInterpolator());
        go.setFillAfter(true);
        go.setDuration(ANIMATION_DURATION);
        go.setInterpolator(new AccelerateInterpolator());
        return go;
    }

    public static final Animation getToSelfAnimation(int y) {
        final TranslateAnimation go = new TranslateAnimation(
                Animation.ABSOLUTE, 0, Animation.ABSOLUTE, 0,
                Animation.ABSOLUTE, y, Animation.ABSOLUTE, 0);
        go.setInterpolator(new AccelerateDecelerateInterpolator());
        go.setFillAfter(true);
        go.setDuration(ANIMATION_DURATION);
        go.setInterpolator(new AccelerateInterpolator());
        return go;
    }

    public interface DragListener {
        void onDrag(int position);

        void onDrop(int position);

        View getTriggerView(View convertView);
    }

}
package me.yourbay.litelistdragger;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * Created by ram on 15/10/30.
 */
public class DragHelper {
    private Context mContext;
    private View mDraggedView;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowParams;

    public DragHelper(Context ctx) {
        mContext = ctx;
        mWindowManager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
    }

    public void showDrag(Bitmap bmp, int x, int y) {
        if (mWindowParams == null) {
            mWindowParams = new WindowManager.LayoutParams();
            mWindowParams.gravity = Gravity.TOP | Gravity.LEFT;
            mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
            mWindowParams.alpha = 0.8f;
            mWindowParams.windowAnimations = 0;
            mWindowParams.format = PixelFormat.TRANSLUCENT;
        }

        mWindowParams.x = x;
        mWindowParams.y = y;

        if (mDraggedView == null) {
            ImageView iv = new ImageView(mContext);
            iv.setImageBitmap(bmp);
            iv.setBackgroundColor(0x55555555);
            mWindowManager.addView(iv, mWindowParams);
            mDraggedView = iv;
        } else {
            mWindowManager.updateViewLayout(mDraggedView, mWindowParams);
        }
    }

    public void dismiss() {
        if (mDraggedView == null) {
            return;
        }
        mWindowManager.removeView(mDraggedView);
        mDraggedView = null;
    }

    public boolean isDragging() {
        return mDraggedView != null;
    }
}

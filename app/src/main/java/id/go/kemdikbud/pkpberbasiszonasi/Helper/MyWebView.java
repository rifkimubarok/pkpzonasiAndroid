package id.go.kemdikbud.pkpberbasiszonasi.Helper;

import android.content.Context;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

public class MyWebView extends WebView implements NestedScrollingChild {
    private static String TAG = MyWebView.class.getSimpleName();
    private int mLastMotionY;

    private final int[] mScrollOffset = new int[2];
    private final int[] mScrollConsumed = new int[2];

    private int mNestedYOffset;

    private NestedScrollingChildHelper mChildHelper;

    public MyWebView(Context context) {
        super(context);
        init();
    }

    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event){
        try {
            int height = (int) Math.floor(this.getContentHeight() * this.getScale());
            int webViewHeight = this.getMeasuredHeight();
            if(this.getScrollY() + webViewHeight >= height){
                requestDisallowInterceptTouchEvent(false);
            }else{
                requestDisallowInterceptTouchEvent(true);
            }
            return super.onTouchEvent(event);
        }catch (Exception e){

        }
        return super.onTouchEvent(event);
    }
    /*@Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = false;


        MotionEvent trackedEvent = MotionEvent.obtain(event);

        final int action = MotionEventCompat.getActionMasked(event);

        if (action == MotionEvent.ACTION_DOWN) {
            mNestedYOffset = 0;
        }

        int y = (int) event.getY();

        event.offsetLocation(0, mNestedYOffset);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = y;
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
                result = super.onTouchEvent(event);
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaY = mLastMotionY - y;

                if (dispatchNestedPreScroll(0, deltaY, mScrollConsumed, mScrollOffset)) {
                    deltaY -= mScrollConsumed[1];
                    trackedEvent.offsetLocation(0, mScrollOffset[1]);
                    mNestedYOffset += mScrollOffset[1];
                }

                mLastMotionY = y - mScrollOffset[1];

                int oldY = getScrollY();
                int newScrollY = Math.max(0, oldY + deltaY);
                int dyConsumed = newScrollY - oldY;
                int dyUnconsumed = deltaY - dyConsumed;

                if (dispatchNestedScroll(0, dyConsumed, 0, dyUnconsumed, mScrollOffset)) {
                    mLastMotionY -= mScrollOffset[1];
                    trackedEvent.offsetLocation(0, mScrollOffset[1]);
                    mNestedYOffset += mScrollOffset[1];
                }

                result = super.onTouchEvent(trackedEvent);
                trackedEvent.recycle();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                stopNestedScroll();
                result = super.onTouchEvent(event);
                break;
        }
        return result;
    }*/

    // NestedScrollingChild

   /* @Override
    public boolean onTouchEvent(MotionEvent event) {

        //Check pointer index to avoid -1 (error)
        if (MotionEventCompat.findPointerIndex(event, 0) == -1) {
            return super.onTouchEvent(event);
        }

        if (event.getPointerCount() >= 2) {
            requestDisallowInterceptTouchEvent(true);
        } else {
            requestDisallowInterceptTouchEvent(false);
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        requestDisallowInterceptTouchEvent(true);
    }*/
}

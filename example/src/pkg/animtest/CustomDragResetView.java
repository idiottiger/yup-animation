package pkg.animtest;

import com.idiot2ger.yupanimation.Animation;
import com.idiot2ger.yupanimation.AnimationCallback;
import com.idiot2ger.yupanimation.AnimationStateChangeCallback;
import com.idiot2ger.yupanimation.State;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class CustomDragResetView extends View {


  static int STATE_NONE = 0;
  static int STATE_MOVE = 1;
  static int STATE_ANIMATION = 2;

  private Bitmap mBitmap;
  private PointF mDefaultPos;
  private PointF mCurrentPos;
  private boolean mIsInited;
  private Paint mPaint;
  private Float2ValueAnimation mResetAnimation;
  private PointF mTouchPos;
  private int mState;


  public CustomDragResetView(Context context) {
    this(context, null);
  }

  public CustomDragResetView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public CustomDragResetView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    mDefaultPos = new PointF();
    mCurrentPos = new PointF();
    mTouchPos = new PointF();
    mPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
    mResetAnimation = new Float2ValueAnimation();
    mResetAnimation.setDuration(500);
    mResetAnimation.setInterpolator(new DecelerateInterpolator(0.245f));

    mResetAnimation.addCallback(new AnimationCallback<PointF>() {
      @Override
      public void onAnimationUpdate(float fraction, PointF value) {
        mCurrentPos.set(value);
        postInvalidate();
      }
    });

    mResetAnimation.addStateChangeCallback(new AnimationStateChangeCallback() {
      @Override
      public void onAnimationStateChanged(Animation<?> animation, State state) {
        if (state == State.STATE_END) {
          mState = STATE_NONE;
        }
      }
    });
  }

  public void setDragBitmapItem(Bitmap bitmap) {
    mBitmap = bitmap;
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {

    if (mState == STATE_ANIMATION) {
      return false;
    }

    final int action = event.getAction() & MotionEvent.ACTION_MASK;
    final float x = event.getX();
    final float y = event.getY();
    switch (action) {
      case MotionEvent.ACTION_DOWN:
        if (isBitmapClicked(x, y)) {
          mTouchPos.set(x, y);
          mState = STATE_MOVE;
        }
        break;
      case MotionEvent.ACTION_MOVE:
        if (mState == STATE_MOVE) {
          float mx = x - mTouchPos.x;
          float my = y - mTouchPos.y;
          mCurrentPos.x += mx;
          mCurrentPos.y += my;
          mTouchPos.set(x, y);
          postInvalidate();
        }
        break;
      case MotionEvent.ACTION_CANCEL:
      case MotionEvent.ACTION_UP:
        if (!mCurrentPos.equals(mDefaultPos)) {
          mState = STATE_ANIMATION;
          mResetAnimation.reset();
          mResetAnimation.setStartValue(new PointF(mCurrentPos.x, mCurrentPos.y));
          mResetAnimation.setEndValue(new PointF(mDefaultPos.x, mDefaultPos.y));
          mResetAnimation.start();
        } else {
          mState = STATE_NONE;
        }
        break;
    }

    return true;
  }

  private boolean isBitmapClicked(float x, float y) {
    return mBitmap != null && !mBitmap.isRecycled() && x > mCurrentPos.x && x < mCurrentPos.x + mBitmap.getWidth()
        && y > mCurrentPos.y && y < mCurrentPos.y + mBitmap.getHeight();
  }


  @Override
  public void draw(Canvas canvas) {
    if (mBitmap != null && !mBitmap.isRecycled()) {
      if (!mIsInited) {
        mDefaultPos.x = mCurrentPos.x = (canvas.getWidth() - mBitmap.getWidth()) / 2.0f;
        mDefaultPos.y = mCurrentPos.y = (canvas.getHeight() - mBitmap.getHeight()) / 2.0f;
        
        mIsInited = true;
      }

      canvas.drawBitmap(mBitmap, mCurrentPos.x, mCurrentPos.y, mPaint);
    }
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    mIsInited = false;
  }


}

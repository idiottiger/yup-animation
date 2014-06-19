package pkg.animtest;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.idiot2ger.yupanimation.AnimationCallback;
import com.idiot2ger.yupanimation.AnimationUtils;
import com.idiot2ger.yupanimation.IAnimationTransaction;

public class MainActivity extends Activity {


  private SinewaveAnimation mMoveAnimation;
  private RotateAnimation mRotateAnimation;
  private SinewaveAnimation mAlphaAnimation;
  private TextView mTextView;
  private float mPosY;

  private IAnimationTransaction mTransaction;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(pkg.animtest.R.layout.activity_main);

    mTextView = (TextView) findViewById(R.id.tv1);

    mMoveAnimation = new SinewaveAnimation(300);
    mMoveAnimation.setDuration(3000);
    mMoveAnimation.setRepeatTimes(2);
    // mAnimation.setLoop(true);

    mRotateAnimation = new RotateAnimation();
    mRotateAnimation.setStartValue(0.f);
    mRotateAnimation.setEndValue(-360.0f);
    mRotateAnimation.setDuration(3000);
    mRotateAnimation.setRepeatTimes(2);

    mAlphaAnimation = new SinewaveAnimation(1.0f);
    mAlphaAnimation.setDuration(300);
    mAlphaAnimation.setRepeatTimes(2);


    mPosY = 400;

    mTextView.setY(mPosY);
    mMoveAnimation.addCallback(new AnimationCallback<Float>() {
      @Override
      public void onAnimationUpdate(float fraction, Float value) {
        mTextView.setY(mPosY + value);
      }
    });


    mRotateAnimation.addCallback(new AnimationCallback<Float>() {

      @Override
      public void onAnimationUpdate(float fraction, Float value) {
        mTextView.setRotation(value);
      }
    });

    mAlphaAnimation.addCallback(new AnimationCallback<Float>() {

      @Override
      public void onAnimationUpdate(float fraction, Float value) {
        mTextView.setAlpha(value);
      }
    });

    mTransaction = AnimationUtils.newParallelAnimations(mMoveAnimation, mRotateAnimation);
    mTransaction.setRepeatTimes(3);
  }


  public void processOnClick(View view) {
    final int id = view.getId();
    if (id == R.id.startBtn) {
      mTransaction.start();
    } else if (id == R.id.stopBtn) {
      mTransaction.stop();
    } else if (id == R.id.pauseBtn) {
      mTransaction.pause();
    } else if (id == R.id.resumeBtn) {
      mTransaction.resume();
    }
  }

}

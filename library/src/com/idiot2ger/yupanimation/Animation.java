package com.idiot2ger.yupanimation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import android.view.animation.Interpolator;

/**
 * <b>animation core class, diff with system animation, here doesn't have the UI part, only the
 * value, like ValueAnimator, but can work on all android platform</b>
 * <p>
 * usage:<br>
 * 1. create a class extends {@link Animation}, override method
 * {@link #getTransformationValue(float, Object, Object)}
 * <p>
 * 2.create a class implements {@link AnimationCallback}, and register callback
 * {@link #addCallback(AnimationCallback)}
 * <p>
 * 3.if you care the animation state change, you can use
 * {@link #addStateChangeCallback(AnimationStateChangeCallback)} to listen the state change callback
 * <p>
 * 
 * 4. you can use {@link #start()} to start the animation, the state change methods also have:
 * {@link #pause()}, {@link #stop()}, {@link #resume()}, {@link #reset()}
 * <p>
 * 
 * 5. don't forget to use {@link #release()} method to release the animation
 * 
 * @author r2d2
 * 
 */
public abstract class Animation<T> implements IAnimation<T> {

  static final String TAG = "Animation";

  private static final float MAX_FRACTION = 1.0f;
  private static final AtomicInteger mCounter = new AtomicInteger(10024);

  private List<AnimationCallback<T>> mAnimationCallbacks = new ArrayList<AnimationCallback<T>>();
  private List<AnimationStateChangeCallback> mAnimationStateCallbacks = new ArrayList<AnimationStateChangeCallback>();


  private ReentrantLock mLock = new ReentrantLock();
  private State mState = State.STATE_NONE;

  private long mPreviousTime = -1, mCurrentTime = -1;
  private long mDuration = -1;
  private float mFraction = 0.0f;

  private T mStartValue;
  private T mEndValue;

  private Interpolator mInterpolator;

  private IAnimationSystem mAnimationSystem;

  private int mRepeatTimes, mTimes;

  private int mId;

  public Animation() {
    this(null, null);
  }

  public Animation(T start, T end) {
    mStartValue = start;
    mEndValue = end;

    mAnimationSystem = AnimationSystem.getInstance();

    mId = mCounter.incrementAndGet();
    mAnimationSystem.registerAnimationSystem(this);

    mRepeatTimes = 1;
  }

  public void setStartValue(T start) {
    mStartValue = start;
  }

  public void setEndValue(T end) {
    mEndValue = end;
  }

  public void setLoop(boolean loop) {
    setRepeatTimes(loop ? -1 : 1);
  }

  /**
   * set repeat times
   * 
   * @param times, if < 0, mean loop
   */
  public void setRepeatTimes(int times) {
    if (mState == State.STATE_NONE || mState == State.STATE_END || mState == State.STATE_RESET) {
      mRepeatTimes = times;
      mTimes = 0;
    } else {
      throw new IllegalStateException("when setRepeatTimes, the state must be: STATE_NONE or STATE_END or STATE_RESET");
    }
  }

  @Override
  public void start() {
    preCheck();
    if (mState != State.STATE_START) {
      reset2();
      mAnimationSystem.startAnimation(this);
    }
  }

  @Override
  public void pause() {
    preCheck();
    if (mState == State.STATE_START || mState == State.STATE_RESUME) {
      mAnimationSystem.pauseAnimation(this);
    }
  }

  @Override
  public void resume() {
    preCheck();
    if (mState == State.STATE_PAUSE) {
      mAnimationSystem.resumeAnimation(this);
    }
  }

  @Override
  public void stop() {
    preCheck();
    if (mState == State.STATE_START || mState == State.STATE_PAUSE || mState == State.STATE_RESUME) {
      reset2();
      mAnimationSystem.stopAnimation(this);
    }
  }

  @Override
  public void reset() {
    if (mState != State.STATE_RESET) {
      reset2();
      mAnimationSystem.resetAnimation(this);
    }
  }

  private void reset2() {
    mTimes = 0;
  }

  @Override
  public void release() {
    mAnimationSystem.unregisterAnimationSystem(this);
    removeAllCallback();
    removeAllStateChangeCallback();
  }

  @Override
  public State getAnimationState() {
    return mState;
  }

  public void setInterpolator(Interpolator interpolator) {
    mInterpolator = interpolator;
  }

  public boolean haveInterpolator() {
    return mInterpolator != null;
  }


  private void preCheck() {
    if (mDuration == -1) {
      throw new IllegalStateException("need setDuration");
    }
    // if (mStartValue == null || mEndValue == null) {
    // throw new IllegalStateException("need setStartValue and setEndValue");
    // }

  }

  private long now() {
    return System.currentTimeMillis();
  }

  public void setDuration(long millisecond) {
    if (millisecond < 1 || millisecond > Long.MAX_VALUE) {
      throw new IllegalArgumentException("duration need between 1 and " + Long.MAX_VALUE);
    }
    mDuration = millisecond;
  }


  public void addCallback(AnimationCallback<T> callback) {
    mLock.lock();
    try {
      mAnimationCallbacks.add(callback);
    } finally {
      mLock.unlock();
    }
  }


  public void removeCallback(AnimationCallback<T> callback) {
    mLock.lock();
    try {
      mAnimationCallbacks.remove(callback);
    } finally {
      mLock.unlock();
    }
  }


  public void removeAllCallback() {
    mLock.lock();
    try {
      mAnimationCallbacks.clear();
    } finally {
      mLock.unlock();
    }
  }

  public void addStateChangeCallback(AnimationStateChangeCallback callback) {
    mLock.lock();
    try {
      mAnimationStateCallbacks.add(callback);
    } finally {
      mLock.unlock();
    }
  }


  public void removeStateChangeCallback(AnimationStateChangeCallback callback) {
    mLock.lock();
    try {
      mAnimationStateCallbacks.remove(callback);
    } finally {
      mLock.unlock();
    }
  }


  public void removeAllStateChangeCallback() {
    mLock.lock();
    try {
      mAnimationStateCallbacks.clear();
    } finally {
      mLock.unlock();
    }
  }

  @Override
  public int getAnimationId() {
    return mId;
  }

  @Override
  public boolean updateAnimationState(State state) {
    mState = state;

    // state callback
    mLock.lock();
    try {
      for (AnimationStateChangeCallback callback : mAnimationStateCallbacks) {
        callback.onAnimationStateChanged(this, state);
      }
    } finally {
      mLock.unlock();
    }

    if (state == State.STATE_START || state == State.STATE_RESUME) {
      mCurrentTime = mPreviousTime = now();

      // if start, need set percent to zero
      if (state == State.STATE_START) {
        mFraction = 0.0f;
      }
    } else if (state == State.STATE_RESET) {
      mFraction = 0.0f;
    }

    return true;
  }

  @Override
  public final void animationRunning() {
    mCurrentTime = now();
    mFraction += (((float) (mCurrentTime - mPreviousTime)) / mDuration);
    mFraction = Math.min(mFraction, MAX_FRACTION);

    // cal the interpolator
    final float interpolator = mInterpolator == null ? 0 : mInterpolator.getInterpolation(mFraction);
    final float newFraction = mInterpolator == null ? mFraction : interpolator;
    T value = getTransformationValue(newFraction, mStartValue, mEndValue);

    mLock.lock();
    try {
      for (AnimationCallback<T> callback : mAnimationCallbacks) {
        callback.onAnimationUpdate(mFraction, value);
      }
    } finally {
      mLock.unlock();
    }

    mPreviousTime = mCurrentTime;

    if (mFraction >= MAX_FRACTION) {
      mTimes++;
      if (mTimes < 0) {
        mTimes = 0;
      }
      if (mRepeatTimes < 0 || mRepeatTimes > mTimes) {
        mCurrentTime = mPreviousTime = now();
        mFraction = 0.0f;
      } else {
        mAnimationSystem.markAnimationEnd(this);
      }
    }
  }



}

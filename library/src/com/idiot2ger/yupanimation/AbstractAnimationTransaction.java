package com.idiot2ger.yupanimation;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author r2d2
 * 
 */
abstract class AbstractAnimationTransaction implements IAnimationTransaction, AnimationStateChangeCallback {

  protected List<Animation<?>> mAnimations = new ArrayList<Animation<?>>();
  protected List<Animation<?>> mRunningAnimations = new ArrayList<Animation<?>>();

  protected int mRepeatTimes, mTimes;

  private boolean mIsStarted;

  protected AbstractAnimationTransaction() {
    mAnimations.clear();
    mRunningAnimations.clear();

    mIsStarted = false;
  }

  public void addAnimation(Animation<?> animation) {
    mAnimations.add(animation);
    animation.addStateChangeCallback(this);
  }


  public abstract void initStartRunningAnimations();

  @Override
  public void start() {
    if (!mAnimations.isEmpty() && !mIsStarted) {
      mIsStarted = true;
      resetAll();
      initStartRunningAnimations();
      for (Animation<?> animation : mRunningAnimations) {
        animation.start();
      }
    }
  }

  @Override
  public void pause() {
    for (Animation<?> animation : mRunningAnimations) {
      animation.pause();
    }
  }

  @Override
  public void resume() {
    for (Animation<?> animation : mRunningAnimations) {
      animation.resume();
    }
  }

  @Override
  public void stop() {
    for (Animation<?> animation : mRunningAnimations) {
      animation.stop();
    }
    mIsStarted = false;
  }

  @Override
  public void release() {
    resetAll();
    for (Animation<?> animation : mAnimations) {
      animation.removeStateChangeCallback(this);
    }
    mAnimations.clear();
    mIsStarted = false;
  }

  @Override
  public void reset() {
    resetAll();
    mIsStarted = false;
  }

  @Override
  public void setRepeatTimes(int times) {
    resetAll();
    mRepeatTimes = times;
    mTimes = 0;
  }

  @Override
  public void setLoop(boolean loop) {
    setRepeatTimes(-1);
  }

  protected void resetAll() {
    for (Animation<?> animation : mAnimations) {
      animation.reset();
    }
    mTimes = 0;
    mRunningAnimations.clear();
  }

}

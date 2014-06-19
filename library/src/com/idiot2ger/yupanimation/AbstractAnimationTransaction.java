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

  protected AbstractAnimationTransaction() {
    mAnimations.clear();
    mRunningAnimations.clear();
  }

  public void addAnimation(Animation<?> animation) {
    mAnimations.add(animation);
    animation.addStateChangeCallback(this);
  }


  public abstract void initStartRunningAnimations();

  @Override
  public void start() {
    if (!mAnimations.isEmpty()) {
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
  }

  @Override
  public void release() {
    resetAll();
    for (Animation<?> animation : mAnimations) {
      animation.removeStateChangeCallback(this);
    }
    mAnimations.clear();
  }

  @Override
  public void reset() {
    resetAll();
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

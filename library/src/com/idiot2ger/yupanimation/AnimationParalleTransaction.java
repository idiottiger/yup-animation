package com.idiot2ger.yupanimation;


/**
 * 
 * @author r2d2
 * 
 */
class AnimationParalleTransaction extends AbstractAnimationTransaction {

  @Override
  public void initStartRunningAnimations() {
    mRunningAnimations.addAll(mAnimations);
  }

  private boolean isAllAnimationEnd() {
    for (Animation<?> animation : mRunningAnimations) {
      if (animation.getAnimationState() != State.STATE_END) {
        return false;
      }
    }
    return true;
  }

  private void startWithoutReset() {
    for (Animation<?> animation : mRunningAnimations) {
      animation.start();
    }
  }

  @Override
  public void onAnimationStateChanged(Animation<?> animation, State state) {
    if (state == State.STATE_END && isAllAnimationEnd()) {
      // once end
      mTimes++;
      if (mTimes < 0) {
        mTimes = 0;
      }
      if (mRepeatTimes < 0 || mTimes < mRepeatTimes) {
        startWithoutReset();
      }
    }
  }

}

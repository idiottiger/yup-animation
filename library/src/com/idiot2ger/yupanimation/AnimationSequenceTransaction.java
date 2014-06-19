package com.idiot2ger.yupanimation;


/**
 * 
 * @author r2d2
 * 
 */
class AnimationSequenceTransaction extends AbstractAnimationTransaction {


  @Override
  public void initStartRunningAnimations() {
    mRunningAnimations.add(mAnimations.get(0));
  }

  @Override
  public void onAnimationStateChanged(Animation<?> animation, State state) {
    Animation<?> currentAnimation = null;
    if (state == State.STATE_START) {
      currentAnimation = animation;
    } else if (state == State.STATE_END) {
      final int index = mAnimations.indexOf(animation);
      final int size = mAnimations.size();
      if (index + 1 < size) {
        currentAnimation = mAnimations.get(index + 1);
        currentAnimation.start();
      } else if (index == size - 1) {
        // last animation
        mTimes++;
        if (mTimes < 0) {
          mTimes = 0;
        }
        if (mRepeatTimes < 0 || mTimes < mRepeatTimes) {
          currentAnimation = mAnimations.get(0);
          currentAnimation.start();
        }
      }
    }

    if (currentAnimation != null) {
      mRunningAnimations.clear();
      mRunningAnimations.add(currentAnimation);
    }
  }



}

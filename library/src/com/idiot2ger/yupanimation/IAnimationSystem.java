package com.idiot2ger.yupanimation;


/**
 * 
 * @author r2d2
 * 
 */
interface IAnimationSystem {

  public void registerAnimationSystem(IAnimation<?> animation);

  public void unregisterAnimationSystem(IAnimation<?> animation);

  public void startAnimation(IAnimation<?> animation);

  public void stopAnimation(IAnimation<?> animation);

  public void pauseAnimation(IAnimation<?> animation);

  public void resumeAnimation(IAnimation<?> animation);

  public void resetAnimation(IAnimation<?> animation);

  public void markAnimationEnd(IAnimation<?> animation);

  public void updateAnimationState(IAnimation<?> animation, State state);


}

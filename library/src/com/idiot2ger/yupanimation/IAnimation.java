package com.idiot2ger.yupanimation;

import android.view.animation.Interpolator;

/**
 * 
 * @author r2d2
 * 
 * @param <T>
 */
interface IAnimation<T> {

  /**
   * animation id
   * 
   * @return
   */
  public int getAnimationId();

  /**
   * animation running
   */
  public void animationRunning();

  /**
   * new animation state callback
   * 
   * @param state
   * @return true or false, true set ok, otherwise failture
   */
  public boolean updateAnimationState(State state);

  /**
   * get the transformation value
   * 
   * @param fraction, value [0.0-1.0], and it's value already calculate with the
   *        {@link Interpolator}
   * @param start
   * @param end
   * @return
   */
  public T getTransformationValue(float fraction, T start, T end);

  /**
   * start animation
   */
  public void start();

  /**
   * pause animation
   */
  public void pause();

  /**
   * resume animation
   */
  public void resume();

  /**
   * stop animation
   */
  public void stop();

  /**
   * reset animation
   */
  public void reset();

  /**
   * release animation
   */
  public void release();

  /**
   * get current animation state
   * 
   * @return
   */
  public State getAnimationState();

}

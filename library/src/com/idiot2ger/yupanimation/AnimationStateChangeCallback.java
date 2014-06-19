package com.idiot2ger.yupanimation;


public interface AnimationStateChangeCallback {
  /**
   * animation state change callback, there are: {@link State#STATE_START},
   * {@link State#STATE_PAUSE} ,{@link State#STATE_RESUME},{@link State#STATE_STOP},
   * {@link State#STATE_END}, {@link State#STATE_RESET}
   * 
   * @param animation
   * @param state
   */
  public void onAnimationStateChanged(Animation<?> animation, State state);
}

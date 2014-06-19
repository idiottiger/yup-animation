package com.idiot2ger.yupanimation;


public class AnimationUtils {

  private static final int SEQUENCE_TYPE = 1;
  private static final int PARALLE_TYPE = 2;

  /**
   * create a sequence animations, run one by one
   * 
   * @param animations
   * @return
   */
  public static IAnimationTransaction newSequenceAnimations(Animation<?>... animations) {
    return newAnimationTransaction(SEQUENCE_TYPE, animations);
  }

  /**
   * create a parallel animation, run at the sametime
   * 
   * @param animations
   * @return
   */
  public static IAnimationTransaction newParallelAnimations(Animation<?>... animations) {
    return newAnimationTransaction(PARALLE_TYPE, animations);
  }

  private static IAnimationTransaction newAnimationTransaction(int type, Animation<?>... animations) {
    if (animations == null) {
      return null;
    }
    AbstractAnimationTransaction transaction =
        (type == SEQUENCE_TYPE) ? (new AnimationSequenceTransaction()) : (new AnimationParalleTransaction());
    for (Animation<?> animation : animations) {
      transaction.addAnimation(animation);
    }
    return transaction;
  }

}

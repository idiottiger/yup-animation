package pkg.animtest;

import com.idiot2ger.yupanimation.Animation;

public class SinewaveAnimation extends Animation<Float> {

  private float mMove;

  public SinewaveAnimation(float move) {
    mMove = move;
    setStartValue(0.0f);
    setEndValue(1.0f);
  }

  @Override
  public Float getTransformationValue(float fraction, Float start, Float end) {
    float value = (float) Math.PI * 2 * fraction;
    return mMove * (float) Math.sin(value);
  }



}

package pkg.animtest;

import com.idiot2ger.yupanimation.Animation;

public class FloatValueAnimation extends Animation<Float> {

  @Override
  public Float getTransformationValue(float fraction, Float start, Float end) {
    return start + (end - start) * fraction;
  }
}

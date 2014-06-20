package pkg.animtest;

import android.graphics.PointF;

import com.idiot2ger.yupanimation.Animation;

public class Float2ValueAnimation extends Animation<PointF> {

  public final PointF mPoint = new PointF();

  @Override
  public PointF getTransformationValue(float fraction, PointF start, PointF end) {
    float x = start.x + (end.x - start.x) * fraction;
    float y = start.y + (end.y - start.y) * fraction;
    mPoint.set(x, y);
    return mPoint;
  }

}

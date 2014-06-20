package pkg.animtest;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;

public class CustomViewActivity extends Activity {

  private CustomDragResetView mDragResetView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.custom_view_layout);

    mDragResetView = (CustomDragResetView) findViewById(R.id.dragRestView);
    mDragResetView.setDragBitmapItem(BitmapFactory.decodeResource(getResources(), R.drawable.test));
  }

}

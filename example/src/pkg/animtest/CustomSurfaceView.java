package pkg.animtest;

import pkg.animtest.RendererThread.RendererRunnable;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class CustomSurfaceView extends SurfaceView implements Callback, RendererRunnable {

  private RendererThread mRenderer;
  private SurfaceHolder mHolder;
  private Bitmap mBitmap;
  private float mCanvasWidth, mCanvasHeight;

  public CustomSurfaceView(Context context) {
    this(context, null);
  }

  public CustomSurfaceView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public CustomSurfaceView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  public void setBitmap(Bitmap bitmap) {
    mBitmap = bitmap;
  }


  @Override
  public void surfaceCreated(SurfaceHolder holder) {
    mHolder = holder;
    mHolder.addCallback(this);
  }

  @Override
  public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    mCanvasWidth = width;
    mCanvasHeight = height;

    if (mRenderer != null) {
      mRenderer.stopRender();
    }
    mRenderer = new RendererThread(this);
    mRenderer.startRender();
  }

  @Override
  public void surfaceDestroyed(SurfaceHolder holder) {
    if (mRenderer != null) {
      mRenderer.stopRender();
    }
    mHolder.removeCallback(this);
  }

  @Override
  public void onRendererRunning() {

  }



}

package pkg.animtest;

import java.util.concurrent.atomic.AtomicInteger;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;



class RendererThread extends Thread implements Callback {

  private static AtomicInteger mCounter = new AtomicInteger();
  private static final int DEFAULT_FPS = 60;
  private static final int MAX_FPS = 200;
  private static final int MSG_RENDER_LOOP = 1000;

  private boolean mIsRenderering;
  private RendererRunnable mRunnable;
  private int mInterval = (int) (1000.0f / DEFAULT_FPS);
  private Looper mLooper;
  private Handler mHandler;

  RendererThread(RendererRunnable runnable) {
    if (runnable == null) {
      throw new NullPointerException("runnable cann't be null");
    }
    mRunnable = runnable;
  }

  public void startRender() {
    if (!mIsRenderering) {
      mIsRenderering = true;
      start();
    }
  }

  public void setFPS(int fps) {
    if (fps <= 0 && fps > MAX_FPS) {
      throw new IllegalArgumentException("fps need bigger than 0 and smaller than " + MAX_FPS);
    }
    mInterval = (int) (1000.0f / fps);
  }

  @Override
  public void run() {
    setName("render #" + mCounter.incrementAndGet());
    Looper.prepare();
    mLooper = Looper.myLooper();
    mHandler = new Handler(mLooper, this);
    mHandler.sendEmptyMessage(MSG_RENDER_LOOP);

    Looper.loop();

    mRunnable = null;
  }

  public void stopRender() {
    mIsRenderering = false;
    mLooper.quit();
  }

  public interface RendererRunnable {
    public void onRendererRunning();
  }

  @Override
  public boolean handleMessage(Message msg) {
    final int what = msg.what;
    if (what == MSG_RENDER_LOOP) {
      mRunnable.onRendererRunning();
      mHandler.sendEmptyMessageDelayed(MSG_RENDER_LOOP, mInterval);
    }
    return false;
  }
}

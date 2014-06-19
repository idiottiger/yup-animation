package com.idiot2ger.yupanimation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.Choreographer;
import android.view.Choreographer.FrameCallback;

/**
 * 
 * @author r2d2
 * 
 */
class AnimationSystem implements IAnimationSystem {

  private static AnimationSystem sAnimationSystem;

  private static final int MSG_UPDATE_STATE = 1 << 20;

  public synchronized static AnimationSystem getInstance() {
    if (sAnimationSystem == null) {
      sAnimationSystem = new AnimationSystem();
    }
    return sAnimationSystem;
  }

  private AnimationLooper mLooper;
  private AnimationStateHandler mStateHandler;
  private ReentrantLock mLocker = new ReentrantLock();
  private List<IAnimation<?>> mAnimations = new ArrayList<IAnimation<?>>();
  private List<IAnimation<?>> mRunningAnimations = new ArrayList<IAnimation<?>>();

  private AnimationSystem() {
    mAnimations.clear();
    mRunningAnimations.clear();

    mStateHandler = new AnimationStateHandler();

    // init
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      mLooper = new AnimationChoreographerLooper();
    } else {
      mLooper = new AnimationHandlerLooper();
    }
  }

  @Override
  public void registerAnimationSystem(IAnimation<?> animation) {
    mLocker.lock();
    try {
      if (!mAnimations.contains(animation)) {
        mAnimations.add(animation);
      }
    } finally {
      mLocker.unlock();
    }
  }

  @Override
  public void unregisterAnimationSystem(IAnimation<?> animation) {
    mLocker.lock();
    try {
      mAnimations.remove(animation);
    } finally {
      mLocker.unlock();
    }
  }

  @Override
  public void startAnimation(IAnimation<?> animation) {
    updateAnimationState(animation, State.STATE_START);
  }

  @Override
  public void stopAnimation(IAnimation<?> animation) {
    updateAnimationState(animation, State.STATE_STOP);
  }

  @Override
  public void pauseAnimation(IAnimation<?> animation) {
    updateAnimationState(animation, State.STATE_PAUSE);
  }

  @Override
  public void resumeAnimation(IAnimation<?> animation) {
    updateAnimationState(animation, State.STATE_RESUME);
  }

  @Override
  public void resetAnimation(IAnimation<?> animation) {
    updateAnimationState(animation, State.STATE_RESET);
  }

  @Override
  public void markAnimationEnd(IAnimation<?> animation) {
    updateAnimationState(animation, State.STATE_END);
  }

  @Override
  public void updateAnimationState(IAnimation<?> animation, State state) {
    // send message
    Message message = mStateHandler.obtainMessage(MSG_UPDATE_STATE, state.ordinal(), animation.getAnimationId());
    message.setTarget(mStateHandler);
    message.sendToTarget();
  }

  private IAnimation<?> getAnimationById(int animationId) {
    mLocker.lock();
    try {
      for (IAnimation<?> animation : mAnimations) {
        if (animation.getAnimationId() == animationId) {
          return animation;
        }
      }
    } finally {
      mLocker.unlock();
    }
    return null;
  }

  private void addRunningAnimation(IAnimation<?> animation) {
    if (!mRunningAnimations.contains(animation)) {
      mRunningAnimations.add(animation);
    }
  }

  private void removeRunningAnimation(IAnimation<?> animation) {
    mRunningAnimations.remove(animation);
  }

  private void removeAllRunningAnimation() {
    mRunningAnimations.clear();
  }


  /**
   * looper interface
   * 
   * @author r2d2
   * 
   */
  static interface AnimationLooper {

    public void startLoop(IAnimation<?> animation);

    public void stopLoop(IAnimation<?> animation);

    public void releaseLoop();
  }


  private abstract class AbstractAnimationLooper implements AnimationLooper {

    protected volatile boolean mIsRunning = false;

    @Override
    public void startLoop(IAnimation<?> animation) {
      mIsRunning = true;
      addRunningAnimation(animation);
      loop();
    }

    @Override
    public void stopLoop(IAnimation<?> animation) {
      removeRunningAnimation(animation);
    }

    @Override
    public void releaseLoop() {
      mIsRunning = false;
      removeAllRunningAnimation();
    }

    protected void animationLoop() {
      if (!mIsRunning) {
        return;
      }

      if (!mRunningAnimations.isEmpty()) {
        for (IAnimation<?> animation : mRunningAnimations) {
          animation.animationRunning();
        }
        loop();
      }
    }

    public abstract void loop();

  }


  private class AnimationHandlerLooper extends AbstractAnimationLooper {

    private Handler mHandler;
    private Runnable mRunnable;

    AnimationHandlerLooper() {
      mHandler = new Handler();

      mRunnable = new Runnable() {
        @Override
        public void run() {
          animationLoop();
        }
      };
    }

    @Override
    public void loop() {
      mHandler.removeCallbacks(mRunnable);
      mHandler.post(mRunnable);
    }
  }


  @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
  private class AnimationChoreographerLooper extends AbstractAnimationLooper {

    private Choreographer mChoreographer;
    private FrameCallback mCallback;


    AnimationChoreographerLooper() {
      mChoreographer = Choreographer.getInstance();

      mCallback = new FrameCallback() {

        @Override
        public void doFrame(long frameTimeNanos) {
          animationLoop();
        }
      };
    }


    @Override
    public void loop() {
      mChoreographer.removeFrameCallback(mCallback);
      mChoreographer.postFrameCallback(mCallback);
    }


  }

  @SuppressLint("HandlerLeak")
  private class AnimationStateHandler extends Handler {

    @Override
    public void handleMessage(Message msg) {
      final int what = msg.what;
      if (what == MSG_UPDATE_STATE) {
        final State newState = State.values()[msg.arg1];
        final IAnimation<?> animation = getAnimationById(msg.arg2);
        if (animation != null) {
          final boolean result = animation.updateAnimationState(newState);
          if (result) {
            if (newState == State.STATE_START || newState == State.STATE_RESUME) {
              mLooper.startLoop(animation);
            } else if (newState == State.STATE_PAUSE || newState == State.STATE_STOP || newState == State.STATE_END
                || newState == State.STATE_RESET) {
              mLooper.stopLoop(animation);
            }
          }
        }
      }
    }
  }



}

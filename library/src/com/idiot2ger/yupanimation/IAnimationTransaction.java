package com.idiot2ger.yupanimation;

public interface IAnimationTransaction {

  public void start();

  public void pause();

  public void resume();

  public void stop();

  public void release();

  public void reset();

  public void setRepeatTimes(int times);

  public void setLoop(boolean loop);

}

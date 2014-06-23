package pkg.animtest;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * BetterAdapter extends {@link BaseAdapter}, have some convenient methods:
 * <p>
 * <b>1.invoke {@link #notifyDataSetChanged()}, DONT care run in ui thread or not.</br>2.has a
 * default hanlder, you can send message using {@link #sendMessage(Message, long)}, etc...</br>3.if
 * you want to process message, you can override {@link #processMessage(Message)} to
 * process.</br>4.we have a default {@link #getView(int, View, ViewGroup)} implements.</br>
 * 
 * @see #getView(int, View, ViewGroup)
 * @author idiottiger
 * @version 1.0
 * @param <T> the adapter item type
 * @param <E> the view holder type
 */
public abstract class SimpleBetterAdapter<T, E> extends BaseAdapter {

  static final int UPDATE_ADAPTER = 1 << 15;

  static final int DEFAULT_POSITION = -1;

  static final String TAG = "BetterAdapter";

  private Thread uiThread;

  LayoutInflater mInflater;

  private int mItemLayout;

  /**
   * 
   * @param context
   */
  public SimpleBetterAdapter(Context context) {
    uiThread = Looper.getMainLooper().getThread();
    mInflater = LayoutInflater.from(context);
  }

  /**
   * creator using itemlayout
   * 
   * @param context
   * @param itemLayout
   */
  public SimpleBetterAdapter(Context context, int itemLayout) {
    this(context);
    mItemLayout = itemLayout;
  }

  /**
   * send empty message just has what and delay.
   * 
   * @param what
   * @param delay
   * @see Handler#sendEmptyMessage(int)
   * @see Handler#sendEmptyMessageDelayed(int, long)
   */
  public void sendEmptyMessage(int what, long delay) {
    mHandler.sendEmptyMessageDelayed(what, delay);
  }

  /**
   * send message.
   * 
   * @param message
   * @param delay
   * @see Handler#sendMessage(message)
   * @see Handler#sendMessageDelayed(int, long)
   */
  public void sendMessage(Message message, long delay) {
    mHandler.sendMessageDelayed(message, delay);
  }

  /**
   * post the runnable.
   * 
   * @param r
   * @param delay
   * @see Handler#post(Runnable)
   * @see Handler#postDelayed(Runnable, long)
   */
  public void postRunnable(Runnable r, long delay) {
    mHandler.postDelayed(r, delay);
  }

  /**
   * dont care run in ui thread or not.
   * 
   * @see {@link BaseAdapter#notifyDataSetChanged()}
   */
  @Override
  public void notifyDataSetChanged() {
    if (isRunInUiThread()) {
      super.notifyDataSetChanged();
    } else {
      sendEmptyMessage(UPDATE_ADAPTER, 0);
    }
  }

  /**
   * Convenient method inflate the layout id to view
   * 
   * @see {@link LayoutInflater#inflate(int, android.view.ViewGroup)}
   * @param resid
   * @return
   */
  public View inflate(int resid, ViewGroup parent, boolean attachToRoot) {
    return mInflater.inflate(resid, parent, attachToRoot);
  }

  boolean isRunInUiThread() {
    return Thread.currentThread() == uiThread;
  }

  /**
   * only the sub class can see.
   */
  protected Handler mHandler = new Handler() {
    public void handleMessage(Message msg) {
      final int what = msg.what;
      if (what == UPDATE_ADAPTER) {
        notifyDataSetChanged();
      } else {
        pendProcessMessage(this, msg);
        processMessage(msg);
      }
    };
  };

  /**
   * supply to the one package class to use
   * 
   * @hide
   * @param handler
   * @param message
   */
  void pendProcessMessage(Handler handler, Message message) {

  }

  /**
   * handle message
   * 
   * @param message
   */
  protected void processMessage(Message message) {

  }

  /**
   * get current adpater's handler
   * 
   * @return
   */
  public Handler getHandler() {
    return mHandler;
  }

  public abstract T getItem(int position);

  /**
   * we have a simple implements, if you want to use this implements, you need not override this
   * method, Opposite you need override {@link #updateView(int, E)},
   * {@link #getViewHolder(View convertView)}, and you need {@link #setItemViewLayout(int)}, we will
   * use the layout xml to init the covertview.<br>
   * <b> This implement has some limits: 1.the convertView should be same dont depend on the
   * position;2.if the adapter has a complicated layout, you can override this method, just dont
   * invoke super method.</b>
   * 
   * @see #getViewHolder(View convertView)
   * @see #updateView(int, ViewHolder)
   * @see #setItemViewLayout(int)
   */
  @SuppressWarnings("unchecked")
  public View getView(int position, View convertView, ViewGroup parent) {
    E holder;
    if (convertView == null) {
      convertView = inflate(mItemLayout, parent, false);
      holder = getViewHolder(convertView);

      convertView.setTag(holder);
    } else {
      holder = (E) convertView.getTag();
    }

    updateView(position, holder);
    return convertView;
  }

  /**
   * set the adapter's item layout.
   * 
   * @param layout
   */
  public final void setItemViewLayout(int layout) {
    mItemLayout = layout;
  }

  /**
   * update the adapter's item base on the position.
   * 
   * @param position
   * @param holder
   */
  protected void updateView(int position, E holder) {

  }

  /**
   * get the ViewHolder
   * 
   * @param convertView
   * @return
   */
  protected E getViewHolder(View convertView) {
    return null;
  }

  @Override
  public int getItemViewType(int position) {
    return 0;
  }

  @Override
  public int getViewTypeCount() {
    return 1;
  }

}

package pkg.animtest;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity implements OnItemClickListener {


  private ListView mListView;
  private DemoAdapter mAdapter;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mListView = (ListView) findViewById(R.id.demoListView);
    mAdapter = new DemoAdapter(this);
    mListView.setAdapter(mAdapter);

    mListView.setOnItemClickListener(this);
    mAdapter.initAdapter();

  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    DemoAdapterItem item = mAdapter.getItem(position);
    Intent intent = new Intent(this, item.cls);
    startActivity(intent);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mListView.setAdapter(null);
  }


  private class DemoAdapterItem {
    String alias;
    Class<?> cls;

    public DemoAdapterItem(String alias, Class<?> cls) {
      this.alias = alias;
      this.cls = cls;
    }
  }

  private class DemoAdapterViewHolder {
    TextView textView;
  }

  private class DemoAdapter extends SimpleBetterAdapter<DemoAdapterItem, DemoAdapterViewHolder> {

    private List<DemoAdapterItem> mItems = new ArrayList<DemoAdapterItem>();

    public DemoAdapter(Context context) {
      super(context, R.layout.list_item);
      mItems.clear();
    }

    public void initAdapter() {
      mItems.clear();
      mItems.add(new DemoAdapterItem("Animation Transaction Demo", AnimationTransactionActivity.class));
      mItems.add(new DemoAdapterItem("Custom View Demo", CustomViewActivity.class));
    }

    @Override
    public int getCount() {
      return mItems.size();
    }

    @Override
    public long getItemId(int position) {
      return 0;
    }

    @Override
    public DemoAdapterItem getItem(int position) {
      return mItems.get(position);
    }

    @Override
    protected void updateView(int position, DemoAdapterViewHolder holder) {
      holder.textView.setText(getItem(position).alias);
    }

    @Override
    protected DemoAdapterViewHolder getViewHolder(View convertView) {
      DemoAdapterViewHolder holder = new DemoAdapterViewHolder();
      holder.textView = (TextView) convertView.findViewById(R.id.listItemTextView);
      return holder;
    }


  }



}

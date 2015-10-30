package me.yourbay.litelistdragger.demo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import me.yourbay.litelistdragger.DragListView;

public class DragListAdapter<T> extends BaseAdapter implements DragListView.DragListener {
    private List<T> mList;
    private int mStartPosition = -1;

    public DragListAdapter() {
    }

    public void setData(List<T> data) {
        mList = data;
    }

    @Override
    public View getView(int p, View view, ViewGroup parent) {
        final Holder mHolder;
        if (view == null) {
            final Context c = parent.getContext();
            view = LayoutInflater.from(c).inflate(R.layout.drag_list_item, parent, false);
            mHolder = new Holder(view);
        } else {
            mHolder = (Holder) view.getTag();
            view.clearAnimation();
        }
        //
        mHolder.mText.setText(mList.get(p).toString());
        mHolder.mParent.setVisibility(p == mStartPosition ? View.INVISIBLE : View.VISIBLE);
        return view;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getTriggerView(View convertView) {
        return convertView.findViewById(R.id.drag_list_item_image);
    }

    @Override
    public void onDrag(int position) {
        mStartPosition = position;
        notifyDataSetChanged();
    }

    @Override
    public void onDrop(int position) {
        move(mStartPosition, position);
        mStartPosition = -1;
        notifyDataSetChanged();
    }

    class Holder {
        TextView mText;
        View mParent;

        Holder(View v) {
            mParent = v.findViewById(R.id.ll_item_parent);
            mText = (TextView) v.findViewById(R.id.drag_list_item_text);
            v.setTag(this);
        }
    }

    private void move(int from, int to) {
        if (from == to) {
            return;
        }
        T obj = mList.remove(from);
        mList.add(to, obj);
    }

}
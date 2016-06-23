
package com.ttxgps.adapter;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 基础适配器
 * 
 * @param <T>
 */
public abstract class MyBaseAdapter<T> extends BaseAdapter {
	private List<T> mlist;

	public MyBaseAdapter(List<T> list) {
		super();
		this.mlist = list;
	}

	public List<T> getAllList() {
		return mlist;
	}

	/**
	 * 往顶部添加数据
	 * 
	 * @param list
	 */
	public void add2Head(List<T> list) {
		mlist.addAll(0, list);
		notifyDataSetChanged();
	}

	/**
	 * 刪除某一項數據
	 * 
	 * @param position
	 */
	public void removeItem(int position){
		mlist.remove(position);
		notifyDataSetChanged();
	}

	/**
	 * 刪除某一項數據
	 * 
	 * @param obj
	 */
	public void removeItem(T obj){
		mlist.remove(obj);
		notifyDataSetChanged();
	}


	/**
	 * 往底部添加数据
	 * 
	 * @param list
	 */
	public void add2Bottom(List<T> list) {
		mlist.addAll(list);
		notifyDataSetChanged();
	}

	/**
	 * @Title: clearAllList
	 * @Description: TODO(清空Adapter中所有的list内容)
	 * @param     设定文件
	 * @return void    返回类型
	 * @throws
	 */
	public void clearAllList(){
		mlist.clear();
		notifyDataSetChanged();
	}


	/**
	 * @Title: updateListView
	 * @Description: TODO(更新BaseAdapter中的数据)
	 * @param @param list    设定文件
	 * @return void    返回类型
	 * @throws
	 */
	public void updateListView(List<T> list){
		mlist = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mlist.size();
	}

	/**
	 * @Name getItem
	 * @Description TODO(子Adapter中调用对应list制定position的内容填充数据时，调用这个)
	 * @param position
	 * @return
	 * @see android.widget.Adapter#getItem(int)
	 * @Date 2015-1-6 下午3:22:17
	 **/
	@Override
	public T getItem(int position) {
		return mlist.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	/**
	 * 实际显示View的方法，使用抽象方法强制调用者覆写！
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getViewItem(position, convertView, parent);
	}

	/**
	 * 抽象方法，强制调用者覆写！
	 * 
	 * @param position
	 * @param convertView
	 * @param parent
	 * @return
	 */
	protected abstract View getViewItem(int position, View convertView,
			ViewGroup parent);

}

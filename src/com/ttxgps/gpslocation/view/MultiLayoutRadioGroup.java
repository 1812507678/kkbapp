package com.ttxgps.gpslocation.view;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;

public class MultiLayoutRadioGroup extends LinearLayout{
	private int mCheckedId;
	private android.widget.CompoundButton.OnCheckedChangeListener mChildOnCheckedChangeListener;
	private OnCheckedChangeListener mOnCheckedChangeListener;
	private PassThroughHierarchyChangeListener mPassThroughListener;
	private boolean mProtectFromCheckedChange;

	private class CheckedStateTracker implements android.widget.CompoundButton.OnCheckedChangeListener {
		private CheckedStateTracker() {
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (!MultiLayoutRadioGroup.this.mProtectFromCheckedChange) {
				MultiLayoutRadioGroup.this.mProtectFromCheckedChange = true;
				if (MultiLayoutRadioGroup.this.mCheckedId != -1) {
					MultiLayoutRadioGroup.this.setCheckedStateForView(MultiLayoutRadioGroup.this.mCheckedId, false);
				}
				MultiLayoutRadioGroup.this.mProtectFromCheckedChange = false;
				MultiLayoutRadioGroup.this.setCheckedId(buttonView.getId());
			}
		}
	}

	public static class LayoutParams extends android.widget.LinearLayout.LayoutParams {
		public LayoutParams(Context c, AttributeSet attrs) {
			super(c, attrs);
		}

		public LayoutParams(int w, int h) {
			super(w, h);
		}

		public LayoutParams(int w, int h, float initWeight) {
			super(w, h, initWeight);
		}

		public LayoutParams(android.view.ViewGroup.LayoutParams p) {
			super(p);
		}

		public LayoutParams(MarginLayoutParams source) {
			super(source);
		}

		@Override
		protected void setBaseAttributes(TypedArray a, int widthAttr, int heightAttr) {
			if (a.hasValue(widthAttr)) {
				this.width = a.getLayoutDimension(widthAttr, "layout_width");
			} else {
				this.width = -2;
			}
			if (a.hasValue(heightAttr)) {
				this.height = a.getLayoutDimension(heightAttr, "layout_height");
			} else {
				this.height = -2;
			}
		}
	}

	public interface OnCheckedChangeListener {
		void onCheckedChanged(MultiLayoutRadioGroup multiLayoutRadioGroup, int i);
	}

	private class PassThroughHierarchyChangeListener implements OnHierarchyChangeListener {
		private OnHierarchyChangeListener mOnHierarchyChangeListener;

		private PassThroughHierarchyChangeListener() {
		}

		@Override
		public void onChildViewAdded(View parent, View child) {
			if (parent == MultiLayoutRadioGroup.this && (child instanceof RadioButton)) {
				if (child.getId() == -1) {
					child.setId(child.hashCode());
				}
				((RadioButton) child).setOnCheckedChangeListener(MultiLayoutRadioGroup.this.mChildOnCheckedChangeListener);
			} else if (parent == MultiLayoutRadioGroup.this && (child instanceof ViewGroup)) {
				ArrayList<RadioButton> btns = MultiLayoutRadioGroup.this.findRadioButton((ViewGroup) child);
				int size = btns.size();
				for (int i = 0; i < size; i++) {
					RadioButton btn = btns.get(i);
					if (btn.getId() == -1) {
						btn.setId(btn.hashCode());
					}
					btn.setOnCheckedChangeListener(MultiLayoutRadioGroup.this.mChildOnCheckedChangeListener);
				}
			}
			if (this.mOnHierarchyChangeListener != null) {
				this.mOnHierarchyChangeListener.onChildViewAdded(parent, child);
			}
		}

		@Override
		public void onChildViewRemoved(View parent, View child) {
			if (parent == MultiLayoutRadioGroup.this && (child instanceof RadioButton)) {
				((RadioButton) child).setOnCheckedChangeListener(null);
			} else if (parent == MultiLayoutRadioGroup.this && (child instanceof ViewGroup)) {
				ArrayList<RadioButton> btns = MultiLayoutRadioGroup.this.findRadioButton((ViewGroup) child);
				int size = btns.size();
				for (int i = 0; i < size; i++) {
					btns.get(i).setOnCheckedChangeListener(null);
				}
			}
			if (this.mOnHierarchyChangeListener != null) {
				this.mOnHierarchyChangeListener.onChildViewRemoved(parent, child);
			}
		}
	}

	public MultiLayoutRadioGroup(Context context) {
		super(context);
		this.mCheckedId = -1;
		this.mProtectFromCheckedChange = false;
		setOrientation(1);
		init();
	}

	public MultiLayoutRadioGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mCheckedId = -1;
		this.mProtectFromCheckedChange = false;
		init();
	}

	private void init() {
		this.mChildOnCheckedChangeListener = new CheckedStateTracker();
		this.mPassThroughListener = new PassThroughHierarchyChangeListener();
		super.setOnHierarchyChangeListener(this.mPassThroughListener);
	}

	@Override
	public void setOnHierarchyChangeListener(OnHierarchyChangeListener listener) {
		this.mPassThroughListener.mOnHierarchyChangeListener = listener;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		if (this.mCheckedId != -1) {
			this.mProtectFromCheckedChange = true;
			setCheckedStateForView(this.mCheckedId, true);
			this.mProtectFromCheckedChange = false;
			setCheckedId(this.mCheckedId);
		}
	}

	@Override
	public void addView(View child, int index, android.view.ViewGroup.LayoutParams params) {
		RadioButton button;
		if (child instanceof RadioButton) {
			button = (RadioButton) child;
			if (button.isChecked()) {
				this.mProtectFromCheckedChange = true;
				if (this.mCheckedId != -1) {
					setCheckedStateForView(this.mCheckedId, false);
				}
				this.mProtectFromCheckedChange = false;
				setCheckedId(button.getId());
			}
		} else if (child instanceof ViewGroup) {
			ArrayList<RadioButton> buttons = findRadioButton((ViewGroup) child);
			int size = buttons.size();
			for (int i = 0; i < size; i++) {
				button = buttons.get(i);
				if (button.isChecked()) {
					this.mProtectFromCheckedChange = true;
					if (this.mCheckedId != -1) {
						setCheckedStateForView(this.mCheckedId, false);
					}
					this.mProtectFromCheckedChange = false;
					setCheckedId(button.getId());
				}
			}
		}
		super.addView(child, index, params);
	}

	public ArrayList<RadioButton> findRadioButton(ViewGroup group) {
		ArrayList<RadioButton> resBtn = new ArrayList();
		int len = group.getChildCount();
		for (int i = 0; i < len; i++) {
			if (group.getChildAt(i) instanceof RadioButton) {
				resBtn.add((RadioButton) group.getChildAt(i));
			} else if (group.getChildAt(i) instanceof ViewGroup) {
				findRadioButton((ViewGroup) group.getChildAt(i));
			}
		}
		return resBtn;
	}

	public void check(int id) {
		if (id == -1 || id != this.mCheckedId) {
			if (this.mCheckedId != -1) {
				setCheckedStateForView(this.mCheckedId, false);
			}
			if (id != -1) {
				setCheckedStateForView(id, true);
			}
			setCheckedId(id);
		}
	}

	private void setCheckedId(int id) {
		this.mCheckedId = id;
		if (this.mOnCheckedChangeListener != null) {
			this.mOnCheckedChangeListener.onCheckedChanged(this, this.mCheckedId);
		}
	}

	private void setCheckedStateForView(int viewId, boolean checked) {
		View checkedView = findViewById(viewId);
		if (checkedView != null && (checkedView instanceof RadioButton)) {
			((RadioButton) checkedView).setChecked(checked);
		}
	}

	public int getCheckedRadioButtonId() {
		return this.mCheckedId;
	}

	public void clearCheck() {
		check(-1);
	}

	public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
		this.mOnCheckedChangeListener = listener;
	}

	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new LayoutParams(getContext(), attrs);
	}

	@Override
	protected boolean checkLayoutParams(android.view.ViewGroup.LayoutParams p) {
		return p instanceof LayoutParams;
	}

	@Override
	protected android.widget.LinearLayout.LayoutParams generateDefaultLayoutParams() {
		return new LayoutParams(-2, -2);
	}

}

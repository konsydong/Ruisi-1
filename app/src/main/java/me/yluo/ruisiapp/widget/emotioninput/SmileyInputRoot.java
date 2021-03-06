package me.yluo.ruisiapp.widget.emotioninput;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import me.yluo.ruisiapp.R;
import me.yluo.ruisiapp.utils.DimenUtils;


public class SmileyInputRoot extends LinearLayout {

    private int mOldHeight = -1;
    private SmileyContainer mSmileyContainer;
    private int maxHeight = 100;


    public SmileyInputRoot(Context context) {
        super(context);
        init();
    }

    public SmileyInputRoot(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SmileyInputRoot(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        final Activity activity = (Activity) getContext();
        mSmileyContainer = new SmileyContainer(activity);
        mSmileyContainer.setBackgroundResource(R.color.bg_secondary);
        mSmileyContainer.setVisibility(GONE);
        addView(mSmileyContainer);
    }

    public void initSmiley(EditText editText, View smileyBtn, final View sendBtn) {
        mSmileyContainer.init(editText, smileyBtn, sendBtn);
    }

    public void setMoreView(View moreViewIn, View moreBtn) {
        mSmileyContainer.setMoreView(moreViewIn, moreBtn);
    }

    //return is handled
    public boolean hideSmileyContainer() {
        if (mSmileyContainer.getVisibility() == VISIBLE) {
            mSmileyContainer.hideContainer(false);
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if (height > maxHeight) {
            maxHeight = height;
        }

        if (height < 200 && mOldHeight == height) {
            return;
        }

        //测得的时键盘未展开的高度
        if (mOldHeight == -1) {
            mOldHeight = height;
            return;
        }

        final int offset = mOldHeight - height;
        mOldHeight = height;

        // 检测到布局变化非键盘引起
        if (Math.abs(offset) < DimenUtils.dip2px(getContext(), 180)) {
            return;
        }

        // offset > 0 键盘弹起了
        if (mSmileyContainer != null) {
            mSmileyContainer.onMainViewSizeChange(offset);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (!changed
                && (maxHeight > (b - t))
                && mSmileyContainer.isVisible
                && mSmileyContainer.isKeyboardShowing) {
            return;
        }
        int childTop = 0;
        // 遍历所有子视图
        int childCount = getChildCount();

        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                if (child instanceof SmileyContainer) {
                    continue;
                }

                final int childWidth = child.getMeasuredWidth();
                final int childHeight = child.getMeasuredHeight();

                final LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) child.getLayoutParams();
                childTop += lp.topMargin;

                child.layout(l, childTop, l + childWidth + lp.leftMargin, childTop + childHeight);
                childTop += childHeight + lp.bottomMargin;
            }
        }

        if (mSmileyContainer != null && mSmileyContainer.getVisibility() != GONE) {
            mSmileyContainer.layout(l, childTop, r, b);
        }
    }
}

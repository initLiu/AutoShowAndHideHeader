package com.example.lzp.demos;

import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ListView mList;
    LinearLayout mHeader;
    Map<Integer, Integer> heights = new HashMap<>();
    int mLastScrollY;
    int mMiniY, mSensityY;
    int mscrollY;
    boolean show = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mList = (ListView) findViewById(R.id.list);
        mHeader = (LinearLayout) findViewById(R.id.header);

        mMiniY = getResources().getDimensionPixelSize(R.dimen.miniY);
        mSensityY = getResources().getDimensionPixelSize(R.dimen.deltaY);

        mList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                View firstView = view.getChildAt(0);
                if (firstView == null) {
                    return;
                }
                View firstVisibleView = view.getChildAt(firstVisibleItem);
                if (firstVisibleView == null) {
                    return;
                }

                heights.put(firstVisibleItem, firstVisibleView.getHeight());
                int previousItemsHeight = 0;
                for (int i = 0; i < firstVisibleItem; i++) {
                    previousItemsHeight += heights.get(i) != null ? heights.get(i) : 0;
                }

                int currentScrollY = previousItemsHeight - firstView.getTop() + view.getPaddingTop();
                autoShowAndHideHeader(currentScrollY, currentScrollY - mLastScrollY);
                mLastScrollY = currentScrollY;
            }
        });
    }

    private void autoShowAndHideHeader(int currentY, int deltaY) {
        //向下滑动是deltaY<0,向上滑动时deltaY>0
        //如果deltaY  * mscrollY >0说明上次和这次是相同方向的滑动
        //如果deltaY  * mscrollY < 0说明上次和这次是不同方向的滑动
        //如果是相同方向的滑动，就将两次的滑动距离累加
        if (Math.signum(deltaY) * Math.signum(mscrollY) < 0) {
            mscrollY = deltaY;
        } else {
            mscrollY += deltaY;
        }

        if (Math.abs(mscrollY) > mMiniY) {
            boolean shouldShow = mscrollY > 0 ? false : true;
            autoShowAndHideHeader(shouldShow);
            mscrollY = 0;
        }
    }

    private void autoShowAndHideHeader(boolean shouldShow) {
        if (shouldShow == show) {
            return;
        }
        show = shouldShow;
        if (show) {
            ViewCompat.animate(mHeader).translationY(0).setDuration(300).setInterpolator(new DecelerateInterpolator()).withLayer().start();
        } else {
            ViewCompat.animate(mHeader).translationY(-mHeader.getBottom()).setDuration(300).setInterpolator(new DecelerateInterpolator()).withLayer().start();
        }
    }
}

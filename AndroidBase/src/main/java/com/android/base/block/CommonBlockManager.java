package com.android.base.block;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/11/18 0018.
 */
public class CommonBlockManager {

    private List<CommonBlock> mCommonBlockList;

    protected Activity activity;

    public CommonBlockManager(@NonNull Activity activity) {
        this.activity = activity;
    }

    public CommonBlockManager add(@NonNull CommonBlock commonBlock) {
        commonBlock.attachActivity(activity);
        if (mCommonBlockList == null) {
            mCommonBlockList = new ArrayList<>();
        }
        mCommonBlockList.add(commonBlock);
        return this;
    }

    public CommonBlockManager remove(@NonNull CommonBlock commonBlock) {
        commonBlock.onDestroy();
        if (mCommonBlockList != null && mCommonBlockList.contains(commonBlock)) {
            mCommonBlockList.remove(commonBlock);
        }
        return this;
    }

    @CheckResult
    public <T extends CommonBlock> T get(@NonNull Class<T> cls) {
        if (mCommonBlockList != null) {
            for (int i = 0, size = mCommonBlockList.size(); i < size; i++) {
                if (mCommonBlockList.get(i).getClass().getName().equals(cls.getName())) {
                    return (T) mCommonBlockList.get(i);
                }
            }
        }
        return null;
    }

    @CheckResult
    public List<CommonBlock> getCommonBlockList() {
        return mCommonBlockList;
    }


    /// 回调 start -------------------

    public boolean onBackPressed() {
        boolean handled = false;
        if (mCommonBlockList != null) {
            for (int i = 0, size = mCommonBlockList.size(); i < size; i++) {
                handled = mCommonBlockList.get(i).onBackPressed();
                if (handled) {
                    break;
                }
            }
        }
        return handled;
    }

    public void onResume() {
        if (mCommonBlockList != null) {
            callBlock(new Callback() {
                @Override
                public void onCall(int i) {
                    mCommonBlockList.get(i).onResume();
                }
            });
        }
    }

    public void onPause() {
        if (mCommonBlockList != null) {
            callBlock(new Callback() {
                @Override
                public void onCall(int i) {
                    mCommonBlockList.get(i).onPause();
                }
            });
        }
    }

    public void onDestroy() {
        if (mCommonBlockList != null) {
            callBlock(new Callback() {
                @Override
                public void onCall(int i) {
                    mCommonBlockList.get(i).onDestroy();
                }
            });
            mCommonBlockList.clear();
            mCommonBlockList = null;
        }
    }

    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        callBlock(new Callback() {
            @Override
            public void onCall(int i) {
                if(mCommonBlockList != null){
                    mCommonBlockList.get(i).onActivityResult(requestCode, resultCode, data);
                }
            }
        });
    }

    //// 回调 end -------------------

    private void callBlock(final Callback callback) {
        if (mCommonBlockList != null) {
            for (int i = 0, size = mCommonBlockList.size(); i < size; i++) {
                callback.onCall(i);
            }
        }
    }

    private interface Callback {

        void onCall(int i);

    }
}
package com.zhuyong.customchatdemo;

/**
 * Created by zhuyong on 2018/8/30.
 */

public class MyModel {
    private int position;
    private float val;

    public MyModel(int position, float val) {
        this.position = position;
        this.val = val;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public float getVal() {
        return val;
    }

    public void setVal(float val) {
        this.val = val;
    }
}

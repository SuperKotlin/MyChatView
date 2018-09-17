package com.zhuyong.customchatdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 多层折线图控件
 * Created by zhuyong on 2018/8/30.
 */

public class MyChatView extends View {

    private Context mContext;

    private Paint mPaintLine;//折线图
    private Paint mPaintCircle;//圆的外边框
    private Paint mPaintPoint;//圆内填充
    private Paint mPaintBottomLine;//底部X轴
    private Paint mPaintLimit;//指示线
    private Paint mPaintText;//底部X坐标文字
    private int mBottomTextHeight = 50;//底部X轴文字所占总高度，单位dp
    private int mSingleLineHeight = 100;//单个折线图的高度，单位dp
    private int mPaddingTB = 10;//折线图上下的偏移量，单位dp
    private int mLineColor;//折线图的颜色
    protected int[] mColors;//几种颜色
    private List<List<MyModel>> mListAll = new ArrayList<>();//数据源
    private int mViewWidth;//控件宽高
    private int mViewHeight;//控件宽高

    public MyChatView(Context context) {
        this(context, null);
    }

    public MyChatView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyChatView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    /**
     * 赋值
     *
     * @param list
     */
    public void setData(List<List<MyModel>> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        this.mListAll = list;
        invalidate();
    }

    /**
     * 设置折线图颜色
     *
     * @param position
     */
    private void setLineColor(int position) {
        mLineColor = mColors[position % mColors.length];
        mPaintLine.setColor(mLineColor);
        mPaintCircle.setColor(mLineColor);
    }

    private void initView() {
        mColors = new int[]{ContextCompat.getColor(mContext, R.color.colorAccent)
                , ContextCompat.getColor(mContext, R.color.colorPrimary)};

        mPaintLine = new Paint();
        mPaintLine.setStyle(Paint.Style.STROKE);
        mPaintLine.setStrokeWidth(2);
        mPaintLine.setAntiAlias(true);

        mPaintCircle = new Paint();
        mPaintCircle.setStyle(Paint.Style.STROKE);
        mPaintCircle.setStrokeWidth(3);
        mPaintCircle.setAntiAlias(true);

        mPaintPoint = new Paint();
        mPaintPoint.setStyle(Paint.Style.FILL);
        mPaintPoint.setColor(Color.WHITE);
        mPaintPoint.setAntiAlias(true);

        mPaintBottomLine = new Paint();
        mPaintBottomLine.setStyle(Paint.Style.STROKE);
        mPaintBottomLine.setStrokeWidth(3);
        mPaintBottomLine.setColor(Color.parseColor("#999999"));
        mPaintBottomLine.setAntiAlias(true);

        mPaintLimit = new Paint();
        mPaintLimit.setStyle(Paint.Style.FILL);
        mPaintLimit.setStrokeWidth(2);
        mPaintLimit.setColor(Color.parseColor("#000000"));
        mPaintLimit.setAntiAlias(true);

        //画笔->绘制字体
        mPaintText = new Paint();
        mPaintText.setAntiAlias(true);
        mPaintText.setStyle(Paint.Style.FILL);
        mPaintText.setColor(Color.parseColor("#666666"));
        mPaintText.setTextSize(sp2px(mContext, 14));

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int jjj = 0; jjj < mListAll.size(); jjj++) {
            List<MyModel> itemList = mListAll.get(jjj);
            if (itemList != null && itemList.size() > 0) {
                float mMaxVal = Collections.max(itemList, new MyComparator()).getVal();
                Log.i("TAG", "最大值：" + mMaxVal);
                setLineColor(jjj);
                Path path = new Path();
                List<Point> pointList = new ArrayList<>();
                for (int i = 0; i < itemList.size(); i++) {
                    int xDiv = 0;
                    if (itemList.size() > 1) {
                        xDiv = (mViewWidth - getPaddingLeft() - getPaddingRight()) / (itemList.size() - 1);
                    }
                    MyModel item = itemList.get(i);
                    float x = i * xDiv;
                    float y = item.getVal() * (dip2px(mContext, mSingleLineHeight - mPaddingTB * 2)) / mMaxVal;

                    y = ((dip2px(mContext, mSingleLineHeight)) * (jjj + 1)) - dip2px(mContext, mPaddingTB * 2) - y;

                    if (i == 0) {
                        path.moveTo(x + getPaddingLeft(), y + dip2px(mContext, mPaddingTB));
                    } else {
                        path.lineTo(x + getPaddingLeft(), y + dip2px(mContext, mPaddingTB));
                    }
                    /**
                     * 这里记录一下xy坐标，用于后面绘制小球
                     */
                    Point point = new Point();
                    point.x = (int) x;
                    point.y = (int) y;
                    pointList.add(point);
                }
                //画折线
                canvas.drawPath(path, mPaintLine);
                //画小圆球
                drawCircle(canvas, pointList, jjj);
                //画文字
                if (jjj == mListAll.size() - 1) {
                    drawText(canvas, pointList);
                }
            }
        }

        /**
         * 画竖线，指示线
         */
        if (mLineX > 0) {
            canvas.drawLine(mLineX, 0, mLineX, mViewHeight - dip2px(mContext, mBottomTextHeight), mPaintLimit);
        }
    }

    /**
     * 画圆和底部X轴
     *
     * @param canvas
     * @param pointList
     */
    private void drawCircle(Canvas canvas, List<Point> pointList, int jjj) {
        for (int i = 0; i < pointList.size(); i++) {
            Point point = pointList.get(i);
            //画圆圈
            canvas.drawCircle(point.x + getPaddingLeft(), point.y + dip2px(mContext, mPaddingTB), 10, mPaintCircle);
            if (position == i && mLineX > 0) {
                mPaintPoint.setColor(mLineColor);
            } else {
                mPaintPoint.setColor(Color.WHITE);
            }
            //填充圆内空间
            canvas.drawCircle(point.x + getPaddingLeft(), point.y + dip2px(mContext, mPaddingTB), 9, mPaintPoint);
            //画X轴间隔线
            canvas.drawLine(point.x + getPaddingLeft(), dip2px(mContext, mSingleLineHeight) * (jjj + 1), point.x + getPaddingLeft(), dip2px(mContext, mSingleLineHeight) * (jjj + 1) - dip2px(mContext, 5), mPaintBottomLine);
        }

        //底部X轴
        canvas.drawLine(0, dip2px(mContext, mSingleLineHeight) * (jjj + 1), mViewWidth, dip2px(mContext, mSingleLineHeight) * (jjj + 1), mPaintBottomLine);

    }

    /**
     * 画文字
     *
     * @param canvas
     * @param pointList
     */
    private void drawText(Canvas canvas, List<Point> pointList) {
        for (int i = 0; i < pointList.size(); i++) {
            Point point = pointList.get(i);
            //画底部文字
            String text = (i + 1) + "";
            //获取文字宽度
            float textWidth = mPaintText.measureText(text, 0, text.length());
            float dx = point.x + getPaddingLeft() - textWidth / 2;
            Paint.FontMetricsInt fontMetricsInt = mPaintText.getFontMetricsInt();
            float dy = (fontMetricsInt.bottom - fontMetricsInt.top) / 2 - fontMetricsInt.bottom;
            float baseLine = dip2px(mContext, mSingleLineHeight) * mListAll.size() + dip2px(mContext, mBottomTextHeight / 2) + dy;
            canvas.drawText(text, dx, baseLine, mPaintText);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /**
         * 这里根据数据有多少组来动态计算整个view的高度，然后重新设置尺寸
         */
        mViewHeight = dip2px(mContext, mSingleLineHeight) * mListAll.size() + dip2px(mContext, mBottomTextHeight);
        mViewWidth = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(mViewWidth, mViewHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                getPointLine(event.getX());
        }

        return true;
    }

    private float mLineX = 0;
    private int position = 0;

    /**
     * 判断触摸的坐标距离哪个点最近
     *
     * @param mRawX
     */
    private void getPointLine(float mRawX) {
        if (mListAll == null || mListAll.size() == 0) {
            return;
        }
        float newLineX = 0;
        //触摸在折线区域
        if (mRawX <= mViewWidth - getPaddingRight() && mRawX >= getPaddingLeft()) {
            if (mListAll.get(0).size() == 1) {
                newLineX = getPaddingLeft();
                position = 0;
            } else {
                for (int i = 0; i < mListAll.get(0).size(); i++) {
                    int xDiv = 0;
                    if (mListAll.get(0).size() > 1) {
                        xDiv = (mViewWidth - getPaddingLeft() - getPaddingRight()) / (mListAll.get(0).size() - 1);
                    }

                    float x1 = i * xDiv + getPaddingLeft();
                    float x2 = (i + 1) * xDiv + getPaddingLeft();
                    //判断触摸在两个点之间时，离谁更近一些
                    if (mRawX > x1 && mRawX < x2) {
                        float cneterX = x1 + (x2 - x1) / 2;
                        if (mRawX > cneterX) {
                            newLineX = x2;
                            position = i + 1;
                            if (position == mListAll.get(0).size()) {
                                position = i;
                            }
                        } else {
                            newLineX = x1;
                            position = i;
                        }
                        break;
                    }
                }
            }
        } else if (mRawX < getPaddingLeft()) {//触摸在折线左边
            newLineX = getPaddingLeft();
            position = 0;
        } else {//触摸在折线右边
            if (mListAll.get(0).size() == 1) {
                newLineX = getPaddingLeft();
                position = 0;
            } else {
                newLineX = mViewWidth - getPaddingRight();
                position = mListAll.get(0).size() - 1;
            }
        }
        /**
         * 这里判断如果跟上次的触摸结果一样，则不处理
         */
        if (mLineX == newLineX) {
            return;
        }
        mLineX = newLineX;

        notifyUI(mLineX);

    }

    /**
     * 选中某一组
     *
     * @param position
     */
    public void setPosition(int position) {
        try {
            this.position = position;
            int xDiv = (mViewWidth - getPaddingLeft() - getPaddingRight()) / (mListAll.get(0).size() - 1);
            mLineX = position * xDiv + getPaddingLeft();

            notifyUI(mLineX);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("MyChatView", "Exception:" + e);
        }
    }

    private void notifyUI(float mLineX) {
        this.mLineX = mLineX;
        if (onClickListener != null) {
            onClickListener.click(position);
        }
        invalidate();
    }

    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener listener) {
        this.onClickListener = listener;
    }


    public interface OnClickListener {
        void click(int position);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    private class MyComparator implements Comparator<MyModel> {
        public int compare(MyModel o1, MyModel o2) {
            return (o1.getVal() < o2.getVal() ? -1 : (o1.getVal() == o2.getVal() ? 0 : 1));
        }
    }

}




















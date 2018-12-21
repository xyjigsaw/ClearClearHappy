package cn.xt.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import cn.xt.activity.R;
import cn.xt.interfaces.MainViewInterface;
import cn.xt.model.FlashBitmap;
import cn.xt.utils.DisplayUtil;

/**
 * Created by SuperYang88 on 2018/1/12.
 */

public class MainView extends View {

    private Bitmap startBtn; //模式1
    private Bitmap startBtnMode2; //模式2
    private Bitmap rank; //排行榜
    private Bitmap lock; //家长锁
    private Bitmap background; // 背景图片
    private Paint paint; // 画笔
    private int screenWidth;
    private int screenHeight;
    private int x, y, w, h; // 开始按钮显示的区域
    private int x1,y1,w1,h1;
    private int x2,y2,w2,h2;
    private int x3,y3,w3,h3;
    private MainViewInterface listener; // 事件监听接口

    public MainView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Resources res = this.getResources();
        startBtn = BitmapFactory.decodeResource(res, R.mipmap.mode1);
        startBtnMode2 = BitmapFactory.decodeResource(res, R.mipmap.mode2);
        rank = BitmapFactory.decodeResource(res, R.mipmap.rank);
        lock = BitmapFactory.decodeResource(res, R.mipmap.lock);
        // 加载背景图片
        background = BitmapFactory.decodeResource(res, R.mipmap.play_bg);
        screenHeight = DisplayUtil.getScreenHeight(context);
        screenWidth = DisplayUtil.getScreenWidth(context);
        // 实例化画笔
        paint = new Paint();
        paint.setAntiAlias(false); // 消除锯齿
    }

    /*
    * 主界面显示区域
    *
    * */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Bitmap bgBitmap, btnBitmap, btnBitmap2,btnBitmap3,btnBitmap4;
        // 绘制背景图片
        bgBitmap = DisplayUtil.resizeBitmap(background, screenWidth, screenHeight);
        canvas.drawBitmap(bgBitmap, 0, 0, paint);
        // 计算开始按钮显示的坐标
        w = (int) (screenWidth * 0.5);
        h = (int) (screenHeight * 0.08);
        x = screenWidth / 2 - w / 2;
        y = screenHeight - (int) (screenHeight * 0.75);
        btnBitmap = DisplayUtil.resizeBitmap(startBtn, w, h);
        canvas.drawBitmap(btnBitmap, x, y, paint);

        // 模式2按钮的位置
        w1 = (int) (screenWidth * 0.5);
        h1 = (int) (screenHeight * 0.08);
        x1 = screenWidth / 2 - w1 / 2;
        y1 = screenHeight - (int) (screenHeight * 0.60);
        btnBitmap2 = DisplayUtil.resizeBitmap(startBtnMode2, w1, h1);
        canvas.drawBitmap(btnBitmap2, x1, y1, paint);

        // 排行榜
        w2 = (int) (screenWidth * 0.35);
        h2 = (int) (screenHeight * 0.07);
        x2 = screenWidth / 2 - w2 / 2;
        y2 = screenHeight - (int) (screenHeight * 0.48);
        btnBitmap3 = DisplayUtil.resizeBitmap(rank, w2, h2);
        canvas.drawBitmap(btnBitmap3, x2, y2, paint);

        // 家长锁
        w3 = (int) (screenWidth * 0.35);
        h3 = (int) (screenHeight * 0.07);
        x3 = screenWidth / 2 - w3 / 2;
        y3 = screenHeight - (int) (screenHeight * 0.35);
        btnBitmap4 = DisplayUtil.resizeBitmap(lock, w3, h3);
        canvas.drawBitmap(btnBitmap4, x3, y3, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 获取当前触控位置
        int ex = (int) event.getX();
        int ey = (int) event.getY();
        switch (event.getAction()) {
            // 按下
            case MotionEvent.ACTION_DOWN:
                if (ex > x && ex < (x + w)
                        && ey > y && ey < (y + h)) {
                    // Toast.makeText(MainView.this.getContext(), "点击开始", Toast.LENGTH_SHORT).show();
                    listener.startGame();
                }else if(ex > x1 && ex < (x1 + w1)&& ey > y1 && ey < (y1 + h1)) {//第二个触发状态
                    // Toast.makeText(MainView.this.getContext(), "点击开始", Toast.LENGTH_SHORT).show();
                    listener.startGameMode2();//模式需要改
                }
                break;
            // 移动
            case MotionEvent.ACTION_MOVE:
                break;
            // 抬起
            case MotionEvent.ACTION_UP:
                break;
        }
        // 刷新界面
        invalidate();
        // 使系统响应事件，返回true
        return true;
    }

    /**
     * 此方法交由MainActivity调用
     * 目的是获取公共接口对象以实现需要功能
     *
     * @param mvi
     */
    public void setMainViewInterface(MainViewInterface mvi) {
        this.listener = mvi;
    }
}

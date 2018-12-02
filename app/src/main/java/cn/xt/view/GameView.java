package cn.xt.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.xt.activity.R;
import cn.xt.model.FlashBitmap;
import cn.xt.utils.DisplayUtil;
import cn.xt.utils.StageUtil;
import cn.xt.utils.ZooUtil;


public class GameView extends View {

    private Bitmap background; // 游戏背景
    private Bitmap floorBg;
    private Bitmap mode1;
    private Paint paint; // 画笔
    private int screenWidth;
    private int screenHeight;
    private Context context;
    // 资源统一管理
    private int row = StageUtil.row;
    private int col = StageUtil.col;
    // 两个方块的交换状态
    private boolean swapState = false;
    // 载入动物头像动画是否结束状态
    private boolean loadAnimalState = false;
    // 是否要加载舞台消除动画(程序运行时立即加载)
    private boolean load = true;
    private boolean clearLoad = true;
    // 虚拟背景
    private FlashBitmap[][] transBitmap = new FlashBitmap[row][col];
    // 动物头像以及游戏坐标
    private FlashBitmap[][] bitmaps = new FlashBitmap[row][col];
    // 背景音乐
    private MediaPlayer bgMedia;
    private SoundPool soundPool = new SoundPool.Builder().setMaxStreams(10).build();
    private Map<Integer, Integer> soundPoolMap = new HashMap<>();
    // 下落音乐索引
    private int clearMediaIndex = 0;
    // 线程池
    ExecutorService pool = Executors.newFixedThreadPool(5);
//    private BlockingQueue queue = new LinkedBlockingQueue();
//    private ThreadPoolExecutor pool = new ThreadPoolExecutor(3,
//            10,
//            10,
//            TimeUnit.SECONDS,
//            queue);
    // 游戏相关数据
    private int level = 1;
    private int currScore = 0;
    private int[] accessScore = {50, 100};

    public GameView(Context context, AttributeSet attr) {
        super(context, attr);
        this.context = context;
        screenHeight = DisplayUtil.getScreenHeight(context);
        screenWidth = DisplayUtil.getScreenWidth(context);
        // 音乐相关初始化
        // soundPoolMap.put(1, soundPool.load(this.getContext(), R.raw.bg_game, 1));
        bgMedia = MediaPlayer.create(context, R.raw.bg_game);
        bgMedia.start();
        // 循环播放监听
        bgMedia.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                bgMedia.start();
                bgMedia.setLooping(true);
            }
        });
        soundPoolMap.put(2, soundPool.load(this.getContext(), R.raw.swap_one, 1));
        soundPoolMap.put(3, soundPool.load(this.getContext(), R.raw.swap_two, 1));

        soundPoolMap.put(4, soundPool.load(this.getContext(), R.raw.clear_1, 1));
        soundPoolMap.put(5, soundPool.load(this.getContext(), R.raw.clear_2, 1));
        soundPoolMap.put(6, soundPool.load(this.getContext(), R.raw.clear_3, 1));
        soundPoolMap.put(7, soundPool.load(this.getContext(), R.raw.clear_4, 1));
        soundPoolMap.put(8, soundPool.load(this.getContext(), R.raw.clear_5, 1));
        soundPoolMap.put(9, soundPool.load(this.getContext(), R.raw.clear_6, 1));
        soundPoolMap.put(10, soundPool.load(this.getContext(), R.raw.clear_7, 1));

        // soundPool.play(soundPoolMap.get(1), 1, 1, 1, -1, 1);

        // int ave = screenWidth / (row + 2); // 将屏幕宽度分为 row + 2 等份
        int ave = 0;
        int size = (screenWidth - ave * 2) / row; // 其它两分为：舞台距离屏幕左右边的像素
        ZooUtil.initZooData(size, size, this.getResources());                                           // 初始化动物头像数据
        // 背景图片
        background = BitmapFactory.decodeResource(this.getResources(), R.mipmap.game_bg);
        floorBg = BitmapFactory.decodeResource(this.getResources(), R.mipmap.floor_bg);
        mode1 = BitmapFactory.decodeResource(this.getResources(), R.mipmap.mode1);
        /*
         * 计算出舞台距离左边屏幕的距离
         * 计算方式为：
         *   (屏幕总宽度 - 人物头像的宽 * 总行数) / 2
         */
        int leftSpan = (screenWidth - ZooUtil.getAnimalWidth() * row) / 2;
        int topSpan = (screenHeight - ZooUtil.getAnimalHeight() * col) / 3;
        // 将游戏舞台的坐标、高宽保存起来
        StageUtil.initStage(leftSpan, topSpan,
                leftSpan + ZooUtil.getAnimalWidth() * row,
                topSpan + ZooUtil.getAnimalHeight() * col);
        // 实例化画笔
        paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true); // 消除锯齿
        initGamePoint();
    }

    /**
     * 生成游戏坐标
     */
    private void initGamePoint() {
        currScore = 0; // 清空当前得分
        bitmaps = new FlashBitmap[row][col];
        // 生成背景图片的坐标(仅背景图片，后续可考虑将特效也加进来)
        for (int i = 0; i < row; ++i) {
            for (int j = 0; j < col; ++j) {
                do {
                    // 计算头像坐标
                    FlashBitmap bitmap = ZooUtil.getAnimal();
                    bitmap.setX(StageUtil.getStage().getX() + i * ZooUtil.getAnimalWidth());
                    bitmap.setY(StageUtil.getStage().getY() + j * ZooUtil.getAnimalHeight());
                    transBitmap[i][j] = bitmap.clone();
                    bitmap.setY(0); // 在顶部慢慢下落
                    bitmaps[i][j] = bitmap;
                    System.out.println("111111111111");
                } while(StageUtil.checkClearPoint(bitmaps));
            }
        }
        load = true;
        clearLoad = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制背景图片
        Bitmap bgBitmap = DisplayUtil.resizeBitmap(background, screenWidth, screenHeight);
        canvas.drawBitmap(bgBitmap, 0, 0, paint);

        // 模式1的图片
        int w1 = (int) (screenWidth * 0.5);
        int h1 = (int) (screenHeight * 0.08);
        int x1 = screenWidth / 2 - w1 / 2;
        int y1 = screenHeight - (int) (screenHeight * 0.98);
        Bitmap btnBitmap = DisplayUtil.resizeBitmap(mode1, w1, h1);
        canvas.drawBitmap(btnBitmap, x1, y1, paint);


        Bitmap floor = DisplayUtil.resizeBitmap(floorBg, ZooUtil.getAnimalWidth(), ZooUtil.getAnimalHeight());
        // 每一个小头像背后的背景
        for (int i = 0; i < row; ++i) {
            for (int j = 0; j < col; ++j) {
                if(transBitmap[i][j] != null){
                    int x = (int) transBitmap[i][j].getX();
                    int y = (int) transBitmap[i][j].getY();
                    canvas.drawBitmap(floor, x, y, paint);
                }
            }
        }
        // 舞台中的所有动物头像
        FlashBitmap bitmap;
        for (int i = 0; i < row; ++i) {
            for (int j = 0; j < col; ++j) {
                bitmap = bitmaps[i][j];
                if(bitmap != null // 并且坐标点要进入舞台
                        && StageUtil.inStage(bitmap.getX(), bitmap.getY() + bitmap.getHeight() / 2)){
                    canvas.drawBitmap(bitmap.getBitmap(), bitmap.getX(), bitmap.getY(), paint);
                }
            }
        }
        // 是否需要加载消除
        if (clearLoad && load) {
            clearBitmap();
            load = false;
        }
        paint.setColor(Color.WHITE);
        paint.setTextSize(32);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD , Typeface.BOLD));
        canvas.drawText("当前关卡：" + level, 10, StageUtil.getStage().getHeight() + 70, paint);
        canvas.drawText("当前得分：" + currScore, 10, StageUtil.getStage().getHeight() + 120, paint);
        canvas.drawText("通关分数：" + accessScore[level - 1], 10, StageUtil.getStage().getHeight() + 170, paint);
        // 刷新屏幕的频率(理论上小于25，人就会感觉物体是在移动)
        postInvalidateDelayed(1);
    }

    // 用来保存鼠标按下的两个坐标值
    FlashBitmap p1 = new FlashBitmap();
    FlashBitmap p2 = new FlashBitmap();
    boolean isDown = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 判断交换状态是否完毕
        if(swapState){
            return false;
        }
        // 如果正在做下落动画不允许操作
        if(loadAnimalState){
            return false;
        }
        // 获取当前触控位置
        float ex = event.getX();
        float ey = event.getY();
        switch (event.getAction()) {
            // 按下
            case MotionEvent.ACTION_DOWN:
                // 判断是否该点是按在舞台上
                if (!isDown && StageUtil.inStage(ex, ey)) {
                    p1.setX(ex);
                    p1.setY(ey);
                    isDown = true;
                    soundPool.play(soundPoolMap.get(2), 1, 1, 0, 0, 1);
                }
                break;
            // 移动
            case MotionEvent.ACTION_MOVE:
                // 判断是否该点是按在舞台上
                if (!isDown && StageUtil.inStage(ex, ey)) {
                    p1.setX(ex);
                    p1.setY(ey);
                    isDown = true;
                }
                break;
            // 抬起
            case MotionEvent.ACTION_UP:
                if (isDown) {
                    p2.setX(ex);
                    p2.setY(ey);
                    isDown = false;
                    prepSwap();
                }
                break;
        }
        // 使系统响应事件，返回true
        return true;
    }


    /**
     * 交换预处理
     */
    private void prepSwap() {
        pool.execute(new Runnable() {
            @Override
            public void run() {
                // 保存真实比例的四个点坐标
                swapState = true;
                int[] point = new int[4];
                if (StageUtil.checkTwoPoint(p1, p2, point)) {
                    // 尝试交换
                    soundPool.play(soundPoolMap.get(3), 1, 1, 0, 0, 1);
                    swap(point[0], point[1], point[2], point[3]);
                    // 判断交换后能不能消除，如果能消除则消除点
                    if (StageUtil.checkClearPoint(bitmaps)) {
                        load = true; // 告诉程序可以更新
                    } else {
                        // 如果不能消除继续交换回来
                        swap(point[0], point[1], point[2], point[3]);
                    }
                }
                // 交换完成改回状态
                swapState = false;
            }
        });
    }

    /**
     * 交换
     */
    final int time = 1; // 交换间隔时间
    float speed = 1; // 交换的速度
    Thread t1 = null;
    Thread t2 = null;
    private void swap(int x1, int y1, int x2, int y2) {
        speed = screenWidth / 560.0f; // 根据分辨率计算出不同的下落速度
        // 判断是横着交换还是竖的交换
        final int px1 = (int) StageUtil.getStage().getX() + x1 * ZooUtil.getAnimalWidth();
        final int py1 = (int) StageUtil.getStage().getY() + y1 * ZooUtil.getAnimalHeight();
        final int px2 = (int) StageUtil.getStage().getX() + x2 * ZooUtil.getAnimalWidth();
        final int py2 = (int) StageUtil.getStage().getY() + y2 * ZooUtil.getAnimalHeight();

        final FlashBitmap one = bitmaps[x1][y1];
        final FlashBitmap two = bitmaps[x2][y2];
        // 先进行真实坐标点互换
        FlashBitmap temp = bitmaps[x1][y1];
        bitmaps[x1][y1] = bitmaps[x2][y2];
        bitmaps[x2][y2] = temp;
        /*
         * 计算交换方式
         */
        // 判断是x轴交换还是y轴交换
        if (Math.abs(x1 - x2) == 1) {
            if (px1 < px2) {
                // 横着交换
                t1 = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (float i = px1; i <= px2; i += speed) {
                            one.setX(i);
                            DisplayUtil.sleep(time);
                        }
                    }
                });
                t2 = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (float i = px2; i >= px1; i -= speed) {
                            two.setX(i);
                            DisplayUtil.sleep(time);
                        }
                    }
                });
            } else {
                // 横着交换
                t1 = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (float i = px2; i <= px1; i += speed) {
                            two.setX(i);
                            DisplayUtil.sleep(time);
                        }
                    }
                });
                t2 = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (float i = px1; i >= px2; i -= speed) {
                            one.setX(i);
                            DisplayUtil.sleep(time);
                        }
                    }
                });
            }
        } else {
            if (y1 < y2) {
                // 横着交换
                t1 = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (float i = py1; i <= py2; i += speed) {
                            one.setY(i);
                            DisplayUtil.sleep(time);
                        }
                    }
                });
                t2 = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (float i = py2; i >= py1; i -= speed) {
                            two.setY(i);
                            DisplayUtil.sleep(time);
                        }
                    }
                });
            } else {
                // 横着交换
                t1 = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (float i = py2; i <= py1; i += speed) {
                            two.setY(i);
                            DisplayUtil.sleep(time);
                        }
                    }
                });
                t2 = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (float i = py1; i >= py2; i -= speed) {
                            one.setY(i);
                            DisplayUtil.sleep(time);
                        }
                    }
                });
            }
        }
        pool.execute(t1);
        pool.execute(t2);
        try {
            t1.join();
            t2.join();
        } catch (Exception e){
            e.printStackTrace();
        }
        DisplayUtil.sleep(100);
    }

    /**
     * 清除头像
     */
    private synchronized void clearBitmap() {
        clearLoad = false;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                loadAnimalState = true; // 开始加载动物
                int size = 0;
                while(StageUtil.checkClearPoint(bitmaps)){
                    List<FlashBitmap> clearList = StageUtil.getOneGroupClearPoint(bitmaps);
                    // 避免越界
                    if(4 + clearMediaIndex >= soundPoolMap.size()){
                        --clearMediaIndex;
                    }
                    // 清除的声音是从第4个位置开始的
                    soundPool.play(soundPoolMap.get(4 + clearMediaIndex), 1, 1, 0, 0, 1);
                    for (FlashBitmap point : clearList) {
                        bitmaps[(int) point.getX()][(int) point.getY()] = null;
                        ++size;
                    }
                    ++clearMediaIndex;
                    DisplayUtil.sleep(200);
                }
                // 移动其它头像
                boolean updateFlag = false;
                boolean canDoWhile;
                int[] index = new int[col];
                do {
                    canDoWhile = false;
                    // 开始进行下落处理
                    for(int j = col - 1; j >= 0; --j){
                        for(int i = 0; i < row; ++i){
                            if(bitmaps[i][j] != null){
                                if(!moveStageAnimal(i, j)){
                                    canDoWhile = true;
                                }
                            } else {
                                // 只要检测到有任意一个空位就要进行更新
                                updateFlag = true;
                                // 如果是空，检测从当前位置往上是否还有其它动物头像
                                boolean hasPoint = false;
                                for(int k = j - 1; k >= 0; --k){
                                    // 如果检测到有一个点的话就不搞事情了
                                    if(bitmaps[i][k] != null){
                                        hasPoint = true;
                                    }
                                }
                                // 如果没有就直接生成一个新点在这个位置
                                // 真实坐标是这个位置，但显示在地图上的坐标要给个到 (x, 0)
                                if(!hasPoint) {
                                    FlashBitmap bitmap = ZooUtil.getAnimal();
                                    bitmap.setX(StageUtil.getStage().getX() + i * ZooUtil.getAnimalWidth());
                                    bitmap.setY(StageUtil.getStage().getY() - (index[i] + 1) * ZooUtil.getAnimalHeight());
                                    bitmaps[i][j] = bitmap;
                                    index[i]++;
                                }
                            }
                        }
                    }
                    // 动画停留间隔
                    DisplayUtil.sleep(time);
                } while(canDoWhile);
                loadAnimalState = false; // 加载动物头像完毕
                // 提示系统进行可消除检测，停0.15秒再载入动画
                if(updateFlag || StageUtil.checkClearPoint(bitmaps)) {
                    DisplayUtil.sleep(150);
                    load = true;
                } else {
                    clearMediaIndex = 0;
                }
                // 计算得分
                if(size > 0 && size <= 6){
                    currScore += size;
                } else if(size > 6 && size <= 9) {
                    currScore += size * 3;
                } else {
                    currScore += size * 5;
                }
                if(level > accessScore.length){
                    showMsg("没有更多关卡了!");
                    DisplayUtil.sleep(3000);
                    return;
                }
                // 判断分数是否符合要求
                if(currScore >= accessScore[level - 1]){
                    ++level;
                    showMsg("恭喜您通关啦!");
                    DisplayUtil.sleep(3000);
                    initGamePoint();
                }
            }
        });
        pool.execute(thread);
        try {
            thread.join();
        }catch (Exception e){
            e.printStackTrace();
        }
        clearLoad = true;
    }

    /**
     * 移动舞台动物
     * @param x x
     * @param y y
     * @return 成功true 失败false
     */
    private boolean moveStageAnimal(int x, int y) {
        // 记录当前的点
        int j;
        float currY = bitmaps[x][y].getY();
        // 寻找最佳底部的空位
        for(j = col - 1; j >= 0; --j){
            // 不能小于以前的位置
            if(j <= y){
                break;
            }
            // 从底往上找，找到的第一个空位就为要到的位置
            if(bitmaps[x][j] == null){
                break;
            }
        }
        // 有最新点时才进行交换
        if(j != y){
            FlashBitmap temp = bitmaps[x][y];
            bitmaps[x][y] = null;
            bitmaps[x][j] = temp;
        }
        // 不允许在这之上的方块提前下落到下一个方块后面
        if(j < col - 1 && bitmaps[x][j + 1] != null){
            if(currY + ZooUtil.getAnimalHeight() >= bitmaps[x][j + 1].getY()){
                return true;
            }
        }
        // 到达指定高度停止
        if(currY >= j * ZooUtil.getAnimalHeight() + StageUtil.getStage().getY()){
            return true;
        }
        // 大于舞台高度直接停止
        if(currY + bitmaps[x][j].getHeight() >= StageUtil.getStage().getHeight()){
            return true;
        }
        // 自增
        currY += speed;
        bitmaps[x][j].setY(currY);
        return false;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        soundPool.release();
        pool.shutdown();
        while (true) {
            if(pool.isTerminated()) {
                System.out.println("pool is shutdown");
                break;
            }
            DisplayUtil.sleep(100);
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(context, msg.obj.toString(), Toast.LENGTH_LONG).show();
        }
    };

    public void showMsg(String msg) {
        Message message = new Message();
        message.obj = msg;
        handler.sendMessage(message);
    }
}

package cn.xt.utils;

import android.content.res.Resources;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.xt.activity.R;
import cn.xt.model.FlashBitmap;

/**
 * Created by 小天 on 2018/1/16.
 */
public class ZooUtil {
    // 动物头像的宽和高
    private static int animalWidth = 0;
    private static int animalHeight = 0;
    private static Map<Integer, FlashBitmap> map = new HashMap<>();
    private static FlashBitmap ali = new FlashBitmap(); // 阿狸
    private static FlashBitmap xj = new FlashBitmap(); // 小鸡
    private static FlashBitmap gx = new FlashBitmap(); // 狗熊
    private static FlashBitmap qw = new FlashBitmap(); // 青蛙
    private static FlashBitmap hm = new FlashBitmap(); // 河马

    /**
     * 初始化动物头像数据
     * @param width
     * @param height
     * @param res
     */
    public static void initZooData(int width, int height, Resources res) {
        animalWidth = width;
        animalHeight = height;
        // 初始化动物图像
        ali.setId(1);
        ali.setSize(width, height);
        ali.setBitmap(BitmapFactory.decodeResource(res, R.mipmap.ali));
        xj.setId(2);
        xj.setSize(width, height);
        xj.setBitmap(BitmapFactory.decodeResource(res, R.mipmap.xj));
        gx.setId(3);
        gx.setSize(width, height);
        gx.setBitmap(BitmapFactory.decodeResource(res, R.mipmap.gx1));
        qw.setId(4);
        qw.setSize(width, height);
        qw.setBitmap(BitmapFactory.decodeResource(res, R.mipmap.qw));
        hm.setId(5);
        hm.setSize(width, height);
        hm.setBitmap(BitmapFactory.decodeResource(res, R.mipmap.hm));
        map.put(ali.getId(), ali);
        map.put(xj.getId(), xj);
        map.put(gx.getId(), gx);
        map.put(qw.getId(), qw);
        map.put(hm.getId(), hm);
    }

    /**
     * 随机获取动物头像
     * @return
     */
    private static List<Integer> prepIndex = new ArrayList<>();
    private static final int overflow = 3; // 该值 >= 0 <= map.size() - 1
    public static FlashBitmap getAnimal() {
        while(true){
            int index = (int)(Math.random() * map.size() + 1);
            // 判断是否相同
            if(prepIndex.contains(index)){
                continue;
            }
            prepIndex.add(index);
            // 只要不与上 overflow 次的相同就行了
            if(prepIndex.size() > overflow){
                prepIndex.remove(0);
            }
            return map.get(index).clone();
        }
    }

    public static int getAnimalWidth() {
        return animalWidth;
    }

    public static int getAnimalHeight() {
        return animalHeight;
    }
}

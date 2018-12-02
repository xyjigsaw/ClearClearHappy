package cn.xt.utils;

import android.content.res.Resources;
import android.graphics.BitmapFactory;

import java.lang.reflect.Field;
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
    private static FlashBitmap img[][] = {{new FlashBitmap(),new FlashBitmap(),new FlashBitmap(),new FlashBitmap(),new FlashBitmap()}};



    /**
     * 初始化动物头像数据
     * @param width
     * @param height
     * @param res
     */
    public static void initZooData(int width, int height, Resources res) {
        animalWidth = width;
        animalHeight = height;

    /**
     * 初始化汉字图像
     * @return
     */
        for(int i=0;i<img.length;i++){
            for(int j=0;j<img[i].length;j++){
                img[i][j].setId((i*img[i].length+j+1));
                img[i][j].setSize(width,height);
                String temp = "c" + (i+1);
                temp += (j+1);
                int tempID = getResId(temp,R.mipmap.class);
                img[i][j].setBitmap(BitmapFactory.decodeResource(res,tempID));
                map.put(img[i][j].getId(),img[i][j]);
                System.out.print("cdscdsc");
            }
        }
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

    public static int getResId(String variableName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(variableName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int getAnimalWidth() {
        return animalWidth;
    }

    public static int getAnimalHeight() {
        return animalHeight;
    }
}

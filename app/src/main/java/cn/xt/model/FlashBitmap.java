package cn.xt.model;

import android.graphics.Bitmap;

import cn.xt.utils.DisplayUtil;

/**
 * Created by 小天 on 2018/1/12.
 */
public class FlashBitmap implements Cloneable {
    private volatile transient int id;
    private Bitmap bitmap;
    private volatile transient float x;
    private volatile transient float y;
    private int width;
    private int height;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        // 调用屏幕像素转换的方法
        this.bitmap = DisplayUtil.resizeBitmap(bitmap, this.width, this.height);
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public FlashBitmap clone() {
        FlashBitmap bitmap = null;
        try {
            bitmap = (FlashBitmap) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}

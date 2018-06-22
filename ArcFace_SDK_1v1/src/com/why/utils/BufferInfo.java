package com.why.utils;

/**
 * @Author :王皓月
 * @Date :2018/6/1 上午9:53
 * @Description :BufferInfo工具类
 */

public class BufferInfo {

    public int width;
    public int height;
    public byte[] buffer;

    public BufferInfo(int w, int h, byte[] buf) {
        width = w;
        height = h;
        buffer = buf;
    }

}
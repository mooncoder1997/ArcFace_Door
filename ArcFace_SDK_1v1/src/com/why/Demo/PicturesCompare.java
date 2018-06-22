package com.why.Demo;

import com.why.*;
import com.why.utils.ImageTool;

/**
 * @Author :王皓月
 * @Date :2018/6/22 下午1:27
 * @Description :打印两张图片中人脸相似度
 */

public class PicturesCompare {

    public static void main(String[] args) {

        // SDK引擎初始化
        SDKInit.FDFR_SDK_Init();

        // 加载图片
        ASVLOFFSCREEN inputImgA = ImageTool.LoadImage("pic1.jpg");
        ASVLOFFSCREEN inputImgB = ImageTool.LoadImage("pic2.jpg");

        // 打印相似值
        System.out.println(String.format("两张图片的相似度是 %f" , SDKInit.compareFaceSimilarity(SDKInit.hFDEngine, SDKInit.hFREngine, inputImgA, inputImgB)));

        // SDK引擎释放
        SDKInit.FDFR_SDK_Release();
    }

}

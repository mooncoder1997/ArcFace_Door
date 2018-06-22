package com.why.Demo;

import com.why.ASVLOFFSCREEN;
import com.why.MySQLConn.SDKSQL;
import com.why.utils.ImageTool;

/**
 * @Author :王皓月
 * @Date :2018/6/22 下午1:27
 * @Description :人脸数据库注册
 */

public class SQLRegister {

    public static void main(String[] args) {
        // SDK引擎初始化
        SDKInit.FDFR_SDK_Init();

        // 加载图片
        ASVLOFFSCREEN inputImg = ImageTool.LoadImage("pic1.jpg");

        // 注册人脸特征到数据库
        SDKSQL.getFeature(SDKInit.hFDEngine, SDKInit.hFREngine, inputImg);

        // SDK引擎释放
        SDKInit.FDFR_SDK_Release();
    }

}

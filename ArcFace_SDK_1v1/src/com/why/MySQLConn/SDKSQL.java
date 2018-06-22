package com.why.MySQLConn;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.FloatByReference;
import com.why.AFR_FSDKLibrary;
import com.why.AFR_FSDK_FACEMODEL;
import com.why.ASVLOFFSCREEN;
import com.why.Demo.SDKInit;
import com.why.FaceInfo;

/**
 * @Author :王皓月
 * @Date :2018/6/22 下午2:53
 * @Description :SDK的数据库操作工具
 */

public class SDKSQL {

    /**
     * 从图片中提取图片并保存到人脸数据库
     * @param hFDEngine 人脸检测引擎
     * @param hFREngine 人脸识别引擎
     * @param inputImg 输入注册图片
     */
    public static void getFeature(Pointer hFDEngine, Pointer hFREngine, ASVLOFFSCREEN inputImg) {
        // 第一张人脸信息通过FD获得
        FaceInfo[] faceInfo = SDKInit.doFaceDetection(hFDEngine, inputImg);
        if (faceInfo.length < 1) {
            System.out.println("no face in Image A ");
            return;
        }

        // 获取第一张人脸特征
        AFR_FSDK_FACEMODEL faceFeature = SDKInit.extractFRFeature(hFREngine, inputImg, faceInfo[0]);
        if (faceFeature == null) {
            System.out.println("extract face feature in Image A failed");
            return;
        }

        try {
            // 将提取到的人脸特征数据保存在二进制数组
            byte[] featureInByteArray = faceFeature.toByteArray();
            // 连接数据库
            DBManager.Connect();
            String name = "林允儿";
            // 向数据库中添加人脸特征数据
            DBManager.addFaceInfo(name, featureInByteArray);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     *
     * @param hFDEngine 人脸检测引擎
     * @param hFREngine 人脸识别引擎
     * @param inputImg 输入图片
     */
    public static void compareFaceSimilarity(Pointer hFDEngine, Pointer hFREngine, ASVLOFFSCREEN inputImg) {
        // 执行人脸检测
        FaceInfo[] faceInfo = SDKInit.doFaceDetection(hFDEngine, inputImg);
        if (faceInfo.length < 1) {
            System.out.println("no face in Image A ");
        }

        // 提取人脸特征信息
        AFR_FSDK_FACEMODEL faceFeatureA = SDKInit.extractFRFeature(hFREngine, inputImg, faceInfo[0]);
        if (faceFeatureA == null) {
            System.out.println("extract face feature in Image A failed");
        }

        DBManager.Connect();
        try {
            AFR_FSDK_FACEMODEL faceFeatureB = AFR_FSDK_FACEMODEL.fromByteArray(DBManager.readFaceInfo());
            FloatByReference fSimilScore = new FloatByReference(0.0f);
            NativeLong ret = AFR_FSDKLibrary.INSTANCE.AFR_FSDK_FacePairMatching(hFREngine, faceFeatureA, faceFeatureB, fSimilScore);
            faceFeatureA.freeUnmanaged();
            faceFeatureB.freeUnmanaged();
            if (ret.longValue() != 0) {
                System.out.println(String.format("AFR_FSDK_FacePairMatching failed:ret 0x%x" ,ret.longValue()));
            }
            // 打印对比值
            System.out.println("Similarity value:" + fSimilScore.getValue());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}

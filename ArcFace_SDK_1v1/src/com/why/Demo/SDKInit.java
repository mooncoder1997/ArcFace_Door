package com.why.Demo;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.PointerByReference;
import com.why.*;

/**
 * @Author :王皓月
 * @Date :2018/6/22 下午1:34
 * @Description :人脸检测(FD)、人脸识别(FR)引擎初始化
 */

public class SDKInit {

    public static final String    APPID  = "AAAAAAAAAAAAA";
    public static final String FD_SDKKEY = "BBBBBBBBBBBBB";
    public static final String FR_SDKKEY = "CCCCCCCCCCCCC";

    public static final int FD_WORKBUF_SIZE = 20 * 1024 * 1024;
    public static final int FR_WORKBUF_SIZE = 40 * 1024 * 1024;
    public static final int MAX_FACE_NUM = 50;

    static Pointer pFDWorkMem;
    static Pointer pFRWorkMem;
    static Pointer hFDEngine;
    static Pointer hFREngine;

    /**
     * 人脸检测(FD)、人脸识别(FR)引擎初始化
     */
    public static void FDFR_SDK_Init() {
        pFDWorkMem = CLibrary.INSTANCE.malloc(FD_WORKBUF_SIZE);
        pFRWorkMem = CLibrary.INSTANCE.malloc(FR_WORKBUF_SIZE);

        PointerByReference phFDEngine = new PointerByReference();
        NativeLong ret = AFD_FSDKLibrary.INSTANCE.AFD_FSDK_InitialFaceEngine(APPID, FD_SDKKEY, pFDWorkMem, FD_WORKBUF_SIZE, phFDEngine, _AFD_FSDK_OrientPriority.AFD_FSDK_OPF_0_HIGHER_EXT, 32, MAX_FACE_NUM);
        if (ret.longValue() != 0) {
            CLibrary.INSTANCE.free(pFDWorkMem);
            CLibrary.INSTANCE.free(pFRWorkMem);
            System.out.println(String.format("AFD_FSDK_InitialFaceEngine ret 0x%x",ret.longValue()));
            System.exit(0);
        }

        hFDEngine = phFDEngine.getValue();
        AFD_FSDK_Version versionFD = AFD_FSDKLibrary.INSTANCE.AFD_FSDK_GetVersion(hFDEngine);
        System.out.println(String.format("%d %d %d %d", versionFD.lCodebase, versionFD.lMajor, versionFD.lMinor, versionFD.lBuild));
        System.out.println(versionFD.Version);
        System.out.println(versionFD.BuildDate);
        System.out.println(versionFD.CopyRight);

        PointerByReference phFREngine = new PointerByReference();
        ret = AFR_FSDKLibrary.INSTANCE.AFR_FSDK_InitialEngine(APPID, FR_SDKKEY, pFRWorkMem, FR_WORKBUF_SIZE, phFREngine);
        if (ret.longValue() != 0) {
            AFD_FSDKLibrary.INSTANCE.AFD_FSDK_UninitialFaceEngine(hFDEngine);
            CLibrary.INSTANCE.free(pFDWorkMem);
            CLibrary.INSTANCE.free(pFRWorkMem);
            System.out.println(String.format("AFR_FSDK_InitialEngine ret 0x%x" ,ret.longValue()));
            System.exit(0);
        }

        hFREngine = phFREngine.getValue();
        AFR_FSDK_Version versionFR = AFR_FSDKLibrary.INSTANCE.AFR_FSDK_GetVersion(hFREngine);
        System.out.println(String.format("%d %d %d %d", versionFR.lCodebase, versionFR.lMajor, versionFR.lMinor, versionFR.lBuild));
        System.out.println(versionFR.Version);
        System.out.println(versionFR.BuildDate);
        System.out.println(versionFR.CopyRight);
    }

    /**
     * 人脸检测(FD)、人脸识别(FR)引擎释放
     */
    public static void FDFR_SDK_Release() {
        AFD_FSDKLibrary.INSTANCE.AFD_FSDK_UninitialFaceEngine(hFDEngine);
        AFR_FSDKLibrary.INSTANCE.AFR_FSDK_UninitialEngine(hFREngine);

        CLibrary.INSTANCE.free(pFDWorkMem);
        CLibrary.INSTANCE.free(pFRWorkMem);
    }

    /**
     * 对比两张图片
     * @param hFDEngine 人脸检测引擎
     * @param hFREngine 人脸识别引擎
     * @param inputImgA 输入图片1
     * @param inputImgB 输入图片2
     * @return 相似度
     */
    public static float compareFaceSimilarity(Pointer hFDEngine, Pointer hFREngine, ASVLOFFSCREEN inputImgA, ASVLOFFSCREEN inputImgB) {
        // 执行人脸检测
        FaceInfo[] faceInfosA = doFaceDetection(hFDEngine, inputImgA);
        if (faceInfosA.length < 1) {
            System.out.println("no face in Image A ");
            return 0.0f;
        }

        // 执行人脸检测
        FaceInfo[] faceInfosB = doFaceDetection(hFDEngine, inputImgB);
        if (faceInfosB.length < 1) {
            System.out.println("no face in Image B ");
            return 0.0f;
        }

        // 提取人脸特征
        AFR_FSDK_FACEMODEL faceFeatureA = extractFRFeature(hFREngine, inputImgA, faceInfosA[0]);
        if (faceFeatureA == null) {
            System.out.println("extract face feature in Image A failed");
            return 0.0f;
        }

        // 提取人脸特征
        AFR_FSDK_FACEMODEL faceFeatureB = extractFRFeature(hFREngine, inputImgB, faceInfosB[0]);
        if (faceFeatureB == null) {
            System.out.println("extract face feature in Image B failed");
            faceFeatureA.freeUnmanaged();
            return 0.0f;
        }

        // 计算两个人脸的相似度
        FloatByReference fSimilScore = new FloatByReference(0.0f);
        NativeLong ret = AFR_FSDKLibrary.INSTANCE.AFR_FSDK_FacePairMatching(hFREngine, faceFeatureA, faceFeatureB, fSimilScore);
        faceFeatureA.freeUnmanaged();
        faceFeatureB.freeUnmanaged();
        if (ret.longValue() != 0) {
            System.out.println(String.format("AFR_FSDK_FacePairMatching failed:ret 0x%x" ,ret.longValue()));
            return 0.0f;
        }
        return fSimilScore.getValue();
    }

    /**
     * 对输入图片执行人脸检测
     * @param hFDEngine 人脸检测引擎
     * @param inputImg 输入图片
     * @return 人脸信息（位置）
     */
    public static FaceInfo[] doFaceDetection(Pointer hFDEngine, ASVLOFFSCREEN inputImg) {
        FaceInfo[] faceInfo = new FaceInfo[0];

        PointerByReference ppFaceRes = new PointerByReference();
        NativeLong ret = AFD_FSDKLibrary.INSTANCE.AFD_FSDK_StillImageFaceDetection(hFDEngine, inputImg, ppFaceRes);
        if (ret.longValue() != 0) {
            System.out.println(String.format("AFD_FSDK_StillImageFaceDetection ret 0x%x" , ret.longValue()));
            return faceInfo;
        }

        AFD_FSDK_FACERES faceRes = new AFD_FSDK_FACERES(ppFaceRes.getValue());
        if (faceRes.nFace > 0) {
            faceInfo = new FaceInfo[faceRes.nFace];
            for (int i = 0; i < faceRes.nFace; i++) {
                MRECT rect = new MRECT(new Pointer(Pointer.nativeValue(faceRes.rcFace.getPointer()) + faceRes.rcFace.size() * i));
                int orient = faceRes.lfaceOrient.getPointer().getInt(i * 4);
                faceInfo[i] = new FaceInfo();

                faceInfo[i].left = rect.left;
                faceInfo[i].top = rect.top;
                faceInfo[i].right = rect.right;
                faceInfo[i].bottom = rect.bottom;
                faceInfo[i].orient = orient;

                System.out.println(String.format("%d (%d %d %d %d) orient %d", i, rect.left, rect.top, rect.right, rect.bottom, orient));
            }
        }
        return faceInfo;
    }

    /**
     * 提取人脸特征值
     * @param hFREngine 人脸识别引擎
     * @param inputImg 输入图片
     * @param faceInfo 人脸信息（位置）
     * @return 人脸特征值
     */
    public static AFR_FSDK_FACEMODEL extractFRFeature(Pointer hFREngine, ASVLOFFSCREEN inputImg, FaceInfo faceInfo) {

        AFR_FSDK_FACEINPUT faceinput = new AFR_FSDK_FACEINPUT();
        faceinput.lOrient = faceInfo.orient;
        faceinput.rcFace.left = faceInfo.left;
        faceinput.rcFace.top = faceInfo.top;
        faceinput.rcFace.right = faceInfo.right;
        faceinput.rcFace.bottom = faceInfo.bottom;

        AFR_FSDK_FACEMODEL faceFeature = new AFR_FSDK_FACEMODEL();
        NativeLong ret = AFR_FSDKLibrary.INSTANCE.AFR_FSDK_ExtractFRFeature(hFREngine, inputImg, faceinput, faceFeature);
        if (ret.longValue() != 0) {
            System.out.println(String.format("AFR_FSDK_ExtractFRFeature ret 0x%x" ,ret.longValue()));
            return null;
        }

        try {
            return faceFeature.deepCopy();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}

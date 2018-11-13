# ArcFace_SDK V1.1 1v1对比

## 功能：
 - 1v1图片对比
 - 特征值数据库注册
 - 1v1与数据库对比

## 注意事项：
1. Windows:请将SDK里的动态库放到readme.txt同级目录下；
   Linux:请将SDK里的动态库放到 {readme.txt同级目录}/bin/linux-x86-64/ 目录下。
2. 32位SDK搭配使用32位的jre,64位SDK搭配使用64位的jre,否则会失败。
3. 依赖jna-4.4.0。
4. 请设置好APPID FD_SDKKEY FR_SDKKEY:
    public static final String    APPID  = "AAAAAAAAAAAAA";
    public static final String FD_SDKKEY = "BBBBBBBBBBBBB";
    public static final String FR_SDKKEY = "CCCCCCCCCCCCC";
5. 请使用jpg格式的图片。
package com.why.MySQLConn;

import java.sql.*;

/**
 * @Author :王皓月
 * @Date :2018/6/22 下午2:47
 * @Description :人脸数据库操作工具类
 */

public class DBManager {

    protected static String dbClassName = "com.mysql.jdbc.Driver";
    protected static String dbUrl = "jdbc:mysql://127.0.0.1:3306/db_faceinfo";
    protected static String dbUser = "username";
    protected static String dbPwd = "password";
    protected static String dbName = "db_faceinfo";
    protected static String second = null;
    public static Connection conn = null;

    /**
     * 连接数据库
     */
    public static void Connect() {
        try {
            Class.forName(dbClassName).newInstance();
            conn = DriverManager.getConnection(dbUrl, dbUser, dbPwd);
            System.out.println("特征数据库连接成功！");
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    /**
     * 向数据库中添加人脸添加
     * @param name 姓名
     * @param feature 人脸特征值
     */
    public static void addFaceInfo(String name,byte[] feature) {
        String addFaceInfo = "insert into tb_faceinfo(name,face) values(?,?)";
        PreparedStatement pStatement;
        try {
            pStatement = conn.prepareStatement(addFaceInfo);
            pStatement.setString(1,name);
            pStatement.setBytes(2,feature);
            int row = pStatement.executeUpdate();
            if(row>0) {
                System.out.println("成功添加了 " + row + "条数据！");
            }
            pStatement.close();
            conn.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.println("添加失败！");
            e.printStackTrace();
        }
    }

    /**
     * 从数据库中读取人脸
     * @return
     * @throws Exception
     */
    public static byte[] readFaceInfo() throws Exception {
        String readFaceInfo = "select face from tb_faceinfo";
        byte[] featureInByteArray = null;
        try {
            PreparedStatement pStatement = conn.prepareStatement(readFaceInfo);
            ResultSet resultSet = pStatement.executeQuery();
            if(resultSet.next()) {
                featureInByteArray = resultSet.getBytes("face");
            }
            pStatement.close();
            conn.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return featureInByteArray;
    }

}

package com.test;

import com.alibaba.fastjson.JSONObject;
import com.utils.FaceUtil;

import java.io.File;

public class test {

    public static void main1(String[] args) {
        File file=new File("C:\\Users\\ldsq\\Desktop\\图片\\default.jpg");
        try {
            String str= FaceUtil.detect(file);//得到face——token的值
            System.out.println(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        File file=new File("C:\\Users\\ldsq\\Desktop\\图片\\gyy男神.png");
        try {
            String str= FaceUtil.detect(file);//获取face_token
            float buty=FaceUtil.a;
            System.out.println(buty);
            boolean res = FaceUtil.search(str);
            //添加人脸到集合：
            boolean addFaceres=FaceUtil.addFace(str);
            JSONObject obj1=new JSONObject();
            obj1.put("succ",res);
            //   String msg="{\"success\":"+res+"}";

            // JSONObject obj = JSONObject.parseObject(msg);

            obj1.put("buty",buty);

            System.out.println(obj1);


            System.out.println("人脸搜索的结果："+res);//在linaset里面搜索，失败是因为没有人脸的集合

            res=FaceUtil.getDetail();
            System.out.println("集合有无："+res);
            if(!res)
            {
                res=FaceUtil.createFaceSet();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

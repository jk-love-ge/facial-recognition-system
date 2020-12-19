package com.web;

import com.alibaba.fastjson.JSONObject;
import com.utils.FaceUtil;
import com.utils.ImageUtils;

import javax.jws.WebService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/faceController")
public class Servlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("进来了！");
        //登录
        /*
        上传图片到指定的文件夹（中介操作，
        判断是否有人脸信息
        返回结果
         */
        File file=ImageUtils.uploadImg(req,"imgData","upimg");//保存图片操作

        boolean res=false;
        boolean delflag=true;
        try {
            String facetoken=FaceUtil.detect(file);
            float beauty=FaceUtil.detect2(file);
           // float beauty=FaceUtil.a;//FaceUtil一旦被调用就会初始化这个值
            if(facetoken!=null)//成功获取图片后
            {

                String type=req.getParameter("type");
                if("register".equals(type))//如果是注册
                {
                    if(res)
                    {
                      res=false;
                    }
                    else{
                        res=FaceUtil.addFace(facetoken);//加入集合，代表注册成功
                        delflag=false;//不删除图片
                    }
                }

                res=FaceUtil.search(facetoken);//这是登录，搜索到了说明登录成功，后续也为登录操作
                PrintWriter writer2 = resp.getWriter();
                //writer2.write("你的颜值为："+beauty);

            }
            else
            {
                System.out.println("没有找到！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if(delflag)
            {
                file.delete();//删除登录的临时照片，清理内存
            }
//返回数据给客户端
            PrintWriter writer1 = resp.getWriter();
            float a1= FaceUtil.a;
            String msg="{\"success\":"+res+"}";

            writer1.write(msg);

            writer1.close();
        }

    }
}

package com.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.util.HashMap;

/*
https  http+验证
 */

public class FaceUtil {
	
	
	/**
	 * 根据传入图片进行人脸检测
	 * @param file 传入的人脸照片
	 * @return 返回人脸照片的facetoken,如果为空说明图片不符合要求
	 * @throws Exception
	 */
	public static float a=0;
	public static String detect(File file) throws Exception {
		byte[] buff = HTTPUtil.getBytesFromFile(file);
		String url = "https://api-cn.faceplusplus.com/facepp/v3/detect";
		HashMap<String, byte[]> byteMap = new HashMap<>();
		byteMap.put("image_file", buff);
		HTTPUtil.map.put("return_attributes","beauty");//单独把颜值请求放进去

		byte[] bacd = HTTPUtil.post(url, HTTPUtil.map, byteMap);
		String str = new String(bacd);

		System.out.println(str);

		JSONObject obj = JSONObject.parseObject(str);
		int faceNum=obj.getIntValue("face_num");
		System.out.println(faceNum);
		if(faceNum>=1) {
			//获取facetoken
			JSONArray jsonArray= (JSONArray) obj.get("faces");
			JSONObject face= (JSONObject) jsonArray.get(0);
			String faceToken=face.getString("face_token");

			JSONObject attributes= (JSONObject) face.get("attributes");//获取attributes对象
			String jsonStr = JSONObject.toJSONString(attributes);
			//System.out.println(jsonStr);

			JSONObject butyf= (JSONObject) attributes.get("beauty");
			String jsonStr2 = JSONObject.toJSONString(butyf);
			//System.out.println(jsonStr2);

			float male_score=butyf.getFloatValue("male_score");
			System.out.println(male_score);
			a=male_score;//静态变量保存

			//System.out.println(male_score1);

			return faceToken;
		}
		return null;
	}
	//返回颜值
	public static float detect2(File file) throws Exception {
		byte[] buff = HTTPUtil.getBytesFromFile(file);
		String url = "https://api-cn.faceplusplus.com/facepp/v3/detect";
		HashMap<String, byte[]> byteMap = new HashMap<>();
		byteMap.put("image_file", buff);
		HTTPUtil.map.put("return_attributes","beauty");//单独把颜值请求放进去

		byte[] bacd = HTTPUtil.post(url, HTTPUtil.map, byteMap);
		String str = new String(bacd);//获得“键值对”模式字符串

		JSONObject obj = JSONObject.parseObject(str);
		int faceNum=obj.getIntValue("face_num");
		System.out.println(faceNum);//人脸数量
		if(faceNum>=1) {
			//获取facetoken
			JSONArray jsonArray= (JSONArray) obj.get("faces");
			JSONObject face= (JSONObject) jsonArray.get(0);
			JSONObject attributes= (JSONObject) face.get("attributes");//获取attributes对象
			JSONObject buty=(JSONObject) face.get("beauty");//获取beauty对象

			if(buty!=null) {
				float male_score=buty.getFloatValue("male_score");
				return  male_score;
			}

		}

		return 0;
	}
	/**
	 * 查询指定的照片是否在人脸集合faceset中存在
	 * @param faceToken
	 * @return
	 * @throws Exception
	 */
	public static boolean search(String faceToken) throws Exception {
		String url = "https://api-cn.faceplusplus.com/facepp/v3/search";
		HTTPUtil.map.put("face_token", faceToken);
		byte[] bacd = HTTPUtil.post(url, HTTPUtil.map, null);//将参数传过去，获取结果

		String str = new String(bacd);
		if(str.indexOf("error_message")==-1) {//请求没有错误
			JSONObject json = JSONObject.parseObject(str);//转为json对象

			JSONObject thresholds=(JSONObject) json.get("thresholds");
			Double le5=thresholds.getDouble("1e-5");//十万分之一的阈值
			JSONArray results=(JSONArray) json.get("results");
			if(results!=null && results.size()>=1) {
				Double confidence=((JSONObject)results.get(0)).getDouble("confidence");
				if(confidence>le5) {
					return true;
				}
			}
		}
		return false;
	}	

	/**
	 * 添加人脸到faceset中
	 * @param face_tokens 要添加的人脸
	 * @return 
	 * @throws Exception
	 */
	public static boolean addFace(String face_tokens) throws Exception {
		if(!getDetail()) {//先获取人脸集合，没有集合就创建一个
			System.out.println("没有获取到指定人脸集合");
			boolean res=createFaceSet();
			if(!res) {
				System.out.println("创建人脸集合出问题了!");
				return false;
			}
			System.out.println("创建人脸集合成功！");
		}
		String url = "https://api-cn.faceplusplus.com/facepp/v3/faceset/addface";//放到人脸集合中
		HTTPUtil.map.put("face_tokens", face_tokens);
		byte[] bacd = HTTPUtil.post(url, HTTPUtil.map, null);
		String str = new String(bacd);
		if(str.indexOf("error_message")!=-1) {
			return false;
		}
		return true;
	}	
	
	
	/**
	 * 创建一个人脸的集合 FaceSet，用于存储人脸标识 face_token。
	 * @return
	 * @throws Exception
	 */
	public  static boolean createFaceSet() throws Exception {
		String url = "https://api-cn.faceplusplus.com/facepp/v3/faceset/create";
		byte[] bacd = HTTPUtil.post(url, HTTPUtil.map, null);
		String str = new String(bacd);
		System.out.println(str);
		if(str.indexOf("error_message")!=-1) {
			return false;
		}
		System.out.println("创建人脸集合："+str);
		return true;
	}
	
	/**
	 * 获取一个faceset
	 * @return
	 * @throws Exception
	 */
	public  static boolean getDetail() throws Exception {
		String url = "https://api-cn.faceplusplus.com/facepp/v3/faceset/getdetail";
		byte[] bacd = HTTPUtil.post(url, HTTPUtil.map, null);
		String str = new String(bacd);
		if(str.indexOf("error_message")!=-1) {
			return false;
		}
		return true;
	}

}

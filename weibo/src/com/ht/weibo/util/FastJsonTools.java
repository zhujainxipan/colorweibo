package com.ht.weibo.util;

/*
 * ʱ�䣺150417
 * ���ܣ�����fastjson�ͻ��˰ѷ������˴�������json�ַ���ת�ɶ�Ӧ�Ķ�����߼��ϵȡ�
 */
import java.util.List;
import java.util.Map;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
public class FastJsonTools {
	
	//Json�ַ�������>����һ��JavaBean
	public static <T> T getObject(String jsonStr,Class<T> cls)
	{
		T t = null;
		t = JSON.parseObject(jsonStr,cls);
		return t;
	}
	
	//Json�ַ�������>���Ͷ���List<JavaBean>
	public static <T> List<T> getLists(String jsonStr,Class<T> cls)
	{
		List<T> list = null;
		list = JSON.parseArray(jsonStr, cls);
		return list;
	}
	
	//Json�ַ�������>������:List<String>
	public static  List<String> getStrings(String jsonStr)
	{
		List<String> list =null;
		//list = JSON.parseArray(jsonStr,String.class);//������������
		list = JSON.parseObject(jsonStr,new TypeReference<List<String>>(){}); //Ҳ����������ʹ�ã������������ַ���
		return list;
	}
	
	//Json�ַ�������>������:List<Map<String,Object>>
	public static List<Map<String,Object>> getListMap(String jsonStr)
	{
		List<Map<String,Object>> list = null;
		list = JSON.parseObject(jsonStr,new TypeReference<List<Map<String,Object>>>(){});//ע����parseObject����parseArray���˼�api������ô�����
		return list;
	}
}

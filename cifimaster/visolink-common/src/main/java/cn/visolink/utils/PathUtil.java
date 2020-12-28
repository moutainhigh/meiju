package cn.visolink.utils;

import java.util.Properties;

public class PathUtil {
	/**
	 * 得到应用的ClasPath，如果是windows，则从盘符开始，如果是非windows，则是从/开始
	 * @return 应用的ClasPath
	 */
	public static String getAppClassPath(){
		String appClassPath = PathUtil.class.getResource("/").toString();
		String os = getOs();
		if(os.startsWith("win") || os.startsWith("Win")){
			appClassPath = appClassPath.substring(6).replaceAll("%20", " ");
		}else{
			appClassPath = appClassPath.substring(5).replaceAll("%20", " ");
		}
		return appClassPath;
	}
	
	/**
	 * 得到class文件的URI目录（不包括自己）
	 * @param clazz 类文件
	 * @return 类文件的URI目录
	 */
	public static String getClassFilePath(Class<?> clazz){
		String classFilePath = clazz.getResource("").getPath().replaceAll("%20", " ");;
		String os = getOs();
		if(os.startsWith("win") || os.startsWith("Win")){
			classFilePath = classFilePath.substring(1);
		}
		return classFilePath;
	}
	
	private static String getOs(){
		Properties prop = System.getProperties();
		String os = prop.getProperty("os.name");
		return os;
	}
	
}

package _util;

import java.util.HashMap;

public class ConfigUtil {
	private static HashMap<String, String> config = null;

	public static void loadConfig() {
		String path = System.getProperty("user.dir");
//		String path = "/home/djyu/yulabgroup/TargetLBS/java/bin";
		
		String fileConfig = path
			+ System.getProperty("file.separator")
			+ "Config.properties";
		
		config = PropUtil.loadPropertiesInHashMap(fileConfig);
	}

	public static String getConfig(String key) {
		if (config == null)
			loadConfig();
		return config.get(key);
	}

	public static HashMap<String, String> getConfigMap() {
		if (config == null)
			loadConfig();
		return config;
	}
	
	public static void main(String[] args){
		System.out.println(ConfigUtil.getConfig("EMAIL_PSWD"));
		System.out.println(ConfigUtil.getConfig("LOG_FOLDER_DIR"));
	}
}
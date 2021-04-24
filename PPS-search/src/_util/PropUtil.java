package _util;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

public class PropUtil {
	public static Properties loadProperties(String fileName) {
		Properties prop = new Properties();
		try { 
			FileInputStream in = new FileInputStream(fileName);
			prop.load(in);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			prop = null;
		}
		return prop;
	}

	public static HashMap<String, String> loadPropertiesInHashMap(
			String fileName) {
		Properties prop = loadProperties(fileName);
		HashMap<String, String> propInHashMap = new HashMap<String, String>();
		if (prop != null) {
			Set<Object> keys = prop.keySet();
			for (Object key : keys) {
				propInHashMap.put((String) key, (String) prop.get(key));
			}
		}
		return propInHashMap;
	}
}
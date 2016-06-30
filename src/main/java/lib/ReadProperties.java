package lib;


//import java.io.File;
//import java.io.FileInputStream;
import java.io.FileNotFoundException;
//import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ReadProperties {
	private static Properties defaultProp = new Properties();	
	private final static String DEFAULTPROPERTIESFILENAME = "config.properties";

	
	public static void readAllProperties() {
		try {
			InputStream inStream = ReadProperties.class.getClassLoader().getResourceAsStream(DEFAULTPROPERTIESFILENAME);	
			if (inStream != null){
				defaultProp.load(inStream);				
			} else {			
				throw new FileNotFoundException("Property file '" + DEFAULTPROPERTIESFILENAME + "' not found in the class path");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	/**Method to return value of the specified key from default properties file
	 * 
	 * @param key
	 * @return (String)keyValue
	 */
	public static String getEnvVariableValue(String key) {
		if (defaultProp.containsKey(key))
			return defaultProp.getProperty(key);
		else
			return null;
	}
	
	
}

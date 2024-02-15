package devidin.net.yavumeter.configuration;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

public abstract class Configuration {
	private static Logger logger = null;

	@SuppressWarnings("unchecked")
	public static Configuration loadConfiguration(@SuppressWarnings("rawtypes") Class configurationClass, String configFileName) throws Exception {
		if (logger == null)
			logger = LoggerFactory.getLogger(Configuration.class);

		Yaml yaml = new Yaml();
		Configuration newConfiguration = null;
		try (InputStream in = ClassLoader.getSystemResourceAsStream(configFileName)) {
			newConfiguration = (Configuration) yaml.loadAs(in, configurationClass);
		} catch (Exception ex) {

			ex.getCause().printStackTrace();
			ex.printStackTrace();

			logger.error(ex.toString());
			throw ex;
		}
		return newConfiguration;
	}

	public static File readFileFromClassPath(String fileName) throws Exception {
		File firstFoundFile = null;
		try {
			// Get the class loader
			ClassLoader classLoader = Configuration.class.getClassLoader();
			URL resourceUrl = null;
			// Use getResources to obtain all URLs for the given resource name
			Enumeration<URL> resources = classLoader.getResources(fileName);
			int instances=0;
			while (resources.hasMoreElements()) {
				resourceUrl = resources.nextElement();
				instances++;
				logger.info(instances + ": File found in classpath: " + resourceUrl.getFile());
				if (firstFoundFile==null) firstFoundFile = new File(resourceUrl.getFile());
			}

			if (instances>1) 
				logger.error("File found "+instances+" times in classpath. First found used : "+firstFoundFile.getCanonicalPath() );
			else if (instances<1)
				logger.error("File not found in classpath." );
		} catch (Exception e) {
			logger.error("Error while looking for file in classpath: " + fileName);
			throw e;
		}
		return firstFoundFile;
	}
}

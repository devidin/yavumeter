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

	public static Configuration loadConfiguration(Class configurationClass, String configFileName) throws Exception {
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
		try {
			// Get the class loader
			ClassLoader classLoader = Configuration.class.getClassLoader();
			URL resourceUrl = null;

			// Use getResources to obtain all URLs for the given resource name
			Enumeration<URL> resources = classLoader.getResources(fileName);
			while (resources.hasMoreElements()) {
				resourceUrl = resources.nextElement();
				logger.info("File found in classpath: " + resourceUrl.getFile());
				return new File(resourceUrl.getFile());
				// If you need to convert the URL to a File object (works if the resource is on
				// the file system)
				// File file = new File(resourceUrl.getFile());
			}

			if (!resources.hasMoreElements()) {
				logger.error("File found in classpath: " + resourceUrl.getFile());
			}
		} catch (Exception e) {
			logger.error("Error while looking for file found in classpath: " + fileName);
			throw e;
		}
		return null;
	}
}

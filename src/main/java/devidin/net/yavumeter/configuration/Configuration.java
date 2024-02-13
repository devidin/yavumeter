package devidin.net.yavumeter.configuration;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

public abstract class Configuration {
	private static Logger logger = null;
	public abstract Configuration loadConfiguration();
	public static Configuration loadConfiguration(Class configurationClass, String configFileName) throws Exception {
		if (logger == null)
			logger = LoggerFactory.getLogger(Configuration.class);

		Yaml yaml = new Yaml();
		Configuration newConfiguration = null;
		try (InputStream in = ClassLoader.getSystemResourceAsStream(configFileName)) {
			newConfiguration = (Configuration) yaml.loadAs(in, configurationClass);
		} catch (Exception ex) {
			logger.error(ex.toString());
			throw ex;
		}
		return newConfiguration;
	}
}

package org.doubrava.ergologger.bl;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class ApplicationProperties {

    private static String FILENAME = ".ergologger.properties";
    private static String KEY_EXPORT_DIRECTORY = "export.directory";

    private volatile static ApplicationProperties uniqueApplicationProperties;

    private Properties properties;

    private ApplicationProperties() {
        this.properties = new Properties();

        File propsFile;
        propsFile = new File(ApplicationProperties.getFileLocation());
        if (propsFile.exists()) {
            // Load properties
            try (InputStream input = new FileInputStream(ApplicationProperties.getFileLocation())) {
                this.properties.load(input);

            } catch (IOException ex) {
                ex.printStackTrace();
            }

        } else {
            // Create default properties
            try (OutputStream output = new FileOutputStream(ApplicationProperties.getFileLocation())) {
                this.properties.setProperty(ApplicationProperties.getPropertyKey(ApplicationProperty.EXPORT_DIRECTORY), System.getProperty("user.home"));
                this.properties.store(output, null);

            } catch (IOException io) {
                io.printStackTrace();
            }
        }

    }

    public static synchronized ApplicationProperties getInstance() {
        if (uniqueApplicationProperties == null) {
            synchronized (ApplicationProperties.class) {
                if (uniqueApplicationProperties == null) {
                    uniqueApplicationProperties = new ApplicationProperties();
                }
            }
        }
        return uniqueApplicationProperties;
    }

    public static String getFileLocation() {
        String path = System.getProperty("user.home");
        if (!path.endsWith(File.separator)) {
            path = path + File.separator;
        }
        return path + ApplicationProperties.FILENAME;
    }

    public boolean saveProperties() {
        try (OutputStream output = new FileOutputStream(ApplicationProperties.getFileLocation())) {

            this.properties.store(output, null);

        } catch (IOException io) {
            io.printStackTrace();
            return false;
        }
        return true;
    }

    private static String getPropertyKey(ApplicationProperty p) {
        switch (p) {
            case EXPORT_DIRECTORY:
                return "export.directory";
            default:
                return "unknown.property.key";
        }
    }

    public String getProperty(ApplicationProperty key) {
        return this.properties.getProperty(ApplicationProperties.getPropertyKey(key));
    }

    public void setProperty(ApplicationProperty key, String value) {
        this.properties.setProperty(ApplicationProperties.getPropertyKey(key), value);
    }

}

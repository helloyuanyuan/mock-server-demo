package com.solera.global.qa.mockservertest.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class PropertyUtils {

  static PropertyUtils instance;
  Properties properties;

  public static PropertyUtils getInstance() {
    if (null == instance) {
      instance = new PropertyUtils();
    }
    return instance;
  }

  public PropertyUtils() {
    String propertiesFile = loadProperties("environment/env.properties").getProperty("env");
    propertiesFile = "environment/" + propertiesFile + ".properties";
    this.properties = loadProperties(propertiesFile);
  }

  synchronized public static Properties loadProperties(String propertiesFile) {
    Properties properties = new Properties();
    InputStream in = null;
    try {
      in = PropertyUtils.class.getClassLoader().getResourceAsStream(propertiesFile);
      properties.load(new InputStreamReader(in, "UTF-8"));
    } catch (FileNotFoundException e) {
      LogUtils.error(propertiesFile + "FILE NOT FOUND!");
    } catch (IOException e) {
      LogUtils.error(propertiesFile + "LOADING IOException!");
    } finally {
      try {
        if (null != in) {
          in.close();
        }
      } catch (IOException e) {
        LogUtils.error(propertiesFile + "CLOSING IOException!");
      }
    }
    return properties;
  }

  public String getProperty(String key) {
    return properties.getProperty(key);
  }

}


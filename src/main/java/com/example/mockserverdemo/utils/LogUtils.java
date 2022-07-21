package com.example.mockserverdemo.utils;

import java.io.File;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

public class LogUtils {

    public static Logger logger = LogManager.getLogger(LogUtils.class.getName());

    public static void logInitialize() {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        File file = new File("src//main//resources//log4j2.xml");
        context.setConfigLocation(file.toURI());
    }

    public static void startTestSuite(String testSuiteName) {
        logInitialize();
        logger.info("---------------------- TEST SUITE START ----------------------");
        logger.info(" TEST SUITE : {}", testSuiteName);
        logger.info("--------------------------------------------------------------");
    }

    public static void endTestSuite() {
        logger.info("----------------------- TEST SUITE END -----------------------\n");
    }

    public static void excuteTestCase(String testName) {
        logger.info(" TEST CASE : {}", testName);
    }

    public static void info(String message) {
        logger.info("> INFO : {}", message);
    }

    public static void warn(String message) {
        logger.warn("> WARN : {}", message);
    }

    public static void error(String message) {
        logger.error("> ERROR : {}", message);
    }

    public static void fatal(String message) {
        logger.fatal("> FATAL : {}", message);
    }

    public static void debug(String message) {
        logger.debug("> DEBUG : {}", message);
    }

    public static void expected(String message) {
        logger.info("> EXPECTED RESULT : {}", message);
    }

    public static void actual(String message) {
        logger.info("> ACTUAL RESULT : {}", message);
    }

}

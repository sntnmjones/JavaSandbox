package org.example;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger();
    public static void main(String[] args) {
        LOGGER.trace("It is a trace logger.");
        LOGGER.debug("It is a debug logger.");
        LOGGER.info("It is a info logger.");
        LOGGER.warn("It is a warn logger.");
        LOGGER.error("It is an error logger.");
        LOGGER.fatal("It is a fatal logger.");
        System.out.println("Hello world!");
    }
}
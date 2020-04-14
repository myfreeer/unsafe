package myfreeer.unsafe.utils.log.slf4j;

import myfreeer.unsafe.utils.log.Logger;

public class Slf4jLogger implements Logger {
    private final org.slf4j.Logger logger;

    public Slf4jLogger(org.slf4j.Logger logger) {
        this.logger = logger;
    }

    @Override
    public void info(String msg) {
        if (logger.isInfoEnabled()) {
            logger.info(msg);
        }
    }

    @Override
    public void info(String msg, Throwable e) {
        if (logger.isInfoEnabled()) {
            logger.info(msg, e);
        }
    }

    @Override
    public void warn(String msg) {
        logger.warn(msg);
    }

    @Override
    public void warn(String msg, Throwable e) {
        logger.warn(msg, e);
    }
}

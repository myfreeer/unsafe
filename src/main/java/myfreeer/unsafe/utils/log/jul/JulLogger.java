package myfreeer.unsafe.utils.log.jul;

import myfreeer.unsafe.utils.log.Logger;

import java.util.logging.Level;

public class JulLogger implements Logger {
    private final java.util.logging.Logger logger;

    public JulLogger(java.util.logging.Logger logger) {
        this.logger = logger;
    }

    @Override
    public void info(String msg) {
        logger.log(Level.INFO, msg);
    }

    @Override
    public void info(String msg, Throwable e) {
        logger.log(Level.INFO, msg, e);
    }

    @Override
    public void warn(String msg) {
        logger.log(Level.WARNING, msg);
    }

    @Override
    public void warn(String msg, Throwable e) {
        logger.log(Level.WARNING, msg, e);

    }
}

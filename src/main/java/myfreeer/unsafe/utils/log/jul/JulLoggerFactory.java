package myfreeer.unsafe.utils.log.jul;

import myfreeer.unsafe.utils.log.Logger;
import myfreeer.unsafe.utils.log.LoggerFactory;

public class JulLoggerFactory implements LoggerFactory {
    @Override
    public Logger getLogger(Class<?> type) {
        return getLogger(type.getName());
    }

    @Override
    public Logger getLogger(String name) {
        return new JulLogger(java.util.logging.Logger.getLogger(name));
    }
}

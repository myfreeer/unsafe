package myfreeer.unsafe.utils.log.nop;

import myfreeer.unsafe.utils.log.Logger;
import myfreeer.unsafe.utils.log.LoggerFactory;

public class NopLoggerFactory implements LoggerFactory {
    private final NopLogger logger = new NopLogger();

    @Override
    public Logger getLogger(Class<?> type) {
        return logger;
    }

    @Override
    public Logger getLogger(String name) {
        return logger;
    }
}

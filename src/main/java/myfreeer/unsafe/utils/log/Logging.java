package myfreeer.unsafe.utils.log;

import myfreeer.unsafe.utils.log.jul.JulLoggerFactory;
import myfreeer.unsafe.utils.log.nop.NopLoggerFactory;
import myfreeer.unsafe.utils.log.slf4j.Slf4jLoggerFactory;

public class Logging {
    private static final LoggerFactory FACTORY;

    static {
        if (hasClass("org.slf4j.Logger")) {
            FACTORY = new Slf4jLoggerFactory();
        } else if (hasClass("java.util.logging.Logger")) {
            FACTORY = new JulLoggerFactory();
        } else {
            FACTORY = new NopLoggerFactory();
        }
    }

    private static boolean hasClass(String name) {
        try {
            Class.forName(name);
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    public static Logger getLogger(Class<?> type) {
        return FACTORY.getLogger(type);
    }
}

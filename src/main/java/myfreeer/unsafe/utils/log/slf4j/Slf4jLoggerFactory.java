package myfreeer.unsafe.utils.log.slf4j;

import myfreeer.unsafe.utils.log.Logger;
import myfreeer.unsafe.utils.log.LoggerFactory;

public class Slf4jLoggerFactory implements LoggerFactory {
    @Override
    public Logger getLogger(Class<?> type) {
        return new Slf4jLogger(org.slf4j.LoggerFactory.getLogger(type));
    }

    @Override
    public Logger getLogger(String name) {
        return new Slf4jLogger(org.slf4j.LoggerFactory.getLogger(name));
    }
}

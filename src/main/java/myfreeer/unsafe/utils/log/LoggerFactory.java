package myfreeer.unsafe.utils.log;

public interface LoggerFactory {
    Logger getLogger(Class<?> type);

    Logger getLogger(String name);
}

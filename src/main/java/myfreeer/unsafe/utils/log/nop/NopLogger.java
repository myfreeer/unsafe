package myfreeer.unsafe.utils.log.nop;

import myfreeer.unsafe.utils.log.Logger;

public class NopLogger implements Logger {
    @Override
    public void info(String msg) {

    }

    @Override
    public void info(String msg, Throwable e) {

    }

    @Override
    public void warn(String msg) {

    }

    @Override
    public void warn(String msg, Throwable e) {

    }
}

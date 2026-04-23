package core;

public class TerminalLogger implements ILogger {
    @Override
    public void log(String msg) { log(msg, Constants.DEFAULT); }

    @Override
    public void log(String msg, String color) {
        System.out.print(color + msg + Constants.RESET);
    }

    @Override
    public void log(byte b) {
        log(b, Constants.DEFAULT);
    }

    @Override
    public void log(byte b, String color) {
        System.out.printf("%s0x%02X ", color, b);
    }
}

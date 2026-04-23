package core;

public interface ILogger {

    void log(String msg);
    void log(String msg, String color);
    void log(byte b);
    void log(byte b, String color);
}

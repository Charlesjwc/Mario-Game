package util;

public class Time {
    //  Starting time
    public static float timeStarted = System.nanoTime();

    //  Get time between start time and current time
    public static float getTime() {
        return (float)((System.nanoTime() - timeStarted) * 1E-9);
    }
}

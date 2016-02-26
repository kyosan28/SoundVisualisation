package com.company;

/**
 * Created by okyo on 09.02.16.
 */

public class TimeElapsed {
    enum Format {
        WITHMS, WITHOUTMS, WITHOUTDAYS, WITHWORDS
    };

    @SuppressWarnings("unused")
    protected long msecond;
    public long ms;
    public int seconds;
    public int minutes;
    public int hours;
    public int days;

    public TimeElapsed(long millisecond) {
        msecond = millisecond;
        ms = millisecond;
        shift();
    }

    private void shift() {

        if (ms >= 1000) {
            seconds += ms / 1000;
            ms %= 1000;
        }

        if (seconds >= 60) {
            minutes += seconds / 60;
            seconds %= 60;
        }

        if (minutes >= 60) {
            hours += minutes / 60;
            minutes %= 60;
        }

        if (hours >= 24) {
            days += hours / 24;
            hours %= 24;
        }

    }

    public String toString(Format f) {

        String res = "";
        switch (f) {
            case WITHMS:
                if (days > 0) {
                    res += "" + days;
                    res += String.format(":%02d", hours);
                    res += String.format(":%02d", minutes);
                    res += String.format(":%02d", seconds);
                    res += String.format(":%03d", ms);
                } else if (hours > 0) {
                    res += "" + hours;
                    res += String.format(":%02d", minutes);
                    res += String.format(":%02d", seconds);
                    res += String.format(":%03d", ms);
                } else if (minutes > 0) {
                    res += "" + minutes;
                    res += String.format(":%02d", seconds);
                    res += String.format(":%04d", ms);

                } else if (seconds > 0) {
                    res += "" + seconds;
                    res += String.format(":%03d", ms);
                } else {
                    res = "" + ms;
                }
                return res;
            case WITHOUTMS:
                if (days > 0) {
                    res += "" + days;
                    res += String.format(":%02d", hours);
                    res += String.format(":%02d", minutes);
                    res += String.format(":%02d", seconds);
                } else if (hours > 0) {
                    res += "" + hours;
                    res += String.format(":%02d", minutes);
                    res += String.format(":%02d", seconds);
                } else if (minutes > 0) {
                    res += "" + minutes;
                    res += String.format(":%02d", seconds);

                } else if (seconds > 0) {
                    res = String.format("%02d", seconds);
                }
                return res;
            case WITHOUTDAYS:
                if (days > 0) {

                    res += hours + 24 * days;
                    res += String.format(":%02d", minutes);
                    res += String.format(":%02d", seconds);
                    res += String.format(":%03d", ms);
                } else if (hours > 0) {
                    res += "" + hours;
                    res += String.format(":%02d", minutes);
                    res += String.format(":%02d", seconds);
                    res += String.format(":%03d", ms);
                } else if (minutes > 0) {
                    res += "" + minutes;
                    res += String.format(":%02d", seconds);
                    res += String.format(":%03d", ms);

                } else if (seconds > 0) {
                    res += "" + seconds;
                    res += String.format(":%03d", ms);
                } else {
                    res = String.format("%03d", ms);
                }
                return res;
            case WITHWORDS:
                if (days > 0)
                    res += days + "d ";
                if (hours > 0)
                    res += hours + "h ";
                if (minutes > 0)
                    res += minutes + "min ";
                if (seconds > 0)
                    res += seconds + "sec ";
                if (ms > 0)
                    res += ms + "msec";
                return res;

            default:
                return "unsupproted format";
        }

    }

}

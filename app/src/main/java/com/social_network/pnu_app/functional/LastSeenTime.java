package com.social_network.pnu_app.functional;

import android.content.Context;
import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LastSeenTime {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;


    public String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        if (diff < MINUTE_MILLIS) {
            return "був у мережі прямо зараз";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "був у мережі хвилину тому";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return "був у мережі " + diff / MINUTE_MILLIS + " хвилин тому";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "був у мережі годину назад";
        } else if (diff < 24 * HOUR_MILLIS) {
            return "був у мережі " + diff / HOUR_MILLIS + " годин тому";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "був у мережі вчора о " + df.format(time);
        } else {
            return "був у мережі " + diff / DAY_MILLIS + " днів тому";
        }
    }

    public String getTimeMessenger(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        SimpleDateFormat dd = new SimpleDateFormat("dd/MM");
        SimpleDateFormat dy = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calendarMessage = Calendar.getInstance();
        Calendar calendarCurrent = Calendar.getInstance();
        calendarMessage.setTimeInMillis(time);
        calendarCurrent.setTimeInMillis(now);

        int yearMesage = calendarMessage.get(Calendar.YEAR);
        int currentYear = calendarCurrent.get(Calendar.YEAR);

        int currentMonth = calendarCurrent.get(Calendar.MONTH);
        int monthMessage = calendarMessage.get(Calendar.MONTH);

        int dayMessage = calendarMessage.get(Calendar.DATE);
        int currentDay = calendarCurrent.get(Calendar.DATE);


        int currentDayOFWeek = calendarMessage.get(Calendar.DAY_OF_WEEK);
        String dayOFWeek ="";

        if (dayMessage == currentDay && monthMessage == currentMonth && currentYear == yearMesage) {
            return df.format(time);
        } else if ((currentDay - dayMessage) == 1 && monthMessage == currentMonth && currentYear == yearMesage ) {
            return "вчора о " + df.format(time);
        }
        else if ((currentDay - dayMessage) < 7 && (currentDay - dayMessage) > 1
        && monthMessage == currentMonth && currentYear == yearMesage) {
            if (currentDayOFWeek == 2) {
                dayOFWeek = "Пн " + df.format(time);
            } else if (currentDayOFWeek == 3) {
                dayOFWeek =  "Вт " + df.format(time);
            } else if (currentDayOFWeek == 4) {
                dayOFWeek =  "Ср " + df.format(time);
            } else if (currentDayOFWeek == 5) {
                dayOFWeek =  "Чт " + df.format(time);
            } else if (currentDayOFWeek == 6) {
                dayOFWeek =  "Пт " + df.format(time);
            } else if (currentDayOFWeek == 7) {
                dayOFWeek =  "Сб " + df.format(time);
            } else if (currentDayOFWeek == 1) {
                dayOFWeek =  "Нд " + df.format(time);
            }
            return dayOFWeek;
        } else if (yearMesage == currentYear) {
            return dd.format(time);
        } else {
            return dy.format(time);
        }
    }

    public String getTimePost(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
      /*  if (time > now || time <= 0) {
            return null;
        }*/

        // TODO: localize
        final long diff = now - time;
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        SimpleDateFormat dd = new SimpleDateFormat("dd/MM");
        SimpleDateFormat dy = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calendarMessage = Calendar.getInstance();
        Calendar calendarCurrent = Calendar.getInstance();
        calendarMessage.setTimeInMillis(time);
        calendarCurrent.setTimeInMillis(now);

        int yearMesage = calendarMessage.get(Calendar.YEAR);
        int currentYear = calendarCurrent.get(Calendar.YEAR);

        int currentMonth = calendarCurrent.get(Calendar.MONTH);
        int monthMessage = calendarMessage.get(Calendar.MONTH);

        int dayMessage = calendarMessage.get(Calendar.DATE);
        int currentDay = calendarCurrent.get(Calendar.DATE);


        int currentDayOFWeek = calendarMessage.get(Calendar.DAY_OF_WEEK);
        String dayOFWeek ="";

        if (dayMessage == currentDay && monthMessage == currentMonth && currentYear == yearMesage) {
            return "сьогодні о " + df.format(time);
        } else if ((currentDay - dayMessage) == 1 && monthMessage == currentMonth && currentYear == yearMesage ) {
            return "вчора о " + df.format(time);
        }
        else if ((currentDay - dayMessage) < 7 && (currentDay - dayMessage) > 1
                && monthMessage == currentMonth && currentYear == yearMesage) {
            if (currentDayOFWeek == 2) {
                dayOFWeek = "Пн " + df.format(time);
            } else if (currentDayOFWeek == 3) {
                dayOFWeek =  "Вт " + df.format(time);
            } else if (currentDayOFWeek == 4) {
                dayOFWeek =  "Ср " + df.format(time);
            } else if (currentDayOFWeek == 5) {
                dayOFWeek =  "Чт " + df.format(time);
            } else if (currentDayOFWeek == 6) {
                dayOFWeek =  "Пт " + df.format(time);
            } else if (currentDayOFWeek == 7) {
                dayOFWeek =  "Сб " + df.format(time);
            } else if (currentDayOFWeek == 1) {
                dayOFWeek =  "Нд " + df.format(time);
            }
            return dayOFWeek;
        } else if (yearMesage == currentYear) {
            return dd.format(time) + " " + df.format(time);
        } else {
            return dy.format(time) + " " + df.format(time);
        }
    }
}

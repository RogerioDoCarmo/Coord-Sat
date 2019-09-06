/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.NTRIP;
/**
 *
 * @author Fernanda
 */
public class Tempo {

   static final int months[] = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    //-----------------------------------------funçoes tempo-------------------------------------------------------------
 public static void updatetime(Auxiliar auxi, int mSecOfWeek, int fixnumleap) {
        int y, m, d, k, l, nul;

        int j = (int)auxi.week * (7 * 24 * 60 * 60) + (int)auxi.secOfWeek + 5 * 24 * 60 * 60 + 3 * 60 * 60;
        int glo_daynumber = 0, glo_timeofday;

        for (y = 1980; j >= (int) ((k = (l = (365 + longyear(y, 0))) * 24 * 60 * 60))
                + gnumleap(y + 1, 1, 1); ++y) {
            j -= k;
            glo_daynumber += l;
        }
        for (m = 1; j >= (int) (k = (l = months[m] + longyear(y, m)) * 24 * 60 * 60)
                + gnumleap(y, m + 1, 1); ++m) {
            j -= k;
            glo_daynumber += l;
        }
        for (d = 1; j >= 24l * 60l * 60l + gnumleap(y, m, d + 1); ++d) {
            j -= 24 * 60 * 60;
        }
        glo_daynumber -= 16 * 365 + 4 - d;
        nul = gnumleap(y, m, d);
        glo_timeofday = j - nul;

        if (mSecOfWeek < 5 * 60 * 1000 && glo_timeofday > 23 * 60 * 60) {
            auxi.secOfWeek += 24 * 60 * 60;
        } else if (glo_timeofday < 5 * 60 && mSecOfWeek > 23 * 60 * 60 * 1000) {
            auxi.secOfWeek -= 24 * 60 * 60;
        }
        auxi.secOfWeek += mSecOfWeek / 1000 - glo_timeofday;
        if (fixnumleap != 0) {
            auxi.secOfWeek -= nul;
        }
        if (auxi.secOfWeek < 0) {
            auxi.secOfWeek += 24 * 60 * 60 * 7;
            --auxi.week;
        }
        if (auxi.secOfWeek >= 24 * 60 * 60 * 7) {
            auxi.secOfWeek -= 24 * 60 * 60 * 7;
            ++auxi.week;
        }
        
    }


    static int gnumleap(int year, int month, int day) {
        int ls = 0;

        for (int i = 0; leap[i].taicount != 0 && year >= leap[i].year; ++i) {
            if (year > leap[i].year || month > leap[i].month || (month == leap[i].month && day > leap[i].day)) {
                ls = leap[i].taicount - Constantes.GPSLEAPSTART;
            }
        }
        return ls;
    }

    static int longyear(int year, int month) {
        if (!((year % 4) != 0) && (!((year % 400) != 0) || (year % 100) != 0)) {
            if (!(month != 0) || month == 2) {
                return 1;
            }
        }
        return 0;
    }

    static final leapseconds leap[] = {
        /*new LeapSeconds(31, 12, 1971, 11),*/
        /*new LeapSeconds(31, 12, 1972, 12),*/
        /*new LeapSeconds(31, 12, 1973, 13),*/
        /*new LeapSeconds(31, 12, 1974, 14),*/
        /*new LeapSeconds(31, 12, 1975, 15),*/
        /*new LeapSeconds(31, 12, 1976, 16),*/
        /*new LeapSeconds(31, 12, 1977, 17),*/
        /*new LeapSeconds(31, 12, 1978, 18),*/
        /*new LeapSeconds(31, 12, 1979, 19),*/
        new leapseconds(30, 06, 1981, 20),
        new leapseconds(30, 06, 1982, 21),
        new leapseconds(30, 06, 1983, 22),
        new leapseconds(30, 06, 1985, 23),
        new leapseconds(31, 12, 1987, 24),
        new leapseconds(31, 12, 1989, 25),
        new leapseconds(31, 12, 1990, 26),
        new leapseconds(30, 06, 1992, 27),
        new leapseconds(30, 06, 1993, 28),
        new leapseconds(30, 06, 1994, 29),
        new leapseconds(31, 12, 1995, 30),
        new leapseconds(30, 06, 1997, 31),
        new leapseconds(31, 12, 1998, 32),
        new leapseconds(31, 12, 2005, 33),
        new leapseconds(31, 12, 2008, 34),
        new leapseconds(0, 0, 0, 0) /* end marker */
    };

     static void convertTime(converttime cvt, int week, int tow) {
System.out.println("handle.Data.week!"+week);
                    System.out.println("handle.Data.timeofweek!"+tow);
        int i, k, doy, j; /* temporary variables */

        j = week * (7 * 24 * 60 * 60) + tow + 5 * 24 * 60 * 60;
        for (i = 1980; j >= (k = (365 + longyear(i, 0)) * 24 * 60 * 60); ++i) {
            j -= k;
        }
        System.out.println("handle.Data.week!"+week);
        cvt.year = i;System.out.println("handle.Data.week!"+week);
        doy = 1 + (j / (24 * 60 * 60));
        j %= (24 * 60 * 60);System.out.println("j!"+j);
        cvt.hour = j / (60 * 60);System.out.println("hour!"+cvt.hour);
        j %= (60 * 60);
        cvt.minute = j / 60;System.out.println("min!"+cvt.minute);
        cvt.second = j % 60;System.out.println("sec!"+cvt.second);
        j = 0;
        for (i = 1; j + (k = months[i] + longyear(cvt.year, i)) < doy; ++i) {
            j += k;
        }
        cvt.month = i;
        cvt.day = doy - j;
    }

//-------------------------------------fim funçoes tempo-----------------------------------------------------------------

}

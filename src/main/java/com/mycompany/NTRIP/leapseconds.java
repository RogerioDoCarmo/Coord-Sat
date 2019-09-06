/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.NTRIP;

/**
 *
 * @author Fernanda
 */
public class leapseconds {
    int day;        /* this is the day, where 23:59:59 exists 2 times */
    int month;      /* not the next day! */
    int year;
    int taicount;

    public leapseconds(int day, int month, int year, int taicount) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.taicount = taicount;
    }


}

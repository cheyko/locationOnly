package com.cheyko.locationonly;

import java.util.Date;

/**
 * Created by ariel on 4/5/18.
 */

public class Displacements {
    private Coordinates points;
    private int busNumber;
    private int timeThus;
    private Date currentTime;

    public Displacements() {

    }

    public Displacements( int busNumber,Date currentTime, Coordinates points, int timeThus) {
        this.busNumber = busNumber;
        this.points = points;
        this.timeThus = timeThus;
        this.currentTime = currentTime;

    }

    public int getBusNumber(){
        return this.busNumber;
    }

    public void setBusNumber(int newBusNumber){
        this.busNumber = newBusNumber;
    }

    public Date getCurrentTime(){
        return this.currentTime;
    }

    public void setCurrentTime(Date newTime){
        this.currentTime = newTime;
    }

    public int getTimeThus(){
        return this.timeThus;
    }

    public void setTimeThus(int newTime){
        this.timeThus = newTime;
    }

    public Coordinates getPoints(){
        return this.points;
    }

    public void setPoints(Coordinates newPoints){
        this.points = newPoints;
    }
}

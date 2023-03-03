package businesslogic.event;

import java.sql.Time;
public class Shift {
    protected int id;
    protected Time startTime;
    protected Time endTime;


    public int getId() {
        return id;
    }
    public Time getStartTime() {
        return startTime;
    }
    public Time getEndTime() {
        return endTime;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }
    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }
}

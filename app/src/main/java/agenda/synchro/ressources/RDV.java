package agenda.synchro.ressources;

import androidx.annotation.NonNull;
import com.owlike.genson.Genson;

import java.util.Date;

public class RDV {
    private int idRDV;
    private String name;
    private String dateString;

    private String timeString;
    private Date date;
    private Date time;
    private String location;

    public RDV() {
        this.setIdRDV(-1);
        this.setName("unknown");
        this.setDate("unknown");
        this.setTime("unknown");
        this.setLocation("unknown");
    }

    public RDV(String name, Date date, Date time, String location){
        this.setName(name);
        this.setDateDate(date);
        this.setTimeTime(time);
        this.setLocation(location);
    }
    public RDV(int idRDV, String name, String date, String time, String location){
        this.setIdRDV(idRDV);
        this.setName(name);
        this.setDate(date);
        this.setTime(time);
        this.setLocation(location);
    }

    public RDV(String name, String date, String time, String location){
        this.setName(name);
        this.setDate(date);
        this.setTime(time);
        this.setLocation(location);
    }

    public RDV(int idRDV, String name, String time){
        this.idRDV = idRDV;
        this.name = name;
        this.timeString = time;
    }

    public int getIdRDV() {return idRDV;}

    public void setIdRDV(int idRDV) {
        this.idRDV = idRDV;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return dateString;
    }

    public Date getTimeTime() {
        return time;
    }

    public void setTimeTime(Date time){
        this.time = time;
    }

    public Date getDateDate() {
        return date;
    }

    public void setDateDate(Date date){
        this.date = date;
    }

    public void setDate(String date) {
        this.dateString = date;
    }

    public String getTime() {
        return timeString;
    }

    public void setTime(String time) {
        this.timeString = timeString;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @NonNull
    @Override
    public String toString() {
        Genson genson = new Genson();
        return genson.serialize(this);
    }
}
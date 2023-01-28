package agenda.synchro;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class RDV {
    private int idRDV;
    private String name;
    private String date;
    private String time;
    private String location;

    public RDV(){
        this(0,"unknown","unknown","unknown","unknown");
    }

    public RDV(int idRDV, String name, String date, String time, String location){
        this.setIdRDV(idRDV);
        this.setName(name);
        this.setDate(date);
        this.setTime(time);
        this.setLocation(location);
    }

    public RDV(int idRDV, String name){
        this.idRDV = idRDV;
        this.name = name;
    }

    public int getIdRDV() {
        return idRDV;
    }

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
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("idRDV", idRDV);
            jsonObject.put("name", name);
            jsonObject.put("date", date);
            jsonObject.put("time", time);
            jsonObject.put("location", location);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
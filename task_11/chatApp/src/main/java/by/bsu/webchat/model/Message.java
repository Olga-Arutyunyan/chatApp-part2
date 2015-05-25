package by.bsu.webchat.model;
import java.text.*;
import java.util.*;
public class Message {
    private String id;
    private String user;
    private String text;
    private String date;
    private String deleted;


    public Message(String id, String user, String text, String date, String deleted) {
        this.id = id;
        this.user = user;
        this.text = text;
        if (!date.equals("1"))
            this.date = date;
        else
            this.date = getNewDate();
        this.deleted = deleted;

    }

    public String getNewDate(){
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy hh:mm");
        return format.format(date);
    }

    public String getDate(){
        return this.date;
    }
    public String getId() {
        return id;
    }

    public void setDeleted(){
        this.deleted = "true";
        this.text = "[deleted]";
    }
    public String isDelete(){
        if (this.deleted.equals(("true")))
            return "true";
        return "false";
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }

    public String toString() {
        return "{\"id\":\"" + this.id + "\",\"user\":\"" + this.user + "\",\"text\":\"" + this.text + "\",\"deleted\":\"" + this.deleted +  "\"}";
    }
}
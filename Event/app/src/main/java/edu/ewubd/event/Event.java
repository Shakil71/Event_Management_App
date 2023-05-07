package edu.ewubd.event;

import java.util.ArrayList;
import java.util.Arrays;

public class Event {
    private String key = "";
    private String name = "";
    private String place = "";
    private String type = "";
    private String dateAndTime = "";
    private String capacity = "";
    private String budget = "";
    private String email = "";
    private String phone = "";
    private String description = "";
    private final ArrayList<String> labels = new ArrayList<>(Arrays.asList("Key", "Name", "Place", "Type", "Date & Time", "Capacity", "Budget", "Email", "Phone", "Description"));

    public Event() {
    }

    public Event(String key, String name, String place, String type, String dateAndTime,
                 String capacity, String budget, String email, String phone, String description) {
        this.key = key;
        this.name = name;
        this.place = place;
        this.type = type;
        this.dateAndTime = dateAndTime;
        this.capacity = capacity;
        this.budget = budget;
        this.email = email;
        this.phone = phone;
        this.description = description;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getPlace() {
        return place;
    }

    public String getType() {
        return type;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public String getCapacity() {
        return capacity;
    }

    public String getBudget() {
        return budget;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getDescription() {
        return description;
    }

    private String checkString(String string) {
        if (string == null || string.isEmpty()) {
            string = "";
        }
        return string;
    }

    private boolean checkData() {
        if (!checkString(key).equals("") && !checkString(name).equals("") && !checkString(place).equals("") && !checkString(type).equals("")
                && !checkString(dateAndTime).equals("") && !checkString(capacity).equals("") && !checkString(budget).equals("")
                && !checkString(email).equals("") && !checkString(phone).equals("") && !checkString(description).equals("")) {
            return true;
        }
        return false;
    }

    public String getData() {
        if (checkData()) {
            return name + ", " + place + ", " + type + ", " + dateAndTime + ", " + capacity + ", " + budget + ", " + email + ", " + phone + ", " + description;
        }
        return "";
    }

    public String getDataAsString() {
        if (checkData()) {
            return labels.get(0) + ": " + key + "\n" + labels.get(1) + ": " + name + "\n" + labels.get(2) + ": " + place + "\n" + labels.get(3) + ": " + type + "\n" +
                    labels.get(4) + ": " + dateAndTime + "\n" + labels.get(5) + ": " + capacity + "\n" + labels.get(6) + ": " + budget + "\n" +
                    labels.get(7) + ": " + email + "\n" + labels.get(8) + ": " + phone + "\n" + labels.get(9) + ": " + description + "\n";
        }
        return "Invalid data";
    }

    public ArrayList<String> getDataAsArray() {
        ArrayList<String> eventData = new ArrayList<>();
        eventData.add(key);
        eventData.add(name);
        eventData.add(place);
        eventData.add(type);
        eventData.add(dateAndTime);
        eventData.add(capacity);
        eventData.add(budget);
        eventData.add(email);
        eventData.add(phone);
        eventData.add(description);
        return eventData;
    }

    public ArrayList<String> getLabels() {
        return labels;
    }
}

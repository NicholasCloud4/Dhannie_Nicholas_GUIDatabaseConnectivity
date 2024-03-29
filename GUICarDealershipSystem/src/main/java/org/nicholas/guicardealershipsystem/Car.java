package org.nicholas.guicardealershipsystem;

public class Car {
    private int id;
    private int year;
    private String make;
    private String model;
    private String color;
    private String engine;
    private String transmissionType;
    private double price;
    private boolean sold;


    //Car constructor
    public Car(int id, int year, String make, String model, String color, String engine, String transmissionType, double price) {
        this.id = id;
        this.year = year;
        this.make = make;
        this.model = model;
        this.color = color;
        this.engine = engine;
        this.transmissionType = transmissionType;
        this.price = price;
    }

    // Getters and Setters for the cars attributes
    public int getId() {
        return id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public String getTransmissionType() {
        return transmissionType;
    }

    public void setTransmissionType(String transmissionType) {
        this.transmissionType = transmissionType;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isSold() {
        return sold;
    }

    public void setSold(boolean sold) {
        this.sold = sold;
    }

    //To show the cars in the file
    @Override
    public String toString(){
        return "ID: " + id + ", Year: " + year + ", Make: " + make + ", Model: " + model + ", Color: " + color + ", Engine: " + engine + ", Transmission: " + transmissionType + ", Price: $" + String.format("%.2f",price);
    }
}
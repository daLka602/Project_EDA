package com.connectme.model.entities;

public class Contact {

    private int id;
    private int userId;
    private String name;
    private String phone;
    private String email;
    private String address;

    public Contact(int id, int userId, String name, String phone, String email, String address) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    public Contact(int userId, String name, String phone, String email, String address) {
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
}

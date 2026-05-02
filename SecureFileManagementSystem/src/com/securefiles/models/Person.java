package com.securefiles.models;

public abstract class Person {
    protected String personId;
    protected String name;
    protected String contact;
    protected String address;
    protected String nationality;
    protected String gender;
    protected int age;
    protected String qualification;
    protected String department;
    protected String designation;
    protected String joinDate;
    protected String status;
    protected String remarks;
    protected String emergencyContact;
    protected String bloodGroup;
    
    public abstract void displayInfo();
}

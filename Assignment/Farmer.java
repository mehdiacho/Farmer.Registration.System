package Assignment;

import java.sql.Date;

public class Farmer extends User {

    //instance variables
    private String firstName, lastName, postalAddress, gender, district, subDistrict, dob;
    private int  idNum, farmSize, farmingArea, contactNum;



    //default and parameterized constructor
    public Farmer() {
        this.idNum = 0;
        this.firstName = "";
        this.lastName = "";
        this.postalAddress = "0000";
        this.gender = "";
        this.contactNum = 0;
        this.district = "";
        this.subDistrict = "";
        this.farmSize = 0;
        this.farmingArea = 0;
    }

    public Farmer(int idNum, String firstName, String lastName, String postAddress, String gender, int contactNum, String emailAddress, String district, String subDistrict, int farmSize, int farmingArea, String password) {
        this.idNum = idNum;
        this.firstName = firstName;
        this.lastName = lastName;
        this.postalAddress = postAddress;
        this.gender = gender;
        this.contactNum = contactNum;
        this.emailAddress = emailAddress;
        this.district = district;
        this.subDistrict = subDistrict;
        this.farmSize = farmSize;
        this.farmingArea = farmingArea;
        this.userName = firstName + " " + lastName;
        this.password = password;

    }


    //getters and setters


    public int getIdNum() {
        return idNum;
    }

    public void setIdNum(int idNum) {
        this.idNum = idNum;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(String postalAddress) {
        this.postalAddress = postalAddress;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getContactNum() {
        return contactNum;
    }

    public void setContactNum(int contactNum) {
        this.contactNum = contactNum;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getSubDistrict() {
        return subDistrict;
    }

    public void setSubDistrict(String subDistrict) {
        this.subDistrict = subDistrict;
    }

    public int getFarmSize() {
        return farmSize;
    }

    public void setFarmSize(int farmSize) {
        this.farmSize = farmSize;
    }

    public int getFarmingArea() {
        return farmingArea;
    }

    public void setFarmingArea(int farmingArea) {
        this.farmingArea = farmingArea;
    }


    //toString method


    @Override
    public String toString() {
        return "Farmers{" +
                "idNum='" + idNum + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", postAddress='" + postalAddress + '\'' +
                ", gender='" + gender + '\'' +
                ", contactNum='" + contactNum + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", district='" + district + '\'' +
                ", subDistrict='" + subDistrict + '\'' +
                ", farmSize=" + farmSize +
                ", farmingArea=" + farmingArea +
                '}';
    }


}

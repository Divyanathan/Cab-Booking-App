package com.adaptavant.cabapp.jdo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by user on 01/06/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerJDO implements Serializable {

    @JsonProperty("Description")
    String Description;
    @JsonProperty("address")
    String Address;
    @JsonProperty("name")
    String Name;
    @JsonProperty("key")
    String Key;
    @JsonProperty("id")
    String Id;
    @JsonProperty("description")
    String Description2;
    @JsonProperty("status")
    String Status;
    @JsonProperty("contactType")
    String ContactType;
    @JsonProperty("password")
    String Password;
    @JsonProperty("companyLogo")
    String CompanyLogo;
    @JsonProperty("staffLogin")
    String StaffLogin;
    @JsonProperty("firstName")
    String FirstName;
    @JsonProperty("lastName")
    String LastName;
    @JsonProperty("f_Key")
    String F_Key;
    @JsonProperty("loginId")
    String LoginId;
    @JsonProperty("phone")
    String Phone;
    @JsonProperty("companyName")
    String CompanyName;
    @JsonProperty("vehiclePassStatus")
    String VehiclePassStatus;
    @JsonProperty("preferredService")
    String PreferredService;
    @JsonProperty("preferedStaff")
    String PreferedStaff;

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getDescription2() {
        return Description2;
    }

    public void setDescription2(String description2) {
        Description2 = description2;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getContactType() {
        return ContactType;
    }

    public void setContactType(String contactType) {
        ContactType = contactType;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getCompanyLogo() {
        return CompanyLogo;
    }

    public void setCompanyLogo(String companyLogo) {
        CompanyLogo = companyLogo;
    }

    public String getStaffLogin() {
        return StaffLogin;
    }

    public void setStaffLogin(String staffLogin) {
        StaffLogin = staffLogin;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getF_Key() {
        return F_Key;
    }

    public void setF_Key(String f_Key) {
        F_Key = f_Key;
    }

    public String getLoginId() {
        return LoginId;
    }

    public void setLoginId(String loginId) {
        LoginId = loginId;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getCompanyName() {
        return CompanyName;
    }

    public void setCompanyName(String companyName) {
        CompanyName = companyName;
    }

    public String getVehiclePassStatus() {
        return VehiclePassStatus;
    }

    public void setVehiclePassStatus(String vehiclePassStatus) {
        VehiclePassStatus = vehiclePassStatus;
    }

    public String getPreferredService() {
        return PreferredService;
    }

    public void setPreferredService(String preferredService) {
        PreferredService = preferredService;
    }

    public String getPreferedStaff() {
        return PreferedStaff;
    }

    public void setPreferedStaff(String preferedStaff) {
        PreferedStaff = preferedStaff;
    }


}

package com.examplesonly.android.model;

import com.google.gson.annotations.SerializedName;
public class User {

    @SerializedName("uuid")
    String id;

    @SerializedName("firstName")
    String firstName;

    @SerializedName("middleName")
    String middleName;

    @SerializedName("lastName")
    String lastName;

    @SerializedName("email")
    String email;

    @SerializedName("gender")
    String gender;

    @SerializedName("dob")
    String dob;

    @SerializedName("password")
    String password;

    @SerializedName("profilePhoto")
    String profilePhoto;

    @SerializedName("verified")
    Boolean verified;

    public User() {
    }

    public User(final String id, final String firstName, final String middleName, final String lastName,
            final String email, final String gender, final String dob, final String password,
            final String profilePhoto, final boolean verified) {
        this.id = id;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.gender = gender;
        this.dob = dob;
        this.password = password;
        this.profilePhoto = profilePhoto;
        this.verified = verified;
    }

    public String getId() {
        return id;
    }

    public User setId(final String id) {
        this.id = id;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public User setFirstName(final String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getMiddleName() {
        return middleName;
    }

    public User setMiddleName(final String middleName) {
        this.middleName = middleName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public User setLastName(final String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(final String email) {
        this.email = email;
        return this;
    }

    public String getGender() {
        return gender;
    }

    public User setGender(final String gender) {
        this.gender = gender;
        return this;
    }

    public String getDob() {
        return dob;
    }

    public User setDob(final String dob) {
        this.dob = dob;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(final String password) {
        this.password = password;
        return this;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public User setProfilePhoto(final String profilePhoto) {
        this.profilePhoto = profilePhoto;
        return this;
    }

    public Boolean getVerified() {
        return verified;
    }

    public User setVerified(final Boolean verified) {
        this.verified = verified;
        return this;
    }
}

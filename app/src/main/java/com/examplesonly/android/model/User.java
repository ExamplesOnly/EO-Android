package com.examplesonly.android.model;

public class User {

    String id;
    String firstName;
    String middleName;
    String lastName;
    String profilePhoto;

    public User() {
    }

    public User(final String id, final String firstName, final String middleName, final String lastName,
            final String profilePhoto) {
        this.id = id;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.profilePhoto = profilePhoto;
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

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public User setProfilePhoto(final String profilePhoto) {
        this.profilePhoto = profilePhoto;
        return this;
    }
}

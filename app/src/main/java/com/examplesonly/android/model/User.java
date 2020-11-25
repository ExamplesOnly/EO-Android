package com.examplesonly.android.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
public class User implements Parcelable {

    @SerializedName("uuid")
    String uuid;

    @SerializedName("firstName")
    String firstName;

    @SerializedName("middleName")
    String middleName;

    @SerializedName("lastName")
    String lastName;

    @SerializedName("email")
    String email;

    @SerializedName("bio")
    String bio;

    @SerializedName("gender")
    String gender;

    @SerializedName("dob")
    String dob;

    @SerializedName("password")
    String password;

    @SerializedName("profileImage")
    String profilePhoto;

    @SerializedName("coverImage")
    String coverPhoto;

    @SerializedName("verified")
    Boolean verified;

    public User() {
    }

    protected User(Parcel in) {
        uuid = in.readString();
        firstName = in.readString();
        middleName = in.readString();
        lastName = in.readString();
        email = in.readString();
        bio = in.readString();
        gender = in.readString();
        dob = in.readString();
        password = in.readString();
        profilePhoto = in.readString();
        int tmpVerified = in.readInt();
        verified = tmpVerified != 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel parcel, final int i) {
        parcel.writeString(uuid);
        parcel.writeString(firstName);
        parcel.writeString(middleName);
        parcel.writeString(lastName);
        parcel.writeString(email);
        parcel.writeString(bio);
        parcel.writeString(gender);
        parcel.writeString(dob);
        parcel.writeString(password);
        parcel.writeString(profilePhoto);
        parcel.writeInt(verified ? 1 : 0);
    }

    public String getUuid() {
        return uuid;
    }

    public User setUuid(final String uuid) {
        this.uuid = uuid;
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

    public String getBio() {
        return bio;
    }

    public User setBio(final String bio) {
        this.bio = bio;
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

    public String getCoverPhoto() {
        return coverPhoto;
    }

    public User setCoverPhoto(final String coverPhoto) {
        this.coverPhoto = coverPhoto;
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

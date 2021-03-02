package com.examplesonly.android.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class User implements Parcelable {

    @SerializedName("uuid")
    String uuid;

    @SerializedName("firstName")
    String firstName = "";

    @SerializedName("middleName")
    String middleName = "";

    @SerializedName("lastName")
    String lastName = "";

    @SerializedName("email")
    String email = "";

    @SerializedName("bio")
    String bio = "";

    @SerializedName("gender")
    String gender = "";

    @SerializedName("dob")
    String dob = "";

    @SerializedName("password")
    String password = "";

    @SerializedName("profileImage")
    String profilePhoto = "";

    @SerializedName("coverImage")
    String coverPhoto = "";

    @SerializedName("emailVerified")
    boolean emailVerified = false;

    @SerializedName("profileVerified")
    boolean profileVerified = false;

    @SerializedName("blocked")
    boolean blocked = false;

    @SerializedName("isFollowing")
    boolean isFollowing;

    @SerializedName("isFollowedBy")
    boolean isFollowedBy;

    @SerializedName("Categories")
    ArrayList<Category> categories = new ArrayList<>();

    @SerializedName("Videos")
    ArrayList<Video> videos = new ArrayList<>();

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
        emailVerified = in.readBoolean();
        blocked = in.readBoolean();
        isFollowedBy = in.readBoolean();
        isFollowing = in.readBoolean();

        categories = in.readArrayList(Category.class.getClassLoader());
        videos = in.readArrayList(Video.class.getClassLoader());
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
        parcel.writeBoolean(emailVerified);
        parcel.writeBoolean(blocked);
        parcel.writeBoolean(isFollowedBy);
        parcel.writeBoolean(isFollowing);
        parcel.writeList(categories);
        parcel.writeList(videos);
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

    public Boolean isEmailVerified() {
        return emailVerified;
    }

    public User setEmailVerified(final Boolean verified) {
        this.emailVerified = verified;
        return this;
    }

    public Boolean isProfileVerified() {
        return emailVerified;
    }

    public User setProfileVerified(final Boolean verified) {
        this.profileVerified = verified;
        return this;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setFollowing(boolean following) {
        isFollowing = following;
    }

    public boolean isFollowedBy() {
        return isFollowedBy;
    }

    public void setFollowedBy(boolean followedBy) {
        isFollowedBy = followedBy;
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public User setCategories(final ArrayList<Category> categories) {
        this.categories = categories;
        return this;
    }

    public ArrayList<Video> getVideos() {
        return videos;
    }

    public User setVideos(final ArrayList<Video> videos) {
        this.videos = videos;
        return this;
    }

    @Override
    public String toString() {
        return "User{" +
                "uuid='" + uuid + '\'' +
                ", firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", bio='" + bio + '\'' +
                ", gender='" + gender + '\'' +
                ", dob='" + dob + '\'' +
                ", password='" + password + '\'' +
                ", profilePhoto='" + profilePhoto + '\'' +
                ", coverPhoto='" + coverPhoto + '\'' +
                ", emailVerified=" + emailVerified +
                ", profileVerified=" + profileVerified +
                ", blocked=" + blocked +
                ", isFollowing=" + isFollowing +
                ", isFollowedBy=" + isFollowedBy +
                ", categories=" + categories +
                ", videos=" + videos +
                '}';
    }
}

package com.example.profinal2019.Model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "dogs")
public class DogEntity implements Serializable {
    @PrimaryKey
    private int id;
    private String name;
    private String description;
    private String pictureUrl;
    private String primaryBreed;
    private boolean isMixed;
    private String age;
    private String gender;
    private String size;
    private boolean isDesexed;
    private boolean isHouseTrained;
    private boolean hasSpecialNeeds;

    // shelter details
    // if I were to scale this app up, I'd have a second entity for organizations
    // that would correlate to each dog by the organization ID assigned to each DogEntity
    private String organizationID;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String zip;
    private String petFinderUrl;

    @Ignore
    public DogEntity() { }

    public DogEntity(int id, String name, String description, String pictureUrl, String primaryBreed,
                     boolean isMixed, String age, String gender, String size, boolean isDesexed,
                     boolean isHouseTrained, boolean hasSpecialNeeds, String organizationID, String phone,
                     String address, String city, String state, String zip, String petFinderUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.pictureUrl = pictureUrl;
        this.primaryBreed = primaryBreed;
        this.isMixed = isMixed;
        this.age = age;
        this.gender = gender;
        this.size = size;
        this.isDesexed = isDesexed;
        this.isHouseTrained = isHouseTrained;
        this.hasSpecialNeeds = hasSpecialNeeds;
        this.organizationID = organizationID;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.petFinderUrl = petFinderUrl;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public String getPrimaryBreed() {
        return primaryBreed;
    }

    public boolean isMixed() {
        return isMixed;
    }

    public String getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getSize() {
        return size;
    }

    public boolean isDesexed() {
        return isDesexed;
    }

    public boolean isHouseTrained() {
        return isHouseTrained;
    }

    public boolean hasSpecialNeeds() {
        return hasSpecialNeeds;
    }

    public String getOrganizationID() {
        return organizationID;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZip() {
        return zip;
    }

    public String getPetFinderUrl() {
        return petFinderUrl;
    }
}

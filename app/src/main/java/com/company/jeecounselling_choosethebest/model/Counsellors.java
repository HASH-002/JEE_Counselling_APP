package com.company.jeecounselling_choosethebest.model;

public class Counsellors {

    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String id;
    private String imageUrl;
    private String experience;
    private String skills;
    private String achievements;

    // Constructors

    public Counsellors() {
    }

    public Counsellors(String firstname, String lastname, String email, String password, String id, String imageUrl,
                       String experience, String skills, String achievements) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.id = id;
        this.imageUrl = imageUrl;
        this.experience = experience;
        this.skills = skills;
        this.achievements = achievements;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getAchievements() {
        return achievements;
    }

    public void setAchievements(String achievements) {
        this.achievements = achievements;
    }
}

package com.company.jeecounselling_choosethebest.model;

public class Counsellors extends Users{

    private String experience;
    private String skills;
    private String achievements;

    // Constructors
    public Counsellors() {
    }

    public Counsellors(String firstname, String lastname, String email, String password, String id, String imageUrl, String experience, String skills, String achievements) {
        super(firstname, lastname, email, password, id, imageUrl);
        this.experience = experience;
        this.skills = skills;
        this.achievements = achievements;
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

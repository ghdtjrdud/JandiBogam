package com.ssafy.mvc.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class UserDto {

    private long id; // 넉넉하게 long
    private String loginId; // camelcase
    private String password;
    private String name;
    private LocalDate birthDate; // 날짜만 필요한 타입형
    private String gender;
    private String email;
    private double height;
    private double weight;
    private String uiTheme;
    private LocalDateTime createdAt; // 날짜 + 시간정보가 필요할 때
    private LocalDateTime updatedAt;

    public long getId() {
        return id;
    }

    public String getLoginId() {
        return loginId;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getGender() {
        return gender;
    }

    public String getEmail() {
        return email;
    }

    public double getHeight() {
        return height;
    }

    public double getWeight() {
        return weight;
    }

    public String getUiTheme() {
        return uiTheme;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setUiTheme(String uiTheme) {
        this.uiTheme = uiTheme;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public UserDto(long id, String loginId, String password, String name, LocalDate birthDate, String gender, String email, double height, double weight, String uiTheme, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.birthDate = birthDate;
        this.gender = gender;
        this.email = email;
        this.height = height;
        this.weight = weight;
        this.uiTheme = uiTheme;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UserDto() {
    }
}

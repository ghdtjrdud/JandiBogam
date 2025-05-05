package com.ssafy.mvc.model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;


//@Setter
//@Getter
public class MealDto {
    private int id;
    private int userId;
    private LocalDate eatDate;
    private String timeSlot; //enum 처리해야 돼
    private String photoUrl;
    private String foodName;
    private String memo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MealDto(int id, int userId, LocalDate eatDate, String timeSlot, String photoUrl, String foodName, String memo, LocalDateTime updatedAt, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.eatDate = eatDate;
        this.timeSlot = timeSlot;
        this.photoUrl = photoUrl;
        this.foodName = foodName;
        this.memo = memo;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public LocalDate getEatDate() {
        return eatDate;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getFoodName() {
        return foodName;
    }

    public String getMemo() {
        return memo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setEatDate(LocalDate eatDate) {
        this.eatDate = eatDate;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

package com.ssafy.mvc.model.dto;

import lombok.Data;

@Data
public class FoodNutrientDto {
    private int id;
    private String foodName;
    private String servingSize;
    private Float energy;
    private Float protein;
    private Float fat;
    private Float carbohydrate;
    private Float sugar;
    private Float fiber;
    private Float potassium;
    private Float sodium;
    private Float cholesterol;
    private Float saturatedFat;
}

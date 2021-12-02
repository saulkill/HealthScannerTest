package com.cookandroid.healthscanner.ui.food.datatable;

public class Food {

    private String foodName;
    private Double protein, carb, fat, kcal;

    public Food() {
    }

    public Food(String foodName, Double protein, Double carb, Double fat, Double kcal) {
        this.foodName = foodName;
        this.protein = protein;
        this.carb = carb;
        this.fat = fat;
        this.kcal = kcal;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public Double getProtein() {
        return protein;
    }

    public void setProtein(Double protein) {
        this.protein = protein;
    }

    public Double getCarb() {
        return carb;
    }

    public void setCarb(Double carb) {
        this.carb = carb;
    }

    public Double getFat() {
        return fat;
    }

    public void setFat(Double fat) {
        this.fat = fat;
    }

    public Double getKcal() {
        return kcal;
    }

    public void setKcal(Double kcal) {
        this.kcal = kcal;
    }
}

package com.cookandroid.healthscanner.ui.food.datatable;

public class DeleteFood {
    private String foodName,date,fileName;
    private float protein, carb, fat, kcal;

    public DeleteFood() {
    }

    public DeleteFood(String foodName, String date, String fileName, float protein, float carb, float fat, float kcal) {
        this.foodName = foodName;
        this.date = date;
        this.fileName = fileName;
        this.protein = protein;
        this.carb = carb;
        this.fat = fat;
        this.kcal = kcal;
    }

    @Override
    public String toString() {
        return "DeleteFood{" +
                "foodName='" + foodName + '\'' +
                ", date='" + date + '\'' +
                ", fileName='" + fileName + '\'' +
                ", protein=" + protein +
                ", carb=" + carb +
                ", fat=" + fat +
                ", kcal=" + kcal +
                '}';
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public float getProtein() {
        return protein;
    }

    public void setProtein(float protein) {
        this.protein = protein;
    }

    public float getCarb() {
        return carb;
    }

    public void setCarb(float carb) {
        this.carb = carb;
    }

    public float getFat() {
        return fat;
    }

    public void setFat(float fat) {
        this.fat = fat;
    }

    public float getKcal() {
        return kcal;
    }

    public void setKcal(float kcal) {
        this.kcal = kcal;
    }
}

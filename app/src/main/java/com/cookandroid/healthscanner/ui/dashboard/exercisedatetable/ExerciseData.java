package com.cookandroid.healthscanner.ui.dashboard.exercisedatetable;

public class ExerciseData {
    private String exercise;
   private String exerciseName;
   private String exerciseExplanation;
   private int startDate;
   private int lastDate;
   private String image;
   private boolean saveData = false;

    public ExerciseData() {
    }

    public ExerciseData(String exercise, String exerciseName, String exerciseExplanation, int startDate, int lastDate, String image, boolean saveData) {
        this.exercise = exercise;
        this.exerciseName = exerciseName;
        this.exerciseExplanation = exerciseExplanation;
        this.startDate = startDate;
        this.lastDate = lastDate;
        this.image = image;
        this.saveData = saveData;
    }

    public String getExercise() {
        return exercise;
    }

    public void setExercise(String exercise) {
        this.exercise = exercise;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public String getExerciseExplanation() {
        return exerciseExplanation;
    }

    public void setExerciseExplanation(String exerciseExplanation) {
        this.exerciseExplanation = exerciseExplanation;
    }

    public int getStartDate() {
        return startDate;
    }

    public void setStartDate(int startDate) {
        this.startDate = startDate;
    }

    public int getLastDate() {
        return lastDate;
    }

    public void setLastDate(int lastDate) {
        this.lastDate = lastDate;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isSaveData() {
        return saveData;
    }

    public void setSaveData(boolean saveData) {
        this.saveData = saveData;
    }

    @Override
    public String toString() {
        return "ExerciseData{" +
                "exercise='" + exercise + '\'' +
                ", exerciseName='" + exerciseName + '\'' +
                ", exerciseExplanation='" + exerciseExplanation + '\'' +
                ", startDate=" + startDate +
                ", lastDate=" + lastDate +
                ", image='" + image + '\'' +
                ", saveData=" + saveData +
                '}';
    }
}

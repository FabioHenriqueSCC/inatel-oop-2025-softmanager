package models.Task;

import java.time.LocalDate;

import models.Person.Person;

public class FeatureTask extends Task {

    private double businessValue;

    public FeatureTask(int id, String title, LocalDate deadline, double estimatedHours, Person responsible, double businessValue) {
        super(id, title, deadline, estimatedHours, responsible);
        this.businessValue = businessValue;
    }

    @Override
    public double calculateScore() {
        return businessValue / estimatedHours;
    }
}

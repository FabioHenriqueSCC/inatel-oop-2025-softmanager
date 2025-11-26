package models.Task;

import java.time.LocalDate;

import models.Person.Person;

public class BugTask extends Task {
    private int severity;

    public BugTask(int id, String title, LocalDate deadline, double estimatedHours, Person responsible, int severity) {
        super(id, title, deadline, estimatedHours, responsible);
        this.severity = severity;
    }

    @Override
    public double calculateScore() {
        return severity * estimatedHours;
    }
}

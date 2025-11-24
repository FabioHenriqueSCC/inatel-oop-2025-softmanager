package models.Task;

import java.time.LocalDate;

import models.Person.Person;

public abstract class Task {
    protected int id;
    protected String title;
    protected LocalDate deadline;
    protected TaskStatus status;
    protected double estimatedHours;
    protected Person responsible;

    public Task(int id, String title, LocalDate deadline, double estimatedHours, Person responsible) {
        this.id = id;
        this.title = title;
        this.deadline = deadline;
        this.estimatedHours = estimatedHours;
        this.status = TaskStatus.TODO;
        this.responsible = responsible;
    }

    public abstract double calculateScore();
}

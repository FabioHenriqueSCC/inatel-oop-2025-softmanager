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

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public TaskStatus getStatus(){
        return this.status;
    }

    public double getEstimatedHours() {
        return estimatedHours;
    }

    public Person getResponsible() {
        return responsible;
    }
}

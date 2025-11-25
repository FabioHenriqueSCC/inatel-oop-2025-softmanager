package models.Project;

import java.util.ArrayList;
import java.util.List;

import models.Person.Client;
import models.Person.Developer;
import models.Task.Task;
import models.Task.TaskStatus;

public class Project {
    private int id;
    private String name;
    private Client client;
    private Developer owner;
    private List<Task> tasks;

    public Project(int id, String name, Client client, Developer owner) {
        this.id = id;
        this.name = name;
        this.client = client;
        this.owner = owner;

        this.tasks = new ArrayList<>();
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public double getProgress() {
        if (tasks.isEmpty()) {
            return 0;
        }
        int completedTasks = 0;
        for (Task task : tasks) {
            if (task.getStatus() == TaskStatus.DONE) {
                completedTasks++;
            }
        }
        return (double) completedTasks / tasks.size() * 100;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Client getClient() {
        return client;
    }

    public Developer getOwner() {
        return owner;
    }


    public List<Task> getTasks() {
        return tasks;
    }

}

package models.Project;

import java.util.List;

import interfaces.ProjectRepository;
import models.Person.Client;
import models.Person.Developer;
import models.Task.Task;

public class ProjectService {
    private ProjectRepository repository;

    public ProjectService(ProjectRepository repository) {
        this.repository = repository;
    }

    public Project createProject(int id, String name, Client client, Developer owner) {
        Project project = new Project(id, name, client, owner);
        repository.save(project);
        return project;
    }

    public void addTaskToProject(int projectId, Task task) {
        Project project = repository.findById(projectId); 
        if (project != null) {
            project.addTask(task); 
            repository.save(project); 
        } else {
            throw new IllegalArgumentException("Projeto não encontrado! Por favor, escolha um projeto existente.");
        }
    }

    public List<Project> listProjects() {
        return repository.findAll(); 
    }
}

package interfaces;

import java.util.List;

import models.Project.Project;

public interface ProjectRepository {
    void save(Project project);  
    List<Project> findAll();     
    Project findById(int id);    
}


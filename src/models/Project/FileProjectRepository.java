package models.Project;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import interfaces.ProjectRepository;
import models.Person.Client;
import models.Person.Developer;

public class FileProjectRepository implements ProjectRepository {
    private String filePath;

    public FileProjectRepository(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void save(Project project) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(project.getId() + "," + project.getName() + "," + project.getClient().getName() + ","
                    + project.getOwner().getName());
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Erro ao salvar o projeto.");
            e.printStackTrace();
        }
    }

    @Override
    public List<Project> findAll() {
        List<Project> projects = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                Project project = new Project(Integer.parseInt(parts[0]), parts[1], new Client(parts[2], "", parts[2]),
                        new Developer(parts[3], "", ""));
                projects.add(project);
            }
        } catch (IOException e) {
            System.out.println("Erro ao listar o projeto. Verifique se o arquivo existe e está de acordo com o padrao.");
            e.printStackTrace();
        }
        return projects;
    }

    @Override
    public Project findById(int id) {
        for (Project project : findAll()) {
            if (project.getId() == id) {
                return project;
            }
        }
        return null;
    }
}

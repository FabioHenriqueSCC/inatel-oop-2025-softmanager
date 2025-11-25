package models.Project;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import interfaces.ProjectRepository;
import models.Person.Client;
import models.Person.Developer;
import models.Task.BugTask;
import models.Task.FeatureTask;
import models.Task.Task;

public class FileProjectRepository implements ProjectRepository {

    private String dbFile = "database.txt";
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public FileProjectRepository() {
    }

    @Override
    public void save(Project project) {

        List<Project> allProjects = findAll();

        allProjects.removeIf(p -> p.getId() == project.getId());

        allProjects.add(project);

        rewriteFile(allProjects);
    }

    private void rewriteFile(List<Project> projects) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dbFile, false))) { 

            for (Project p : projects) {
                String projLine = String.format("PROJ;%d;%s;%s;%s",
                        p.getId(),
                        p.getName(),
                        p.getClient().getName(),
                        p.getOwner().getName());
                writer.write(projLine);
                writer.newLine();

                for (Task t : p.getTasks()) {
                    String type = (t instanceof BugTask) ? "BUG" : "FEATURE";
                    String extra = (t instanceof BugTask) ? "3" : "100.0"; 

                    String taskLine = String.format("TASK;%d;%s;%d;%s;%s;%.2f;%s;%s",
                            p.getId(),
                            type,
                            t.getId(),
                            t.getTitle(),
                            t.getDeadline().format(formatter),
                            t.getEstimatedHours(),
                            t.getResponsible().getName(),
                            extra);
                    writer.write(taskLine);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar no banco de dados: " + e.getMessage());
        }
    }

    @Override
    public List<Project> findAll() {
        List<Project> projects = new ArrayList<>();
        File file = new File(dbFile);

        if (!file.exists())
            return projects;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                String prefix = parts[0]; 

                if (prefix.equals("PROJ")) {
                    int id = Integer.parseInt(parts[1]);
                    String name = parts[2];
                    Client cli = new Client(parts[3], "N/A", "N/A");
                    Developer dev = new Developer(parts[4], "N/A", "N/A");

                    projects.add(new Project(id, name, cli, dev));

                } else if (prefix.equals("TASK")) {
                    int projectId = Integer.parseInt(parts[1]);

                    Project targetProject = null;
                    for (Project p : projects) {
                        if (p.getId() == projectId)
                            targetProject = p;
                    }

                    if (targetProject != null) {
                        String type = parts[2];
                        int taskId = Integer.parseInt(parts[3]);
                        String title = parts[4];
                        LocalDate deadline = LocalDate.parse(parts[5], formatter);
                        double hours = Double.parseDouble(parts[6].replace(",", "."));
                        Developer resp = new Developer(parts[7], "N/A", "N/A");

                        if (type.equals("BUG")) {
                            int sev = Integer.parseInt(parts[8]);
                            targetProject.addTask(new BugTask(taskId, title, deadline, hours, resp, sev));
                        } else {
                            double val = Double.parseDouble(parts[8]);
                            targetProject.addTask(new FeatureTask(taskId, title, deadline, hours, resp, val));
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao ler o banco de dados: " + e.getMessage());
        }
        return projects;
    }

    @Override
    public Project findById(int id) {
        for (Project p : findAll()) {
            if (p.getId() == id)
                return p;
        }
        return null;
    }
}
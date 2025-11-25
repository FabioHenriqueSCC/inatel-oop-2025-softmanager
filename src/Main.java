import interfaces.ProjectRepository;

import models.Person.Client;
import models.Person.Developer;
import models.Project.FileProjectRepository;
import models.Project.Project;
import models.Project.ProjectService;
import models.Task.BugTask;
import models.Task.FeatureTask;
import models.Task.Task;

import utils.LoadingThread;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        
        Scanner scanner = new Scanner(System.in);
        ProjectRepository repository = new FileProjectRepository();
        ProjectService service = new ProjectService(repository);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        boolean isRunning = true;

        System.out.println("=== SOFTMANAGER 2025: SISTEMA INTEGRADO ===");

        while (isRunning) {
            System.out.println("\n--- MENU PRINCIPAL ---");
            System.out.println("1. Criar Novo Projeto (e Tarefas)");
            System.out.println("2. Adicionar Tarefa a Projeto Existente");
            System.out.println("3. Relatório Completo (Ler Banco de Dados)");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");

            try {
                String input = scanner.nextLine();
                if (input.isEmpty()) continue;
                int option = Integer.parseInt(input);

                switch (option) {
                    case 1:
                        System.out.println("\n>> CRIANDO NOVO PROJETO");
                        
                        System.out.print("ID do Projeto: ");
                        int id = Integer.parseInt(scanner.nextLine());
                        System.out.print("Nome do Projeto: ");
                        String name = scanner.nextLine();
                        System.out.print("Nome do Cliente: ");
                        String cName = scanner.nextLine();
                        System.out.print("Nome do Líder (Dev): ");
                        String dName = scanner.nextLine();

                        Client client = new Client(cName, "email@dummy.com", "Client Co.");
                        Developer lead = new Developer(dName, "dev@inatel.br", "Senior");

                        LoadingThread saveThread = new LoadingThread("Salvando Projeto");
                        saveThread.start();
                        try { saveThread.join(); } catch (InterruptedException e) {}

                        service.createProject(id, name, client, lead);
                        System.out.println("Sucesso: Projeto criado!");

                        System.out.print("\nDeseja adicionar uma tarefa agora? (S/N): ");
                        String answer = scanner.nextLine();

                        if (answer.equalsIgnoreCase("S")) {
                            System.out.println(">> ADICIONANDO TAREFA INICIAL");
                            
                            System.out.println("Tipo: [1] Bug | [2] Feature");
                            int type = Integer.parseInt(scanner.nextLine());

                            System.out.print("ID da Tarefa: ");
                            int taskId = Integer.parseInt(scanner.nextLine());
                            System.out.print("Título: ");
                            String title = scanner.nextLine();
                            System.out.print("Prazo (dd/MM/yyyy): ");
                            LocalDate deadline = LocalDate.parse(scanner.nextLine(), formatter);
                            System.out.print("Horas Estimadas: ");
                            double hours = Double.parseDouble(scanner.nextLine());

                            Task newTask = null;
                            if (type == 1) {
                                System.out.print("Severidade (1-5): ");
                                int sev = Integer.parseInt(scanner.nextLine());
                                newTask = new BugTask(taskId, title, deadline, hours, lead, sev);
                            } else {
                                System.out.print("Valor Negócio: ");
                                double val = Double.parseDouble(scanner.nextLine());
                                newTask = new FeatureTask(taskId, title, deadline, hours, lead, val);
                            }

                            LoadingThread updateThread = new LoadingThread("Vinculando Tarefa");
                            updateThread.start();
                            try { updateThread.join(); } catch (InterruptedException e) {}

                            service.addTaskToProject(id, newTask);
                            System.out.println("Tarefa salva com sucesso!");
                        }
                        break;

                    case 2:
                        System.out.println("\n>> ADICIONAR TAREFA (PROJETO EXISTENTE)");
                        System.out.print("ID do Projeto Destino: ");
                        int targetId = Integer.parseInt(scanner.nextLine());
                        
                        Project targetProj = service.listProjects().stream()
                                .filter(p -> p.getId() == targetId).findFirst().orElse(null);

                        if (targetProj == null) {
                            System.out.println("Erro: Projeto não encontrado.");
                            break;
                        }

                        System.out.println("Tipo: [1] Bug | [2] Feature");
                        int tType = Integer.parseInt(scanner.nextLine());
                        System.out.print("ID Tarefa: ");
                        int tId = Integer.parseInt(scanner.nextLine());
                        System.out.print("Título: ");
                        String tTitle = scanner.nextLine();
                        System.out.print("Prazo (dd/MM/yyyy): ");
                        LocalDate tDead = LocalDate.parse(scanner.nextLine(), formatter);
                        System.out.print("Horas: ");
                        double tHours = Double.parseDouble(scanner.nextLine());

                        Task tTask = null;
                        if (tType == 1) {
                            System.out.print("Severidade (1-5): ");
                            int sev = Integer.parseInt(scanner.nextLine());
                            tTask = new BugTask(tId, tTitle, tDead, tHours, targetProj.getOwner(), sev);
                        } else {
                            System.out.print("Valor Negócio: ");
                            double val = Double.parseDouble(scanner.nextLine());
                            tTask = new FeatureTask(tId, tTitle, tDead, tHours, targetProj.getOwner(), val);
                        }

                        LoadingThread taskThread = new LoadingThread("Salvando Tarefa");
                        taskThread.start();
                        try { taskThread.join(); } catch (InterruptedException e) {}

                        service.addTaskToProject(targetId, tTask);
                        System.out.println("Sucesso!");
                        break;

                    case 3: 
                        System.out.println("\n--- RELATÓRIO DO ARQUIVO ---");

                        LoadingThread loadThread = new LoadingThread("Carregando Banco de Dados");
                        loadThread.start();
                        try { loadThread.join(); } catch (InterruptedException e) {}

                        var list = service.listProjects();
                        
                        if (list.isEmpty()) {
                            System.out.println("Banco de dados vazio.");
                        } else {
                            for (Project p : list) {
                                System.out.println("PROJETO [" + p.getId() + "] " + p.getName());
                                System.out.println("   Líder: " + p.getOwner().getName());
                                
                                if (p.getTasks() != null && !p.getTasks().isEmpty()) {
                                    System.out.println("   * TAREFAS:");
                                    for (Task t : p.getTasks()) {
                                        String type = (t instanceof BugTask) ? "[BUG]" : "[FEAT]";
                                        System.out.println("     -> " + type + " " + t.getTitle() + 
                                                           " (Prazo: " + t.getDeadline().format(formatter) + ")");
                                    }
                                } else {
                                    System.out.println("   (Sem tarefas)");
                                }
                                System.out.println("-------------------------");
                            }
                        }
                        break;

                    case 0:
                        isRunning = false;
                        System.out.println("Saindo do sistema...");
                        break;

                    default:
                        System.out.println("Opção inválida.");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
        scanner.close();
    }
}
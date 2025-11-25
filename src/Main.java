import interfaces.ProjectRepository;
import models.Person.Client;
import models.Person.Developer;
import models.Project.FileProjectRepository;
import models.Project.Project;
import models.Project.ProjectService;
import models.Task.BugTask;
import models.Task.FeatureTask;
import models.Task.Task;

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
            System.out.println("1. Criar Novo Projeto");
            System.out.println("2. Adicionar Tarefa (Bug ou Feature)");
            System.out.println("3. Relatório Completo (Ler Banco de Dados)");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");

            try {
                String input = scanner.nextLine();
                if (input.isEmpty())
                    continue;
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

                        Client client = new Client(cName, "email@cliente.com", "Empresa Cliente");

                        System.out.print("Nome do Líder Técnico: ");
                        String dName = scanner.nextLine();

                        Developer lead = new Developer(dName, "dev@inatel.br", "Senior");

                        service.createProject(id, name, client, lead);
                        System.out.println("Sucesso: Projeto salvo em 'database.txt'!");
                        break;

                    case 2: 
                        System.out.println("\n>> ADICIONANDO TAREFA");

                        System.out.print("Digite o ID do Projeto Destino: ");
                        int targetId = Integer.parseInt(scanner.nextLine());

                        Project targetProject = service.listProjects().stream()
                                .filter(p -> p.getId() == targetId)
                                .findFirst()
                                .orElse(null);

                        if (targetProject == null) {
                            System.out.println("Erro: Projeto com ID " + targetId + " não encontrado.");
                            break;
                        }

                        System.out.println("Selecione o Tipo: [1] Bug | [2] Feature");
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
                            System.out.print("Severidade (1-Baixa a 5-Crítica): ");
                            int severity = Integer.parseInt(scanner.nextLine());

                            newTask = new BugTask(taskId, title, deadline, hours, targetProject.getOwner(), severity);
                        } else if (type == 2) {
                            System.out.print("Valor de Negócio ($): ");
                            double value = Double.parseDouble(scanner.nextLine());

                            newTask = new FeatureTask(taskId, title, deadline, hours, targetProject.getOwner(), value);
                        } else {
                            System.out.println("Tipo inválido. Operação cancelada.");
                            break;
                        }

                        service.addTaskToProject(targetId, newTask);
                        System.out.println("Sucesso: Tarefa vinculada e salva!");
                        break;

                    case 3: 
                        System.out.println("\n--- RELATÓRIO GERAL (DATABASE.TXT) ---");
                        var projectList = service.listProjects();

                        if (projectList.isEmpty()) {
                            System.out.println("Nenhum registro encontrado no banco de dados.");
                        }

                        for (Project p : projectList) {
                            System.out.println("==========================================");
                            System.out.println("PROJETO [" + p.getId() + "] " + p.getName());
                            System.out.println("   Cliente: " + p.getClient().getName());
                            System.out.println("   Líder:   " + p.getOwner().getName());

                            if (p.getTasks().isEmpty()) {
                                System.out.println("   * Nenhuma tarefa cadastrada.");
                            } else {
                                System.out.println("   * Tarefas:");
                                for (Task t : p.getTasks()) {
                                    String taskType = (t instanceof BugTask) ? "[BUG]" : "[FEATURE]";

                                    System.out.println("     -> " + taskType + " ID:" + t.getId() + " - " + t.getTitle());
                                    System.out.println("        Prazo: " + t.getDeadline().format(formatter) + " | Horas: " + t.getEstimatedHours());
                                }
                            }
                        }
                        System.out.println("==========================================");
                        break;

                    case 0:
                        isRunning = false;
                        System.out.println("Saindo do sistema...");
                        break;

                    default:
                        System.out.println("Opção inválida. Tente novamente.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Erro: Digite apenas números nos campos numéricos.");
            } catch (Exception e) {
                System.out.println("Erro inesperado: " + e.getMessage());
            }
        }
        scanner.close();
    }
}
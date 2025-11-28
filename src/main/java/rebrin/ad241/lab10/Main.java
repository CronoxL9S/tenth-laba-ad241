import dao.*;
import models.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static final SupplierDAO supplierDAO = new SupplierDAO();
    private static final MaterialDAO materialDAO = new MaterialDAO();
    private static final EmployeeDAO employeeDAO = new EmployeeDAO();
    private static final ProjectDAO projectDAO = new ProjectDAO();
    private static final TaskDAO taskDAO = new TaskDAO();

    public static void main(String[] args) {
        DBUtil.initDatabase();
        System.out.println("\nСистема обліку будівельної компанії");

        while (true) {
            printMenu();
            String cmd = sc.nextLine().trim();
            try {
                switch (cmd) {
                    case "1": addProject(); break;
                    case "2": addTask(); break;
                    case "3": addMaterial(); break;
                    case "4": addEmployee(); break;
                    case "5": addSupplier(); break;
                    case "6": listProjects(); break;
                    case "7": searchProjectsByClient(); break;
                    case "8": viewTasksForProject(); break;
                    case "9": calculateProjectCost(); break;
                    case "10": listMaterials(); break;
                    case "11": listEmployees(); break;
                    case "12": listSuppliers(); break;
                    case "0":
                        System.out.println("До побачення!");
                        return;
                    default:
                        System.out.println("Невідома команда");
                }
            } catch (SQLException ex) {
                System.out.println("Помилка БД: " + ex.getMessage());
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("1.  Додати проект");
        System.out.println("2.  Додати завдання до проекту");
        System.out.println("3.  Додати матеріал");
        System.out.println("4.  Додати працівника");
        System.out.println("5.  Додати постачальника");
        System.out.println("6.  Список проектів");
        System.out.println("7.  Пошук проектів за клієнтом");
        System.out.println("8.  Перегляд завдань проекту");
        System.out.println("9.  Розрахунок вартості проекту");
        System.out.println("10. Список матеріалів");
        System.out.println("11. Список працівників");
        System.out.println("12. Список постачальників");
        System.out.println("0.  Вихід");
        System.out.println("═══════════════════════════════════════");
        System.out.print("➤ Ваш вибір: ");
    }

    private static void addProject() throws SQLException {
        System.out.print("Назва проекту: ");
        String name = sc.nextLine();
        System.out.print("Клієнт: ");
        String client = sc.nextLine();
        System.out.print("Дата початку (YYYY-MM-DD): ");
        String startDate = sc.nextLine();
        System.out.print("Дата завершення (YYYY-MM-DD або Enter): ");
        String endDate = sc.nextLine().trim();
        System.out.print("Статус (NEW/IN_PROGRESS/DONE): ");
        String status = sc.nextLine();

        Project p = new Project(null, name, client, startDate, endDate.isEmpty() ? null : endDate, status);
        projectDAO.create(p);
        System.out.println("Проект створено з ID: " + p.getId());
    }

    private static void addTask() throws SQLException {
        System.out.print("ID проекту: ");
        int projectId = Integer.parseInt(sc.nextLine());
        System.out.print("Назва завдання: ");
        String name = sc.nextLine();
        System.out.print("ID працівника (або Enter): ");
        String empStr = sc.nextLine().trim();
        Integer employeeId = empStr.isEmpty() ? null : Integer.parseInt(empStr);
        System.out.print("ID матеріалу (або Enter): ");
        String matStr = sc.nextLine().trim();
        Integer materialId = matStr.isEmpty() ? null : Integer.parseInt(matStr);
        System.out.print("Кількість матеріалу: ");
        double qty = Double.parseDouble(sc.nextLine());
        System.out.print("Вартість завдання (грн): ");
        double cost = Double.parseDouble(sc.nextLine());
        System.out.print("Статус (PLANNED/IN_PROGRESS/DONE): ");
        String status = sc.nextLine();
        System.out.print("Дата початку (YYYY-MM-DD або Enter): ");
        String startDate = sc.nextLine().trim();
        System.out.print("Дата завершення (YYYY-MM-DD або Enter): ");
        String endDate = sc.nextLine().trim();

        Task t = new Task(null, projectId, name, employeeId, materialId, qty, cost, status,
                startDate.isEmpty() ? null : startDate, endDate.isEmpty() ? null : endDate);
        taskDAO.create(t);
        System.out.println("Завдання створено з ID: " + t.getId());
    }

    private static void addMaterial() throws SQLException {
        System.out.print("Назва матеріалу: ");
        String name = sc.nextLine();
        System.out.print("ID постачальника (або Enter): ");
        String supStr = sc.nextLine().trim();
        Integer supplierId = supStr.isEmpty() ? null : Integer.parseInt(supStr);
        System.out.print("Ціна за одиницю (грн): ");
        double price = Double.parseDouble(sc.nextLine());

        Material m = new Material(null, name, supplierId, price);
        materialDAO.create(m);
        System.out.println("Матеріал створено з ID: " + m.getId());
    }

    private static void addEmployee() throws SQLException {
        System.out.print("Ім'я працівника: ");
        String name = sc.nextLine();
        System.out.print("Спеціалізація: ");
        String spec = sc.nextLine();
        System.out.print("Телефон: ");
        String phone = sc.nextLine();

        Employee e = new Employee(null, name, spec, phone);
        employeeDAO.create(e);
        System.out.println("Працівника створено з ID: " + e.getId());
    }

    private static void addSupplier() throws SQLException {
        System.out.print("Назва постачальника: ");
        String name = sc.nextLine();
        System.out.print("Контакт: ");
        String contact = sc.nextLine();

        Supplier s = new Supplier(null, name, contact);
        supplierDAO.create(s);
        System.out.println("Постачальника створено з ID: " + s.getId());
    }

    private static void listProjects() throws SQLException {
        System.out.print("Фільтр за статусом (або Enter): ");
        String status = sc.nextLine().trim();
        System.out.print("Сортувати за датою завершення? (y/n): ");
        boolean sort = "y".equalsIgnoreCase(sc.nextLine().trim());
        System.out.print("За зростанням? (y/n): ");
        boolean asc = "y".equalsIgnoreCase(sc.nextLine().trim());

        List<Project> list = projectDAO.findAll(status.isEmpty() ? null : status, sort ? "end_date" : null, asc);
        System.out.println("\nСписок проектів:");
        for (Project p : list) {
            System.out.println(p);
        }
    }

    private static void searchProjectsByClient() throws SQLException {
        System.out.print("Клієнт (частина назви): ");
        String client = sc.nextLine();
        List<Project> list = projectDAO.findByClient(client);
        System.out.println("\n🔍 Результати пошуку:");
        for (Project p : list) {
            System.out.println(p);
        }
    }

    private static void viewTasksForProject() throws SQLException {
        System.out.print("ID проекту: ");
        int projectId = Integer.parseInt(sc.nextLine());
        System.out.print("Фільтр за статусом (або Enter): ");
        String status = sc.nextLine().trim();

        List<Task> list = taskDAO.findByProject(projectId, status.isEmpty() ? null : status);
        System.out.println("\nЗавдання проекту #" + projectId + ":");
        for (Task t : list) {
            System.out.println(t);
        }
    }

    private static void calculateProjectCost() throws SQLException {
        System.out.print("ID проекту: ");
        int projectId = Integer.parseInt(sc.nextLine());
        double total = taskDAO.sumCostsForProject(projectId);
        System.out.printf("\nЗагальна вартість проекту #%d: %.2f грн\n", projectId, total);
    }

    private static void listMaterials() throws SQLException {
        System.out.print("Пошук за назвою (або Enter): ");
        String name = sc.nextLine().trim();
        List<Material> list = materialDAO.findAll(name.isEmpty() ? null : name);
        System.out.println("\nСписок матеріалів:");
        for (Material m : list) {
            System.out.println(m);
        }
    }

    private static void listEmployees() throws SQLException {
        List<Employee> list = employeeDAO.findAll();
        System.out.println("\nСписок працівників:");
        for (Employee e : list) {
            System.out.println(e);
        }
    }

    private static void listSuppliers() throws SQLException {
        List<Supplier> list = supplierDAO.findAll();
        System.out.println("\nСписок постачальників:");
        for (Supplier s : list) {
            System.out.println(s);
        }
    }
}

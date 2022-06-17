package groupk.shared.PresentationLayer;

import groupk.shared.PresentationLayer.EmployeesLogistics.MainEmployeesAndDelivery;
import groupk.shared.PresentationLayer.Inventory.InventoryPresentationFacade;
import groupk.shared.PresentationLayer.Suppliers.SupplierPresentationFacade;
import groupk.shared.PresentationLayer.Suppliers.UserInput;
import groupk.shared.PresentationLayer.Suppliers.UserOutput;
import groupk.inventory_suppliers.SchemaInit;
import groupk.inventory_suppliers.dataLayer.dao.PersistenceController;
import groupk.shared.service.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;


public class App {
    public final PersistenceController dal;
    public final SupplierPresentationFacade supplierPresentation;
    public final InventoryPresentationFacade inventoryPresentationFacade;
    public final Connection conn;
    public final Service service;

    public App(String dbPath, boolean tests) {
        boolean isNew = !new java.io.File(dbPath).exists();
        boolean shouldLoadExample = false;
        conn = connect(dbPath);
        SchemaInit.init(conn);
        if (isNew && !tests) {
            UserOutput.getInstance().println(
                    "You don't have a previous database file stored, so we'll create a new one for you " +
                            "from scratch.");
            shouldLoadExample = UserInput.getInstance().nextBoolean("Would you like to start with some example data?");
        }
        dal =  new PersistenceController(conn);
        service = new Service(dal);
        inventoryPresentationFacade = new InventoryPresentationFacade(service);
        supplierPresentation = new SupplierPresentationFacade(service);
        if (shouldLoadExample) {
            ExampleData.loadExampleData(service);
        }
    }

    public void main() {
        int in = 0;
        while (true) {
            in = UserInput.getInstance().nextInt("Which module do you need?\n" +
                    "1. Supplier module\n" +
                    "2. Inventory module\n" +
                    "3. Employees and Logistics modules\n" +
                    "4. Exit\n");
            switch (in) {
                case (1): {
                    supplierPresentation.startSupplierMenu();
                    break;
                }
                case (2): {
                    startInventoryMenu();
                    break;
                }
                case (3) : {
                    MainEmployeesAndDelivery.mainEmployeesAndDelivery(service, conn);
                    break;
                }
                case (4): {
                    UserOutput.getInstance().println("Goodbye!");
                    return;
                }
                default:
                    UserOutput.getInstance().println("Please try again.");
            }

        }
    }


    private void startInventoryMenu() {
        Scanner scan = new Scanner(System.in);
        String input = "";
        do {
            input = scan.nextLine();
            inventoryPresentationFacade.execute(input);
        }
        while (!input.equals("exit"));
        System.out.println("thank you");
    }


    public void disconnect() {
        try {
            conn.close();
        } catch (SQLException throwables) {
            throw new RuntimeException(throwables);
        }
    }


    private Connection connect(String dbPath) {
        try {
            return DriverManager.getConnection("jdbc:sqlite:"+dbPath);
        } catch (SQLException throwables) {
            throw new RuntimeException(throwables);
        }
    }

}

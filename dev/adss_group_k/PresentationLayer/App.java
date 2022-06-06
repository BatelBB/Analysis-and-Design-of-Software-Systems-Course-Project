package adss_group_k.PresentationLayer;

import adss_group_k.BusinessLayer.Inventory.Controllers.CategoryController;
import adss_group_k.BusinessLayer.Inventory.Controllers.ProductController;
import adss_group_k.BusinessLayer.Inventory.Service.CategoryService;
import adss_group_k.BusinessLayer.Inventory.Service.ProductService;
import adss_group_k.BusinessLayer.Inventory.Service.ReportService;
import adss_group_k.BusinessLayer.Inventory.Service.Service;
import adss_group_k.BusinessLayer.Suppliers.Service.ISupplierService;
import adss_group_k.BusinessLayer.Suppliers.Service.SupplierService;
import adss_group_k.PresentationLayer.Inventory.InventoryPresentationFacade;
import adss_group_k.PresentationLayer.Suppliers.SupplierPresentationFacade;
import adss_group_k.PresentationLayer.Suppliers.UserInput;
import adss_group_k.PresentationLayer.Suppliers.UserOutput;
import adss_group_k.SchemaInit;
import adss_group_k.dataLayer.dao.PersistenceController;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;


public class App {
    private final PersistenceController dal;
    private final ISupplierService supplierService;
    private final SupplierPresentationFacade supplierPresentation;
    private final InventoryPresentationFacade inventoryPresentationFacade;
    private final Service inventoryService;

    public App(String dbPath) {
        boolean isNew = !new java.io.File(dbPath).exists();
        boolean shouldLoadExample = false;

        AppContainer ioc = new AppContainer(dbPath);
        Connection appConnection = ioc.get(Connection.class);

        if (isNew) {
            SchemaInit.init(appConnection);
            UserOutput.getInstance().println(
                    "You don't have a previous database file stored, so we'll create a new one for you " +
                            "from scratch.");
            shouldLoadExample = UserInput.getInstance().nextBoolean("Would you like to start with some example data?");
        }

        dal = ioc.get(PersistenceController.class);
        inventoryPresentationFacade = ioc.get(InventoryPresentationFacade.class);
        supplierPresentation = ioc.get(SupplierPresentationFacade.class);
        inventoryService = ioc.get(Service.class);
        supplierService = ioc.get(ISupplierService.class);
        if (shouldLoadExample) {
            ExampleData.loadExampleData(inventoryService, supplierService);
        }
    }

    public void main() {
        int in = 0;
        while (true) {
            in = UserInput.getInstance().nextInt("Which module do you need?\n" +
                    "1. Supplier module\n" +
                    "2. Inventory module\n" +
                    "3. Exit\n");
            switch (in) {
                case (1): {
                    supplierPresentation.startSupplierMenu();
                    break;
                }
                case (2): {
                    startInventoryMenu();
                    break;
                }
                case (3): {
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
}

package adss_group_k.Tests.Inventory;

import adss_group_k.BusinessLayer.Inventory.Service.Objects.*;
import adss_group_k.BusinessLayer.Suppliers.Service.ISupplierService;
import adss_group_k.BusinessLayer.Suppliers.Service.SupplierService;
import adss_group_k.SchemaInit;
import adss_group_k.dataLayer.dao.PersistenceController;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class ServiceTest {
    private Service service;
    private ISupplierService supplierService;

    private PersistenceController pc;

    @BeforeEach
    void setService() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite::memory:");
            SchemaInit.init(conn);
        } catch (SQLException throwables) {
            throw new RuntimeException(throwables);
        }
        pc = new PersistenceController(conn);
        supplierService = new SupplierService(pc);
        service = new Service(supplierService, pc);
    }

    void restartService(){
        service.restart();
    }

    @Test
    void addCategory() {
        try {
            assertTrue(service.getCategoriesNames().data.isEmpty());
            service.addCategory("Dairy Products");
            assertTrue(service.getCategoriesNames().data.contains("Dairy Products"));
            service.addCategory("Dairy Products");
        } catch (Exception e) {
            assertEquals(e.getMessage(), "The categories already exists in the system");
        }
    }

    @Test
    void removeCategory() {
        try {
            service.addCategory("Dairy Products");
            service.removeCategory("Dairy Products");
            assertFalse(service.getCategoriesNames().data.contains("Dairy Products"));
            service.removeCategory("Dairy Products");
        } catch (Exception e) {
            assertEquals(e.getMessage(), "Category doesn't exists");
        }
    }

    @Test
    void getCategory() {
        try {
            service.addCategory("Dairy Products");
            Category category = service.getCategory("Dairy Products").data;
            assertEquals(category.getName(), "Dairy Products");
            service.removeCategory("Dairy Products");
            service.getCategory("Dairy Products");
        } catch (Exception e) {
            assertEquals(e.getMessage(), "Category doesn't exists");
        }
    }

    @Test
    void addProduct() {
        try {
            assertTrue(service.getProductNames().data.isEmpty());
            initCategories();
            service.addProduct("Milk", "Tnova", 4, 5.9f, 350, 6,
                    "Dairy Products","Milks","Cow Milk");
            assertTrue(service.getProductNames().data.contains("Milk"));
            service.addProduct("", "Tnova", 4, 5.9f, 350, 6,
                    "Dairy Products","Milks","Cow Milk");
        } catch (Exception e) {
            assertEquals(e.getMessage(), "product name empty");
            restartService();
        }
    }

    private void initCategories() {
        service.addCategory("Dairy Products");
        service.addSubCategory("Dairy Products", "Milks");
        service.addSubSubCategory("Dairy Products", "Milks", "Cow Milk");
    }

    @Test
    void removeProduct() {
        try {
            initCategories();
            String productName = service.addProduct("Milk", "Tnova", 4, 5.9f,
                    350, 6, "Dairy Products","Milks","Cow Milk")
                    .data.getName();
            assertTrue(service.getProductNames().data.contains("Milk"));
            service.removeProduct(1);
            assertFalse(service.getProductNames().data.contains("Milk"));
            service.removeProduct(0);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "product id does not exist");
            restartService();
        }
    }

    @Test
    void removeReport() {
            ResponseT<Report> missingReport = service.createMissingReport("MissingReport", "Michel");
            int reportId = missingReport.data.getId();
            assertTrue(service.getReportListIds().data.contains(reportId));
            assertTrue(service.removeReport(reportId).success);
            assertFalse(service.getProductNames().data.contains(reportId));
            assertFalse(service.removeProduct(reportId).success);
    }

    @Test
    void getReport() {
        try {
            Integer id = service.createMissingReport("MissingReport", "Michel").data.getId();
            Report report = service.getReport(id).data;
            assertEquals(report.getName(), "MissingReport");
            service.removeReport(id);
            service.getReport(id);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "Report id doesn't exists");
        }
    }

    @Test
    void createMissingReport() {
        try {
            service.createMissingReport("MissingReport", "Michel");
            
            assertTrue(service.getReportListIds().data.contains(1));
            service.createMissingReport("MissingReport", "Michel");
            service.removeReport(1);
            service.createMissingReport("MissingReport", "Michel");
        } catch (Exception e) {
            assertEquals(e.getMessage(), "The ReportId already exists in the system");
            
        }
    }

    @Test
    void createExpiredReport() {
        try {
            Integer id = service.createExpiredReport("ExpiredReport", "Michel").data.getId();
            assertTrue(service.getReportListIds().data.contains(id));
            service.removeReport(id);
            service.createExpiredReport("ExpiredReport", "Michel");
        } catch (Exception e) {
            assertEquals(e.getMessage(), "The ReportId already exists in the system");
            
        }
    }

    @Test
    void createBySupplierReport() {
        try {
            service.createBySupplierReport("BySupplierReport", "Michel", 0);
            
            assertTrue(service.getReportListIds().data.contains(1));
            service.createBySupplierReport("MissingReport", "Michel", 0);
            service.removeReport(1);
            service.createBySupplierReport("ExpiredReport", "Michel", 0);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "The ReportId already exists in the system");
            
        }
    }
}
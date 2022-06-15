package groupk.shared.service.Inventory;

import groupk.shared.business.CategoryController;
import groupk.shared.business.ProductController;
import groupk.shared.business.ReportController;
import groupk.shared.service.Inventory.Objects.Report;
import groupk.inventory_suppliers.dataLayer.dao.PersistenceController;
import groupk.shared.service.ServiceBase;

import java.util.List;

public class ReportService extends ServiceBase {
    private final ReportController report_controller;

    public ReportService(PersistenceController pc, CategoryController catCont, ProductController prodCont) {
        report_controller = new ReportController(pc, prodCont);
    }

    public ResponseT<List<Integer>> getReportListNames() {
        return responseFor(report_controller::getReportListNames);
    }

    public Response removeReport(int id) {
        return responseForVoid(() -> report_controller.removeReport(id));
    }

    public ResponseT<Report> getReport(int id) {
        return responseFor(() -> new Report(report_controller.getReportForProduct(id)));
    }

    public ResponseT<Report> createMissingReport(String name, String report_producer) {
        return responseFor(() -> new Report(report_controller.createMissingReport(name, report_producer)));
    }

    public ResponseT<Report> createExpiredReport(String name, String report_producer) {
        return responseFor(() -> new Report(report_controller.createExpiredReport(name, report_producer)));
    }

    public ResponseT<Report> createSurplusesReport(String name, String report_producer) {
        return responseFor(() -> new Report(report_controller.createSurplusesReport(name, report_producer)));
    }

    public ResponseT<Report> createDefectiveReport(String name, String report_producer) {
        return responseFor(() -> new Report(report_controller.createDefectiveReport(name, report_producer)));
    }

    public ResponseT<Report> createBySupplierReport(String name, String report_producer, int suppName) {
        return responseFor(() -> new Report(report_controller.createBySupplierReport(name, report_producer, suppName)));
    }

    public ResponseT<Report> createByProductReport(String name, String report_producer, String proName) {
        return responseFor(() -> new Report(report_controller.createByProductReport(name, report_producer, proName)));
    }

    public ResponseT<Report> createByCategoryReport(String name, String report_producer, String CatName, String subCatName, String subSubCatName) {
        return responseFor(() -> new Report(
                report_controller.createByCategoryReport(name, report_producer,
                        CatName, subCatName, subSubCatName)));
    }
}
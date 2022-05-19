package groupk.shared.business;

import groupk.shared.service.Response;
import groupk.shared.service.dto.*;
import groupk.workers.data.DalController;

import java.time.LocalDateTime;
import java.util.*;

public class Facade {
    EmployeesController employees;
    LogisticsController logistics;

    public Facade() {
        employees = new EmployeesController();
        logistics = new LogisticsController();
    }

    //fot test use
    public void deleteEmployeeDB(){
        employees.deleteEmployeeDB();
    }

    public void deleteLogisticsDB() {
        logistics.deleteDB();
    }

    public void loadEmployeeDB(){ employees.loadEmployeeDB();}

    // Previously addEmployee.
    public Response<Employee> createEmployee(
            String name,
            String id,
            String bank,
            int bankID,
            int bankBranch,
            Calendar employmentStart,
            int salaryPerHour,
            int sickDaysUsed,
            int vacationDaysUsed,
            Employee.Role role,
            Set<Employee.ShiftDateTime> shiftPreferences) {
        return employees.createEmployee(name, id, bank, bankID, bankBranch, employmentStart, salaryPerHour, sickDaysUsed, vacationDaysUsed, role, shiftPreferences);
    }

    // Previously addShift.
    public Response<Shift> createShift(String subjectID, Calendar date, Shift.Type type,
                                    LinkedList<Employee> staff,
                                    HashMap<Employee.Role, Integer> requiredStaff){
        return employees.createShift(subjectID, date, type, staff, requiredStaff);
    }

    public Response<Employee> readEmployee(String subjectID, String employeeID) {
        return employees.readEmployee(subjectID, employeeID);
    }

    public Response<Shift> readShift(String subjectID, Calendar date , Shift.Type type) {
        return employees.readShift(subjectID, date, type);
    }

    public Response<Employee> deleteEmployee(String subjectID, String employeeID) {
        Response<Employee> subject = employees.readEmployee(subjectID, subjectID);
        if (subject.isError()) {
            return subject;
        }
        if (subject.getValue().role == Employee.Role.TruckingManger) {
            Response<List<Delivery>> futureDeliveries = logistics.listFutureDeliveries(Integer.parseInt(subjectID));
            if (futureDeliveries.isError() | futureDeliveries.getValue() == null)
                return new Response<>("Oops, we are currently unable to find data on this trucking manager's leads.\n"+
                        "Therefore, the operation cannot be performed at this time.");
            if (futureDeliveries.getValue().size() > 0)
                return new Response<>("Oops, the truck manager has future truckings. Before you address the issue, you will not be able to take action");
        }
        else if (subject.getValue().role == Employee.Role.Driver) {
            Response<List<Delivery>> futureDeliveries = logistics.listFutureDeliveriesByDriver(Integer.parseInt(subjectID));
            if (futureDeliveries.isError() | futureDeliveries.getValue() == null)
                return new Response<>("Oops, we are currently unable to find data on this trucking manager's leads.\n"+
                        "Therefore, the operation cannot be performed at this time.");
            if (futureDeliveries.getValue().size() > 0)
                return new Response<>("Oops, the truck manager has future truckings. Before you address the issue, you will not be able to take action");
        }
        return employees.deleteEmployee(subjectID, employeeID);
    }

    public Response<List<Employee>> listEmployees(String subjectID) {
        return employees.listEmployees(subjectID);
    }

    public Response<Shift> addEmployeeToShift(String subjectID, Calendar date, Shift.Type type, String employeeID) {
        return employees.addEmployeeToShift(subjectID, date, type, employeeID);
    }

    public Response<Shift> removeEmployeeFromShift(String subjectID, Calendar date, Shift.Type type, String employeeID) {
        //TODO: check availability of worker. need to know shift hours for that
        return employees.removeEmployeeFromShift(subjectID, date, type, employeeID);
    }

    public Response<Employee> updateEmployee(String subjectID, Employee changed) {
        return employees.updateEmployee(subjectID, changed);
    }

    public Response<Employee> addEmployeeShiftPreference(String subjectID, String employeeID, Employee.ShiftDateTime shift) {
        return employees.addEmployeeShiftPreference(subjectID, employeeID, shift);
    }

    public Response<Employee> setEmployeeShiftsPreference(String subjectID, String employeeID, Set<Employee.ShiftDateTime> shiftPreferences) {
        return employees.setEmployeeShiftsPreference(subjectID, employeeID, shiftPreferences);
    }

    public Response<Employee> deleteEmployeeShiftPreference(String subjectID, String employeeID, Employee.ShiftDateTime shift){
        return employees.deleteEmployeeShiftPreference(subjectID, employeeID, shift);
    }

    public Response<List<Shift>> listShifts(String subjectID) {
        return employees.listShifts(subjectID);
    }

    public Response<List<Shift>> listEmployeeShifts(String subjectID, String employeeID) {
        return employees.listEmployeeShifts(subjectID, employeeID);
    }

    public Response<Integer> numOfEmployeeShifts(String subjectID, String employeeID) {
        return employees.numOfEmployeeShifts(subjectID, employeeID);
    }

    public Response<Shift> updateRequiredRoleInShift(String subjectId, Calendar date, Shift.Type type, Employee.Role role, int count) {
        return employees.updateRequiredRoleInShift(subjectId, date, type, role, count);
    }

    public Response<Shift> setRequiredStaffInShift(String subjectId, Calendar date, Shift.Type type, HashMap<Employee.Role, Integer> requiredStaff) {
        return employees.setRequiredStaffInShift(subjectId, date, type, requiredStaff);
    }

    public Response<List<Employee>> whoCanWorkWithRole(String subjectId, Employee.ShiftDateTime day, Employee.Role role) {
        return employees.whoCanWorkWithRole(subjectId, day, role);
    }

    public Response<List<Employee>> whoCanWork(String subjectId, Employee.ShiftDateTime day) {
        return employees.whoCanWork(subjectId, day);
    }

    // Previously removeTrucking
    public Response<Boolean> deleteDelivery(String subjectID, int deliveryID) {
        Response<Employee> subject = employees.readEmployee(subjectID, subjectID);
        if (subject.isError()) {
            return new Response<>(subject.getErrorMessage());
        }
        if (subject.getValue().role != Employee.Role.TruckingManger) {
            return new Response<>("Subject must be of trucking manager role.");
        }
        return logistics.deleteDelivery(Integer.parseInt(subjectID), deliveryID);
    }

    // Previously printBoard
    public Response<List<Delivery>> listDeliveries(String subjectID) {
        Response<Employee> subject = employees.readEmployee(subjectID, subjectID);
        if (subject.isError()) {
            return new Response<>(subject.getErrorMessage());
        }
        if (subject.getValue().role == Employee.Role.TruckingManger) {
            return logistics.listDeliveries(Integer.parseInt(subjectID));
        }
        if (subject.getValue().role == Employee.Role.Driver) {
            return logistics.listDeliveriesByDriver(Integer.parseInt(subjectID));
        }
        else
            return new Response<>("You are not authorized to perform this operation");
    }

    public Response<List<String>> listVehicles(String subjectID) {
        Response<Employee> subject = employees.readEmployee(subjectID, subjectID);
        if (subject.isError()) {
            return new Response<>(subject.getErrorMessage());
        }
        if (subject.getValue().role != Employee.Role.TruckingManger) {
            return new Response<>("You are not authorized to perform this operation");
        }
        return logistics.listVehicles();
    }

    public Response<List<String>[]> createDelivery(String subjectID, String registrationPlateOfVehicle, LocalDateTime date, String driverUsername, List<Site> sources, List<Site> destinations, List<Product> products, long hours, long minutes) {
        Response<Employee> truckManager = employees.readEmployee(subjectID, subjectID);
        if (truckManager.isError()) {
            return new Response<>(truckManager.getErrorMessage());
        }
        if (truckManager.getValue().role != Employee.Role.TruckingManger) {
            return new Response<>("You are not authorized to perform this operation");
        }
        Response<Employee> driver = employees.readEmployee(subjectID, subjectID);
        if (driver.isError()) {
            return new Response<>(driver.getErrorMessage());
        }
        if (driver.getValue().role != Employee.Role.Driver) {
            return new Response<>("There is no driver with id: " + driverUsername);
        }
        //TODO: check if there is storekeeper in shift
        return logistics.createDelivery(Integer.parseInt(subjectID), registrationPlateOfVehicle, date, Integer.parseInt(driverUsername), sources, destinations, products, hours, minutes);
    }

    public Response<List<Delivery>> listDeliveriesWithVehicle(String subjectID, String registration) {
        Response<Employee> subject = employees.readEmployee(subjectID, subjectID);
        if (subject.isError()) {
            return new Response<>(subject.getErrorMessage());
        }
        if (subject.getValue().role != Employee.Role.TruckingManger) {
            return new Response<>("You are not authorized to perform this operation");
        }
        return logistics.listDeliveriesWithVehicle(subjectID);
    }

    public Response<Boolean> createVehicle(String subjectID, String license, String registrationPlate, String model, int weight, int maxWeight) {
        Response<Employee> subject = employees.readEmployee(subjectID, subjectID);
        if (subject.isError()) {
            return new Response<>(subject.getErrorMessage());
        }
        if (subject.getValue().role != Employee.Role.TruckingManger) {
            return new Response<>("You are not authorized to perform this operation");
        }
        return logistics.createVehicle(license, registrationPlate,model, weight, maxWeight);
    }

    public Response<Boolean> setWeightForDelivery(String subjectID, int deliveryID, int weight) {
        Response<Employee> subject = employees.readEmployee(subjectID, subjectID);
        if (subject.isError()) {
            return new Response<>(subject.getErrorMessage());
        }
        if (subject.getValue().role != Employee.Role.Driver) {
            return new Response<>("You are not authorized to perform this operation");
        }
        return logistics.setWeightForDelivery(Integer.parseInt(subjectID), deliveryID, weight);
    }

    public Response<Boolean> addProductsToTrucking(String subjectID, int truckingID, Product products) {
        Response<Employee> subject = employees.readEmployee(subjectID, subjectID);
        if (subject.isError()) {
            return new Response<>(subject.getErrorMessage());
        }
        if (subject.getValue().role != Employee.Role.TruckingManger) {
            return new Response<>("You are not authorized to perform this operation");
        }
        return logistics.addProductsToTrucking(Integer.parseInt(subjectID), truckingID, products);
    }

    public Response<List<String>> updateSources(String subjectID, int truckingID, List<Site> sources) {
        Response<Employee> subject = employees.readEmployee(subjectID, subjectID);
        if (subject.isError()) {
            return new Response<>(subject.getErrorMessage());
        }
        if (subject.getValue().role != Employee.Role.TruckingManger) {
            return new Response<>("You are not authorized to perform this operation");
        }
        return logistics.updateSources(Integer.parseInt(subjectID), truckingID, sources);
    }

    public Response<List<String>> updateDestination(String subjectID, int truckingID, List<Site> destinations) {
        Response<Employee> subject = employees.readEmployee(subjectID, subjectID);
        if (subject.isError()) {
            return new Response<>(subject.getErrorMessage());
        }
        if (subject.getValue().role != Employee.Role.TruckingManger) {
            return new Response<>("You are not authorized to perform this operation");
        }
        return logistics.updateDestination(Integer.parseInt(subjectID), truckingID, destinations);
    }

    public Response<List<String>> addSources(String subjectID, int truckingID, List<Site> sources) {
        Response<Employee> subject = employees.readEmployee(subjectID, subjectID);
        if (subject.isError()) {
            return new Response<>(subject.getErrorMessage());
        }
        if (subject.getValue().role != Employee.Role.TruckingManger) {
            return new Response<>("You are not authorized to perform this operation");
        }
        return logistics.addSources(Integer.parseInt(subjectID), truckingID, sources);
    }

    public Response<List<String>> addDestination(String subjectID, int truckingID, List<Site> destinations) {
        Response<Employee> subject = employees.readEmployee(subjectID, subjectID);
        if (subject.isError()) {
            return new Response<>(subject.getErrorMessage());
        }
        if (subject.getValue().role != Employee.Role.TruckingManger) {
            return new Response<>("You are not authorized to perform this operation");
        }
        return logistics.addDestination(Integer.parseInt(subjectID), truckingID, destinations);
    }

    public Response<Boolean> moveProducts(String subjectID, int truckingID, Product product) {
        Response<Employee> subject = employees.readEmployee(subjectID, subjectID);
        if (subject.isError()) {
            return new Response<>(subject.getErrorMessage());
        }
        if (subject.getValue().role != Employee.Role.TruckingManger) {
            return new Response<>("You are not authorized to perform this operation");
        }
        return logistics.moveProducts(Integer.parseInt(subjectID), truckingID, product);
    }

    public Response<Boolean> updateVehicleOnTrucking(String subjectID, int truckingID, String registrationPlate) {
        Response<Employee> subject = employees.readEmployee(subjectID, subjectID);
        if (subject.isError()) {
            return new Response<>(subject.getErrorMessage());
        }
        if (subject.getValue().role != Employee.Role.TruckingManger) {
            return new Response<>("You are not authorized to perform this operation");
        }
        return logistics.updateVehicleOnTrucking(Integer.parseInt(subjectID), truckingID, registrationPlate);
    }

    public Response<Boolean> updateDriverOnTrucking(String subjectID, int truckingID, String driverUsername) {
        Response<Employee> subject = employees.readEmployee(subjectID, subjectID);
        if (subject.isError()) {
            return new Response<>(subject.getErrorMessage());
        }
        if (subject.getValue().role != Employee.Role.TruckingManger) {
            return new Response<>("You are not authorized to perform this operation");
        }
        Response<Employee> driver = employees.readEmployee(subjectID, subjectID);
        if (driver.isError()) {
            return new Response<>(driver.getErrorMessage());
        }
        if (driver.getValue().role != Employee.Role.Driver) {
            return new Response<>("There is no driver with id: " + driverUsername);
        }
        return logistics.updateDriverOnTrucking(Integer.parseInt(subjectID), truckingID, Integer.parseInt(driverUsername));
    }

    public Response<Boolean> updateDateOnTrucking(String subjectID, int truckingID, LocalDateTime newDate) {
        Response<Employee> subject = employees.readEmployee(subjectID, subjectID);
        if (subject.isError()) {
            return new Response<>(subject.getErrorMessage());
        }
        if (subject.getValue().role != Employee.Role.TruckingManger) {
            return new Response<>("You are not authorized to perform this operation");
        }
        return logistics.updateDateOnTrucking(Integer.parseInt(subjectID), truckingID, newDate);
    }

    public Response<Boolean> addLicenseForDriver(String subjectID, String license) {
        Response<Employee> subject = employees.readEmployee(subjectID, subjectID);
        if (subject.isError()) {
            return new Response<>(subject.getErrorMessage());
        }
        if (subject.getValue().role != Employee.Role.Driver) {
            return new Response<>("You are not authorized to perform this operation");
        }
        return logistics.addLicenseForDriver(Integer.parseInt(subjectID), license);
    }

    public Response<List<String>> getDriverLicenses(String subjectID) {
        Response<Employee> subject = employees.readEmployee(subjectID, subjectID);
        if (subject.isError()) {
            return new Response<>(subject.getErrorMessage());
        }
        if (subject.getValue().role != Employee.Role.Driver) {
            return new Response<>("You are not authorized to perform this operation");
        }
        return logistics.getDriverLicenses(Integer.parseInt(subjectID));
    }

    public Response<String[]> getLicensesList() {
        return logistics.getLicensesList();
    }

    public Response<String[]> getProductsSKUList() {
        return logistics.getProductsSKUList();
    }

    public Response<String[]> getAreasList() {
        return logistics.getAreasList();
    }

}

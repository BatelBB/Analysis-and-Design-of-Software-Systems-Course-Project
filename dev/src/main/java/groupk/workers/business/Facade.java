package groupk.workers.business;

import groupk.shared.service.Response;
import groupk.workers.data.DalController;
import groupk.shared.service.dto.Employee;
import groupk.shared.service.dto.Shift;

import java.util.*;
import java.util.stream.Collectors;

public class Facade {
    private EmployeeController employees;
    private ShiftController shifts;
    private DalController dalColntroller;

    public Facade() {
        dalColntroller = new DalController();
        employees = new EmployeeController(dalColntroller);
        shifts = new ShiftController(dalColntroller);
    }

    //for test use
    public void deleteDB(){
        dalColntroller.deleteDataBase();
        dalColntroller = new DalController();
    }

    public void loadDB(){
        dalColntroller.loadDataFromDB();
    }

    public Employee addEmployee(
            String name,
            String id,
            String bank,
            int bankID,
            int bankBranch,
            Calendar employmentStart,
            int salaryPerHour,
            int sickDaysUsed,
            int vacationDaysUsed,
            groupk.shared.service.dto.Employee.Role role,
            Set<groupk.shared.service.dto.Employee.ShiftDateTime> shiftPreferences) {
        groupk.workers.data.Employee created = employees.create(
                name, id, bank,
                bankID, bankBranch, employmentStart,
                salaryPerHour, sickDaysUsed, vacationDaysUsed,
                serviceRoleToData(role),
                servicePreferredShiftsToData(shiftPreferences)
                );
        return dataEmployeeToService(created);
    }

    public Shift addShift(String subjectID, Calendar date, Shift.Type type,
                          LinkedList<Employee> staff,
                          HashMap<Employee.Role, Integer> requiredStaff){
        if (employees.isFromHumanResources(subjectID)) {
            LinkedList<groupk.workers.data.Employee> staffData = new LinkedList<>();
            for(Employee e: staff){
                groupk.workers.data.Employee added = serviceEmployeeToData(e);
                if (!added.getAvailableShifts().contains(groupk.workers.data.Employee.toShiftDateTime(date, type == Shift.Type.Evening))) {
                    throw new IllegalArgumentException("Employee must be able to work at day of week and time.");
                }
                staffData.add(added);
            }
            groupk.workers.data.Shift created = new groupk.workers.data.Shift(date, serviceTypeToData(type), staffData ,
                    serviceRequiredRoleInShiftToData(requiredStaff));
            return dataShiftToService(shifts.addShifts(created));
        } else {
            throw new IllegalArgumentException("Subject must be authorized to add shift.");
        }
    }

    public Employee readEmployee(String subjectID, String employeeID) {
        if (subjectID.equals(employeeID) || employees.isFromHumanResources(subjectID)) {
            return dataEmployeeToService(employees.read(employeeID));
        } else {
            throw new IllegalArgumentException("Subject must be authorized to read employees.");
        }
    }

    public Shift readShift(String subjectID, Calendar date ,Shift.Type type) {
        if (employees.isFromHumanResources(subjectID)) {
            return dataShiftToService(shifts.getShift(date, serviceTypeToData(type)));
        }
        else {
            throw new IllegalArgumentException("Subject must be authorized to read shifts.");
        }
    }

    public Employee deleteEmployee(String subjectID, String employeeID) {
        if (employees.isFromHumanResources(subjectID)) {
            return dataEmployeeToService(employees.delete(employeeID));
        } else {
            throw new IllegalArgumentException("Subject must be authorized to delete employees.");
        }
    }

    public List<Employee> listEmployees(String subjectID) {
        if (employees.isFromHumanResources(subjectID)) {
            return employees.list().stream()
                    .map(Facade::dataEmployeeToService)
                    .collect(Collectors.toList());
        } else {
            throw new IllegalArgumentException("Subject must be authorized to read employees.");
        }
    }

    public Shift addEmployeeToShift(String subjectID, Calendar date, Shift.Type type, String employeeID) {
        if (employees.isFromHumanResources(subjectID)) {
            groupk.workers.data.Shift shift = shifts.getShift(date, serviceTypeToData(type));
            groupk.workers.data.Employee added = employees.read(employeeID);
            if (shift.isEmployeeWorking(employeeID)) {
                throw new IllegalArgumentException("Employee already working in this shift.");
            }
            if (!added.getAvailableShifts().contains(groupk.workers.data.Employee.toShiftDateTime(date, type == Shift.Type.Evening))) {
                throw new IllegalArgumentException("Employee must be able to work at day of week and time.");
            }
            return dataShiftToService(shift.addEmployee(employees.getEmployee(employeeID)));
        } else {
            throw new IllegalArgumentException("Subject must be authorized to add employees to shifts.");
        }
    }

    public Shift removeEmployeeFromShift(String subjectID, Calendar date, Shift.Type type, String employeeID) {
        if (employees.isFromHumanResources(subjectID)) {
            groupk.workers.data.Shift shift = shifts.getShift(date, serviceTypeToData(type));
            if (!shift.isEmployeeWorking(employeeID))
                throw new IllegalArgumentException("Employee is not working in this shift.");
            else {
                groupk.workers.data.Employee employee = employees.getEmployee(employeeID);
                int numOfRoles = -1 ; //without this employee
                for(groupk.workers.data.Employee e :shift.getStaff()) {
                    if(e.getRole().equals(employee.getRole()))
                        numOfRoles++;
                }
                if(numOfRoles < shift.getRequiredStaff().get(employee.getRole()))
                    throw new IllegalArgumentException("There are not enough workers with this employee's role to run this shift without him.");
                return dataShiftToService(shift.removeEmployee(employee));
            }
        } else {
            throw new IllegalArgumentException("Subject must be authorized to remove employees from shifts.");
        }
    }

    public Employee updateEmployee(String subjectID, Employee changed) {
        if (employees.isFromHumanResources(subjectID)) {
            return dataEmployeeToService(
                    employees.update(
                            changed.name,
                            changed.id,
                            changed.bank,
                            changed.bankID,
                            changed.bankBranch,
                            changed.employmentStart,
                            changed.salaryPerHour,
                            changed.sickDaysUsed,
                            changed.vacationDaysUsed,
                            serviceRoleToData(changed.role)
                    )
            );
        } else {
            throw new IllegalArgumentException("Subject must be authorized to update employees.");
        }
    }

    public Employee addEmployeeShiftPreference(String subjectID, String employeeID, Employee.ShiftDateTime shift) {
        if(subjectID.equals(employeeID))
            return dataEmployeeToService(employees.addEmployeeShiftPreference(employeeID, serviceWeeklyShiftToData(shift)));
        else
            throw new IllegalArgumentException("Employee can add only to himself shifts preferences.");
    }

    public Employee setEmployeeShiftsPreference(String subjectID, String employeeID, Set<Employee.ShiftDateTime> shiftPreferences) {
        if(subjectID.equals(employeeID))
            return dataEmployeeToService(employees.setEmployeeShiftsPreference(employeeID, servicePreferredShiftsToData(shiftPreferences)));
        else
            throw new IllegalArgumentException("Employee can add only to himself shifts preferences.");
    }

    public Employee deleteEmployeeShiftPreference(String subjectID, String employeeID, Employee.ShiftDateTime shift){
        if(subjectID.equals(employeeID))
            return dataEmployeeToService(employees.deleteEmployeeShiftPreference(employeeID, serviceWeeklyShiftToData(shift)));
        else
            throw new IllegalArgumentException("Employee can delete only to himself shifts preferences.");
    }

    public List<Shift> listShifts(String subjectID) {
        if(employees.isFromHumanResources(subjectID)) {
            List<groupk.workers.data.Shift> dataShifts = shifts.listShifts();
            List<Shift> dtoShifts = new LinkedList<>();
            for (groupk.workers.data.Shift s : dataShifts)
                dtoShifts.add(dataShiftToService(s));
            return dtoShifts;
        }
        throw new IllegalArgumentException("Subject must be authorized to get history of shifts.");
    }

    public List<Shift> listEmployeeShifts(String subjectID, String employeeID) {
        List<Shift> shifts = listShifts(subjectID);
        List<Shift> output = new ArrayList<>();
        for (Shift s: shifts) {
            List<Employee> staff = s.getStaff();
            for (Employee e: staff) {
                if(e.id.equals(employeeID))
                output.add(s);
            }
        }
        return output;
    }

    public Shift setRequiredRoleInShift(String subjectId, Calendar date, Shift.Type type, Employee.Role role, int count) {
        if(employees.isFromHumanResources(subjectId))
            return dataShiftToService(shifts.setRequiredRoleInShift(date, serviceTypeToData(type) ,serviceRoleToData(role), count));
        else
            throw new IllegalArgumentException("Subject must be authorized to set required roles in shifts.");
    }

    public Shift setRequiredStaffInShift(String subjectId, Calendar date, Shift.Type type, HashMap<Employee.Role, Integer> requiredStaff) {
        if(employees.isFromHumanResources(subjectId))
            return dataShiftToService(shifts.setRequiredStaffInShift(date, serviceTypeToData(type) ,serviceRequiredRoleInShiftToData(requiredStaff)));
        else
            throw new IllegalArgumentException("Subject must be authorized to set required roles in shifts.");
    }

    public List<Employee> whoCanWorkWithRole(String subjectId, Employee.ShiftDateTime day, Employee.Role role){
        if(employees.isFromHumanResources(subjectId)) {
            List<Employee> employees = listEmployees(subjectId);
            return employees.stream().filter(p -> p.shiftPreferences.contains(day) && p.role.equals(role)).collect(Collectors.toList());
        }
        else
            throw new IllegalArgumentException("Subject must be authorized to get list of employees.");
    }

    public List<Employee> whoCanWork(String subjectId, Employee.ShiftDateTime day) {
        if(employees.isFromHumanResources(subjectId)) {
            List<Employee> employees = listEmployees(subjectId);
            return employees.stream().filter(p -> p.shiftPreferences.contains(day)).collect(Collectors.toList());
        }
        else
            throw new IllegalArgumentException("Subject must be authorized to get list of employees.");
    }

    public int numOfShifts(String subjectId, String employeeID){
        if(employees.isFromHumanResources(subjectId)) {
            return listEmployeeShifts(subjectId, employeeID).size();
        }
        else
            throw new IllegalArgumentException("Subject must be authorized to get history of shifts.");
    }

    //private helper function


    private static groupk.workers.data.Employee.Role serviceRoleToData(groupk.shared.service.dto.Employee.Role serviceRole) {
        return groupk.workers.data.Employee.Role.values()[serviceRole.ordinal()];
    }

    private static groupk.shared.service.dto.Employee.Role dataRoleToService(groupk.workers.data.Employee.Role dataRole) {
        return groupk.shared.service.dto.Employee.Role.values()[dataRole.ordinal()];
    }

    private static Set<groupk.shared.service.dto.Employee.ShiftDateTime> dataPreferredShiftsToService
            (Set<groupk.workers.data.Employee.ShiftDateTime> dataShifts) {
        Set<groupk.shared.service.dto.Employee.ShiftDateTime> preferredShifts = new HashSet<>();
        for (groupk.workers.data.Employee.ShiftDateTime shift : dataShifts) {
            preferredShifts.add(groupk.shared.service.dto.Employee.ShiftDateTime.values()[shift.ordinal()]);
        }
        return preferredShifts;
    }

    private static Set<groupk.workers.data.Employee.ShiftDateTime> servicePreferredShiftsToData
            (Set<groupk.shared.service.dto.Employee.ShiftDateTime> dtoShifts) {
        Set<groupk.workers.data.Employee.ShiftDateTime> preferredShifts = new HashSet<>();
        for (groupk.shared.service.dto.Employee.ShiftDateTime shift : dtoShifts) {
            preferredShifts.add(groupk.workers.data.Employee.ShiftDateTime.values()[shift.ordinal()]);
        }
        return preferredShifts;
    }

    private static groupk.workers.data.Employee.ShiftDateTime serviceWeeklyShiftToData(groupk.shared.service.dto.Employee.ShiftDateTime dtoShift) {
        return groupk.workers.data.Employee.ShiftDateTime.values()[dtoShift.ordinal()];
    }

    private static groupk.shared.service.dto.Employee dataEmployeeToService(groupk.workers.data.Employee dataEmployee) {
        return new groupk.shared.service.dto.Employee(
                dataEmployee.getId(),
                dataEmployee.getName(),
                dataRoleToService(dataEmployee.getRole()),
                dataEmployee.getAccount().getBank(),
                dataEmployee.getAccount().getBankID(),
                dataEmployee.getAccount().getBankBranch(),
                dataEmployee.getConditions().getSalaryPerHour(),
                dataEmployee.getConditions().getSickDaysUsed(),
                dataEmployee.getConditions().getVacationDaysUsed(),
                dataPreferredShiftsToService(dataEmployee.getAvailableShifts()),
                dataEmployee.getConditions().getEmploymentStart()
        );
    }

    private groupk.workers.data.Employee serviceEmployeeToData(groupk.shared.service.dto.Employee serviceEmployee) {
        return employees.read(serviceEmployee.id);
    }

    private static groupk.shared.service.dto.Shift.Type dataTypeToService(groupk.workers.data.Shift.Type dataType) {
        return groupk.shared.service.dto.Shift.Type.values()[dataType.ordinal()];
    }

    private static groupk.workers.data.Shift.Type serviceTypeToData(groupk.shared.service.dto.Shift.Type dataType) {
        return groupk.workers.data.Shift.Type.values()[dataType.ordinal()];
    }


    private static groupk.shared.service.dto.Shift dataShiftToService(groupk.workers.data.Shift dataShift) {
        return new groupk.shared.service.dto.Shift(
                dataShift.getDate(),
                dataTypeToService(dataShift.getType()),
                dataShift.getStaff().stream().map(Facade::dataEmployeeToService).collect(Collectors.toList()),
                dataRequiredRoleInShiftToService(dataShift.getRequiredStaff()));
    }

    private static HashMap<groupk.shared.service.dto.Employee.Role, Integer> dataRequiredRoleInShiftToService(HashMap<groupk.workers.data.Employee.Role, Integer> staffData) {
        HashMap<groupk.shared.service.dto.Employee.Role, Integer> staffDto = new HashMap<>();
        staffData.forEach((k, v) -> staffDto.put(dataRoleToService(k), v));
        return staffDto;
    }

    private static HashMap<groupk.workers.data.Employee.Role, Integer> serviceRequiredRoleInShiftToData(HashMap<groupk.shared.service.dto.Employee.Role, Integer> staffDto) {
        HashMap<groupk.workers.data.Employee.Role, Integer> staffData = new HashMap<>();
        staffDto.forEach((k, v) -> staffData.put(serviceRoleToData(k), v));
        return staffData;
    }
}



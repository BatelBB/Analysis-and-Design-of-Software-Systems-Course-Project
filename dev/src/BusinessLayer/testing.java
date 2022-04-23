package BusinessLayer;

import static BusinessLayer.Products.Eggs_4902505139314;
import static BusinessLayer.Products.Water_7290019056966;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.*;

import ServiceLayer.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


public class testing {
    private UserController userController =  UserController.getInstance();
    private TruckManagerController truckManagerController = TruckManagerController.getInstance();
    private DriverController driverController = DriverController.getInstance();

    public testing() throws Exception {
    }

    /*

    @Test
    public void registerUsersTests() throws Exception {
        try {
            assertEquals(userController.registerUser("ido shapira", "id","ido1Ido1",Role.truckingManager, "tm1234tm"),true);
        }
        catch (Exception e) {
            assertEquals(e.getMessage(),"The minimum username length should be at least 3 characters");
        }
        try {
            assertEquals(userController.registerUser("ido shapira", "idoShapiraWrong","idoShapiraWrong",Role.truckingManager, "tm1234tm"),true);
        }
        catch (Exception e) {
            assertEquals(e.getMessage(),"The maximum username length should be up to 12 characters");
        }
        try {
            assertEquals(userController.registerUser("ido shapira", "i#d","idoWrong",Role.truckingManager, "tm1234tm"),true);
        }
        catch (Exception e) {
            assertEquals(e.getMessage(),"Username can only contain letters and numbers");
        }

        try {
            assertEquals(userController.registerUser("ido shapira", "iffd","idoWrong",Role.truckingManager, "fgt"),true);
        }
        catch (Exception e) {
            assertEquals(e.getMessage(),"Sorry, the code is not valid");
        }
        try {
            assertEquals(userController.registerUser("ido shapira", "ido1Ido1","idoWron1g",Role.truckingManager, "WRONG"),true);
        }
        catch (Exception e) {
            assertEquals(e.getMessage(),"Sorry, the code is not valid");
        }

    }

    public void registerSuccessTests() throws Exception {
        assertEquals(userController.registerUser("ido shapira", "ido1Ido1", "ido1Ido1", Role.truckingManager, "tm1234tm"), true);
        assertEquals(userController.login("ido1Ido1", "ido1Ido1"), true);
        String hashCode = userController.getRegisterCode();
        assertEquals(userController.logout(), true);
        assertEquals(userController.registerUser("rami fozis", "ramiF", "rami1Rami1", Role.driver, hashCode), true);
        assertEquals(userController.registerUser("tamir avisar", "tam1Tamir1", "tam1Tamir1", Role.driver, hashCode), true);
    }


    public void login_logout_updatePassword_addLicense_Tests() throws Exception {

        assertEquals(userController.login("tam1Tamir1","tam1Tamir1"),true);
        try {
            assertEquals(userController.updatePassword("pass"),true);
        }
        catch (Exception e) {
            assertEquals(e.getMessage(),"The minimum password length should be at least 6 characters");
        }
        assertEquals(userController.updatePassword("passIdo1"),true);
        userController.addLicense(DLicense.C);
        assertEquals(userController.logout(),true);
        assertEquals(userController.login("ramiF","rami1Rami1"),true);
        userController.addLicense(DLicense.C);
        assertEquals(userController.logout(),true);
        assertEquals(userController.login("ido1Ido1","ido1Ido1"),true);

    }

    public void createTruckingAndAddTests() throws Exception {

        userController.addVehicle(DLicense.C,"12345642","BMW",12,14);
        userController.addVehicle(DLicense.C,"45345642","volvo",12,14);
        Site s1 = new Site("mega","herzliya","0543397995","hamatganit",13,2,3,Area.center);
        Site d1 = new Site("viktory","haifa","0524321231","hayarden",100,1,6,Area.north);
        List sources = new LinkedList();
        sources.add(s1);
        List destinations = new LinkedList();
        destinations.add(d1);
        String dInStr = "2022-09-09";
        LocalDate localDate = LocalDate.parse(dInStr);
        LocalDateTime datetime = localDate.atTime(1,50,9);
        ProductForTrucking productForTrucking = new ProductForTrucking(Water_7290019056966,2);
        List prods= new LinkedList();
        prods.add(productForTrucking);
        userController.addTrucking("45345642",datetime,"tam1Tamir1", sources,destinations,prods,2,30);

    }

    private void createTrucking1 () throws Exception {
        Site s1 = new Site("tamirHouse","batYam","0543397995","tamirStr",13,2,3,Area.center);
        Site d1 = new Site("idoHouse","herzliya","0524321231","idoStr",100,1,6,Area.center);
        List sources = new LinkedList();
        sources.add(s1);
        List destinations = new LinkedList();
        destinations.add(d1);
        String dInStr = "2022-09-09";
        LocalDate localDate = LocalDate.parse(dInStr);
        LocalDateTime datetime = localDate.atTime(1,20,9);
        ProductForTrucking productForTrucking = new ProductForTrucking(Water_7290019056966,2);
        List prods= new LinkedList();
        prods.add(productForTrucking);

        try {
            userController.addTrucking("45345642",datetime,"ramiF", sources,destinations,prods,1,30);
        }
        catch (Exception e) {
            assertEquals(e.getMessage(),"Oops, there is another trucking at the same date and with the same driver");
        }

    }

    private void createTrucking2 () throws Exception {
        Site s1 = new Site("tamirHouse","batYam","0543397995","tamirStr",13,2,3,Area.center);
        Site d1 = new Site("idoHouse","herzliya","0524321231","idoStr",100,1,6,Area.center);
        List sources = new LinkedList();
        sources.add(s1);
        List destinations = new LinkedList();
        destinations.add(d1);
        String dInStr = "2022-09-09";
        LocalDate localDate = LocalDate.parse(dInStr);
        LocalDateTime datetime = localDate.atTime(1,55,9);
        ProductForTrucking productForTrucking = new ProductForTrucking(Water_7290019056966,2);
        List prods= new LinkedList();
        prods.add(productForTrucking);
        try {
            userController.addTrucking("12345642",datetime,"tam1Tamir1", sources,destinations,prods,1,30);
        }
        catch (Exception e) {
            assertEquals(e.getMessage(),"Oops, there is another trucking at the same date and with the same vehicle");
        }
    }

    private void createTrucking3 () throws Exception {
        Site s1 = new Site("tamirHouse","batYam","0543397995","tamirStr",13,2,3,Area.center);
        Site d1 = new Site("idoHouse","herzliya","0524321231","idoStr",100,1,6,Area.center);
        List sources = new LinkedList();
        sources.add(s1);
        List destinations = new LinkedList();
        destinations.add(d1);
        String dInStr = "2022-08-09";
        LocalDate localDate = LocalDate.parse(dInStr);
        LocalDateTime datetime = localDate.atTime(1,20,9);
        ProductForTrucking productForTrucking = new ProductForTrucking(Water_7290019056966,2);
        List prods= new LinkedList();
        prods.add(productForTrucking);
        userController.addTrucking("12345642",datetime,"ramiF", sources,destinations,prods,1,30);
        assertEquals(userController.printBoardOfDriver("ramiF").contains("tamirStr"), true);

    }
    private void createTrucking4 () throws Exception {
        Site s1 = new Site("tamirHouse","batYam","0543397995","tamirStr",13,2,3,Area.center);
        Site d1 = new Site("idoHouse","herzliya","0524321231","idoStr",100,1,6,Area.center);
        List sources = new LinkedList();
        sources.add(s1);
        List destinations = new LinkedList();
        destinations.add(d1);
        String dInStr = "2022-09-09";
        LocalDate localDate = LocalDate.parse(dInStr);
        LocalDateTime datetime = localDate.atTime(1,20,9);
        ProductForTrucking productForTrucking = new ProductForTrucking(Water_7290019056966,2);
        List prods= new LinkedList();
        prods.add(productForTrucking);
        userController.addTrucking("45345642",datetime,"tam1Tamir1", sources,destinations,prods,1,30);
        assertEquals(userController.printBoardOfDriver("tam1Tamir1").contains("tamirStr"), true);

    }
    public void checkBoardTests() throws Exception {

        assertEquals(userController.printMyTruckings().contains("herzliya"), true);
        assertEquals(userController.printMyFutureTruckings().contains("herzliya"), true);
        assertEquals(userController.printMyTruckingsHistory().contains("herzliya"), false);
        assertEquals(userController.logout(), true);
        assertEquals(userController.login("ido1Ido1", "ido1Ido1"), true);
        assertEquals(userController.printFutureTruckings().contains("herzliya"), true);
        assertEquals(userController.getVehiclesRegistrationPlates().get(0), "45345642");
        assertEquals(userController.printBoardOfDriver("tam1Tamir1").contains("herzliya"), true);

    }

    public void changeTruckingDetails() throws Exception {

        Site s2 = new Site("halfree", "herzliya", "0543397993", "davidHamelech", 2, 4, 3, Area.center);
        Site d2 = new Site("tivTaam", "haifa", "0543397912", "haravSade", 13, 2, 3, Area.north);
        List add1 = new LinkedList();
        List add2 = new LinkedList();
        add1.add(s2);
        add2.add(d2);
        userController.addSourcesToTrucking(1, add1);
        userController.addDestinationToTrucking(1, add2);
        ProductForTrucking productForTruckingToAdd = new ProductForTrucking(Eggs_4902505139314, 2);
        userController.addProductToTrucking(1, productForTruckingToAdd);
        assertEquals(userController.printBoardOfDriver("tam1Tamir1").contains("halfree"), true);
        assertEquals(userController.printBoardOfDriver("tam1Tamir1").contains("tivTaam"), true);
        assertEquals(userController.printBoardOfDriver("tam1Tamir1").contains("Eggs"), true);
        userController.updateDriverOnTrucking(1, "ramiF");
        userController.updateDestinationsOnTrucking(1, add1);
        userController.updateVehicleOnTrucking(1, "12345642");
        userController.updateSourcesOnTrucking(1, add2);
    }

    public void updateTests() throws Exception {

        assertEquals(userController.printBoardOfDriver("ramiF").contains("12345642"), true);
        assertEquals(userController.printBoardOfDriver("ramiF").contains("hayarden"), false);
        assertEquals(userController.printBoardOfDriver("ramiF").contains("hamatganit"), false);
    }

    @Test
    public void TESTS() throws Exception {
        registerSuccessTests();
        login_logout_updatePassword_addLicense_Tests();
        createTruckingAndAddTests();
        assertEquals(userController.printBoard().contains("tamir avisar"),true);
        assertEquals(userController.logout(),true);
        assertEquals(userController.login("tam1Tamir1","passIdo1"),true);
        checkBoardTests();
        changeTruckingDetails();
        updateTests();
        createTrucking1();
        createTrucking2();
        createTrucking3();
        createTrucking4();
        System.out.println(userController.printBoard());
    }

     */
}

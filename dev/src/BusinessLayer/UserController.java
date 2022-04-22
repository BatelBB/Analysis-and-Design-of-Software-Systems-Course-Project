package BusinessLayer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserController {

    private Map<String, User> users;
    private static UserController singletonUserControllerInstance = null;
    protected User activeUser = null;
    private final int MIN_USERNAME_LENGTH = 3;
    private final int MAX_USERNAME_LENGTH = 12;
    private String CODE_TRUCK_MANAGER = "tm1234tm";
    private Map<String, String> UNIQUE_DRIVER_CODE_OF_TM; //key: hashcode, value: tmUsername

    private UserController() throws Exception {
        users = new ConcurrentHashMap<String, User>();
        UNIQUE_DRIVER_CODE_OF_TM = new ConcurrentHashMap<String, String>();
    }

    protected UserController(String FictiveConstructor) throws Exception {}

    public static UserController getInstance() throws Exception {
        if (singletonUserControllerInstance == null)
            singletonUserControllerInstance = new UserController();
        return singletonUserControllerInstance;
    }

    public synchronized boolean registerUser(String name, String username, String password, Role role, String code) throws Exception {
        synchronized (activeUser) {
            User newUser;
            if (role == null)
                throw new Exception("Please select role");
            if (!validateUsernameToRegister(username))
                throw new Exception("Username is not valid");
            else if (role == Role.truckingManager) {
                if (code != CODE_TRUCK_MANAGER)
                    throw new IllegalArgumentException("Sorry, the code is not valid");
                newUser = new TruckManager(name, username, password, null);
                UNIQUE_DRIVER_CODE_OF_TM.put(String.valueOf(newUser.hashCode()), username);
            }
            else if (role == Role.driver) {
                TruckManager truckManagerOfTheDriver = getTruckManagerByCode(code);
                newUser = new Driver(name, username, password, truckManagerOfTheDriver);
                truckManagerOfTheDriver.addDriver((Driver)newUser);
            }
            else
                throw new Exception("Sorry, we can not yet open a user for this type of employee");
            users.put(username, newUser);
            return true;
        }
    }

    public boolean login(String username, String password) throws Exception {
        synchronized (activeUser) {
            if (activeUser.hashCode() != loggedOutUser.hashCode())
                throw new IllegalArgumentException("There is a user already connected to the system");
            if (username == null)
                throw new IllegalArgumentException("Please enter valid username");
            User user = users.get(username);
            if (user == null)
                throw new IllegalArgumentException("Sorry but there's no user with that username");
            if(users.get(username).login(password)) {
                activeUser = user;
                return true;
            }
            return false;
        }
    }

    public boolean logout() throws Exception {
        synchronized (activeUser) {
            if (activeUser.hashCode() != loggedOutUser.hashCode())
                throw new Exception("There is no active user in the system");
            if(activeUser.logout()) {
                activeUser = null;
                return true;
            }
        }
        return false;
    }

    public boolean updatePassword(String newPassword) throws Exception {
        synchronized (activeUser) {
            if (activeUser.hashCode() != loggedOutUser.hashCode())
                throw new Exception("There is no active user in the system");
            return activeUser.updatePassword(newPassword);
        }
    }

    private boolean validateUsernameToRegister(String username)
    {
        if (username == null)
            return false;
        if (username.length() < MIN_USERNAME_LENGTH)
            throw new IllegalArgumentException("The minimum password length should be at least 3 characters");
        if (username.length() > MAX_USERNAME_LENGTH)
            throw new IllegalArgumentException("The maximum password length should be up to 12 characters");
        for (int i = 0; i < username.length(); i++) {
            if(!(Character.isLetter(username.charAt(i)) | Character.isDigit(username.charAt(i))))
                throw new IllegalArgumentException("Username can only contain letters and numbers");
        }

        if (users.containsKey(username))
            throw new IllegalArgumentException("Username is already exist. try another one.");
        return true;
    }

    private TruckManager getTruckManagerByCode(String hashcode) {
        String TruckManagerUsername = UNIQUE_DRIVER_CODE_OF_TM.get(hashcode);
        if (TruckManagerUsername == null)
            throw new IllegalArgumentException("Sorry, the code is not valid");
        User truckManager = users.get(TruckManagerUsername);
        if (truckManager == null | !(activeUser instanceof TruckManager))
            throw new IllegalArgumentException("Sorry, the code is not valid");
        return (TruckManager)truckManager;
    }

    private final User loggedOutUser = new TruckManager(null, null, null, null);
}


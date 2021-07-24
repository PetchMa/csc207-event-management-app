package controllersGatewaysPresenters;

// A: main run method + startUpMenu + TrialMenu + accountMenu
// H: initializer + mainMenu + adminMenu

// TODO where do we change the field current user when logging in and out?

import entitiesAndUseCases.*;

import java.util.*;


public class SystemController {
    private final UserController userController;
    private final EventController eventController;
    private final Presenter presenter;
    private final InputParser inputParser;

    private final IGateway<User> userParser;
    private final IGateway<Event> eventParser;
    private final IGateway<Template> templateParser;

    private final UserManager userManager;
    private final EventManager eventManager;
    private final TemplateManager templateManager;

    private final Map<String, List<String>> menuMap = new HashMap<>();
    private String currentUser;

    public SystemController() {
        userParser = new UserParser("data/users.json");
        eventParser = new EventParser("data/events.json");
        templateParser = new TemplateParser("data/templates.json");

        userManager = new UserManager(userParser);
        templateManager = new TemplateManager(templateParser);
        eventManager = new EventManager(eventParser, templateManager);

        presenter = new Presenter();
        inputParser = new InputParser();

        eventController = new EventController(userManager, eventManager, templateManager);
        userController = new UserController(userManager, eventManager, templateManager);

        initMenuMap();
    }

    private void initMenuMap() {
        List<String> startupMenu = Arrays.asList("SignUp", "Login", "Trial", "Exit");
        List<String> mainMenu = Arrays.asList("Create Event", "View Attended Events", "View Not Attended Events",
                "View My Events", "Edit Template", "Account Menu", "Save", "Logout");
        List<String> trialMenu = Arrays.asList("Create Event", "View Published Events", "Go Back");
//        List<String> adminMenu = Arrays.asList("Create Event", "View Attended Events", "View Not Attended Events",
//                "View My Events", "Edit Template", "Account Menu", "Save", "Logout");
        List<String> accountMenu = Arrays.asList("Logout", "Change Username", "Change Password", "Change Email",
                "Change User Type to Admin", "Delete My Account", "Go Back");
        menuMap.put("Startup Menu", startupMenu);
        menuMap.put("Main Menu", mainMenu);
        menuMap.put("Account Menu", accountMenu);
        menuMap.put("Trial Menu", trialMenu);
//        menuMap.put("Admin Menu", adminMenu);
    }

    /**
     * Run the program, this runs the "StartUp Menu"
     */
    public void run(){
        boolean program_running = true;
        while (program_running) {
            presenter.printMenu("Startup Menu", this.menuMap.get("Startup Menu"));
            String user_input = inputParser.readLine();
            switch (user_input) {
                case "1":
                    userController.userSignUp();
                    break;
                case "2":
                    String username = userController.userLogin();
                    if (username == null || username.length() == 0) {
                        presenter.printText("Please try to login again");
                    } else {
                        this.currentUser = username;
                        runMainMenu();
//                        User.UserType userType = userManager.retrieveUserType(username);
//                        if (userType == User.UserType.R) {
//                            // TODO: Run Main Menu
//                        } else if (userType == User.UserType.A) {
//                            // TODO: Run Admin Menu
//                        }
                    }
                    break;
                case "3":
                    createTrialUser();
                    runTrialMenu();
                    break;
                case "4":
                    program_running = false;
                    break;
            }
        }
    }

    private void runMainMenu() {
        while (true) {
            String userInput = showMenu("Main Menu");
            int input = Integer.parseInt(userInput);
            switch (input) {
                case 1:
                    int templateChoice = eventController.chooseTemplate(currentUser);
                    String templateName = templateManager.returnTemplateNames().get(templateChoice);
                    eventController.createNewEvent(templateName, currentUser);
                    break;
                case 2:
                    List<String> eventIDList1 = userManager.getAttendingEvents(currentUser);
                    eventController.browseEvents(currentUser, eventIDList1, true);
                    break;
                case 3:
                    eventManager.returnPublishedEvents().removeAll(userManager.getAttendingEvents(currentUser));
                    List<String> eventIDList2 = eventManager.returnPublishedEvents();
                    eventController.browseEvents(currentUser, eventIDList2, false);
                    break;
                case 4:
                    eventController.viewAndEditMyEvents(currentUser);
                    break;
                case 5:
                    if (userManager.retrieveUserType(currentUser) == User.UserType.A){
                        int templateIndex = eventController.chooseTemplate(currentUser);
                        List<String> templateList = templateManager.returnTemplateNames();
                        editTemplateName(templateList.get(templateIndex));
                    }
                    else {
                        presenter.printText("Sorry you do not have permission to edit the templates.");
                    }
                    break;
                case 6:
                    runAccountMenu();
                    break;
                case 7:
                    saveAll();
                    break;
                case 8:
                    saveAll();
                    // TODO call logout method
                    return;
                // TODO bad input?
            }
        }
    }

//    private void runAdminMenu() {
//        while (true) {
//            String userInput = showMenu("Admin Menu");
//            int input = Integer.parseInt(userInput);
//            switch (input) {
//                case 1:
//                    eventController.createEvent(currentUser);
//                    break;
//                case 2:
//                    eventController.browseEvents();
//                    break;
//                case 3:
//                    // TODO view own events?
//                    break;
//                case 4:
//                    // TODO edit template?
//                    break;
//                case 5:
//                    runAccountMenu();
//                    break;
//                case 6:
//                    saveAll();
//                    break;
//                case 7:
//                    saveAll();
//                    // TODO call logout method
//                    return;
//                // TODO bad input?
//            }
//        }
//    }

    public void runTrialMenu(){
        boolean trialMenuActive = true;
        while (trialMenuActive){
            presenter.printMenu("Trial Menu", this.menuMap.get("Trial Menu"));
            String user_input = inputParser.readLine();
            switch (user_input) {
                case "1":
                    int templateChoice = eventController.chooseTemplate(currentUser);
                    String templateName = templateManager.returnTemplateNames().get(templateChoice);
                    eventController.createNewEvent(templateName, currentUser);
                    break;
                case "2":
                    // Since this is a trial user, the unattended events is all of the events.
                    List<String> eventIDList = eventManager.returnPublishedEvents();
                    eventController.browseEvents(currentUser, eventIDList, false);
                    break;
                case "3":
                    trialMenuActive = false;
                    break;
            }
        }
    }

    public void runAccountMenu(){
        boolean accountMenuActive = true;
        while (accountMenuActive) {
            presenter.printMenu("Account Menu", this.menuMap.get("Account Menu"));
            String user_input = inputParser.readLine();
            switch (user_input) {
                case "1":
                    // TODO: Create a logout for the program
                    break;
                case "2":
                    userController.changeUsername(currentUser);
                    break;
                case "3":
                    userController.changePassword(currentUser);
                    break;
                case "4":
                    userController.changeEmail(currentUser);
                    break;
                case "5":
                    userController.changeToAdmin(currentUser);
                    break;
                case "6":
                    userController.deleteUser(currentUser);
                    break;
                case "7":
                    accountMenuActive = false;
                    break;
            }
        }
    }

    /**
     * Create a trial User in the program
     */
    public void createTrialUser(){
        // TODO Should be a constant
        String trial_username = "TRIAL_USER";
        String trial_password = "TRIAL_PASS";
        String trial_email = "TRIAL@EMAIL.COM";
        this.currentUser = trial_username;
        userManager.createUser(trial_username, trial_password, trial_email, User.UserType.T);
    }

    private String showMenu(String menuName) {
        presenter.printMenu(menuName, menuMap.get(menuName));
        return inputParser.readLine(); // TODO maybe presenter should return int of which menu item?
    }

    private void editTemplateName(String templateName) {
        presenter.printText("Please enter a new name for the template.");
        String newName = inputParser.readLine();
        if (templateManager.checkNameUniqueness(newName)){
            templateManager.editTemplateName(templateName, newName);
            presenter.printText("Template name edited successfully.");
        }
        else if (templateName.equals(newName)) {
            presenter.printText("Please enter a different name.");
        }
        else {
            presenter.printText("This name is already taken by another template.");
        }
    }

    private void saveAll() {
        userManager.saveAllUsers();
        eventManager.saveAllEvents();
        templateManager.saveAllTemplates();
    }
}


package controllers;

import entities.*;
import gateways.*;
import gateways.EventGateway;
import gateways.TemplateGateway;
import presenter.InputParser;
import presenter.Presenter;
import usecases.*;
import utility.Command;

import static utility.AppConstant.*;
import static entities.UserType.*;
import static utility.Command.*;

import java.util.*;

/**
 * Controller in charge of delegating to user controller and event controller. Runs the full system.
 */
public class SystemController {
    private final UserController userController;
    private final EventController eventController;
    private final Presenter presenter;
    private final InputParser inputParser;

    private final UserManager userManager;
    private final EventManager eventManager;
    private final TemplateManager templateManager;
    private final MenuManager menuManager;

//    private final Map<String, List<Command>> menuMap = new HashMap<>();
    private String currentUser;
    private UserType currentUserType;

    // TODO factory pattern for initializer maybe?
    // == initializing ==
    public SystemController() {
        IGateway<User> userGateway = new UserGateway("phase2/data/users.json");
        IGateway<Event> eventGateway = new EventGateway("phase2/data/events.json");
        IGateway<Template> templateGateway = new TemplateGateway("phase2/data/templates.json");
        IGateway<Menu> menuGateway = new MenuGateway("phase2/data/menus.json");
        IGateway<UserTypePermissions> userPermissionsGateway = new UserTypePermissionsGateway("phase2/data/usertype_permissions.json")

        userManager = new UserManager(userGateway);
        templateManager = new TemplateManager(templateGateway);
        eventManager = new EventManager(eventGateway, templateManager);
        menuManager = new MenuManager(menuGateway, userPermissionsGateway);

        presenter = new Presenter();
        inputParser = new InputParser();

        eventController = new EventController(userManager, eventManager, templateManager);
        userController = new UserController(userManager, eventManager);
//
//        initMenuMap();
    }

//    private void initMenuMap() {
//        menuMap.put("Start Up Menu", Arrays.asList(SIGN_UP, LOGIN, TRIAL, EXIT));
//        menuMap.put("Main Menu", Arrays.asList(CREATE_EVENT, VIEW_ATTENDED, VIEW_UNATTENDED, VIEW_OWNED, EDIT_TEMPLATE,
//                ACCOUNT_MENU, SAVE, LOG_OUT));
//        // TODO Change GO_BACK to EXIT_TRIAL
//        menuMap.put("Trial Menu", Arrays.asList(CREATE_EVENT, VIEW_PUBLISHED, GO_BACK));
//        menuMap.put("Account Menu", Arrays.asList(CHANGE_USERNAME, CHANGE_PASSWORD, CHANGE_EMAIL, CHANGE_TO_ADMIN,
//                DELETE_ACCOUNT, GO_BACK));
//    }

    // == menus ==
    /**
     * Run the program, this runs the "StartUp Menu"
     */
    public void run(){
        presenter.printText(WELCOME_TEXT);
        runMenu(START_UP);
    }

//    private void runMainMenu() {
//        runMenu("Main Menu");
//    }

//    /**
//     * Run the menu that the trial users interact with
//     */
//    private void runTrialMenu(){
//        createTrialUser();
//        runMenu("Trial Menu");
//        try {
//            deleteAccount();
//        } catch (ExitException ignored) {
//
//        }
//    }

//    /**
//     * Run the menu that allows the User to interact with their account
//     * @return false if the user has been deleted, true if not
//     */
//    private void runAccountMenu() throws ExitException {
//        runMenu("Account Menu");
//        if (currentUser == null)
//            throw new ExitException();
//    }

    // == menu helpers ==
    private void runMenu(Command currentCommand) {
        while (true) {
            Command userInput = getUserMenuChoice(currentCommand);
            if (userInput == null) {
                continue;
            }

            try {
                runUserCommand(userInput);
            } catch (ExitException e) {
                return;
            }
        }
    }

    private Command getUserMenuChoice(Command command) {
        displayMenu(command);
        List<Command> menuOptions = menuManager.getPermittedSubMenu(currentUserType, command);
        int user_input = inputParser.readInt();
        try {
            return menuOptions.get(user_input - 1);
        } catch (IndexOutOfBoundsException e) {
            invalidInput();
            return null;
        }
    }

    private void displayMenu(Command command) {
        List<Command> menuOptions = menuManager.getPermittedSubMenu(currentUserType, command);
        List<String> menuNames = new ArrayList<>();
        for (Command menuOption: menuOptions) {
            menuNames.add(menuOption.getName());
        }
        presenter.printMenu(command.getName(), menuNames);
    }

    private void invalidInput() {
        presenter.printText("You did not enter a valid option, try again");
    }

    private void runUserCommand(Command command) throws ExitException {
        switch (command) {
//            case START_UP:
//                runMenu(START_UP);
            case SIGN_UP:
                signUp();
                break;
            case LOGIN:
                login();
                break;
            case TRIAL_MENU:
                runMenu(Command.TRIAL_MENU);
                break;
            case EXIT:
                exit();
                break;
            case CREATE_EVENT:
                eventController.createNewEvent(retrieveTemplateName(), currentUser);
                break;
            case VIEW_ATTENDED:
            case VIEW_UNATTENDED:
            case VIEW_OWNED:
            case VIEW_PUBLISHED:
                eventController.browseEvents(currentUser, command.getViewType());
                break;
            case EDIT_TEMPLATE:
                editTemplate();
                break;
            case ACCOUNT_MENU:
                runMenu(ACCOUNT_MENU);
                break;
            case SAVE:
                saveAll();
                break;
            case LOG_OUT:
                logOut();
                break;
            case CHANGE_USERNAME:
                changeUsername();
                break;
            case CHANGE_PASSWORD:
                userController.changePassword(currentUser);
                break;
            case CHANGE_EMAIL:
                userController.changeEmail(currentUser);
                break;
            case CHANGE_TO_ADMIN:
                userController.changeToAdmin(currentUser);
                break;
            case DELETE_ACCOUNT:
                deleteAccount();
                break;
            case EXIT_TRIAL:
            case GO_BACK:
                throw new ExitException();
        }
    }

    // == commands ==
    private void signUp() {
        if (userController.userSignUp()){
            saveAll();
        }
    }

    private void login() {
        String attemptedLoginUsername = userController.userLogin();
        if (attemptedLoginUsername != null){
            this.currentUser = attemptedLoginUsername;
            this.currentUserType = userManager.retrieveUserType(attemptedLoginUsername);
            runMenu(LOGIN);
        }
    }

    private void exit() throws ExitException {
        saveAll();
        presenter.printText("Exiting...");
        throw new ExitException();
    }

    private void editTemplate() {
        if (userManager.retrieveUserType(currentUser) == ADMIN){
            editTemplateName(retrieveTemplateName());
        } else {
            presenter.printText("Sorry you do not have permission to edit the templates.");
        }
    }

    private void saveAll() {
        userManager.saveAllUsers();
        eventManager.saveAllEvents();
        templateManager.saveAllTemplates();
        menuManager.saveAllMenuInfo();
        presenter.printText("Everything has been successfully saved.");
    }

    private void logOut() throws ExitException {
        saveAll();
        userManager.logOut(currentUser);
        currentUser = null;
        throw new ExitException();
    }

    private void changeUsername() {
        String newUsername = userController.changeUsername(currentUser);
        if (newUsername != null) {
            currentUser = newUsername;
        }
    }

    private void deleteAccount() throws ExitException {
        boolean result = userController.deleteUser(currentUser);
        if (result) {
            logOut();
            presenter.printText("Your account has been deleted.");
        }
    }

    private void createTrialUser(){
        currentUser = TRIAL_USERNAME;
        userManager.createUser(TRIAL_USERNAME, TRIAL_PASSWORD, TRIAL_EMAIL, UserType.TRIAL);
    }

    // == templates == TODO refactor from here

    // TODO change templateVersionNumber
    private void editTemplateName(String templateName) {
        presenter.printText("Please enter a new name for the template.");
        String newName = inputParser.readLine();
        if (newName.equals("back")) {
            presenter.printText("You have been sent back.");
        }

        else if (templateManager.checkNameUniqueness(newName)){
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

    private String retrieveTemplateName() {
        int templateChoice = eventController.chooseTemplate(currentUser);
        List<String> templateNames = templateManager.returnTemplateNames();
        return retrieveName(templateNames, templateChoice);
    }

    private String retrieveName(List<String> nameList, int chosenIndex) {
        if(chosenIndexLargerThanTheSize(nameList, chosenIndex)) {
            return null;
        }
        return nameList.get(chosenIndex - 1);
    }

    private boolean chosenIndexLargerThanTheSize(List<?> list, int chosenIndex) {
        if(list == null) {
            return true;
        }
        return chosenIndex > list.size();
    }
}


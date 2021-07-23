package controllersGatewaysPresenters;

import entitiesAndUseCases.EventManager;
import entitiesAndUseCases.TemplateManager;
import entitiesAndUseCases.UserManager;

import java.util.*;

/**
 * Controller handling all event related requests.
 */

public class EventController {
    private final UserManager userManager;
    private final EventManager eventManager;
    private final TemplateManager templateManager;
    private final Presenter presenter;
    private final InputParser inputParser;

    public EventController(UserManager userManager, EventManager eventManager, TemplateManager templateManager) {
        this.userManager = userManager;
        this.eventManager = eventManager;
        this.templateManager = templateManager;
        this.presenter = new Presenter();
        this.inputParser = new InputParser();
    }

    public void createEventMenu(String username) {
        List<String> templateList = templateManager.getTemplateList(); // assume this will return list of str
        presenter.printMenu("Type a number corresponding to a template", templateList);
        int choice = getChoice(0, templateList.size());
        this.createNewEvent(templateList.get(choice), username);



    }

    private int getChoice(int lowBound, int highBound) {
        int choice = inputParser.readInt();
        while (choice < lowBound || choice >= highBound){
            presenter.printText("Do it right. Pick a good number: ");
            choice = inputParser.readInt();
        }
        return choice;
    }

    /**
     * Creates a new event based on chosen template and adds to User's owned events.
     * @param templateName - name of the template
     * @param username - username of the currently logged in user
     */
    public void createNewEvent(String templateName, String username){
        String newEventID = this.eventManager.createEvent(templateName, username);

        Map<String, String> fieldMap = this.eventManager.returnFieldNameAndType(newEventID);
        for (Map.Entry<String, String> entry : fieldMap.entrySet()) {
            presenter.printText("Enter " + entry.getKey() + "(" + entry.getValue() + "):");
            String userInput = inputParser.readLine();
            boolean accepted = false;
            while (!accepted) {
                if (eventManager.checkDataValidation(entry.getKey(), userInput, newEventID)) {
                    eventManager.enterFieldValue(entry.getKey(), userInput, newEventID);
                    accepted = true;
                }
                else {
                    presenter.printText("Do it right. Enter " + entry.getKey() + "(" + entry.getValue() + "):");
                    userInput = inputParser.readLine();
                }
            }
        }
    }

    /**
     * Prints details of a single event.
     * @param eventMap- Map representation of Event Entity
     */
    private int viewDetailedEvent(Map<String, String> eventMap) {
        this.presenter.printEntity(eventMap);
        presenter.printMenu("Select an option", new ArrayList<>(Arrays.asList("Join", "Back")));
        return getChoice(0, 2);
    }

    /**
     * Prints a list of all public events created by all users.
     */
    // TODO Need to figure out how this will be presented

    private int viewEventList(List<String> eventNameList) {
        eventNameList.add("Back");
        presenter.printMenu("Type a valid number", eventNameList);

        return getChoice(0, eventNameList.size() + 1);
    }

    public void browsePublicEvents(String username) {
        List<Map<String, String>> eventList = new ArrayList<>();
        List<String> eventNameList = new ArrayList<>();

        for (String eventID : this.eventManager.returnPublishedEvents()) {
            Map<String, String> tempEventMap = this.eventManager.returnEventAsMap(eventID);
            eventList.add(tempEventMap);
            eventNameList.add(tempEventMap.get("name")); //name prob wont be field update when notified
        }
        while (true) {
            int eventIndex = viewEventList(eventNameList);
            if (eventIndex == eventNameList.size()) {
                break;
            }
            int menuChoice = viewDetailedEvent(eventList.get(eventIndex));
            if (menuChoice == 0) {
                attendEvent(username, eventList.get(eventIndex).get("Event Id"));
            }
        }

    }

    /**
     * Adds event to User's joined event list.
     * @param username - username of the currently logged in user
     * @param eventID - unique identifier for event
     */
    private void attendEvent(String username, String eventID) {
        this.userManager.attendEvent(username, eventID);
    }

    /**
     * Removes selected event from User's joined event list.
     * @param username - username of the currently logged in user
     * @param eventID - unique identifier for event
     */
    private void leaveEvent(String username, String eventID) {
        this.userManager.unAttendEvent(username, eventID);
    }

    /**
     * Completely deletes specified event from system.
     * @param username - username of the currently logged in user
     * @param eventID - unique identifier for event
     */
    private void deleteEvent(String username, String eventID) {
        this.userManager.deleteEvent(username, eventID);
        this.eventManager.deleteEvent(eventID);
    }

}

import classes.Deadline;
import classes.Event;
import classes.Task;
import classes.ToDos;
import exceptions.InvalidTaskInputException;

import java.util.ArrayList;
import java.util.Scanner;

public class Duke {
    // Constants
    final static String DUKE_MESSAGE_INDENTATION = "~\t";
    final static String DUKE_UNSURE_COMMAND_REPLY = "Hmm, I don't understand what that means. Can you explain again?";
    // Variables
    static ArrayList<Task> taskList = new ArrayList<>();

    //================================================================================
    // Printing
    //================================================================================

    /**
     * Prints a single divider line
     *
     * @param underscore Determine whether to display _ or -
     * **/
    public static void printDivider(boolean underscore) {
        if (underscore) {
            System.out.println("____________________________________________________________");
        } else {
            System.out.println("------------------------------------------------------------");
        }
    }

    /**
     * Prints system reply in a special accent
     * - Duke will reply with a ~ and dividers at top and bottom
     *
     * @param message Message from Duke
     * **/
    public static void printDukeReply(String message) {
        printDivider(true);
        System.out.println(DUKE_MESSAGE_INDENTATION + message.replaceAll("\n", "\n" + DUKE_MESSAGE_INDENTATION));
        printDivider(false);
    }

    /**
     * Print welcome message
     * - Used at start of program
     * **/
    public static void printWelcomeMessage() {
        printDivider(true);
        String logo = "\t\t\t ____        _        \n"
                + "\t\t\t|  _ \\ _   _| | _____ \n"
                + "\t\t\t| | | | | | | |/ / _ \\\n"
                + "\t\t\t| |_| | |_| |   <  __/\n"
                + "\t\t\t|____/ \\__,_|_|\\_\\___|\tA variant";
        System.out.println(logo);
        System.out.println("............................................................");
        printDukeReply("Hello! I'm Duke\nWhat can I do for you?");
    }

    /**
     * Print ending message
     * - Used in program termination
     * **/
    public static void printEndingMessage() {
        printDukeReply("Bye. Hope to see you again soon!");
    }

    //================================================================================
    // Commands
    //================================================================================

    /**
     * Check for any terminating words
     *
     * @param input The given string to check
     * @return Boolean value to determine whether to terminate the program (e.g. true to terminate)
     * **/
    public static boolean checkTerminatingWord(String input) {
        return input.equals("bye");
    }

    /**
     * Determine the action to be perform based on user's command
     *
     * @param command Input command from user
     * **/
    private static void determineAction(String command) {
        if (command.equals("list")) {
            listTask();
        } else if (command.startsWith("mark") || command.startsWith("unmark")) {
            updateTaskStatus(command);
        } else if (command.startsWith("todo ")) {
            addToTaskList("todo", command.replaceFirst("todo ", ""));
        } else if (command.startsWith("deadline ")) {
            addToTaskList("deadline", command.replaceFirst("deadline ", ""));
        } else if (command.startsWith("event ")) {
            addToTaskList("event", command.replaceFirst("event ", ""));
        } else {
            printDukeReply(DUKE_UNSURE_COMMAND_REPLY);
        }
    }

    /**
     * Add a task to the task list
     *
     * @param type Type of task to be added
     * @param command Task to be added
     * **/
    public static void addToTaskList(String type, String command) {
        try {
            String task = command;
            if (type.equals("todo")) {
                // Add task
                taskList.add(new ToDos(command));
            } else if (type.equals("deadline")) {
                // Terminate should there be no date input
                if (!command.contains(" /by ")) throw new InvalidTaskInputException("Please include the deadline for your task");
                // Else, proceed to add deadline task
                String[] commandList = command.split(" /by ");
                // - Terminate should there be description and date input
                if (commandList.length != 2) throw new InvalidTaskInputException("Please include the description and deadline for your task");
                // Add deadline task
                task = commandList[1];
                taskList.add(new Deadline(commandList[0], task));
            } else if (type.equals("event")) {
                // Terminate should there be no date input
                if (!command.contains(" /at ")) throw new InvalidTaskInputException("Please include the event date");
                // Else, proceed to add deadline task
                String[] commandList = command.split(" /at ");
                // - Terminate should there be description and date input
                if (commandList.length != 2) throw new InvalidTaskInputException("Please include the time and of your event");
                // Add deadline task
                task = commandList[1];
                taskList.add(new Event(commandList[0], task));
            }
            printDukeReply("Roger. I will add this to your list:\n\t" + getTaskDetails(taskList.get(taskList.size()-1)) + "\nYou currently have " + taskList.size() + " task in your list.");
        } catch (InvalidTaskInputException e) {
            printDukeReply(e.getMessage());
        }
    }

    /**
     * Return the full details of a specific task
     *
     * @param selected The selected task
     * @return String containing the task details
     * **/
    public static String getTaskDetails(Task selected) {
        // Type
        String details = "[".concat(selected.getType()).concat("]");
        // Status
        details = details.concat("[").concat(selected.getDoneStatus() ? "X] " : " ] ");
        // Description
        details = details.concat(selected.getDescription());
        return details;
    }

    /**
     * List all the task in the task queue
     * **/
    public static void listTask() {
        if (taskList.isEmpty()) {
            printDukeReply("You have no pending task. Add one now?");
        } else {
            String allTask = "These are your current task:\n";
            for (int i = 0; i < taskList.size(); i++) {
                if (i != 0) {
                    allTask = allTask.concat("\n");
                }
                allTask = allTask.concat(String.valueOf(i+1)).concat(".");
                allTask = allTask.concat(getTaskDetails(taskList.get(i)));
            }
            printDukeReply(allTask);
        }
    }

    /**
     * Update a task status
     *
     * @param command Determine to mark task
     * **/
    public static void updateTaskStatus(String command) {
        if (taskList.isEmpty()) {
            printDukeReply("You do not have any ongoing task. Add one first?");
            return;
        }

        // Filter out command
        boolean isMark = command.startsWith("mark");
        command = command.replaceFirst(isMark ? "mark" : "unmark", "");
        // Process command
        // - Ensure that it is an integer position
        try {
            int index = Integer.parseInt(command.strip());
            index--;
            if (index < 0 || index >= taskList.size()) {
                printDukeReply("classes.Task is not found. Please provide a valid index.");
                listTask();
                return;
            }
            Task selected = taskList.get(index);
            if (selected.getDoneStatus() == isMark) {
                printDukeReply("There are no changes to be made!");
                listTask();
                return;
            }
            selected.setDoneStatus(isMark);
            String reply = isMark ? "Nice! I've marked this task as done:" : "Ok, I've marked this task as not done yet:";
            reply = reply.concat("\n\t").concat(getTaskDetails(selected));
            printDukeReply(reply);
        } catch (NumberFormatException e) {
            printDukeReply("Please provide the index of the task that you wish to remove.");
        }

    }

    //================================================================================
    // Main
    //================================================================================

    public static void main(String[] args) {
        String command;
        Scanner in = new Scanner(System.in);
        // Welcome Message
        printWelcomeMessage();
        while (true) {
            command = in.nextLine();
            if (checkTerminatingWord(command)) {
                break;
            } else {
                determineAction(command);
            }
        }
        // Ending Message
        printEndingMessage();
    }
}

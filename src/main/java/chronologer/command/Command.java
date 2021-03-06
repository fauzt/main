package chronologer.command;

import chronologer.exception.ChronologerException;
import chronologer.exception.MyLogger;
import chronologer.storage.ChronologerStateList;
import chronologer.storage.Storage;
import chronologer.task.Task;
import chronologer.task.TaskList;
import chronologer.ui.UiMessageHandler;

import java.util.ArrayList;

/**
 * Ensures that all the classes of command type have implementations of the method execute.
 *
 * @author Sai Ganesh Suresh
 * @version v1.4
 */
public abstract class Command {
    private static final String LOG_NAME = "CommandErrors";
    MyLogger logger = new MyLogger(this.getClass().getName(), LOG_NAME);

    /**
     * Checks if the index of a Task provided by the user is within the TaskList.
     *
     * @param indexOfTask        Holds the index of the task to be commented on.
     * @param currentSizeOfTasks Holds the integer value of the current list size.
     */
    public boolean isIndexValid(Integer indexOfTask, Integer currentSizeOfTasks) throws ChronologerException {
        if (indexOfTask < 0 || indexOfTask > (currentSizeOfTasks - 1)) {
            UiMessageHandler.outputMessage(ChronologerException.invalidIndex());
            throw new ChronologerException(ChronologerException.invalidIndex());
        }
        return true;
    }

    /**
     * Checks if the index of a Task provided by the user is within the TaskList.
     *
     * @param tasks Holds the list that need to be formatted for UI.
     */
    void outputRequiredList(ArrayList<Task> tasks, String title) {
        int i = 1;
        String requiredList = "";
        for (Task task : tasks) {
            requiredList += i++ + "." + task.toString() + "\n";
        }
        UiMessageHandler.outputMessage(title + "\n" + requiredList);
    }

    /**
     * Contracts all Command type classes to have their own respective execute
     * methods.
     *
     * @param tasks   Holds the list of all the tasks the user has.
     * @param storage Allows the saving of the file to persistent storage.
     * @param history Allows the history features to be done.
     * @throws ChronologerException Throws the exception according to the
     *                              user-defined list: DukeException.
     */
    public abstract void execute(TaskList tasks, Storage storage, ChronologerStateList history)
        throws ChronologerException;
}
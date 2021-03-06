package chronologer.parser;

import chronologer.command.AddCommand;
import chronologer.command.Command;
import chronologer.exception.ChronologerException;
import chronologer.ui.UiMessageHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

//@@author fauzt
/**
 * Extract the components required to add a TodoWithinPeriod task.
 *
 * @author Fauzan Adipratama
 * @version v1.0
 */
public class TodoWithinPeriodParser extends TodoParser {

    public TodoWithinPeriodParser(String userInput, String command) {
        super(userInput, command);
        this.checkType = Flag.BETWEEN.getFlag();
    }

    /**
     * Parses the duration from the input to pass to the AddCommand for Todo.
     * @return the AddCommand to be executed
     * @throws ChronologerException if input is invalid
     */
    @Override
    public Command parse() throws ChronologerException {
        super.extract();
        LocalDateTime startDate = extractStartDate(taskFeatures);
        LocalDateTime endDate = extractEndDate(taskFeatures);

        return new AddCommand(command, taskDescription, startDate, endDate);
    }

    private LocalDateTime extractStartDate(String taskFeatures) throws ChronologerException {
        String dateTimeFromUser = taskFeatures.split(checkType, 2)[1].trim();
        String from;
        try {
            from = dateTimeFromUser.split("-", 2)[0].trim();
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.writeLog(e.toString(), this.getClass().getName(), userInput);
            throw new ChronologerException(ChronologerException.emptyDateOrTime());
        }
        LocalDateTime startDate;
        try {
            startDate = DateTimeExtractor.extractDateTime(from);
        } catch (DateTimeParseException e) {
            logger.writeLog(e.toString(), this.getClass().getName(), userInput);
            throw new ChronologerException(ChronologerException.wrongDateOrTime());
        }
        return startDate;
    }

    private LocalDateTime extractEndDate(String taskFeatures) throws ChronologerException {
        String dateTimeFromUser = taskFeatures.split(checkType, 2)[1].trim();
        String to;
        try {
            to = dateTimeFromUser.split("-", 2)[1].trim();
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.writeLog(e.toString(), this.getClass().getName(), userInput);
            throw new ChronologerException(ChronologerException.emptyDateOrTime());
        }
        LocalDateTime endDate;
        try {
            endDate = DateTimeExtractor.extractDateTime(to);
        } catch (DateTimeParseException e) {
            logger.writeLog(e.toString(), this.getClass().getName(), userInput);
            throw new ChronologerException(ChronologerException.wrongDateOrTime());
        }
        return endDate;
    }
}

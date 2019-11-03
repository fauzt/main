package chronologer.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.regex.PatternSyntaxException;

import chronologer.command.AddRecurringCommand;
import chronologer.command.Command;
import chronologer.command.ListCommand;
import chronologer.exception.ChronologerException;
import chronologer.ui.UiTemporary;

public class RecurringEventParser extends EventParser {

    public RecurringEventParser(String userInput, String command) {
        super(userInput, command);
    }

    @Override
    public Command parse() throws ChronologerException {
        super.extract();
        LocalDateTime fromDate;
        LocalDateTime toDate;
        String date = extractDate(taskFeatures);
        String dateTimeFromUser = formatDateTime(date, taskFeatures);
        System.out.println("------------------------------------>" + date);
        fromDate = super.extractFromDate(dateTimeFromUser);
        toDate = super.extractToDate(dateTimeFromUser);
        assert toDate != null;
        assert fromDate != null;
        String modCode = "";
        if (hasModCode) {
            modCode = extractModCode(taskFeatures);
        }

        return new AddRecurringCommand(command, taskDescription, fromDate, toDate, modCode);
    }

    private String extractDate(String taskFeatures) throws ChronologerException {
        try {
            String dateTimeFromUser = taskFeatures.split(checkType, 2)[1].trim();
            String dayFromUser = dateTimeFromUser.split(" ", 2)[0].trim().toUpperCase();
            return LastDay.getDate(dayFromUser);
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.writeLog(e.toString(), this.getClass().getName(), userInput);
            UiTemporary.printOutput(ChronologerException.emptyDateOrTime());
            throw new ChronologerException(ChronologerException.emptyDateOrTime());
        } catch (PatternSyntaxException e) {
            logger.writeLog(e.toString(), this.getClass().getName(), userInput);
            UiTemporary.printOutput(ChronologerException.emptyDateOrTime());
            throw new ChronologerException(ChronologerException.emptyDateOrTime());
        }
    }

    private String formatDateTime(String date, String taskFeatures) throws ChronologerException {
        try {
            String timeFromUser = taskFeatures.split(checkType, 2)[1].trim().split(" ")[1];
            String startDate = date + timeFromUser.split("-")[0];
            String endDate = date + timeFromUser.split("-")[1];
            return startDate + "-" + endDate;
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.writeLog(e.toString(), this.getClass().getName(), userInput);
            UiTemporary.printOutput(ChronologerException.emptyDateOrTime());
            throw new ChronologerException(ChronologerException.emptyDateOrTime());
        } catch (PatternSyntaxException e) {
            logger.writeLog(e.toString(), this.getClass().getName(), userInput);
            UiTemporary.printOutput(ChronologerException.emptyDateOrTime());
            throw new ChronologerException(ChronologerException.emptyDateOrTime());
        }
    }
}
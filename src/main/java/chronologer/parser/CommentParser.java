package chronologer.parser;

import chronologer.command.Command;
import chronologer.command.CommentCommand;
import chronologer.exception.ChronologerException;
import chronologer.ui.UiMessageHandler;

/**
 * Extract the components required for the comment command from the user input.
 *
 * @author Tan Yi Xiang
 * @version v1.0
 */
public class CommentParser extends IndexParser {

    CommentParser(String userInput, String command) {
        super(userInput, command);
    }

    @Override
    public Command parse() throws ChronologerException {
        super.extract();
        String comment = extractComment(taskFeatures);
        return new CommentCommand(indexOfTask, comment);
    }

    private String extractComment(String taskFeatures) throws ChronologerException {
        String comment;
        try {
            String[] commentCommandParts = taskFeatures.split("\\s+", 2);
            comment = commentCommandParts[1].trim();
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.writeLog(e.toString(), this.getClass().getName(), userInput);
            throw new ChronologerException(ChronologerException.emptyComment());
        }
        return comment;
    }
}

package chronologer.command;

import chronologer.exception.ChronologerException;
import chronologer.storage.Storage;
import chronologer.task.Task;
import chronologer.task.TaskList;

import chronologer.ui.UiTemporary;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.RandomUidGenerator;
import net.fortuna.ical4j.util.UidGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.GregorianCalendar;


/**
 * Export the timeline as an ics file.
 *
 * @author Tan Yi Xiang
 * @version v1.0
 */
public class ExportCommand extends Command {

    private String filePath = System.getProperty("user.dir") + "/src/ChronologerDatabase/";
    private File icsFile = new File(filePath.concat("calendar.ics"));

    @Override
    public void execute(TaskList tasks, Storage storage) throws ChronologerException {

        Calendar calendar = initializeCalendar();
        ArrayList<Task> taskList = tasks.getTasks();
        CalendarOutputter calendarOutputter = new CalendarOutputter();

        for (Task task : taskList) {
            if (tasks.isDeadline(task)) {
                java.util.Calendar deadlineCalendar = convertToCalendar(task.getStartDate());
                DateTime deadlineDate = new DateTime(deadlineCalendar.getTime());
                VEvent deadline = new VEvent(deadlineDate, task.getDescription());
                if (task.getComment() != null) {
                    deadline.getProperties().add(new Description(task.getComment()));
                }
                UidGenerator generator = new RandomUidGenerator();
                deadline.getProperties().add(generator.generateUid());
                calendar.getComponents().add(deadline);
            } else if (tasks.isEvent(task) || tasks.isTodoPeriod(task)) {
                java.util.Calendar eventStartCalendar = convertToCalendar(task.getStartDate());
                java.util.Calendar eventEndCalendar = convertToCalendar(task.getEndDate());
                DateTime startEventDate = new DateTime(eventStartCalendar.getTime());
                DateTime endEventDate = new DateTime(eventEndCalendar.getTime());
                VEvent event = new VEvent(startEventDate, endEventDate, task.getDescription());
                if (task.getComment() != null) {
                    event.getProperties().add(new Description(task.getComment()));
                }
                UidGenerator generator = new RandomUidGenerator();
                event.getProperties().add(generator.generateUid());
                calendar.getComponents().add(event);
            }

        }

        try {
            FileOutputStream outputStream = new FileOutputStream(icsFile);
            calendarOutputter.output(calendar, outputStream);
            UiTemporary.printOutput("Success,ics file written at src/ChronologerDatabase/calendar");
        } catch (IOException e) {
            UiTemporary.printOutput(ChronologerException.errorWriteCalendar());
            throw new ChronologerException(ChronologerException.errorWriteCalendar());
        }

    }

    private Calendar initializeCalendar() {
        Calendar calendar = new Calendar();
        calendar.getProperties().add(new ProdId("-//Chronologer//iCal4j 1.1//EN"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);
        return calendar;
    }

    private java.util.Calendar convertToCalendar(LocalDateTime startDate) {
        java.util.Calendar utilCalendar = new GregorianCalendar();
        utilCalendar.set(java.util.Calendar.YEAR, startDate.getYear());
        utilCalendar.set(java.util.Calendar.MONTH, startDate.getMonthValue() - 1);
        utilCalendar.set(java.util.Calendar.DAY_OF_MONTH, startDate.getDayOfMonth());
        utilCalendar.set(java.util.Calendar.HOUR_OF_DAY, startDate.getHour());
        utilCalendar.set(java.util.Calendar.MINUTE, startDate.getMinute());
        utilCalendar.set(java.util.Calendar.SECOND, 0);
        return utilCalendar;
    }

}
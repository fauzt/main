package chronologer.command;

import chronologer.parser.DateTimeExtractor;
import chronologer.task.Event;
import chronologer.task.Priority;
import chronologer.task.TaskList;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

//@@author fauzt
/**
 * Handles the logic of scheduling a duration value by a given date.
 *
 * @author Fauzan Adipratama
 * @version v1.4
 */
public final class TaskScheduler {

    private static final int SEARCH_HARD_LIMIT = 30;
    private static final String SCHEDULE_ANYTIME_BY_DEADLINE =
            "You can schedule this task from now till the deadline.\n";
    private static final String SCHEDULE_ANYTIME = "You can schedule this task anytime.\n";
    private static final String SCHEDULE_NOW_TILL_FORMAT = "You can schedule this task from now till %s\n";
    private static final String SCHEDULE_FROM_TILL_FORMAT = "You can schedule this task from %s till %s\n";
    private static final String NO_FREE_SLOTS =
            "There is no free slot to insert the task. Consider freeing up your schedule.\n";
    private static final String NOT_ENOUGH_TIME = "The duration is too long to be done within now and the deadline.\n";
    private static final String NOT_ENOUGH_TIME_HARD_LIMIT =
            "The duration is too long to be done within the next 30 days.\n";
    private static final String LOW_PRIORITY =
            "Below are the list of low-priority event(s) that you can consider freeing up.\n";

    private static ArrayList<Event> eventList;
    private static LocalDateTime hardLimitDeadlineDate = LocalDateTime.now().plusDays(SEARCH_HARD_LIMIT);
    private static boolean isFreeBetweenEvents;

    private static MessageBuilder listOfPeriodMessage;
    private static MessageBuilder listOfLowPriorityMessage;

    /**
     * Finds a free period of time within the user's schedule for a given duration by a given deadline.
     * @param tasks is the master task list in the program
     * @param durationToSchedule is the minimum duration to find a large enough period that is free
     * @param deadlineDate is the date to find any periods by
     */
    public static String scheduleByDeadline(TaskList tasks, Long durationToSchedule, LocalDateTime deadlineDate) {
        assert tasks != null;
        assert durationToSchedule != null;
        assert deadlineDate != null;

        if (isThereNotEnoughTime(durationToSchedule, deadlineDate)) {
            return NOT_ENOUGH_TIME;
        }
        setupEventList(tasks, deadlineDate);
        if (isEventListEmpty()) {
            return SCHEDULE_ANYTIME_BY_DEADLINE;
        }
        initialiseOutputMessage();
        searchFreePeriodsInEventList(durationToSchedule, deadlineDate);
        return getOutputMessage();
    }

    /**
     * Finds a free period of time within the user's schedule for a given duration without concerning a deadline.
     * @param tasks is the master task list in the program
     * @param durationToSchedule is the minimum duration to find a large enough period that is free
     */
    public static String scheduleTask(TaskList tasks, Long durationToSchedule) {
        assert tasks != null;
        assert durationToSchedule != null;

        if (isThereNotEnoughTime(durationToSchedule, hardLimitDeadlineDate)) {
            return NOT_ENOUGH_TIME_HARD_LIMIT;
        }
        setupEventList(tasks, hardLimitDeadlineDate);
        if (isEventListEmpty()) {
            return SCHEDULE_ANYTIME;
        }
        initialiseOutputMessage();
        searchFreePeriodsInEventList(durationToSchedule, hardLimitDeadlineDate);
        return getOutputMessage();
    }

    private static void setupEventList(TaskList tasks, LocalDateTime deadlineDate) {
        eventList = tasks.obtainEventList(deadlineDate);
        isFreeBetweenEvents = false;
    }

    private static void initialiseOutputMessage() {
        listOfPeriodMessage = new MessageBuilder();
        listOfLowPriorityMessage = new MessageBuilder();
        listOfLowPriorityMessage.loadMessage(LOW_PRIORITY);
    }

    private static String getOutputMessage() {
        if (isFreeBetweenEvents) {
            return listOfPeriodMessage.getMessage();
        }
        return listOfPeriodMessage.getMessage() + listOfLowPriorityMessage.getMessage();
    }

    private static boolean isThereNotEnoughTime(Long durationToSchedule, LocalDateTime deadlineDate) {
        return durationToSchedule > ChronoUnit.HOURS.between(LocalDateTime.now(), deadlineDate);
    }

    private static boolean isEventListEmpty() {
        return eventList.size() == 0;
    }

    private static void searchFreePeriodsInEventList(Long durationToSchedule, LocalDateTime deadlineDate) {
        assert eventList.size() != 0;

        if (isFreeFromNowTillFirstEvent(durationToSchedule)) {
            loadResult();
        }
        for (int i = 0; i < eventList.size() - 1; i++) {
            if (isFreeBetweenThisEventTillNextEvent(durationToSchedule, i)) {
                loadResult(i);
            }
        }
        if (isFreeBetweenLastEventTillDeadline(durationToSchedule, deadlineDate)) {
            loadResult(deadlineDate);
        }

        if (!isFreeBetweenEvents) {
            listOfPeriodMessage.loadMessage(NO_FREE_SLOTS);
        }
    }

    private static boolean isFreeFromNowTillFirstEvent(Long durationToSchedule) {
        LocalDateTime nextStartDate = eventList.get(0).getStartDate();
        if (nextStartDate.isBefore(LocalDateTime.now())) {
            return false;
        }
        Long duration = ChronoUnit.HOURS.between(LocalDateTime.now(), nextStartDate);
        if (durationToSchedule <= duration) {
            isFreeBetweenEvents = true;
            return true;
        }
        return false;
    }

    private static boolean isFreeBetweenThisEventTillNextEvent(Long durationToSchedule, int i) {
        Event currentEvent = eventList.get(i);
        Event nextEvent = eventList.get(i + 1);
        LocalDateTime currentEndDate = currentEvent.getEndDate();
        LocalDateTime nextStartDate = nextEvent.getStartDate();
        if (currentEvent.getPriority() == Priority.LOW) {
            listOfLowPriorityMessage.loadMessage(currentEvent.toString());
        }
        Long duration = ChronoUnit.HOURS.between(currentEndDate, nextStartDate);
        if (durationToSchedule <= duration) {
            isFreeBetweenEvents = true;
            return true;
        }
        return false;
    }

    private static boolean isFreeBetweenLastEventTillDeadline(Long durationToSchedule, LocalDateTime deadlineDate) {
        Event event = eventList.get(eventList.size() - 1);
        if (event.getPriority() == Priority.LOW) {
            listOfLowPriorityMessage.loadMessage(event.toString());
        }
        LocalDateTime currentEndDate = event.getEndDate();
        if (currentEndDate.isAfter(deadlineDate)) {
            return false;
        }
        Long duration = ChronoUnit.HOURS.between(currentEndDate, deadlineDate);
        if (durationToSchedule <= duration) {
            isFreeBetweenEvents = true;
            return true;
        }
        return false;
    }

    private static void loadResult() {
        String formattedNextStartDate = eventList.get(0).getStartDate().format(DateTimeExtractor.DATE_FORMATTER);
        listOfPeriodMessage.loadMessage(String.format(SCHEDULE_NOW_TILL_FORMAT, formattedNextStartDate));
    }

    private static void loadResult(int index) {
        String formattedCurrentEndDate = eventList.get(index).getEndDate().format(DateTimeExtractor.DATE_FORMATTER);
        String formattedNextStartDate = eventList.get(index + 1).getStartDate()
                .format(DateTimeExtractor.DATE_FORMATTER);
        listOfPeriodMessage.loadMessage(String.format(SCHEDULE_FROM_TILL_FORMAT, formattedCurrentEndDate,
                formattedNextStartDate));
    }

    private static void loadResult(LocalDateTime deadlineDate) {
        String formattedCurrentEndDate = eventList.get(eventList.size() - 1).getEndDate()
                .format(DateTimeExtractor.DATE_FORMATTER);
        String formattedDeadlineDate = deadlineDate.format(DateTimeExtractor.DATE_FORMATTER);
        listOfPeriodMessage.loadMessage(String.format(SCHEDULE_FROM_TILL_FORMAT, formattedCurrentEndDate,
                formattedDeadlineDate));
    }


}

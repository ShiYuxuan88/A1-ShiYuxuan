import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The fixed daily timetable the clinic runs to.
 *
 * <p>Keeping the timetable in its own class means the opening hours and the slot
 * length are defined in exactly one place. If the clinic later opens from 09:00
 * to 17:00 or moves to twenty minute slots, only a new
 * {@code ClinicSchedule} object needs to be supplied: every other class asks this
 * object which slots exist instead of hard coding them, so the rest of the
 * booking system keeps working unchanged.</p>
 *
 * @author Shi Yuxuan (25259256)
 * @version 1.0
 */
public class ClinicSchedule {

    /** Default opening time. */
    public static final LocalTime DEFAULT_START = LocalTime.of(8, 0);

    /** Default closing time. */
    public static final LocalTime DEFAULT_END = LocalTime.of(16, 0);

    /** Default length of one appointment slot, in minutes. */
    public static final int DEFAULT_SLOT_MINUTES = 30;

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private final LocalTime start;
    private final LocalTime end;
    private final int slotMinutes;
    private final List<LocalTime> slots;

    /**
     * Creates a schedule with the clinic's standard hours: 08:00 to 16:00 in
     * thirty minute slots.
     */
    public ClinicSchedule() {
        this(DEFAULT_START, DEFAULT_END, DEFAULT_SLOT_MINUTES);
    }

    /**
     * Creates a custom schedule.
     *
     * @param start       the first slot of the day
     * @param end         the time the clinic closes; no slot may run past it
     * @param slotMinutes the length of one slot in minutes
     * @throws IllegalArgumentException if the hours or the slot length are invalid
     */
    public ClinicSchedule(LocalTime start, LocalTime end, int slotMinutes) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Opening and closing times must be provided.");
        }
        if (slotMinutes <= 0) {
            throw new IllegalArgumentException("Slot length must be greater than zero minutes.");
        }
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("Closing time must be after the opening time.");
        }
        this.start = start;
        this.end = end;
        this.slotMinutes = slotMinutes;
        this.slots = Collections.unmodifiableList(buildSlots(start, end, slotMinutes));
    }

    /**
     * Builds the list of slot start times between two times.
     *
     * @param start       the first slot
     * @param end         the closing time
     * @param slotMinutes the slot length
     * @return the slot start times in ascending order
     */
    private static List<LocalTime> buildSlots(LocalTime start, LocalTime end, int slotMinutes) {
        List<LocalTime> built = new ArrayList<>();
        LocalTime slot = start;
        while (!slot.plusMinutes(slotMinutes).isAfter(end)) {
            built.add(slot);
            slot = slot.plusMinutes(slotMinutes);
        }
        return built;
    }

    /**
     * Returns the opening time.
     *
     * @return the first slot of the day
     */
    public LocalTime getStart() {
        return start;
    }

    /**
     * Returns the closing time.
     *
     * @return the time the clinic closes
     */
    public LocalTime getEnd() {
        return end;
    }

    /**
     * Returns the length of one slot.
     *
     * @return the slot length in minutes
     */
    public int getSlotMinutes() {
        return slotMinutes;
    }

    /**
     * Returns every slot start time of the day.
     *
     * @return an unmodifiable list of slot start times
     */
    public List<LocalTime> getSlots() {
        return slots;
    }

    /**
     * Returns how many slots the day contains.
     *
     * @return the number of slots
     */
    public int getSlotCount() {
        return slots.size();
    }

    /**
     * Tests whether a time is the start of one of the clinic's slots.
     *
     * @param time the time to test
     * @return {@code true} if the time is a valid slot start
     */
    public boolean isValidSlot(LocalTime time) {
        if (time == null) {
            return false;
        }
        return slots.contains(time.truncatedTo(ChronoUnit.MINUTES).withSecond(0).withNano(0));
    }

    /**
     * Returns the slot that contains a given time, or {@code null} when the time
     * falls outside the clinic's hours.
     *
     * @param time the time to locate
     * @return the slot start time, or {@code null}
     */
    public LocalTime slotFor(LocalTime time) {
        if (time == null) {
            return null;
        }
        for (LocalTime slot : slots) {
            LocalTime slotEnd = slot.plusMinutes(slotMinutes);
            if (!time.isBefore(slot) && time.isBefore(slotEnd)) {
                return slot;
            }
        }
        return null;
    }

    /**
     * Describes the timetable in a single line.
     *
     * @return the hours, the slot length and the number of slots
     */
    @Override
    public String toString() {
        return start.format(TIME_FORMAT) + "-" + end.format(TIME_FORMAT)
                + " every " + slotMinutes + " min (" + slots.size() + " slots)";
    }
}

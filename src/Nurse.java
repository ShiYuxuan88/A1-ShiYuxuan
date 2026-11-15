import java.time.DayOfWeek;
import java.util.Objects;
import java.util.Set;

/**
 * A nurse who collects samples and records observations on a ward.
 *
 * <p>The ward shift is modelled as a nested enum because it is a fixed set of
 * known values: the compiler then stops any other value from being supplied.</p>
 *
 * @author Shi Yuxuan (25259256)
 * @version 1.0
 */
public class Nurse extends HealthProfessional {

    /**
     * The shifts a nurse can be rostered on, with the hours each one covers.
     */
    public enum Shift {
        /** Early shift. */
        MORNING("07:00-15:00"),
        /** Late shift. */
        AFTERNOON("15:00-23:00"),
        /** Overnight shift. */
        NIGHT("23:00-07:00");

        private final String hours;

        Shift(String hours) {
            this.hours = hours;
        }

        /**
         * Returns the hours covered by this shift.
         *
         * @return the hours as a text range
         */
        public String getHours() {
            return hours;
        }
    }

    /** The ward this nurse is rostered on. */
    private final String ward;

    /** The shift this nurse works. */
    private final Shift shift;

    /**
     * Creates a nurse with sensible defaults for the specialty information.
     *
     * @param id   the numeric identifier
     * @param name the nurse's full name
     */
    public Nurse(String id, String name) {
        this(id, name, STANDARD_WORKING_DAYS, "General Ward", Shift.MORNING);
    }

    /**
     * Creates a nurse with all details supplied.
     *
     * @param id            the numeric identifier
     * @param name          the nurse's full name
     * @param availableDays the days this nurse works
     * @param ward          the ward this nurse is rostered on
     * @param shift         the shift this nurse works
     */
    public Nurse(String id, String name, Set<DayOfWeek> availableDays,
                 String ward, Shift shift) {
        super(id, name, availableDays);
        this.ward = requireText(ward, "Ward");
        this.shift = Objects.requireNonNull(shift, "Shift must be provided.");
    }

    /**
     * Returns the ward of this nurse.
     *
     * @return the ward label
     */
    public String getWard() {
        return ward;
    }

    /**
     * Returns the shift of this nurse.
     *
     * @return the shift
     */
    public Shift getShift() {
        return shift;
    }

    @Override
    public String getRole() {
        return "Nurse";
    }

    @Override
    protected String getSpecificDetails() {
        return "Ward        : " + ward + System.lineSeparator()
                + "Shift       : " + shift + " (" + shift.getHours() + ")";
    }

    @Override
    public String performDuty() {
        return getName() + " takes a blood sample and records the patient's "
                + "observations on " + ward + " during the " + shift + " shift.";
    }
}

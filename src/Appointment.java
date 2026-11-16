import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * A booked time with a health professional on a given day.
 *
 * <p>An appointment refers to the professional through the abstract
 * {@link HealthProfessional} type, so a single appointment class works with a
 * general practitioner, a surgeon, a dietitian or a nurse without knowing which
 * one it holds. All of the state is fixed at construction time.</p>
 *
 * <p>The class implements {@link Comparable} so that a whole collection of
 * appointments can be sorted from earliest to latest without any external
 * comparator.</p>
 *
 * @author Shi Yuxuan (25259256)
 * @version 1.0
 */
public class Appointment implements Comparable<Appointment> {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("EEE d MMM yyyy", Locale.ENGLISH);
    private static final int SLOT_MINUTES = 30;

    private final Patient patient;
    private final HealthProfessional professional;
    private final LocalDate date;
    private final LocalTime time;

    /**
     * Creates an appointment for today.
     *
     * @param patient      the patient being seen
     * @param professional the chosen health professional
     * @param time         the start time of the appointment
     * @throws IllegalArgumentException if any detail is missing or invalid
     */
    public Appointment(Patient patient, HealthProfessional professional, LocalTime time) {
        this(patient, professional, LocalDate.now(), time);
    }

    /**
     * Creates an appointment for a specific date, which must be today because
     * the clinic only takes bookings for the current day.
     *
     * @param patient      the patient being seen
     * @param professional the chosen health professional
     * @param date         the appointment date, which must be today
     * @param time         the start time of the appointment
     * @throws IllegalArgumentException if any detail is missing or invalid
     */
    public Appointment(Patient patient, HealthProfessional professional,
                       LocalDate date, LocalTime time) {
        if (patient == null) {
            throw new IllegalArgumentException("An appointment must have a patient.");
        }
        if (professional == null) {
            throw new IllegalArgumentException("An appointment must have a health professional.");
        }
        if (date == null) {
            throw new IllegalArgumentException("An appointment must have a date.");
        }
        if (time == null) {
            throw new IllegalArgumentException("An appointment must have a time.");
        }
        if (!date.equals(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Appointments can only be booked for the current day (" + LocalDate.now() + ").");
        }
        this.patient = patient;
        this.professional = professional;
        this.date = date;
        this.time = time.withSecond(0).withNano(0);
    }

    /**
     * Returns the patient being seen.
     *
     * @return the patient
     */
    public Patient getPatient() {
        return patient;
    }

    /**
     * Returns the chosen health professional.
     *
     * @return the professional
     */
    public HealthProfessional getProfessional() {
        return professional;
    }

    /**
     * Returns the appointment date.
     *
     * @return the date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Returns the appointment start time.
     *
     * @return the start time
     */
    public LocalTime getTime() {
        return time;
    }

    /**
     * Returns the end time, derived from the fixed half hour slot length.
     *
     * @return the finish time
     */
    public LocalTime getEndTime() {
        return time.plusMinutes(SLOT_MINUTES);
    }

    /**
     * Tests whether this appointment clashes with a booking for the same
     * professional at the same time.
     *
     * <p>Professionals are compared with {@link HealthProfessional#equals(Object)},
     * which treats two objects with the same id as the same person, so a fresh
     * object for an existing professional is still recognised.</p>
     *
     * @param otherProfessional the professional to test
     * @param otherTime         the time to test
     * @return {@code true} if the professional is already booked at that time
     */
    public boolean conflictsWith(HealthProfessional otherProfessional, LocalTime otherTime) {
        return otherProfessional != null
                && otherTime != null
                && professional.equals(otherProfessional)
                && time.equals(otherTime.withSecond(0).withNano(0));
    }

    /**
     * Tests whether this appointment belongs to the given professional id.
     *
     * @param professionalId the id to test
     * @return {@code true} if the ids match
     */
    public boolean isForProfessional(String professionalId) {
        return professionalId != null && professional.getId().equals(professionalId.trim());
    }

    /**
     * Tests whether this appointment belongs to the given patient mobile number.
     *
     * @param patientMobile the mobile number to test
     * @return {@code true} if the numbers match
     */
    public boolean isForPatient(String patientMobile) {
        if (patientMobile == null) {
            return false;
        }
        return patient.getMobile().equals(patientMobile.replaceAll("[^0-9]", ""));
    }

    /**
     * Orders appointments by start time, then by professional id, then by
     * patient name so that the result is always identical for identical data.
     *
     * @param other the appointment to compare with
     * @return a negative number, zero or a positive number
     */
    @Override
    public int compareTo(Appointment other) {
        int byTime = time.compareTo(other.time);
        if (byTime != 0) {
            return byTime;
        }
        int byProfessional = professional.compareTo(other.professional);
        if (byProfessional != 0) {
            return byProfessional;
        }
        return patient.getName().compareTo(other.patient.getName());
    }

    /**
     * Returns a one line summary, used when listing a whole day of bookings.
     *
     * @return the time slot followed by the patient and the professional
     */
    public String toSummaryLine() {
        return String.format("%s - %s | %-22s | %s",
                time.format(TIME_FORMAT),
                getEndTime().format(TIME_FORMAT),
                patient.getName(),
                professional.getBriefDescription());
    }

    /**
     * Produces the full description of this appointment, including the details
     * of the chosen professional.
     *
     * @return a multi line description of this appointment
     */
    @Override
    public String toString() {
        String lineBreak = System.lineSeparator();
        return "Appointment " + time.format(TIME_FORMAT) + " - " + getEndTime().format(TIME_FORMAT)
                + " on " + date.format(DATE_FORMAT)
                + lineBreak + "    Patient     : " + patient
                + lineBreak + "    Professional: " + professional.getRole()
                + " #" + professional.getId() + " " + professional.getName()
                + lineBreak + "    Duty        : " + professional.performDuty();
    }
}

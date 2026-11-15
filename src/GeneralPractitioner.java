import java.time.DayOfWeek;
import java.util.Set;

/**
 * A general practitioner: the first point of contact for a patient and the only
 * type of professional in this system that can issue a referral to a specialist.
 *
 * @author Shi Yuxuan (25259256)
 * @version 1.0
 */
public class GeneralPractitioner extends HealthProfessional {

    /** The consulting room this practitioner normally uses. */
    private final String clinicRoom;

    /** Whether patients without an appointment may still be seen. */
    private final boolean acceptsWalkIns;

    /**
     * Creates a general practitioner with sensible defaults for the
     * specialty specific information.
     *
     * @param id   the numeric identifier
     * @param name the practitioner's full name
     */
    public GeneralPractitioner(String id, String name) {
        this(id, name, STANDARD_WORKING_DAYS, "Room 1", true);
    }

    /**
     * Creates a general practitioner with all details supplied.
     *
     * @param id            the numeric identifier
     * @param name          the practitioner's full name
     * @param availableDays the days this practitioner works
     * @param clinicRoom    the consulting room
     * @param acceptsWalkIns whether walk-in patients are accepted
     */
    public GeneralPractitioner(String id, String name, Set<DayOfWeek> availableDays,
                               String clinicRoom, boolean acceptsWalkIns) {
        super(id, name, availableDays);
        this.clinicRoom = requireText(clinicRoom, "Clinic room");
        this.acceptsWalkIns = acceptsWalkIns;
    }

    /**
     * Returns the consulting room of this practitioner.
     *
     * @return the room label
     */
    public String getClinicRoom() {
        return clinicRoom;
    }

    /**
     * Returns whether this practitioner accepts walk-in patients.
     *
     * @return {@code true} if walk-ins are accepted
     */
    public boolean acceptsWalkIns() {
        return acceptsWalkIns;
    }

    @Override
    public String getRole() {
        return "General Practitioner";
    }

    @Override
    protected String getSpecificDetails() {
        return "Clinic room : " + clinicRoom + System.lineSeparator()
                + "Walk-ins    : " + (acceptsWalkIns ? "accepted" : "by appointment only");
    }

    @Override
    public String performDuty() {
        return getName() + " reviews the patient's history, prescribes medication "
                + "and issues a referral when specialist care is required.";
    }
}

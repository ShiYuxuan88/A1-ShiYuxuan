import java.time.DayOfWeek;
import java.util.Set;

/**
 * A surgeon who performs operations in a booked operating theatre.
 *
 * @author Shi Yuxuan (25259256)
 * @version 1.0
 */
public class Surgeon extends HealthProfessional {

    /** The field of surgery this professional specialises in. */
    private final String surgicalSpecialty;

    /** The theatre this professional operates in. */
    private final String theatre;

    /**
     * Creates a surgeon with sensible defaults for the specialty information.
     *
     * @param id   the numeric identifier
     * @param name the surgeon's full name
     */
    public Surgeon(String id, String name) {
        this(id, name, STANDARD_WORKING_DAYS, "General Surgery", "Theatre 1");
    }

    /**
     * Creates a surgeon with all details supplied.
     *
     * @param id                the numeric identifier
     * @param name              the surgeon's full name
     * @param availableDays     the days this surgeon operates
     * @param surgicalSpecialty the field of surgery
     * @param theatre           the operating theatre
     */
    public Surgeon(String id, String name, Set<DayOfWeek> availableDays,
                   String surgicalSpecialty, String theatre) {
        super(id, name, availableDays);
        this.surgicalSpecialty = requireText(surgicalSpecialty, "Surgical specialty");
        this.theatre = requireText(theatre, "Theatre");
    }

    /**
     * Returns the field of surgery of this professional.
     *
     * @return the specialty label
     */
    public String getSurgicalSpecialty() {
        return surgicalSpecialty;
    }

    /**
     * Returns the operating theatre of this professional.
     *
     * @return the theatre label
     */
    public String getTheatre() {
        return theatre;
    }

    @Override
    public String getRole() {
        return "Surgeon";
    }

    @Override
    protected String getSpecificDetails() {
        return "Specialty   : " + surgicalSpecialty + System.lineSeparator()
                + "Theatre     : " + theatre;
    }

    @Override
    public String performDuty() {
        return getName() + " performs " + surgicalSpecialty.toLowerCase()
                + " in " + theatre + " and records the post-operative notes.";
    }
}

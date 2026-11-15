import java.time.DayOfWeek;
import java.util.Set;

/**
 * A dietitian who builds and reviews personalised eating plans.
 *
 * @author Shi Yuxuan (25259256)
 * @version 1.0
 */
public class Dietitian extends HealthProfessional {

    /** The clinical focus of this dietitian, for example diabetes management. */
    private final String dietaryFocus;

    /** The length of a standard consultation in minutes. */
    private final int consultationMinutes;

    /**
     * Creates a dietitian with sensible defaults for the specialty information.
     *
     * @param id   the numeric identifier
     * @param name the dietitian's full name
     */
    public Dietitian(String id, String name) {
        this(id, name, STANDARD_WORKING_DAYS, "General Nutrition", 30);
    }

    /**
     * Creates a dietitian with all details supplied.
     *
     * @param id                  the numeric identifier
     * @param name                the dietitian's full name
     * @param availableDays       the days this dietitian works
     * @param dietaryFocus        the clinical focus of this dietitian
     * @param consultationMinutes the standard consultation length in minutes
     * @throws IllegalArgumentException if the consultation length is not positive
     */
    public Dietitian(String id, String name, Set<DayOfWeek> availableDays,
                     String dietaryFocus, int consultationMinutes) {
        super(id, name, availableDays);
        this.dietaryFocus = requireText(dietaryFocus, "Dietary focus");
        if (consultationMinutes <= 0) {
            throw new IllegalArgumentException(
                    "Consultation length must be greater than zero minutes.");
        }
        this.consultationMinutes = consultationMinutes;
    }

    /**
     * Returns the clinical focus of this dietitian.
     *
     * @return the dietary focus
     */
    public String getDietaryFocus() {
        return dietaryFocus;
    }

    /**
     * Returns the standard consultation length.
     *
     * @return the length in minutes
     */
    public int getConsultationMinutes() {
        return consultationMinutes;
    }

    @Override
    public String getRole() {
        return "Dietitian";
    }

    @Override
    protected String getSpecificDetails() {
        return "Focus       : " + dietaryFocus + System.lineSeparator()
                + "Consult time: " + consultationMinutes + " minutes";
    }

    @Override
    public String performDuty() {
        return getName() + " assesses the patient's diet and prepares a "
                + dietaryFocus.toLowerCase() + " meal plan.";
    }
}

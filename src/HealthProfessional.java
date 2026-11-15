import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The common abstraction for every health professional the clinic employs.
 *
 * <p>This class holds the state and behaviour that is genuinely shared by all
 * professionals, no matter what their specialty is: an identity, a name and the
 * fixed set of days they work. Everything that differs between specialties is
 * pushed down to the subclasses through the abstract {@link #getRole()},
 * {@link #getSpecificDetails()} and {@link #performDuty()} methods, so that the
 * shared code is written once and reused everywhere.</p>
 *
 * <p>The class is deliberately <strong>abstract</strong>: a "plain" health
 * professional with no specialty has no meaning in the clinic, so the compiler
 * is used to prevent such an object from ever being created. Because
 * {@link #toString()} is {@code final}, every subclass inherits exactly the
 * same output format and can only customise the parts it genuinely owns.</p>
 *
 * <p>Instances are immutable: every field is {@code final} and the availability
 * set is copied on the way in and wrapped on the way out, so no external code
 * can push an object into an invalid state.</p>
 *
 * @author Shi Yuxuan (25259256)
 * @version 1.0
 */
public abstract class HealthProfessional implements Comparable<HealthProfessional> {

    /** Working days used when the caller does not supply an availability set. */
    protected static final Set<DayOfWeek> STANDARD_WORKING_DAYS =
            Collections.unmodifiableSet(EnumSet.range(DayOfWeek.MONDAY, DayOfWeek.FRIDAY));

    private final String id;
    private final String name;
    private final Set<DayOfWeek> availableDays;

    /**
     * Creates a professional who works the standard Monday-to-Friday week.
     *
     * @param id   the numeric identifier, digits only
     * @param name the professional's full name
     * @throws IllegalArgumentException if the id or the name is invalid
     */
    protected HealthProfessional(String id, String name) {
        this(id, name, STANDARD_WORKING_DAYS);
    }

    /**
     * Creates a professional with an explicitly supplied working week.
     *
     * @param id            the numeric identifier, digits only
     * @param name          the professional's full name
     * @param availableDays the days this professional works; must not be empty
     * @throws IllegalArgumentException if the id, the name or the days are invalid
     */
    protected HealthProfessional(String id, String name, Set<DayOfWeek> availableDays) {
        this.id = validateId(id);
        this.name = requireText(name, "Name");
        this.availableDays = validateAvailableDays(availableDays);
    }

    /**
     * Returns the specialty label shown in front of the professional, for
     * example {@code "General Practitioner"}.
     *
     * @return the human readable role of this professional
     */
    public abstract String getRole();

    /**
     * Returns the extra information that only this specialty records, already
     * formatted as one or more {@code "Label : value"} lines.
     *
     * @return the type specific details, or an empty string if there are none
     */
    protected abstract String getSpecificDetails();

    /**
     * Performs the professional's signature duty and describes it, which lets a
     * mixed collection of professionals respond differently to the same call.
     *
     * @return a description of the duty that was carried out
     */
    public abstract String performDuty();

    /**
     * Returns the identifier of this professional.
     *
     * @return the numeric id
     */
    public final String getId() {
        return id;
    }

    /**
     * Returns the name of this professional.
     *
     * @return the full name
     */
    public final String getName() {
        return name;
    }

    /**
     * Returns the days this professional works.
     *
     * @return an unmodifiable view of the availability set
     */
    public final Set<DayOfWeek> getAvailableDays() {
        return availableDays;
    }

    /**
     * Tests whether this professional works on a given day.
     *
     * @param day the day to test
     * @return {@code true} if the professional is available on that day
     */
    public final boolean isAvailableOn(DayOfWeek day) {
        return availableDays.contains(day);
    }

    /**
     * Returns a short one line description, useful inside appointment output.
     *
     * @return the role, id and name on a single line
     */
    public final String getBriefDescription() {
        return getRole() + " #" + id + " " + name;
    }

    /**
     * Returns the working days as a readable, always identically ordered list.
     *
     * @return the days joined by commas, for example {@code "Mon, Wed, Fri"}
     */
    public final String getAvailableDaysText() {
        return availableDays.stream()
                .sorted()
                .map(day -> day.getDisplayName(TextStyle.SHORT, Locale.ENGLISH))
                .collect(Collectors.joining(", "));
    }

    /**
     * Produces the full description of this professional.
     *
     * <p>This is the template method of the class: the format is fixed here so
     * that every professional prints consistently, while the varying content is
     * supplied by the subclass through {@link #getRole()} and
     * {@link #getSpecificDetails()}.</p>
     *
     * @return a multi line description of this professional
     */
    @Override
    public final String toString() {
        String lineBreak = System.lineSeparator();
        StringBuilder text = new StringBuilder();
        text.append(getRole()).append(" #").append(id).append(" - ").append(name);
        text.append(lineBreak).append("    Available   : ").append(getAvailableDaysText());
        String details = getSpecificDetails();
        if (details != null && !details.trim().isEmpty()) {
            for (String row : details.split("\\r?\\n")) {
                text.append(lineBreak).append("    ").append(row);
            }
        }
        return text.toString();
    }

    /**
     * Two professionals are considered equal when they represent the same
     * identity, that is when they carry the same id. This is what allows the
     * clinic to recognise a repeat booking for the same person even if a
     * different object was passed in.
     *
     * @param other the object to compare with
     * @return {@code true} if both objects describe the same professional
     */
    @Override
    public final boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof HealthProfessional)) {
            return false;
        }
        HealthProfessional that = (HealthProfessional) other;
        return id.equals(that.id);
    }

    /**
     * Returns a hash code consistent with {@link #equals(Object)}.
     *
     * @return the hash code of the id
     */
    @Override
    public final int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Orders professionals by id so that mixed collections have a stable,
     * predictable order.
     *
     * @param other the professional to compare with
     * @return a negative number, zero or a positive number
     */
    @Override
    public final int compareTo(HealthProfessional other) {
        int byId = Long.compare(Long.parseLong(id), Long.parseLong(other.id));
        return byId != 0 ? byId : name.compareTo(other.name);
    }

    /**
     * Validates that an id consists of digits only.
     *
     * @param id the candidate id
     * @return the trimmed id
     * @throws IllegalArgumentException if the id is null, blank or not numeric
     */
    private static String validateId(String id) {
        String candidate = requireText(id, "ID");
        if (!candidate.matches("\\d+")) {
            throw new IllegalArgumentException(
                    "ID must contain numbers only, but was: " + candidate);
        }
        return candidate;
    }

    /**
     * Validates and defensively copies a set of working days.
     *
     * @param days the candidate availability set
     * @return an unmodifiable copy of the set
     * @throws IllegalArgumentException if the set is null or empty
     */
    private static Set<DayOfWeek> validateAvailableDays(Set<DayOfWeek> days) {
        if (days == null || days.isEmpty()) {
            throw new IllegalArgumentException("A professional must work on at least one day.");
        }
        return Collections.unmodifiableSet(EnumSet.copyOf(days));
    }

    /**
     * Shared guard against blank text, reused by every subclass.
     *
     * @param value     the candidate text
     * @param fieldName the field name used in the error message
     * @return the trimmed text
     * @throws IllegalArgumentException if the text is null or blank
     */
    protected static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank.");
        }
        return value.trim();
    }
}

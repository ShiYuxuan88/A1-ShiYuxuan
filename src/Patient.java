import java.util.Objects;

/**
 * A patient of the clinic: the person an appointment is booked for.
 *
 * <p>Modelling the patient as its own class keeps the appointment focused on the
 * booking itself, gives one single place to validate a phone number, and means a
 * patient could later gain more details (date of birth, address) without the
 * appointment class having to change.</p>
 *
 * @author Shi Yuxuan (25259256)
 * @version 1.0
 */
public class Patient {

    private final String name;
    private final String mobile;

    /**
     * Creates a patient.
     *
     * @param name   the patient's full name
     * @param mobile the patient's mobile number; spaces, dashes and brackets are
     *               ignored, and the remaining digits must form a ten digit
     *               number beginning with 0
     * @throws IllegalArgumentException if the name or the mobile number is invalid
     */
    public Patient(String name, String mobile) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Patient name must not be blank.");
        }
        this.name = name.trim();
        this.mobile = validateMobile(mobile);
    }

    /**
     * Returns the patient's name.
     *
     * @return the full name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the patient's mobile number in its compact form.
     *
     * @return the ten digit mobile number
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * Returns the mobile number grouped for display.
     *
     * @return the number as {@code 0412 345 678}
     */
    public String getFormattedMobile() {
        return mobile.substring(0, 4) + " " + mobile.substring(4, 7) + " " + mobile.substring(7);
    }

    /**
     * Two patients are the same person when both name and mobile match.
     *
     * @param other the object to compare with
     * @return {@code true} if both objects describe the same patient
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Patient)) {
            return false;
        }
        Patient that = (Patient) other;
        return mobile.equals(that.mobile) && name.equalsIgnoreCase(that.name);
    }

    /**
     * Returns a hash code consistent with {@link #equals(Object)}.
     *
     * @return a hash code built from the name and the mobile number
     */
    @Override
    public int hashCode() {
        return Objects.hash(name.toLowerCase(), mobile);
    }

    /**
     * Returns a short description of the patient.
     *
     * @return the name followed by the formatted mobile number
     */
    @Override
    public String toString() {
        return name + " (" + getFormattedMobile() + ")";
    }

    /**
     * Normalises and validates a mobile number.
     *
     * @param mobile the candidate number
     * @return the number reduced to digits only
     * @throws IllegalArgumentException if the number is missing or malformed
     */
    private static String validateMobile(String mobile) {
        if (mobile == null) {
            throw new IllegalArgumentException("Mobile number must be provided.");
        }
        String digits = mobile.replaceAll("[^0-9]", "");
        if (!digits.matches("0\\d{9}")) {
            throw new IllegalArgumentException(
                    "Mobile number must be ten digits starting with 0, but was: " + mobile.trim());
        }
        return digits;
    }
}

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;

/**
 * Entry point of the appointment system.
 *
 * <p>The clinic has no user interface, so this class stands in for the people who
 * would normally use it: it builds a realistic set of health professionals and
 * then exercises their behaviour, printing the results to the console.</p>
 *
 * @author Shi Yuxuan (25259256)
 * @version 1.0
 */
public class AssignmentOne {

    private static final String RULE =
            "================================================================================";
    private static final String THIN_RULE =
            "--------------------------------------------------------------------------------";

    /**
     * Starts the scripted demonstration.
     *
     * @param args command line arguments, which are not used
     */
    public static void main(String[] args) {
        printHeader();
        List<HealthProfessional> professionals = createHealthProfessionals();
        demonstrateHealthProfessionals(professionals);
        printFooter();
    }

    /**
     * Prints the title block of the demonstration.
     */
    private static void printHeader() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.ENGLISH);
        System.out.println(RULE);
        System.out.println(" PROG2004 Object Oriented Programming - Assessment 1");
        System.out.println(" Health Service Appointment System");
        System.out.println(" Shi Yuxuan (25259256)");
        System.out.println(" Demonstration run: " + LocalDate.now().format(format));
        System.out.println(RULE);
        System.out.println();
    }

    /**
     * Creates the mix of health professionals the clinic works with.
     *
     * <p>Every object is stored in a single list of the common abstract type, so
     * the list can later be processed without knowing which specialty each entry
     * holds. Adding a new specialty would only mean adding one more line here.</p>
     *
     * @return the professionals of the clinic
     */
    private static List<HealthProfessional> createHealthProfessionals() {
        List<HealthProfessional> professionals = new ArrayList<>();

        // Three general practitioners, using both constructors to show the default
        // values and an explicitly supplied working week.
        professionals.add(new GeneralPractitioner("1001", "Dr Alice Nguyen"));
        professionals.add(new GeneralPractitioner("1002", "Dr Marcus Lee",
                EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
                "Room 3", false));
        professionals.add(new GeneralPractitioner("1003", "Dr Priya Raman",
                EnumSet.of(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY, DayOfWeek.SATURDAY),
                "Room 5", true));

        // Two surgeons.
        professionals.add(new Surgeon("2001", "Dr Helen Okafor",
                EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.THURSDAY),
                "Orthopaedic Surgery", "Theatre 2"));
        professionals.add(new Surgeon("2002", "Dr Samuel Wright",
                EnumSet.of(DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
                "Cardiothoracic Surgery", "Theatre 4"));

        // One dietitian.
        professionals.add(new Dietitian("3001", "Ms Grace Tan",
                EnumSet.of(DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY),
                "Diabetes management", 45));

        // Two nurses, on different shifts.
        professionals.add(new Nurse("4001", "Nurse Olivia Brown",
                EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY),
                "General Ward", Nurse.Shift.MORNING));
        professionals.add(new Nurse("4002", "Nurse Daniel Kim",
                EnumSet.of(DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY),
                "Outpatients", Nurse.Shift.AFTERNOON));

        return professionals;
    }

    /**
     * Displays every health professional and shows the shared behaviour each one
     * inherits and overrides.
     *
     * @param professionals the professionals to display
     */
    private static void demonstrateHealthProfessionals(List<HealthProfessional> professionals) {
        System.out.println(RULE);
        System.out.println(" PART 1-3 : HEALTH PROFESSIONALS OF THE CLINIC");
        System.out.println(RULE);
        System.out.println("The clinic employs " + professionals.size()
                + " health professionals across 4 specialties.");
        System.out.println();

        for (HealthProfessional professional : professionals) {
            System.out.println(professional);
            System.out.println(THIN_RULE);
        }

        System.out.println("Signature duties, called through the common type:");
        System.out.println();
        for (HealthProfessional professional : professionals) {
            // The same call produces a different result for every specialty,
            // because performDuty() is dispatched on the runtime type.
            System.out.println("  * " + professional.performDuty());
        }
        System.out.println();

        System.out.println("Shared information, read through the common type:");
        System.out.println();
        for (HealthProfessional professional : professionals) {
            System.out.printf("  %-10s %-22s works %s%n",
                    "#" + professional.getId(),
                    professional.getName(),
                    professional.getAvailableDaysText());
        }
        System.out.println();
    }

    /**
     * Prints the closing block of the demonstration.
     */
    private static void printFooter() {
        System.out.println(RULE);
        System.out.println(" End of demonstration");
        System.out.println(RULE);
    }
}

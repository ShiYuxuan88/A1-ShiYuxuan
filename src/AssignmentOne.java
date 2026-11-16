import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Entry point of the appointment system.
 *
 * <p>The clinic has no user interface, so this class stands in for the people who
 * would normally use it. It builds a realistic set of health professionals and
 * patients and then runs a scripted demonstration of the whole system, printing
 * the results to the console.</p>
 *
 * <p>The demonstration follows the parts of the brief: the professionals
 * themselves (Parts 1-3), a single appointment (Part 4), the management of the
 * whole collection (Part 5) and an end to end run of one day in the clinic
 * (Part 6).</p>
 *
 * @author Shi Yuxuan (25259256)
 * @version 2.0
 */
public class AssignmentOne {

    private static final String RULE =
            "================================================================================";
    private static final String THIN_RULE =
            "--------------------------------------------------------------------------------";
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Starts the scripted demonstration.
     *
     * @param args command line arguments, which are not used
     */
    public static void main(String[] args) {
        printHeader();

        List<HealthProfessional> professionals = createHealthProfessionals();
        demonstrateHealthProfessionals(professionals);

        List<Patient> patients = createPatients();
        demonstrateAppointmentModel(patients, professionals);
        demonstrateAppointmentManagement(patients, professionals);
        demonstrateOneDayInTheClinic(patients, professionals);

        printFooter();
    }

    // ------------------------------------------------------------------
    // Output helpers
    // ------------------------------------------------------------------

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
     * Prints a section heading.
     *
     * @param title the heading text
     */
    private static void printSection(String title) {
        System.out.println();
        System.out.println(RULE);
        System.out.println(" " + title);
        System.out.println(RULE);
        System.out.println();
    }

    /**
     * Prints a short explanatory line.
     *
     * @param text the text to print
     */
    private static void printNote(String text) {
        System.out.println("  " + text);
    }

    /**
     * Prints the result of an action, aligned with its description.
     *
     * @param action what was attempted
     * @param result the outcome returned by the manager
     */
    private static void printResult(String action, OperationResult result) {
        System.out.printf("  %-54s %s%n", action, result);
    }

    /**
     * Prints a list of appointments in time order.
     *
     * @param label        the heading for the list
     * @param appointments the appointments to print
     */
    private static void printBookingList(String label, List<Appointment> appointments) {
        System.out.println("  " + label + " (" + appointments.size() + ")");
        if (appointments.isEmpty()) {
            System.out.println("      (no appointments)");
        } else {
            for (Appointment appointment : appointments) {
                System.out.println("      " + appointment.toSummaryLine());
            }
        }
        System.out.println();
    }

    // ------------------------------------------------------------------
    // Data used by the demonstration
    // ------------------------------------------------------------------

    /**
     * Creates the mix of health professionals the clinic works with.
     *
     * <p>Every object is stored in a single list of the common abstract type, so
     * the list can be processed without knowing which specialty each entry
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
     * Creates the patients used by the demonstration.
     *
     * @return the patients
     */
    private static List<Patient> createPatients() {
        List<Patient> patients = new ArrayList<>();
        patients.add(new Patient("Chloe Martin", "0412 345 678"));
        patients.add(new Patient("Ravi Patel", "0433 987 654"));
        patients.add(new Patient("Emma Wilson", "0455 123 789"));
        patients.add(new Patient("Liam O'Brien", "0421 555 333"));
        return patients;
    }

    /**
     * Finds a professional in the list by id.
     *
     * @param professionals the professionals to search
     * @param id            the id to look for
     * @return the matching professional
     * @throws IllegalArgumentException if no professional has that id
     */
    private static HealthProfessional findProfessional(List<HealthProfessional> professionals,
                                                       String id) {
        for (HealthProfessional professional : professionals) {
            if (professional.getId().equals(id)) {
                return professional;
            }
        }
        throw new IllegalArgumentException("No health professional with id " + id);
    }

    // ------------------------------------------------------------------
    // Parts 1-3
    // ------------------------------------------------------------------

    /**
     * Displays every health professional and shows the shared behaviour each one
     * inherits and overrides.
     *
     * @param professionals the professionals to display
     */
    private static void demonstrateHealthProfessionals(List<HealthProfessional> professionals) {
        printSection("PART 1-3 : HEALTH PROFESSIONALS OF THE CLINIC");

        printNote("The clinic employs " + professionals.size()
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

    // ------------------------------------------------------------------
    // Part 4
    // ------------------------------------------------------------------

    /**
     * Demonstrates a single appointment: what it holds, how it prints, how it
     * orders against another appointment, and how it refuses to be created in an
     * incomplete state.
     *
     * @param patients      the patients of the demonstration
     * @param professionals the professionals of the clinic
     */
    private static void demonstrateAppointmentModel(List<Patient> patients,
                                                    List<HealthProfessional> professionals) {
        printSection("PART 4 : MODELLING A SINGLE APPOINTMENT");

        printNote("Patients recorded by the clinic:");
        for (Patient patient : patients) {
            printNote("  " + patient);
        }
        System.out.println();

        HealthProfessional doctor = findProfessional(professionals, "1001");
        HealthProfessional surgeon = findProfessional(professionals, "2001");

        // The same Appointment class is used for a general practitioner and for a
        // surgeon, because the field is declared as the abstract type.
        Appointment gpBooking = new Appointment(patients.get(0), doctor, LocalTime.of(9, 0));
        Appointment surgeryBooking = new Appointment(patients.get(1), surgeon, LocalTime.of(9, 30));

        printNote("One appointment printed in full - toString() includes the professional:");
        System.out.println();
        printIndented(gpBooking.toString());
        System.out.println();

        printNote("The same Appointment class works with a completely different type:");
        System.out.println();
        printIndented(surgeryBooking.toString());
        System.out.println();

        // Ordering: the appointments are deliberately added out of order and then
        // sorted using the Comparable implementation on Appointment itself.
        List<Appointment> unsorted = new ArrayList<>();
        unsorted.add(new Appointment(patients.get(2), doctor, LocalTime.of(14, 0)));
        unsorted.add(gpBooking);
        unsorted.add(new Appointment(patients.get(3), surgeon, LocalTime.of(10, 30)));
        unsorted.add(surgeryBooking);

        printNote("Appointments in the order they were added (out of order):");
        for (Appointment appointment : unsorted) {
            printNote("  " + appointment.toSummaryLine());
        }
        System.out.println();

        Collections.sort(unsorted);
        printNote("After Collections.sort(...), using Appointment.compareTo():");
        for (Appointment appointment : unsorted) {
            printNote("  " + appointment.toSummaryLine());
        }
        System.out.println();

        printNote("Guard rails - an appointment cannot be created in an invalid state:");
        try {
            new Appointment(patients.get(0), doctor, LocalDate.now().plusDays(1), LocalTime.of(9, 0));
            printNote("  [WARN] a booking for another day was accepted - this is a bug");
        } catch (IllegalArgumentException exception) {
            printNote("  rejected -> " + exception.getMessage());
        }
        try {
            new Appointment(patients.get(0), null, LocalTime.of(9, 0));
            printNote("  [WARN] a booking without a professional was accepted - this is a bug");
        } catch (IllegalArgumentException exception) {
            printNote("  rejected -> " + exception.getMessage());
        }
        try {
            new Patient("No Phone", "12345");
            printNote("  [WARN] a malformed mobile number was accepted - this is a bug");
        } catch (IllegalArgumentException exception) {
            printNote("  rejected -> " + exception.getMessage());
        }
        System.out.println();
    }

    // ------------------------------------------------------------------
    // Part 5
    // ------------------------------------------------------------------

    /**
     * Demonstrates the management of the appointment collection: adding, viewing,
     * filtering, sorting and cancelling, plus the warnings produced when an
     * operation cannot be performed.
     *
     * @param patients      the patients of the demonstration
     * @param professionals the professionals of the clinic
     */
    private static void demonstrateAppointmentManagement(List<Patient> patients,
                                                         List<HealthProfessional> professionals) {
        printSection("PART 5 : MANAGING THE COLLECTION OF APPOINTMENTS");

        AppointmentManager manager = new AppointmentManager();
        printNote("Clinic timetable: " + manager.getSchedule()
                + " - a professional can be booked once per slot.");
        System.out.println();

        HealthProfessional gpOne = findProfessional(professionals, "1001");
        HealthProfessional surgeon = findProfessional(professionals, "2001");
        HealthProfessional dietitian = findProfessional(professionals, "3001");
        HealthProfessional nurse = findProfessional(professionals, "4001");

        printNote("Adding appointments:");
        printResult("09:00  GP #1001        -> Chloe Martin",
                manager.addAppointment(patients.get(0), gpOne, LocalTime.of(9, 0)));
        printResult("09:00  Surgeon #2001   -> Ravi Patel (same time, other person)",
                manager.addAppointment(patients.get(1), surgeon, LocalTime.of(9, 0)));
        printResult("09:30  GP #1001        -> Ravi Patel",
                manager.addAppointment(patients.get(1), gpOne, LocalTime.of(9, 30)));
        printResult("10:00  Dietitian #3001 -> Emma Wilson",
                manager.addAppointment(patients.get(2), dietitian, LocalTime.of(10, 0)));
        printResult("11:00  Nurse #4001     -> Liam O'Brien",
                manager.addAppointment(patients.get(3), nurse, LocalTime.of(11, 0)));
        printResult("13:30  GP #1001        -> Emma Wilson",
                manager.addAppointment(patients.get(2), gpOne, LocalTime.of(13, 30)));
        System.out.println();

        printNote("Rejected bookings - the warnings the clinic shows:");
        printResult("16:00  GP #1001        (the clinic closes at 16:00)",
                manager.addAppointment(patients.get(0), gpOne, LocalTime.of(16, 0)));
        printResult("08:15  GP #1001        (not on a 30 minute boundary)",
                manager.addAppointment(patients.get(0), gpOne, LocalTime.of(8, 15)));
        printResult("09:00  GP #1001 again  (double booking)",
                manager.addAppointment(patients.get(3), gpOne, LocalTime.of(9, 0)));
        printResult("12:00  no professional supplied",
                manager.addAppointment(patients.get(0), null, LocalTime.of(12, 0)));
        System.out.println();

        printBookingList("All appointments, sorted by time:", manager.getAllAppointments());
        printBookingList("Filtered by professional ID 1001:",
                manager.findByProfessionalId("1001"));
        printBookingList("Filtered by patient mobile 0455 123 789:",
                manager.findByPatientMobile("0455 123 789"));
        printBookingList("Filtered by a professional with no bookings (2002):",
                manager.findByProfessionalId("2002"));

        printNote("Cancelling, two different ways:");
        printResult("cancel by professional ID + time (1001 at 09:30)",
                manager.cancelAppointment("1001", LocalTime.of(9, 30)));
        printResult("cancel by patient mobile (0455 123 789 = Emma Wilson)",
                manager.cancelAppointmentsForPatient("0455 123 789"));
        printResult("cancel something that does not exist (1003 at 09:00)",
                manager.cancelAppointment("1003", LocalTime.of(9, 0)));
        printResult("cancel for an unknown mobile number (0499 000 111)",
                manager.cancelAppointmentsForPatient("0499 000 111"));
        System.out.println();

        printBookingList("All appointments after the cancellations:",
                manager.getAllAppointments());

        // The timetable is an object, so a different clinic day can be supplied
        // without touching any other class.
        printNote("The timetable is configurable - the same classes with a 09:00-12:00 day");
        printNote("in 20 minute slots, which the standard day would reject:");
        ClinicSchedule shortDay = new ClinicSchedule(LocalTime.of(9, 0), LocalTime.of(12, 0), 20);
        AppointmentManager shortDayManager = new AppointmentManager(shortDay);
        printNote("  schedule: " + shortDayManager.getSchedule());
        printResult("09:20  GP #1001",
                shortDayManager.addAppointment(patients.get(0), gpOne, LocalTime.of(9, 20)));
        printResult("09:25  GP #1001        (not on the 20 minute grid)",
                shortDayManager.addAppointment(patients.get(1), gpOne, LocalTime.of(9, 25)));
        System.out.println();
    }

    // ------------------------------------------------------------------
    // Part 6
    // ------------------------------------------------------------------

    /**
     * Runs one day in the clinic end to end: bookings arrive during the day, the
     * receptionist looks at the day in different ways, appointments are cancelled
     * and mistakes are made, and the day is summarised at the end.
     *
     * @param patients      the patients of the demonstration
     * @param professionals the professionals of the clinic
     */
    private static void demonstrateOneDayInTheClinic(List<Patient> patients,
                                                     List<HealthProfessional> professionals) {
        printSection("PART 6 : A DAY IN THE CLINIC, END TO END");

        AppointmentManager clinic = new AppointmentManager();
        HealthProfessional gp = findProfessional(professionals, "1002");
        HealthProfessional surgeon = findProfessional(professionals, "2001");
        HealthProfessional dietitian = findProfessional(professionals, "3001");
        HealthProfessional nurse = findProfessional(professionals, "4001");

        printNote("08:30 - the first bookings of the day arrive");
        printResult("08:00  GP #1002        -> Chloe Martin",
                clinic.addAppointment(patients.get(0), gp, LocalTime.of(8, 0)));
        printResult("08:30  Surgeon #2001   -> Liam O'Brien",
                clinic.addAppointment(patients.get(3), surgeon, LocalTime.of(8, 30)));
        printResult("09:00  Dietitian #3001 -> Emma Wilson",
                clinic.addAppointment(patients.get(2), dietitian, LocalTime.of(9, 0)));
        printResult("09:30  Nurse #4001     -> Ravi Patel",
                clinic.addAppointment(patients.get(1), nurse, LocalTime.of(9, 30)));
        printResult("10:00  GP #1002        -> Ravi Patel",
                clinic.addAppointment(patients.get(1), gp, LocalTime.of(10, 0)));
        printResult("10:00  Surgeon #2001   -> Chloe Martin",
                clinic.addAppointment(patients.get(0), surgeon, LocalTime.of(10, 0)));
        System.out.println();

        printBookingList("The day so far:", clinic.getAllAppointments());

        printNote("11:00 - the receptionist looks at the day in different ways");
        printBookingList("Everything booked with GP #1002:",
                clinic.findByProfessionalId("1002"));
        printBookingList("Everything booked for Ravi Patel (0433 987 654):",
                clinic.findByPatientMobile("0433 987 654"));
        System.out.println();

        printNote("14:00 - two patients change their plans");
        printResult("cancel by professional ID + time (2001 at 10:00)",
                clinic.cancelAppointment("2001", LocalTime.of(10, 0)));
        printResult("cancel by patient mobile (0433 987 654 = Ravi Patel)",
                clinic.cancelAppointmentsForPatient("0433 987 654"));
        System.out.println();

        printBookingList("The day after the cancellations:", clinic.getAllAppointments());
        printResult("the freed 10:00 surgeon slot is bookable again",
                clinic.addAppointment(patients.get(2), surgeon, LocalTime.of(10, 0)));
        System.out.println();

        printNote("Error cases - what the clinic shows when an action cannot be completed");
        printResult("book GP #1002 at 08:00 again (already booked)",
                clinic.addAppointment(patients.get(3), gp, LocalTime.of(8, 0)));
        printResult("book at 16:00 (outside the clinic day)",
                clinic.addAppointment(patients.get(0), nurse, LocalTime.of(16, 0)));
        printResult("book at 12:10 (not a clinic slot)",
                clinic.addAppointment(patients.get(0), nurse, LocalTime.of(12, 10)));
        printResult("cancel a booking that does not exist (4001 at 15:00)",
                clinic.cancelAppointment("4001", LocalTime.of(15, 0)));
        printResult("cancel with an unknown mobile number (0400 000 000)",
                clinic.cancelAppointmentsForPatient("0400 000 000"));
        printResult("book without a patient",
                clinic.addAppointment(null, nurse, LocalTime.of(13, 0)));
        System.out.println();

        printNote("16:00 - end of day summary");
        System.out.println();
        printDaySummary(clinic.getAllAppointments(), clinic.getSchedule());
        System.out.println();
    }

    /**
     * Prints a block of text with every line indented, so that multi line object
     * output stays readable inside the demonstration.
     *
     * @param text the text to indent
     */
    private static void printIndented(String text) {
        for (String line : text.split("\\r?\\n")) {
            System.out.println("  " + line);
        }
    }

    /**
     * Prints how busy each professional was, and how much of the day was filled.
     *
     * @param appointments the appointments of the day, in time order
     * @param schedule     the timetable the day ran to
     */
    private static void printDaySummary(List<Appointment> appointments, ClinicSchedule schedule) {
        if (appointments.isEmpty()) {
            System.out.println("  (no appointments today)");
            return;
        }

        // A LinkedHashMap keeps the professionals in the order they first appear,
        // which a HashMap would not guarantee.
        Map<String, List<Appointment>> byProfessional = new LinkedHashMap<>();
        for (Appointment appointment : appointments) {
            String key = appointment.getProfessional().getBriefDescription();
            byProfessional.computeIfAbsent(key, unused -> new ArrayList<>()).add(appointment);
        }

        System.out.printf("  %-46s %-7s %-6s %s%n", "Professional", "Booked", "Load", "Times");
        System.out.println("  " + THIN_RULE);

        for (Map.Entry<String, List<Appointment>> entry : byProfessional.entrySet()) {
            List<Appointment> bookings = entry.getValue();
            StringBuilder times = new StringBuilder();
            for (Appointment appointment : bookings) {
                if (times.length() > 0) {
                    times.append(", ");
                }
                times.append(appointment.getTime().format(TIME_FORMAT));
            }
            int load = Math.round(bookings.size() * 100f / schedule.getSlotCount());
            System.out.printf("  %-46s %-7d %-6s %s%n",
                    entry.getKey(), bookings.size(), load + "%", times);
        }

        int capacity = schedule.getSlotCount() * byProfessional.size();
        int utilisation = capacity == 0 ? 0 : Math.round(appointments.size() * 100f / capacity);
        System.out.println("  " + THIN_RULE);
        System.out.println("  Total appointments: " + appointments.size()
                + " | professionals booked: " + byProfessional.size()
                + " | combined utilisation: " + utilisation + "%");
        System.out.println("  First appointment: "
                + appointments.get(0).getTime().format(TIME_FORMAT)
                + " | last appointment: "
                + appointments.get(appointments.size() - 1).getTime().format(TIME_FORMAT));
    }

    /**
     * Prints the closing block of the demonstration.
     */
    private static void printFooter() {
        System.out.println();
        System.out.println(RULE);
        System.out.println(" End of demonstration");
        System.out.println(RULE);
    }
}

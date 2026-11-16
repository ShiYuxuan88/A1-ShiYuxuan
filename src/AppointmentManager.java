import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Keeps every appointment of the day in one place and enforces the booking rules.
 *
 * <p>The collection is an {@link ArrayList}. A list is the right choice here
 * because appointments are naturally kept in the order they were added, the
 * clinic needs to iterate over all of them and to sort and filter them
 * repeatedly, and both of those operations are cheap on a list. Duplicate
 * bookings are not prevented by the data structure but by an explicit rule in
 * {@link #addAppointment}, which allows the system to explain <em>why</em> a
 * booking was rejected instead of silently refusing to store it.</p>
 *
 * <p>Every operation that can fail returns an {@link OperationResult} carrying a
 * warning message, so the caller always has something to show the user.</p>
 *
 * @author Shi Yuxuan (25259256)
 * @version 1.0
 */
public class AppointmentManager {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private final List<Appointment> appointments;
    private final ClinicSchedule schedule;

    /**
     * Creates a manager that runs to the clinic's standard timetable.
     */
    public AppointmentManager() {
        this(new ClinicSchedule());
    }

    /**
     * Creates a manager that runs to a supplied timetable.
     *
     * @param schedule the timetable the clinic runs to
     * @throws IllegalArgumentException if the schedule is missing
     */
    public AppointmentManager(ClinicSchedule schedule) {
        if (schedule == null) {
            throw new IllegalArgumentException("A clinic schedule must be provided.");
        }
        this.schedule = schedule;
        this.appointments = new ArrayList<>();
    }

    /**
     * Returns the timetable this manager books against.
     *
     * @return the clinic schedule
     */
    public ClinicSchedule getSchedule() {
        return schedule;
    }

    /**
     * Books an appointment after checking that every required detail is present,
     * that the time is one of the clinic's slots, and that the chosen
     * professional is not already booked at that time.
     *
     * @param patient      the patient to book for
     * @param professional the chosen health professional, of any type
     * @param time         the requested start time
     * @return a result describing whether the booking was made
     */
    public OperationResult addAppointment(Patient patient, HealthProfessional professional,
                                          LocalTime time) {
        if (patient == null || professional == null || time == null) {
            return OperationResult.failure("Booking rejected: patient, health professional "
                    + "and time are all required.");
        }
        LocalTime requested = time.withSecond(0).withNano(0);
        if (!schedule.isValidSlot(requested)) {
            return OperationResult.failure("Booking rejected: " + requested.format(TIME_FORMAT)
                    + " is not a clinic slot. The clinic runs " + schedule + ", so the last "
                    + "bookable slot starts at "
                    + schedule.getSlots().get(schedule.getSlotCount() - 1).format(TIME_FORMAT) + ".");
        }
        if (isProfessionalBooked(professional.getId(), requested)) {
            return OperationResult.failure("Booking rejected: " + professional.getBriefDescription()
                    + " already has an appointment at " + requested.format(TIME_FORMAT) + ".");
        }

        Appointment appointment = new Appointment(patient, professional, requested);
        appointments.add(appointment);
        return OperationResult.success("Booked " + requested.format(TIME_FORMAT) + " for "
                + patient.getName() + " with " + professional.getBriefDescription() + ".");
    }

    /**
     * Cancels the appointment a professional has at a given time.
     *
     * @param professionalId the professional's id
     * @param time           the appointment start time
     * @return a result describing whether an appointment was cancelled
     */
    public OperationResult cancelAppointment(String professionalId, LocalTime time) {
        if (professionalId == null || professionalId.trim().isEmpty() || time == null) {
            return OperationResult.failure("Cancellation rejected: professional ID and time "
                    + "are both required.");
        }
        LocalTime requested = time.withSecond(0).withNano(0);
        for (int index = 0; index < appointments.size(); index++) {
            Appointment appointment = appointments.get(index);
            if (appointment.isForProfessional(professionalId) && appointment.getTime().equals(requested)) {
                appointments.remove(index);
                return OperationResult.success("Cancelled the " + requested.format(TIME_FORMAT)
                        + " appointment for " + appointment.getPatient().getName()
                        + " with " + appointment.getProfessional().getBriefDescription() + ".");
            }
        }
        return OperationResult.failure("Cancellation rejected: no appointment found for "
                + "professional ID " + professionalId.trim() + " at "
                + requested.format(TIME_FORMAT) + ".");
    }

    /**
     * Cancels every appointment belonging to one patient.
     *
     * @param patientMobile the patient's mobile number
     * @return a result describing how many appointments were cancelled
     */
    public OperationResult cancelAppointmentsForPatient(String patientMobile) {
        List<Appointment> matches = new ArrayList<>();
        for (Appointment appointment : appointments) {
            if (appointment.isForPatient(patientMobile)) {
                matches.add(appointment);
            }
        }
        if (matches.isEmpty()) {
            return OperationResult.failure("Cancellation rejected: no appointments found for "
                    + "mobile number " + (patientMobile == null ? "(none supplied)" : patientMobile) + ".");
        }
        appointments.removeAll(matches);
        String times = matches.stream()
                .map(appointment -> appointment.getTime().format(TIME_FORMAT))
                .sorted()
                .collect(Collectors.joining(", "));
        return OperationResult.success("Cancelled " + matches.size() + " appointment(s) for "
                + matches.get(0).getPatient().getName() + " at " + times + ".");
    }

    /**
     * Returns every appointment, sorted from earliest to latest.
     *
     * @return an unmodifiable list in time order
     */
    public List<Appointment> getAllAppointments() {
        return sortedView(appointments);
    }

    /**
     * Returns the appointments of one professional, sorted from earliest to
     * latest.
     *
     * @param professionalId the professional's id
     * @return an unmodifiable list in time order, possibly empty
     */
    public List<Appointment> findByProfessionalId(String professionalId) {
        List<Appointment> matches = new ArrayList<>();
        for (Appointment appointment : appointments) {
            if (appointment.isForProfessional(professionalId)) {
                matches.add(appointment);
            }
        }
        return sortedView(matches);
    }

    /**
     * Returns the appointments of one patient, sorted from earliest to latest.
     *
     * @param patientMobile the patient's mobile number
     * @return an unmodifiable list in time order, possibly empty
     */
    public List<Appointment> findByPatientMobile(String patientMobile) {
        List<Appointment> matches = new ArrayList<>();
        for (Appointment appointment : appointments) {
            if (appointment.isForPatient(patientMobile)) {
                matches.add(appointment);
            }
        }
        return sortedView(matches);
    }

    /**
     * Tests whether a professional is already booked at a time.
     *
     * @param professionalId the professional's id
     * @param time           the time to test
     * @return {@code true} if a booking already exists
     */
    public boolean isProfessionalBooked(String professionalId, LocalTime time) {
        if (professionalId == null || time == null) {
            return false;
        }
        for (Appointment appointment : appointments) {
            if (appointment.isForProfessional(professionalId)
                    && appointment.getTime().equals(time.withSecond(0).withNano(0))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns how many appointments are currently held.
     *
     * @return the number of appointments
     */
    public int getAppointmentCount() {
        return appointments.size();
    }

    /**
     * Returns a sorted, read only copy of a collection of appointments.
     *
     * @param source the appointments to sort
     * @return an unmodifiable list in time order
     */
    private List<Appointment> sortedView(List<Appointment> source) {
        List<Appointment> copy = new ArrayList<>(source);
        Collections.sort(copy);
        return Collections.unmodifiableList(copy);
    }
}

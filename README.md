# A1-ShiYuxuan — Health Service Appointment System

**Unit:** PROG2004 Object Oriented Programming — Assessment 1 (Portfolio/programming, 40%)
**Student:** Shi Yuxuan — 25259256
**Language / JDK:** Java 21

---

## 1. What this project is

The clinic needs the *backbone* of an appointment system: a set of well-designed,
collaborating classes that model health professionals and the appointments patients
book with them. There is no user interface, no menu and no database. Instead,
`AssignmentOne` runs a short, scripted simulation that creates objects, exercises
their behaviour and prints the results to the console.

## 2. How to compile and run

The project contains no third-party libraries, so plain `javac` is enough:

```bash
# from the project root (the folder that contains src)
javac -encoding UTF-8 -d out src/*.java
java -cp out AssignmentOne
```

In an IDE: open the folder as a project, make sure `src` is the source root, and run
`AssignmentOne`. If the IDE is set to an older language level than the code uses,
switch it to JDK 21.

## 3. Class design at a glance

| Class | Role in the system |
|---|---|
| `HealthProfessional` | **Abstract** base type: id, name, working days, shared validation, fixed output format |
| `GeneralPractitioner` | Concrete type: consulting room, walk-ins |
| `Surgeon` | Concrete type: surgical specialty, operating theatre |
| `Dietitian` | Concrete type: dietary focus, consultation length |
| `Nurse` | Concrete type: ward, shift (nested `Shift` enum) |
| `Patient` | The person an appointment is booked for; validates and normalises the mobile number |
| `Appointment` | Links one patient to one professional at one time; `Comparable` by time |
| `ClinicSchedule` | The fixed daily timetable (08:00–16:00, 30-minute slots) in one place |
| `AppointmentManager` | Holds the collection of appointments and enforces the booking rules |
| `OperationResult` | Carries the success flag and warning message returned by every operation |
| `AssignmentOne` | `main` method: the scripted demonstration |

See `docs/design-notes.md` for the reasoning behind each decision.

## 4. Where the four pillars of OOP appear

| Pillar | Where | Why it was used there |
|---|---|---|
| **Abstraction** | `HealthProfessional` (abstract), `performDuty()`, `getSpecificDetails()` | Callers deal with "a health professional", never with the four concrete classes |
| **Encapsulation** | All fields `private final`; validation in constructors; unmodifiable `Set` of working days | An object can never be built or mutated into an invalid state |
| **Inheritance** | The four subclasses extend `HealthProfessional` | Shared state and validation written once, not four times |
| **Polymorphism** | `List<HealthProfessional>` iterated and printed; `performDuty()` overridden per type | One loop handles a mixed collection and each object responds in its own way |

## 5. What the demonstration prints

Running `AssignmentOne` produces four sections, one per group of parts in the brief.

| Section | What it shows |
|---|---|
| Part 1–3 | All eight professionals printed, their signature duty called through the common type, and the inherited availability data read through the same type |
| Part 4 | One appointment printed in full, the same class holding a GP and then a surgeon, four appointments sorted by `compareTo`, and three objects rejected by their constructors |
| Part 5 | Six bookings added, four rejected with warnings, the day listed and filtered two ways, cancellation by professional id + time and by mobile number, and the same code running on a different timetable |
| Part 6 | One day end to end: bookings arriving, the day inspected, two cancellations, the freed slot rebooked, six error cases, then an end-of-day summary with each professional's load |

## 6. Scope decisions

The brief limits bookings to the current day and leaves several details open. The
choices made here are recorded, with reasons, in `docs/design-notes.md`.

# Design notes

Every decision below answers one of the "Design considerations" prompts in the
assessment brief. The brief deliberately leaves the design open, so the reasoning
is recorded here rather than only in the code.

---

## 1. One abstract base type, four concrete types

**Question asked by the brief:** *What relationship between the generic type and the
specific types lets you reuse the common code rather than repeating it?*

`HealthProfessional` is the superclass of `GeneralPractitioner`, `Surgeon`, `Dietitian`
and `Nurse`. The shared state (id, name, working days) and the shared validation live
in the superclass exactly once; a subclass only declares the fields that are genuinely
its own.

The alternative — an interface plus four independent classes — was rejected. The four
types do not merely share a contract, they share *state and behaviour*, and Java
interfaces cannot carry instance fields. Inheritance is the construct that matches the
relationship. Interfaces are still used where they fit: `Comparable`, and the nested
`Nurse.Shift` enum.

**Why the base class is `abstract`:** a health professional with no specialty has no
meaning in the clinic, so the class must not be instantiated. Making it `abstract` lets
the compiler enforce that instead of relying on a comment. It also forces every
subclass to supply `getRole()`, `getSpecificDetails()` and `performDuty()`, so a new
specialty cannot be added half-finished.

---

## 2. Encapsulation: read-only outside, validated inside

**Question asked by the brief:** *External code should be able to read a professional's
information, but should never be able to put an object into an invalid state.*

* Every field is `private final`; there is no setter anywhere.
* All validation happens in the constructor, and a constructor either produces a fully
  valid object or throws `IllegalArgumentException`.
* The availability set is a fixed collection of values, so it is stored as an
  `EnumSet<DayOfWeek>`. It is copied on the way in (`EnumSet.copyOf`) and handed out
  through `Collections.unmodifiableSet`, so a caller cannot modify the internal set
  through the reference it was given. An `EnumSet` is also the right shape for this
  data: the domain really is "a subset of the seven days", membership tests are O(1)
  and the values are guaranteed distinct.
* Getters expose data, never internal structure. `getAvailableDaysText()` returns a
  formatted string for the very reason that the raw collection does not need to leave
  the object at all.

The same pattern is applied to `Patient` (mobile number normalised and validated once)
and to `ClinicSchedule` (slot list returned unmodifiable).

---

## 3. Printing: a final `toString()` used as a template method

**Question asked by the brief:** *When one of your objects is printed … it should
display all of its details in a readable form.*

`HealthProfessional.toString()` is `final`. It prints the role, the id, the name, the
working days and then whatever the subclass returns from `getSpecificDetails()`.

This is the template-method pattern: the *format* is fixed in one place, so all eight
professionals line up identically, while the varying content is supplied by the
subclass through a hook method. If `toString()` were left overridable, the fourth
subclass someone writes could quietly print in a different shape and break the layout
of every listing.

---

## 4. Equality by identity, not by all fields

**Question asked by the brief (Part 5):** *What should it mean for two health
professional objects to be considered equal?*

Two professional objects are the same person when they carry the same **id**, so
`equals` and `hashCode` use the id only, and both are `final` so a subclass cannot
break the contract.

This is what makes double-booking detection work: `AppointmentManager` may be handed a
freshly created `Surgeon` object that represents an existing surgeon, and
`Appointment.conflictsWith()` still recognises the clash. Comparing every field instead
would miss the double booking as soon as one attribute differed.

`compareTo` uses the id as well, keeping the natural ordering consistent with `equals`
(a requirement of the `Comparable` contract).

---

## 5. `Patient` as its own class

**Question asked by the brief:** *Should patient be its own class?*

Yes. Three reasons:

1. The brief says the system records "who the patient is", not just a name string. A
   name and a mobile number that must always travel together are a concept, and a class
   is how a concept is named.
2. Phone validation and normalisation then exist in exactly one place. `Patient`
   strips spaces, dashes and brackets and requires ten digits beginning with 0, so
   `"0412 345 678"` and `"0412345678"` become the same number — which the
   cancel-and-filter operations depend on.
3. An `Appointment` that held loose strings would have to re-validate them, and the
   appointment class would grow every time the clinic records something new about a
   patient.

---

## 6. `Appointment` works with any type of professional

The field is declared as `HealthProfessional`, never as one of the four subclasses, so
one appointment class serves every specialty. The concrete object is supplied at
construction time and never changes, which means no `instanceof` chains anywhere in the
system and no code to change when a fifth specialty is added.

`Appointment` is immutable: all fields are `final` and the date and time are validated
at construction. An appointment therefore cannot exist in an incomplete state — a
missing patient, a missing professional, a missing time or a date other than today all
throw immediately.

---

## 7. Sorting: `Comparable` on the appointment itself

**Question asked by the brief:** *How could you give an appointment the ability to know
how it orders relative to another?*

`Appointment implements Comparable<Appointment>` and orders by `LocalTime`, then by
professional id, then by patient name. Because the comparison lives in the class, any
collection of appointments can be sorted with `Collections.sort(...)` and no caller has
to supply a comparator.

Time is stored as `java.time.LocalTime` rather than a `String` or an `int`. A string
would sort `"9:30"` after `"10:00"`, and a hand-rolled integer would need conversion
code everywhere. `LocalTime` compares correctly, knows about minutes and hours, and
formats itself.

The tie-break matters for reproducibility: the brief says two appointments at the same
time may appear in any order, but a deterministic order makes the console output stable
between runs, which makes it far easier to check that a change did not break anything.

---

## 8. The timetable as an object, not as constants

**Question asked by the brief:** *How will you represent that fixed set of slots so that
there is enough flexibility to change it?*

`ClinicSchedule` builds its slot list from three values — opening time, closing time and
slot length — and exposes `isValidSlot()` and `getSlotCount()`. Nothing else in the
system knows that the day starts at 08:00 or that a slot is thirty minutes.

The test is simple: to move to a 09:00–17:00 day in twenty-minute slots, you construct
`new ClinicSchedule(LocalTime.of(9,0), LocalTime.of(17,0), 20)` and pass it to the
manager. No other class changes. Hard-coded `if (time.equals(LocalTime.of(8,0)))` chains
or a constants class would have failed that test.

`getSlots()` returns the generated list, so a future UI could render a grid of free and
booked slots without duplicating the arithmetic.

---

## 9. `ArrayList` for the collection of appointments

**Question asked by the brief (Part 5):** *What data structure covered in Modules 1–3 is
appropriate for storing the collection of appointments, and why?*

`ArrayList<Appointment>`, held inside `AppointmentManager` and never exposed directly.

* The clinic iterates over every appointment constantly (display all, sort all, filter
  by professional, filter by patient). Iteration is what a list is for, and it is
  cache-friendly and index-based.
* Order of insertion is meaningful information that a `HashSet` would throw away.
* A `HashMap` keyed by time would only allow one appointment per time across the whole
  clinic, which is wrong: several professionals work in parallel at 09:00. A map from
  professional to slots would make "list the whole day in time order" expensive.
* Duplicate bookings are **not** prevented by the data structure. A `Set` could refuse
  the second identical appointment, but it could only do so silently. The clinic needs a
  warning explaining *why* the booking failed, so the rule lives in
  `addAppointment()` where it can produce a message.

The list is never handed out: the getters return `Collections.unmodifiableList` copies in
time order, so callers cannot bypass the rules by adding or removing elements.

---

## 10. `OperationResult` instead of exceptions or console printing

Every operation that can legitimately fail returns an `OperationResult` — a success flag
and a message. This is not an error condition (a doctor being double-booked is a normal
business outcome), so an exception would be the wrong tool: exceptions are for
programming errors and for cases the caller cannot recover from. Printing from inside the
manager would also tie the model to the console, which the brief explicitly does not
want.

`IllegalArgumentException` is still thrown for genuinely invalid input in constructors —
a blank name or a malformed mobile number is a bug in the calling code, not a warning to
show a user.

---

## 11. Scope decisions

The brief fixes several things and leaves others open. The open ones were decided as
follows, deliberately:

| Decision | Reason |
|---|---|
| Working days are recorded but not enforced when booking | Bookings are only allowed for *today*, so enforcing the roster would make the system unusable whenever the demo is run on a day that a given professional does not work. The availability data is displayed and available through `isAvailableOn()` for a future booking calendar. |
| No check that one patient is not booked twice at the same time | The brief asks specifically for the professional's slot to be protected. Patient-side rules were left out so the demonstrable rules stay exactly the ones the brief lists. Noted as future work. |
| No persistence, no login, no UI | Explicitly out of scope in the brief: "you are not building a finished, usable application". |
| Slot length is a schedule-wide constant of 30 minutes | Matches the brief's "08:00-16:00 in 30-minute intervals". Per-professional slot lengths (the dietitian's 45-minute consultation) would be a legitimate extension but conflict with a single shared grid, so the dietitian's consultation length is recorded as information only. |

---

## 12. What would have to change to add a fifth specialty?

1. Add one class extending `HealthProfessional` (roughly 60 lines, no existing file
   edited).
2. Add one line in `AssignmentOne.createHealthProfessionals()`.

Nothing else. No `switch`, no `instanceof`, no change to `Appointment`,
`AppointmentManager` or the printing code — which is the practical test that the
abstraction was drawn in the right place.

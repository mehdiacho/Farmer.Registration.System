package bw.farmers.service;

import bw.farmers.auth.PasswordHasher;
import bw.farmers.db.Database;
import bw.farmers.db.FarmerRepository;
import bw.farmers.db.UserRepository;
import bw.farmers.model.Farmer;
import bw.farmers.model.ReportFilter;
import bw.farmers.model.Status;
import bw.farmers.model.User;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Everything the screens can do, with the rules in one place: sign-up, sign-in,
 * profile changes, admin decisions and reports.
 */
public final class FarmerService {

    private final Database db;
    private final FarmerRepository farmers;
    private final UserRepository users;
    private final PasswordHasher hasher;
    private final Clock clock;

    public FarmerService(Database db, PasswordHasher hasher, Clock clock) {
        this.db = db;
        this.farmers = new FarmerRepository(db);
        this.users = new UserRepository(db);
        this.hasher = hasher;
        this.clock = clock;
    }

    public FarmerService(Database db) {
        this(db, new PasswordHasher(), Clock.systemDefaultZone());
    }

    // ---- accounts ---------------------------------------------------------------------------

    /** True until the first admin account exists; the app then asks for one on start. */
    public boolean needsAdminSetup() {
        return !users.anyAdmin();
    }

    public User createAdmin(String displayName, String email, char[] password, char[] confirm)
            throws ValidationException {
        List<String> errors = new ArrayList<>();
        if (displayName == null || displayName.isBlank()) {
            errors.add("Name is required.");
        }
        if (!Validation.isEmail(email)) {
            errors.add("Enter a valid email address.");
        } else if (users.emailTaken(email.strip(), null)) {
            errors.add("That email address already has an account.");
        }
        errors.addAll(Validation.password(password, confirm));
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
        String e = email.strip();
        long id = users.insert(e, displayName.strip(), hasher.hash(password), true);
        return new User(id, e, displayName.strip(), true);
    }

    /** Signs in by email and password. The account type decides which screen opens. */
    public Optional<User> signIn(String email, char[] password) {
        if (email == null || password == null) {
            return Optional.empty();
        }
        return users.findByEmail(email.strip())
                .filter(c -> hasher.verify(password, c.passwordHash()))
                .map(UserRepository.Credentials::user);
    }

    /** Registers a new farmer and their login account. The registration starts as pending. */
    public User register(Farmer form, char[] password, char[] confirm) throws ValidationException {
        Farmer f = normalise(form).withStatus(Status.PENDING);
        List<String> errors = new ArrayList<>(Validation.farmer(f, today()));
        errors.addAll(Validation.password(password, confirm));
        if (errors.isEmpty()) {
            errors.addAll(uniqueness(f, null, null));
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
        String hash = hasher.hash(password);
        return db.inTransaction(() -> {
            long userId = users.insert(f.email(), f.fullName(), hash, false);
            farmers.insert(f, userId);
            return new User(userId, f.email(), f.fullName(), false);
        });
    }

    public Optional<Farmer> profileOf(User user) {
        return farmers.findByUserId(user.id());
    }

    /**
     * Saves a farmer's own changes. A declined registration that is edited goes back to
     * pending, so the admin sees it again.
     */
    public Farmer updateOwnProfile(User user, Farmer changed) throws ValidationException {
        Farmer current = farmers.findByUserId(user.id())
                .orElseThrow(() -> new ValidationException("This account has no farmer profile."));
        Status status = current.status() == Status.DECLINED ? Status.PENDING : current.status();
        Farmer f = normalise(changed).withStatus(status);
        List<String> errors = new ArrayList<>(Validation.farmer(f, today()));
        if (errors.isEmpty()) {
            errors.addAll(uniqueness(f, current.idNumber(), user.id()));
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
        db.inTransaction(() -> {
            farmers.update(current.idNumber(), f);
            users.updateProfile(user.id(), f.email(), f.fullName());
            return null;
        });
        return f;
    }

    public void changePassword(User user, char[] current, char[] next, char[] confirm) throws ValidationException {
        String stored = users.findById(user.id()).map(UserRepository.Credentials::passwordHash).orElse(null);
        if (!hasher.verify(current, stored)) {
            throw new ValidationException("Your current password is not correct.");
        }
        List<String> errors = Validation.password(next, confirm);
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
        users.updatePasswordHash(user.id(), hasher.hash(next));
    }

    // ---- admin ------------------------------------------------------------------------------

    public List<Farmer> list(ReportFilter filter) {
        return farmers.find(filter);
    }

    public void setStatus(String idNumber, Status status) {
        farmers.setStatus(idNumber, status);
    }

    /** Admin edit. Keeps the farmer's linked account email in step with the record. */
    public Farmer adminUpdate(String originalId, Farmer changed) throws ValidationException {
        Farmer f = normalise(changed);
        Long userId = farmers.userIdOf(originalId).orElse(null);
        List<String> errors = new ArrayList<>(Validation.farmer(f, today()));
        if (errors.isEmpty()) {
            errors.addAll(uniqueness(f, originalId, userId));
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
        db.inTransaction(() -> {
            farmers.update(originalId, f);
            if (userId != null) {
                users.updateProfile(userId, f.email(), f.fullName());
            }
            return null;
        });
        return f;
    }

    public boolean delete(String idNumber) {
        return farmers.delete(idNumber);
    }

    // ---- helpers ----------------------------------------------------------------------------

    private List<String> uniqueness(Farmer f, String exceptFarmerId, Long exceptUserId) {
        List<String> errors = new ArrayList<>();
        if (farmers.idNumberTaken(f.idNumber(), exceptFarmerId)) {
            errors.add("That ID number is already registered.");
        }
        if (farmers.emailTaken(f.email(), exceptFarmerId) || users.emailTaken(f.email(), exceptUserId)) {
            errors.add("That email address is already registered.");
        }
        if (farmers.contactTaken(f.contact(), exceptFarmerId)) {
            errors.add("That contact number is already registered.");
        }
        return errors;
    }

    private static Farmer normalise(Farmer f) {
        return new Farmer(strip(f.idNumber()), strip(f.firstName()), strip(f.lastName()),
                strip(f.contact()) == null ? null : strip(f.contact()).replace(" ", ""),
                f.dateOfBirth(), f.gender(), strip(f.postalAddress()), f.district(), f.subDistrict(),
                strip(f.email()), f.farmSize(), f.farmingArea(), f.status());
    }

    private static String strip(String s) {
        return s == null ? null : s.strip();
    }

    private LocalDate today() {
        return LocalDate.now(clock);
    }
}

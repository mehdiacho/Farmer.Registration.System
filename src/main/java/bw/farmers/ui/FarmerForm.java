package bw.farmers.ui;

import bw.farmers.model.Districts;
import bw.farmers.model.Farmer;
import bw.farmers.model.Gender;
import bw.farmers.model.Status;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import java.awt.GridBagLayout;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

/** The farmer details form, shared by sign-up, the farmer's profile and the admin edit dialog. */
public final class FarmerForm extends JPanel {

    private static final long serialVersionUID = 1L;

    private final JTextField idNumber = text("9 digits");
    private final JTextField firstName = text(null);
    private final JTextField lastName = text(null);
    private final JTextField contact = text("e.g. 71234567");
    private final JSpinner dateOfBirth;
    private final JComboBox<Gender> gender = new JComboBox<>(Gender.values());
    private final JTextField postalAddress = text("e.g. PO Box 101, Gaborone");
    private final JTextField email = text("name@example.com");
    private final JComboBox<String> district = new JComboBox<>(Districts.all().toArray(String[]::new));
    private final JComboBox<String> subDistrict = new JComboBox<>();
    private final JTextField farmSize = text("hectares");
    private final JTextField farmingArea = text("hectares");

    public FarmerForm() {
        super(new GridBagLayout());
        setOpaque(false);

        Calendar cal = Calendar.getInstance();
        cal.set(1990, Calendar.JANUARY, 1);
        dateOfBirth = new JSpinner(new SpinnerDateModel(cal.getTime(), null, new Date(), Calendar.DAY_OF_MONTH));
        dateOfBirth.setEditor(new JSpinner.DateEditor(dateOfBirth, "d MMM yyyy"));

        district.addActionListener(e -> refreshSubDistricts());
        refreshSubDistricts();

        new Ui.Grid(this, 2)
                .field("Omang ID number", idNumber).field("Date of birth", dateOfBirth)
                .field("First name", firstName).field("Last name", lastName)
                .field("Gender", gender).field("Contact number", contact)
                .field("Email address", email).field("Postal address", postalAddress)
                .field("District", district).field("Sub-district", subDistrict)
                .field("Farm size (ha)", farmSize).field("Area available for farming (ha)", farmingArea);
    }

    private static JTextField text(String placeholder) {
        JTextField f = new JTextField(16);
        if (placeholder != null) {
            f.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        }
        return f;
    }

    private void refreshSubDistricts() {
        String d = (String) district.getSelectedItem();
        subDistrict.setModel(new DefaultComboBoxModel<>(Districts.subDistrictsOf(d).toArray(String[]::new)));
    }

    /** Fills every field from {@code f}. */
    public void show(Farmer f) {
        idNumber.setText(f.idNumber());
        firstName.setText(f.firstName());
        lastName.setText(f.lastName());
        contact.setText(f.contact());
        dateOfBirth.setValue(Date.from(f.dateOfBirth().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        gender.setSelectedItem(f.gender());
        postalAddress.setText(f.postalAddress());
        email.setText(f.email());
        district.setSelectedItem(f.district());
        subDistrict.setSelectedItem(f.subDistrict());
        farmSize.setText(number(f.farmSize()));
        farmingArea.setText(number(f.farmingArea()));
    }

    /** Reads the fields into a farmer with {@code status}. Unreadable numbers become NaN and fail validation. */
    public Farmer read(Status status) {
        LocalDate dob = ((Date) dateOfBirth.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return new Farmer(idNumber.getText(), firstName.getText(), lastName.getText(), contact.getText(), dob,
                (Gender) gender.getSelectedItem(), postalAddress.getText(), (String) district.getSelectedItem(),
                (String) subDistrict.getSelectedItem(), email.getText(), parse(farmSize.getText()),
                parse(farmingArea.getText()), status);
    }

    public void setEditable(boolean editable) {
        for (JTextField f : new JTextField[]{idNumber, firstName, lastName, contact, postalAddress, email,
                farmSize, farmingArea}) {
            f.setEditable(editable);
        }
        dateOfBirth.setEnabled(editable);
        gender.setEnabled(editable);
        district.setEnabled(editable);
        subDistrict.setEnabled(editable);
    }

    static double parse(String s) {
        try {
            return Double.parseDouble(s.strip().replace(",", ""));
        } catch (NumberFormatException | NullPointerException e) {
            return Double.NaN;
        }
    }

    private static String number(double v) {
        return v == Math.rint(v) ? String.valueOf((long) v) : String.valueOf(v);
    }
}

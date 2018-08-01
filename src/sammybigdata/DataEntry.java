package sammybigdata;

import javafx.beans.property.*;

import java.sql.Date;

public class DataEntry {
    private IntegerProperty empNo;
    private SimpleObjectProperty<Date> birthDate;
    private StringProperty firstName;
    private StringProperty lastName;
    private SimpleObjectProperty<Character> gender;
    private SimpleObjectProperty<Date> hireDate;

    public DataEntry() {
        this.empNo = new SimpleIntegerProperty();
        this.birthDate = new SimpleObjectProperty<>();
        this.firstName = new SimpleStringProperty();
        this.lastName = new SimpleStringProperty();
        this.gender = new SimpleObjectProperty<>();
        this.hireDate = new SimpleObjectProperty<>();
    }

    public DataEntry(DataEntry dataEntry) {
        this.empNo = new SimpleIntegerProperty(dataEntry.getEmpNo());
        this.birthDate = new SimpleObjectProperty<>(dataEntry.getBirthDate());
        this.firstName = new SimpleStringProperty(dataEntry.getFirstName());
        this.lastName = new SimpleStringProperty(dataEntry.getLastName());
        this.gender = new SimpleObjectProperty<>(dataEntry.getGender());
        this.hireDate = new SimpleObjectProperty<>(dataEntry.getHireDate());
    }

    public int getEmpNo() {
        return empNo.get();
    }

    public IntegerProperty empNoProperty() {
        return empNo;
    }

    public void setEmpNo(int empNo) {
        this.empNo.set(empNo);
    }

    public Date getBirthDate() {
        return birthDate.get();
    }

    public SimpleObjectProperty<Date> birthDateProperty() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate.set(birthDate);
    }

    public String getFirstName() {
        return firstName.get();
    }

    public StringProperty firstNameProperty() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public String getLastName() {
        return lastName.get();
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public Character getGender() {
        return gender.get();
    }

    public SimpleObjectProperty<Character> genderProperty() {
        return gender;
    }

    public void setGender(Character gender) {
        this.gender.set(gender);
    }

    public Date getHireDate() {
        return hireDate.get();
    }

    public SimpleObjectProperty<Date> hireDateProperty() {
        return hireDate;
    }

    public void setHireDate(Date hireDate) {
        this.hireDate.set(hireDate);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof DataEntry)) {
            return false;
        }
        DataEntry other = (DataEntry) obj;
        return getEmpNo() == ((DataEntry) obj).getEmpNo() && getGender().equals(other.getGender());
    }
}

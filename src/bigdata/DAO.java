package bigdata;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DAO {
    public static ObservableList<DataEntry> searchEntry(String field, String value) throws SQLException, ClassNotFoundException {
        String sql = "select * from employees where " + field + " like '%" + value + "%' order by emp_no";

        try {
            ResultSet rs = DBUtil.executeQuery(sql);
            return getEntriesFromResultSet(rs);

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("An error occurred while searching " + value + " under column " + field + ".");
            throw e;
        }
    }

    public static ObservableList<DataEntry> searchEntries() throws SQLException, ClassNotFoundException {
        String sql = "select * from employees order by emp_no";

        try {
            ResultSet rs = DBUtil.executeQuery(sql);
            return getEntriesFromResultSet(rs);
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("An error occurred while searching.");
            throw e;
        }
    }

    private static ObservableList<DataEntry> getEntriesFromResultSet(ResultSet rs) throws SQLException, ClassNotFoundException {
        ObservableList<DataEntry> entries = FXCollections.observableArrayList();
        while (rs.next()) {
            DataEntry entry = new DataEntry();
            entry.setEmpNo(rs.getInt("emp_no"));
            entry.setBirthDate(rs.getDate("birth_date"));
            entry.setFirstName(rs.getString("first_name"));
            entry.setLastName(rs.getString("last_name"));
            entry.setGender(rs.getString("gender").charAt(0));
            entry.setHireDate(rs.getDate("hire_date"));
            entries.add(entry);
        }
        return entries;
    }

    public static void updateEntryStatus (Character gender, Integer empNo) throws SQLException, ClassNotFoundException {
        String sql = "update employees set gender = '" + gender + "' where emp_no = " + empNo;
        try {
            DBUtil.executeUpdate(sql);
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("An error occurred while updating employee: " + empNo + ".");
            throw e;
        }
    }

    public static void addEntry(String gene, String email, String sample, String control) throws SQLException, ClassNotFoundException {
        String sql = "insert into genes (gene, email, sample, control) values ('" +
                gene + "', '" + email + "', '" + sample + "', '" + control + "')";
        try {
            DBUtil.executeUpdate(sql);
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("An error occurred while inserting gene: " + gene + ".");
            throw e;
        }
    }

    public static void deleteEntry(String gene) throws SQLException, ClassNotFoundException {
        String sql = "delete from genes where gene = '" + gene + "'";
        try {
            DBUtil.executeUpdate(sql);
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("An error occurred while deleting gene: " + gene + ".");
            throw e;
        }
    }
}
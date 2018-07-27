package gene;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DAO {
    public static DataEntry searchEntry(String gene) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM genes WHERE gene = " + gene;

        try {
            ResultSet rs = DBUtil.executeQuery(sql);
            return getEntryFromResultSet(rs);

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("An error occurred while searching the gene: " + gene + ".");
            throw e;
        }
    }

    private static DataEntry getEntryFromResultSet(ResultSet rs) throws SQLException {
        DataEntry entry = null;

        if (rs.next()) {
            entry = new DataEntry();
            entry.setTimestamp(rs.getTimestamp("ts"));
            entry.setGene(rs.getString("gene"));
            entry.setEmail(rs.getString("email"));
            entry.setSample(rs.getString("sample"));
            entry.setControl(rs.getString("control"));
            entry.setClassified(rs.getBoolean("classified"));
            entry.setId(rs.getInt("id"));
        }

        return entry;
    }

    public static ObservableList<DataEntry> searchEntries() throws SQLException, ClassNotFoundException {
        String sql = "select * from genes";

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
            entry.setTimestamp(rs.getTimestamp("ts"));
            entry.setGene(rs.getString("gene"));
            entry.setEmail(rs.getString("email"));
            entry.setSample(rs.getString("sample"));
            entry.setControl(rs.getString("control"));
            entry.setClassified(rs.getBoolean("classified"));
            entry.setId(rs.getInt("id"));
            entries.add(entry);
        }
        return entries;
    }

    public static void updateEntryStatus (String gene, String email, boolean classified, int id) throws SQLException, ClassNotFoundException {
        String sql = "update genes set classified = " + classified + " where id=" + id + " and gene = '" + gene + "' and email = '" + email + "'";
        try {
            DBUtil.executeUpdate(sql);
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("An error occurred while updating gene: " + gene + ".");
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

package mergexperiment;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DAO {

    private static int firstIndex;
    private static int lastIndex;
    private static boolean searching = false;
    private static String field;
    private static String value;

    /**
     * Queries entries from the database with some search condition.
     * @param field The column being used as a condition (i.e. employee number)
     * @param value The search value.
     * @return ObservableList of DataEntry Objects that match the given condition, limited to 15
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static ObservableList<DataEntry> searchEntry(String field, String value) throws SQLException, ClassNotFoundException {
        String sql = "select * from test_data where " + field + " like '%" + value + "%' order by id LIMIT 15";
        searching = true;
        DAO.field = field;
        DAO.value = value;

        try {
            ResultSet rs = DBUtil.executeQuery(sql);
            return getEntriesFromResultSet(rs);

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("An error occurred while searching " + value + " under column " + field + ".");
            throw e;
        }
    }

    /**
     * Queries entries from the database without any conditions.
     * @return ObservableList of DataEntry Objects, limited to 15
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static ObservableList<DataEntry> searchEntries() throws SQLException, ClassNotFoundException {
        String sql = "select * from test_data order by id LIMIT 15";

        try {
            ResultSet rs = DBUtil.executeQuery(sql);
            return getEntriesFromResultSet(rs);
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("An error occurred while searching.");
            throw e;
        }
    }

    /**
     * Instantiates DataEntry Objects from a ResultSet, used for Last Page pagination.
     * @param rs
     * @return ObservableList of DataEntry Objects
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    private static ObservableList<DataEntry> getEntriesFromResultSet(ResultSet rs) throws SQLException, ClassNotFoundException {
        ObservableList<DataEntry> entries = FXCollections.observableArrayList();

        boolean first = true;
        while (rs.next()) {
            DataEntry entry = new DataEntry();
            entry.setId(rs.getInt("id"));
            if (first) {
                firstIndex = entry.getId();
                first = false;
            }
            lastIndex = entry.getId();
            entry.setControl(rs.getString("control"));
            entry.setGene(rs.getString("gene"));
            entry.setSample(rs.getString("sample"));
//            entry.setTimestamp(rs.getTimestamp("time_stamp"));
            entry.setAlleleFreq(rs.getDouble("allele_freq"));
            entry.setAvgCnvRatio(rs.getDouble("avg_cnv_ratio"));
            entry.setAvgDupRatio(rs.getDouble("avg_dup_ratio"));
            entry.setBbStd(rs.getDouble("bb_std"));
            entry.setCnvRatio(rs.getDouble("cnv_ratio"));
            entry.setCovStd(rs.getDouble("cov_std"));
            entry.setGcPerc(rs.getDouble("gc_perc"));
            entry.setHetClassification(rs.getBoolean("het_classification"));
            entry.setReadStats(rs.getInt("read_stats"));
            entry.setWindowId(rs.getString("window_id"));
            entry.setUsername(rs.getString("username"));
            entry.setAvgBowtieBwaRatio(rs.getDouble("avg_bowtie_bwa_ratio"));
            entry.setCnvRatioStd(rs.getDouble("cnv_ratio_std"));
            entry.setAvgCov(rs.getDouble("avg_cov"));
            entries.add(entry);
        }
        return entries;
    }

    /**
     * Instantiates DataEntry Objects from a ResultSet in an inverted order, used for Last Page pagination.
     * @param rs
     * @return ObservableList of DataEntry Objects
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    private static ObservableList<DataEntry> getReverseFromResultSet(ResultSet rs) throws SQLException, ClassNotFoundException {
        ObservableList<DataEntry> entries = FXCollections.observableArrayList();

        boolean firstSet = true;
        while (rs.next()) {
            DataEntry entry = new DataEntry();
            entry.setId(rs.getInt("id"));
            entry.setControl(rs.getString("control"));
            entry.setGene(rs.getString("gene"));
            entry.setSample(rs.getString("sample"));
//            entry.setTimestamp(rs.getTimestamp("time_stamp"));
            entry.setAlleleFreq(rs.getDouble("allele_freq"));
            entry.setAvgCnvRatio(rs.getDouble("avg_cnv_ratio"));
            entry.setAvgDupRatio(rs.getDouble("avg_dup_ratio"));
            entry.setBbStd(rs.getDouble("bb_std"));
            entry.setCnvRatio(rs.getDouble("cnv_ratio"));
            entry.setCovStd(rs.getDouble("cov_std"));
            entry.setGcPerc(rs.getDouble("gc_perc"));
            entry.setHetClassification(rs.getBoolean("het_classification"));
            entry.setReadStats(rs.getInt("read_stats"));
            entry.setWindowId(rs.getString("window_id"));
            entry.setUsername(rs.getString("username"));
            entry.setAvgBowtieBwaRatio(rs.getDouble("avg_bowtie_bwa_ratio"));
            entry.setCnvRatioStd(rs.getDouble("cnv_ratio_std"));
            entry.setAvgCov(rs.getDouble("avg_cov"));
            if (firstSet) {
                lastIndex = entry.getId();
                firstSet = false;
            }
            firstIndex = entry.getId();
            entries.add(0, entry);
            System.out.println(entries.toString());
        }
        return entries;
    }

    public static void updateEntryStatus (Boolean classification, Integer idNo) throws SQLException, ClassNotFoundException {
        String sql = "update test_data set het_classification = '" + classification + "' where id = " + idNo;
        try {
            DBUtil.executeUpdate(sql);
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("An error occurred while updating employee: " + idNo + ".");
            throw e;
        }
    }

//    public static void addEntry(String gene, String email, String sample, String control) throws SQLException, ClassNotFoundException {
//        String sql = "insert into genes (gene, email, sample, control) values ('" +
//                gene + "', '" + email + "', '" + sample + "', '" + control + "')";
//        try {
//            DBUtil.executeUpdate(sql);
//        } catch (SQLException | ClassNotFoundException e) {
//            System.out.println("An error occurred while inserting gene: " + gene + ".");
//            throw e;
//        }
//    }
//
//    public static void deleteEntry(String gene) throws SQLException, ClassNotFoundException {
//        String sql = "delete from genes where gene = '" + gene + "'";
//        try {
//            DBUtil.executeUpdate(sql);
//        } catch (SQLException | ClassNotFoundException e) {
//            System.out.println("An error occurred while deleting gene: " + gene + ".");
//            throw e;
//        }
//    }

    /**
     * Loads the last page of entries.
     * @return ObservableList of DataEntry Objects.
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static ObservableList<DataEntry> loadLast() throws SQLException, ClassNotFoundException {

        String sql;
        if (!searching) {
            sql = "select * from test_data where id < " + firstIndex + " order by id desc LIMIT 15;";
        } else {
            sql = "select * from test_data where id < " + firstIndex + " and " + DAO.field + " like '%" + DAO.value + "%' order by id desc LIMIT 15";
        }

        try {
            ResultSet rs = DBUtil.executeQuery(sql);
            return getReverseFromResultSet(rs);
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("An error occurred while searching.");
            throw e;
        }

    }

    /**
     * Loads the next page of entries.
     * @return ObservableList of DataEntry Objects.
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static ObservableList<DataEntry> loadNext() throws SQLException, ClassNotFoundException {

        String sql;
        if (!searching) {
            sql = "select * from test_data where id > " + lastIndex + " order by id asc LIMIT 15;";
        } else {
            sql = "select * from test_data where id > " + lastIndex + " and " + DAO.field + " like '%" + DAO.value + "%' order by id asc LIMIT 15";
        }

        try {
            ResultSet rs = DBUtil.executeQuery(sql);
            return getEntriesFromResultSet(rs);
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("An error occurred while searching.");
            throw e;
        }
    }
}
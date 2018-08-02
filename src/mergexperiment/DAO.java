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
        String sql = "select * from sample_data where " + field + " like '%" + value + "%' order by id LIMIT 15";
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
        String sql = "select * from sample_data order by id LIMIT 15";

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
        int lindex = lastIndex;
        int findex = firstIndex;

        boolean first = true;
        while (rs.next()) {
            DataEntry entry = new DataEntry();
            entry.setId(rs.getInt("id"));
            if (first) {
                firstIndex = entry.getId();
                first = false;
            }
            lastIndex = entry.getId();
            entry.setTimestamp(rs.getTimestamp("time_stamp"));
            entry.setUsername(rs.getString("username"));
            entry.setSample(rs.getString("sample"));
            entry.setControl(rs.getString("control"));
            entry.setWindowId(rs.getString("window_id"));
            entry.setGene(rs.getString("gene"));
            entry.setAvgCnvRatio(rs.getDouble("avg_cnv_ratio"));
            entry.setAvgBowtieBwaRatio(rs.getDouble("avg_bowtie_bwa_ratio"));
            entry.setBbStd(rs.getDouble("bb_std"));
            entry.setCnvRatioStd(rs.getDouble("cnv_ratio_std"));
            entry.setCovStd(rs.getDouble("cov_std"));
            entry.setAvgCov(rs.getDouble("avg_cov"));
            entry.setAvgDupRatio(rs.getDouble("avg_dup_ratio"));
            entry.setGcPerc(rs.getDouble("gc_perc"));
            entry.setAlleleFreq(rs.getDouble("allele_freq"));
            entry.setReadStats(rs.getInt("read_stats"));
            entry.setIsTraining(rs.getBoolean("is_training"));
            entry.setHetClassification(rs.getBoolean("het_classification"));
            entries.add(entry);
        }
        if (entries.size() > 0) {
            return entries;
        } else {
            lastIndex = lindex;
            firstIndex = findex;
            return FXCollections.observableArrayList();
        }
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
        int lindex = lastIndex;
        int findex = firstIndex;

        boolean firstSet = true;
        while (rs.next()) {
            DataEntry entry = new DataEntry();

            entry.setTimestamp(rs.getTimestamp("time_stamp"));
            entry.setUsername(rs.getString("username"));
            entry.setId(rs.getInt("id"));
            entry.setSample(rs.getString("sample"));
            entry.setControl(rs.getString("control"));
            entry.setWindowId(rs.getString("window_id"));
            entry.setGene(rs.getString("gene"));
            entry.setAvgCnvRatio(rs.getDouble("avg_cnv_ratio"));
            entry.setAvgBowtieBwaRatio(rs.getDouble("avg_bowtie_bwa_ratio"));
            entry.setBbStd(rs.getDouble("bb_std"));
            entry.setCnvRatioStd(rs.getDouble("cnv_ratio_std"));
            entry.setCovStd(rs.getDouble("cov_std"));
            entry.setAvgCov(rs.getDouble("avg_cov"));
            entry.setAvgDupRatio(rs.getDouble("avg_dup_ratio"));
            entry.setGcPerc(rs.getDouble("gc_perc"));
            entry.setAlleleFreq(rs.getDouble("allele_freq"));
            entry.setReadStats(rs.getInt("read_stats"));
            entry.setIsTraining(rs.getBoolean("is_training"));
            entry.setHetClassification(rs.getBoolean("het_classification"));
            if (firstSet) {
                lastIndex = entry.getId();
                firstSet = false;
            }
            firstIndex = entry.getId();
            entries.add(0, entry);
            System.out.println(entries.toString());
        }

        if (entries.size() > 0) {
            return entries;
        } else {
            lastIndex = lindex;
            firstIndex = findex;
            return FXCollections.observableArrayList();
        }
    }

    public static void updateHetClassification (Integer id, Boolean status) throws SQLException, ClassNotFoundException {
        String sql = "update sample_data set het_classification = " + status + " where id = " + id;
        try {
            DBUtil.executeUpdate(sql);
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("An error occurred while updating id: " + id + ".");
            throw e;
        }
    }

    /**
     * Loads the last page of entries.
     * @return ObservableList of DataEntry Objects.
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static ObservableList<DataEntry> loadLast() throws SQLException, ClassNotFoundException {

        String sql;
        if (!searching) {
            sql = "select * from sample_data where id < " + firstIndex + " order by id desc LIMIT 15;";
        } else {
            sql = "select * from sample_data where id < " + firstIndex + " and " + DAO.field + " like '%" + DAO.value + "%' order by id desc LIMIT 15";
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
            sql = "select * from sample_data where id > " + lastIndex + " order by id asc LIMIT 15;";
        } else {
            sql = "select * from sample_data where id > " + lastIndex + " and " + DAO.field + " like '%" + DAO.value + "%' order by id asc LIMIT 15";
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
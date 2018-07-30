package genexperiment;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * This class handles loading of data on different pages to minimize the amount of work that has to
 * be done at a time.
 */
public class PaginatingDataHandler {

    private int numPages;
    private final int ENTRIES_PER_PAGE = 15; //Limits the number of entries per page.
    private final int STORAGE_CAP = 20; //Limits the number of pages that will be stored.
    private String tableName;

    private Map<Integer, ObservableList<DataEntry>> dataByPage;   // Each time we load data for a page, store it

    private Connection conn;

    /**
     * The constructor gets and stores a connection from DBUtil and instantiates a new
     * HashMap for storing the page-data pairs. We also determine the number of pages that will be
     * included.
     * @param tableName
     */
    public PaginatingDataHandler(String tableName) {
        //        dataHandler.username = username;
        conn = DBUtil.getConnection();
//        dataHandler.conn = conn;
        dataByPage = new HashMap<>();
        this.tableName = tableName;

        String sql = "SELECT COUNT(*) FROM " + tableName + ";";

        try {
            ResultSet rs = DBUtil.executeQuery(sql);
            rs.next();
            numPages = rs.getInt("COUNT(*)");

            rs.close();
        } catch(SQLException se){
            se.printStackTrace();
        }catch(ClassNotFoundException e){
            //Handle errors for Class.forName
            e.printStackTrace();
        }
        numPages = (int) Math.ceil(((double) numPages)/((double) ENTRIES_PER_PAGE));
    }

    /**
     * Called when the user clicks on a new page. If the page has already been loaded, we
     * can immediately return it from the HashMap. If not, then we must load the data, add it
     * to the HashMap, and return it.
     * @param page
     * @return
     */
    public ObservableList<DataEntry> loadPage(int page) {
        System.out.println(page);
        System.out.println(dataByPage.toString());
        if (dataByPage.containsKey(page)) {
            System.out.println("Page already exists.");
            return dataByPage.get(page);
        } else {
            System.out.println("Page is being made.");
            loadNewPage(page);
            return dataByPage.get(page);
        }
    }

    /**
     * If we are loading a new page, we need to pull the data from the database and store
     * it locally.
     * @param page
     */
    private void loadNewPage(int page) {

        System.out.println("Initializing load new page.");

        Statement stmt = null;
        int indexStart = page * ENTRIES_PER_PAGE;
        ObservableList<DataEntry> newPageData = FXCollections.observableArrayList();

        // Get the entries
        String sql = "SELECT * FROM " + tableName + " LIMIT " + Integer.toString(indexStart) + ","
                + ENTRIES_PER_PAGE + ";";
        System.out.println(sql);

        if (conn != null) {
            try {
                stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);

                while (rs.next()) {
                    String gene = rs.getString("gene");
                    int id = rs.getInt("id");
                    Timestamp ts = rs.getTimestamp("ts");
                    boolean classified = rs.getBoolean("classified");
                    String control = rs.getString("control");
                    String sample = rs.getString("sample");
                    String email = rs.getString("email");

                    DataEntry newEntry = new DataEntry();

                    newEntry.setGene(gene);
                    newEntry.setId(id);
                    newEntry.setTimestamp(ts);
                    newEntry.setClassified(classified);
                    newEntry.setControl(control);
                    newEntry.setSample(sample);
                    newEntry.setEmail(email);
                    newPageData.add(newEntry);

                }
            } catch (SQLException se) {
                se.printStackTrace();
            }

            System.out.println("New Page Data:");
            System.out.println(newPageData.toString());
            if (newPageData.size() > 0) {
                System.out.println("Condition hit");
                dataByPage.put(page, newPageData);

                if (dataByPage.size() >= numPages) {
                    try{
                        conn.close(); //Close the connection if we know we've already loaded all entries.
                    } catch (SQLException se) {
                        se.printStackTrace();
                    }
                }
            }
        }
    }

    public String getTableName() { return tableName; }
    public Connection getConnection() { return conn; }
    public int getNumPages() { return numPages; }

}

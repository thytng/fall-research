package mergexperiment;

import com.sun.rowset.CachedRowSetImpl;

import javax.sql.rowset.CachedRowSet;
import java.sql.*;

/**
 * Includes a number of static methods for connecting to the database and
 * committing changes.
 */
public class DBUtil {
    private static Connection conn;

    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://138.68.228.126:3306/getiriao_iaa";
    private static String DB_USER = "getiriao_sammy";
    private static String DB_PASS = "JK7H{lcWxPa#";

    public static void setDbUser(String user) { DBUtil.DB_USER = user; }

    public static void setDbPass(String pass) { DBUtil.DB_PASS = pass; }

    /**
     * Create a new database connection.
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static void connect() throws SQLException, ClassNotFoundException {
        Class.forName(JDBC_DRIVER);

        conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        conn.setAutoCommit(false);
    }

    /**
     * Check to see if a database connection is already running and
     * create a new connection if there isn't.
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static void getConnection() throws SQLException, ClassNotFoundException {
        if (conn == null || conn.isClosed()) {
            connect();
        }
    }

    /**
     * Close the existing database connection.
     * @throws SQLException
     */
    public static void disconnect() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    /**
     * Return information about the schema of the database.
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static ResultSetMetaData getMetaData() throws SQLException, ClassNotFoundException {
        try {
            return executeQuery("select * from sample_data").getMetaData();
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Problem occurred at getMetaData operation: " + e);
            throw e;
        }
    }

    /**
     * Execute a sql query passed as a String.
     * @param sql
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static ResultSet executeQuery(String sql) throws SQLException, ClassNotFoundException {
        Statement stmt = null;
        ResultSet rs = null;
        CachedRowSet crs = null;
        try {
            getConnection();

            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            crs = new CachedRowSetImpl();
            crs.populate(rs);

        } catch (SQLException e) {
            System.out.println("Problem occurred at executeQuery operation: " + e);
            throw e;
        }

        return crs;
    }

    /**
     * Execute a sql update passed as a String.
     * Note: Updates are not committed on the database until the `commitChanges` method is called.
     * @param sql
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static void executeUpdate(String sql) throws SQLException, ClassNotFoundException {
        Statement stmt = null;
        System.out.println("\nUpdate statement: " + sql + ".");
        try {
            getConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);

        } catch (SQLException e) {
            System.out.println("\nProblem occurred at executeUpdate operation: " + e);
            System.out.println("Changes are NOT committed.");
            conn.rollback();
            throw e;
        }
    }

    /**
     * Push changes made to the database so that they are reflected in the remote database.
     */
    public static void commitChanges() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.commit();
                System.out.println("\nUpdate(s) committed.");
            }
        } catch (SQLException e) {
            System.out.println("\nProblem occurred at commitChanges operation: " + e);
            System.out.println("Changes are NOT committed.");
            revertChanges();
        }
    }

    /**
     * Undo any changes made to the database.
     */
    public static void revertChanges() {
        try {
            if (conn != null && !conn.isClosed()) {
                System.out.println("\nReverted changes.");
                conn.rollback();
            }
        } catch (SQLException e) {
            System.out.println("Problem occurred at rollback.");
            e.printStackTrace();
        }
    }
}


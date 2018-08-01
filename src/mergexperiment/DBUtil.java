package mergexperiment;

import com.sun.rowset.CachedRowSetImpl;

import javax.sql.rowset.CachedRowSet;
import java.sql.*;

public class DBUtil {
    private static Connection conn;

    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/employees?useSSL=false" +
            "&useLegacyDatetimeCode=false&serverTimezone=America/New_York";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Swa!Exa4";

    public static void connect() throws SQLException, ClassNotFoundException {
        Class.forName(JDBC_DRIVER);

        conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        conn.setAutoCommit(false);
    }

    public static void getConnection() throws SQLException, ClassNotFoundException {
        if (conn == null || conn.isClosed()) {
            connect();
        }
    }

    public static void disconnect() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    public static ResultSetMetaData getMetaData() throws SQLException, ClassNotFoundException {
        try {
            return executeQuery("select * from employees").getMetaData();
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Problem occurred at getMetaData operation: " + e);
            throw e;
        }
    }

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


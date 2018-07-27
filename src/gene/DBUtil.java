package gene;

import com.sun.rowset.CachedRowSetImpl;

import javax.sql.rowset.CachedRowSet;
import java.sql.*;

public class DBUtil {
    private static Connection conn;

    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
<<<<<<< HEAD
    private static final String DB_URL = "jdbc:mysql://localhost:3306/fall2018?useSSL=false" +
            "&useLegacyDatetimeCode=false&serverTimezone=America/New_York";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "pass";
=======
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mysql";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Swa!Exa4";
>>>>>>> origin/master

    public static void connect() throws SQLException, ClassNotFoundException {
        Class.forName(JDBC_DRIVER);

        conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        conn.setAutoCommit(false);
    }

    public static void disconnect() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    public static ResultSetMetaData getMetaData() throws SQLException, ClassNotFoundException {
        try {
            return executeQuery("select * from genes").getMetaData();
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
            connect();

            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            crs = new CachedRowSetImpl();
            crs.populate(rs);

        } catch (SQLException e) {
            System.out.println("Problem occurred at executeQuery operation: " + e);
            throw e;

        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            disconnect();
        }
        return crs;
    }

    public static void executeUpdate(String sql) throws SQLException, ClassNotFoundException {
        Statement stmt = null;
        System.out.println("Update statement: " + sql + ".");
        try {
            connect();
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            conn.commit();
            System.out.println("Update committed. \n");

        } catch (SQLException e) {
            System.out.println("Problem occurred at executeUpdate operation: " + e);
            System.out.println("Changes are NOT committed. \n");
            conn.rollback();
            throw e;
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            disconnect();
        }
    }
}


package ru.netology.data;

import org.apache.commons.dbutils.QueryRunner;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLHelper {
    private static final QueryRunner QUERY_RUNNER = new QueryRunner();

    private SQLHelper() {
    }

    private static Connection getConn() throws SQLException {
        return DriverManager.getConnection(
                System.getProperty("db.url"), "app", "pass");
    }

    public static String getVerificationCode() {
        String codeSQL = "SELECT code FROM auth_codes ORDER BY created DESC LIMIT 1";
        try (Connection conn = getConn()) {
            return QUERY_RUNNER.query(conn, codeSQL, rs -> rs.next() ? rs.getString("code") : null);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void cleanDatabase() {
        try (Connection conn = getConn()) {
            QUERY_RUNNER.execute(conn, "DELETE FROM auth_codes");
            QUERY_RUNNER.execute(conn, "DELETE FROM card_transactions");
            QUERY_RUNNER.execute(conn, "DELETE FROM cards");
            QUERY_RUNNER.execute(conn, "DELETE FROM users");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void cleanAuthCodes() {
        try (Connection conn = getConn()) {
            QUERY_RUNNER.execute(conn, "DELETE FROM auth_codes");
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка очистки кодов: " + e.getMessage(), e);
        }
    }
}
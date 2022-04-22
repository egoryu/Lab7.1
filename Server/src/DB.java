import java.sql.*;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;

public class DB {
    private static Connection connection = null;

    public static boolean connect(String login, String password) {
        try {
            if (connection == null)
                connection = DriverManager.getConnection(MyConstant.URL, login, password);
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static boolean checkLogin(String login) {
        String query = "SELECT count(*) from USER_TABLE where USER_LOGIN = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setString(1, login);
            pst.execute();
            try (ResultSet rs = pst.getResultSet()) {
                rs.next();
                int count = rs.getInt(1);
                return count == 1;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static boolean signUp(String login, String password) {
        String query = "INSERT INTO USER_TABLE (USER_LOGIN, USER_PASSWORD) VALUES (?, ?)";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setString(1, login);
            pst.setString(2, password);
            int row = pst.executeUpdate();
            return row > 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static boolean logIn(String login, String password) {
        String query = "SELECT USER_PASSWORD FROM USER_TABLE WHERE USER_LOGIN = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setString(1, login);
            pst.execute();
            try (ResultSet rs = pst.getResultSet()) {
                rs.next();
                if (!password.equals(rs.getString(1))) {
                    return false;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    public static boolean insertLabWork(String key, LabWork labWork) {
        String query = "INSERT INTO LABWORK_TABLE (LABWORK_KEY, LABWORK_ID, LABWORK_NAME, LABWORK_X, LABWORK_Y, LABWORK_DATAOFCREATE, LABWORK_MINIMALPOINT, LABWORK_DISCRIPTION, LABWORK_DIFFICULTY, LABWORK_PERSONNAME, LABWORK_HEIGHT, LABWORK_WEIGHT) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setString(1, key);
            pst.setInt(2, labWork.getId());
            pst.setString(3, labWork.getName());
            pst.setFloat(4, labWork.getCoordinates().getX());
            pst.setInt(5, labWork.getCoordinates().getY());
            pst.setDate(6, Date.valueOf(labWork.getCreationDate().toLocalDate()));
            pst.setInt(7, labWork.getMinimalPoint());
            pst.setString(8, labWork.getDescription());
            pst.setString(9, labWork.getDifficulty().toString());
            pst.setString(10, labWork.getAuthor().getName());
            pst.setDouble(11, labWork.getAuthor().getHeight());
            pst.setDouble(12, labWork.getAuthor().getWeight());
            int row = pst.executeUpdate();
            return row > 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static LinkedHashMap<String, LabWork> readLabWork() {
        LinkedHashMap<String, LabWork> collection = new LinkedHashMap<>();
        String query = "SELECT * FROM LABWORK_TABLE";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.execute();
            try (ResultSet rs = pst.getResultSet()) {
                while (rs.next()) {
                    String key = rs.getString(1);
                    LabWork labWork = new LabWork(rs.getInt(2), rs.getString(3),
                            new Coordinates(rs.getFloat(4), rs.getInt(5)),
                            ZonedDateTime.of(rs.getDate(6).toLocalDate(), LocalTime.now(), ZoneId.of("Europe/Moscow")),
                            rs.getInt(7), rs.getString(8), Difficulty.valueOf(rs.getString(9)),
                            new Person(rs.getString(10), null, rs.getInt(12), rs.getInt(13)));
                    collection.put(key, labWork);
                }
            }
            return collection;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static boolean accessCheck(String login, int id) {
        String query = "SELECT count(*) from THING_TABLE where THING_ID = ? AND USER_LOGIN = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setString(2, login);
            pst.setInt(1, id);
            pst.execute();
            try (ResultSet rs = pst.getResultSet()) {
                rs.next();
                return rs.getInt(1) == 1;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static boolean removeLabwork(int id) {
        String query = "DELETE from LABWORK_TABLE where LABWORK_ID = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, id);
            pst.execute();
            int row = pst.executeUpdate();
            return row > 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static boolean updateLabWork(int id, LabWork labWork) {
        String query = "UPDATE LABWORK_TABLE SET LABWORK_ID = ?, LABWORK_NAME = ?, LABWORK_X = ?," +
                " LABWORK_Y = ?, LABWORK_DATAOFCREATE = ?, LABWORK_MINIMALPOINT = ?, LABWORK_DISCRIPTION = ?," +
                " LABWORK_DIFFICULTY = ?, LABWORK_PERSONNAME = ?, LABWORK_HEIGHT = ?, LABWORK_WEIGHT = ? WHERE LABWORK_ID = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, labWork.getId());
            pst.setString(2, labWork.getName());
            pst.setFloat(3, labWork.getCoordinates().getX());
            pst.setInt(4, labWork.getCoordinates().getY());
            pst.setDate(5, Date.valueOf(labWork.getCreationDate().toLocalDate()));
            pst.setInt(6, labWork.getMinimalPoint());
            pst.setString(7, labWork.getDescription());
            pst.setString(8, labWork.getDifficulty().toString());
            pst.setString(9, labWork.getAuthor().getName());
            pst.setDouble(10, labWork.getAuthor().getHeight());
            pst.setDouble(11, labWork.getAuthor().getWeight());
            pst.setInt(12, id);
            int row = pst.executeUpdate();
            return row > 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static boolean addThing(String login, int id) {
        String query = "INSERT INTO THING_TABLE (USER_LOGIN, THING_ID) VALUES (?, ?)";
        System.out.println(login + " " + id);
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setString(1, login);
            pst.setInt(2, id);
            int row = pst.executeUpdate();
            return row > 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}

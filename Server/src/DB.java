import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.ConcurrentHashMap;

public class DB {
    private static Connection connection = null;

    public static boolean connect(String login, String password) {
        try {
            if (connection == null) {
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(MyConstant.URL, login, password);
            }
            return true;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage() + "lol");
            e.printStackTrace();
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

    public static boolean signUp(String login, String password, String sold) {
        String query = "INSERT INTO USER_TABLE (USER_LOGIN, USER_PASSWORD, SOLD) VALUES (?, ?, ?)";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setString(1, login);
            pst.setString(2, password);
            pst.setString(3, sold);
            int row = pst.executeUpdate();
            return row > 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static boolean logIn(String login, String password) {
        String query = "SELECT * FROM USER_TABLE WHERE USER_LOGIN = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setString(1, login);
            pst.execute();
            try (ResultSet rs = pst.getResultSet()) {
                rs.next();
                String pass = Useful.generatePassword(password, rs.getString(3));
                if (!pass.equals(rs.getString(2))) {
                    return false;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    public static int insertLabWork(String key, LabWork labWork) {
        String query;
        if (labWork.getAuthor().getBirthday() == null)
            query = "INSERT INTO LABWORK_TABLE (LABWORK_KEY, LABWORK_NAME, LABWORK_X, LABWORK_Y, LABWORK_DATAOFCREATE," +
                    " LABWORK_TIMEOFCREATE, LABWORK_MINIMALPOINT, LABWORK_DISCRIPTION, LABWORK_DIFFICULTY," +
                    " LABWORK_PERSONNAME, LABWORK_HEIGHT, LABWORK_WEIGHT) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
        else
            query = "INSERT INTO LABWORK_TABLE (LABWORK_KEY, LABWORK_NAME, LABWORK_X, LABWORK_Y, LABWORK_DATAOFCREATE," +
                    " LABWORK_TIMEOFCREATE, LABWORK_MINIMALPOINT, LABWORK_DISCRIPTION, LABWORK_DIFFICULTY," +
                    " LABWORK_PERSONNAME, LABWORK_HEIGHT, LABWORK_WEIGHT, LABWORK_BIRTHDAY, LABWORK_BIRTHDAYTIME) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setString(1, key);
            pst.setString(2, labWork.getName());
            pst.setFloat(3, labWork.getCoordinates().getX());
            pst.setInt(4, labWork.getCoordinates().getY());
            pst.setDate(5, Date.valueOf(labWork.getCreationDate().toLocalDate()));
            pst.setTime(6, Time.valueOf(labWork.getCreationDate().toLocalTime()));
            pst.setInt(7, labWork.getMinimalPoint());
            pst.setString(8, labWork.getDescription());
            pst.setString(9, labWork.getDifficulty().toString());
            pst.setString(10, labWork.getAuthor().getName());
            pst.setDouble(11, labWork.getAuthor().getHeight());
            pst.setDouble(12, labWork.getAuthor().getWeight());
            if (labWork.getAuthor().getBirthday() != null) {
                pst.setDate(13, Date.valueOf(labWork.getAuthor().getBirthday().toLocalDate()));
                pst.setTime(14, Time.valueOf(labWork.getAuthor().getBirthday().toLocalTime()));
            }
            pst.execute();
            ResultSet rs = pst.getResultSet();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    public static ConcurrentHashMap<String, LabWork> readLabWork() {
        ConcurrentHashMap<String, LabWork> collection = new ConcurrentHashMap<>();
        String query = "SELECT * FROM LABWORK_TABLE";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.execute();
            try (ResultSet rs = pst.getResultSet()) {
                while (rs.next()) {
                    String key = rs.getString(1);
                    LabWork labWork;
                    if (rs.getDate(12) == null) {
                        labWork = new LabWork(rs.getInt(2), rs.getString(3),
                                new Coordinates(rs.getFloat(4), rs.getInt(5)),
                                ZonedDateTime.of(rs.getDate(6).toLocalDate(), rs.getTime(7).toLocalTime(), ZoneId.of("Europe/Moscow")),
                                rs.getInt(8), rs.getString(9), Difficulty.valueOf(rs.getString(10)),
                                new Person(rs.getString(11), null, rs.getInt(14), rs.getInt(15)));
                    } else {
                        labWork = new LabWork(rs.getInt(2), rs.getString(3),
                                new Coordinates(rs.getFloat(4), rs.getInt(5)),
                                ZonedDateTime.of(rs.getDate(6).toLocalDate(), rs.getTime(7).toLocalTime(), ZoneId.of("Europe/Moscow")),
                                rs.getInt(8), rs.getString(9), Difficulty.valueOf(rs.getString(10)),
                                new Person(rs.getString(11),
                                        ZonedDateTime.of(rs.getDate(12).toLocalDate(), rs.getTime(13).toLocalTime(), ZoneId.of("Europe/Moscow")),
                                        rs.getInt(14), rs.getInt(15)));
                    }

                    labWork.setId(rs.getInt(2));
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
        String query = "DELETE from LABWORK_TABLE where id = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, id);
            int row = pst.executeUpdate();
            return row > 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static boolean updateLabWork(int id, LabWork labWork) {
        String query;
        if (labWork.getAuthor().getBirthday() == null) {
            query = "UPDATE LABWORK_TABLE SET LABWORK_TIMEOFCREATE = ?, LABWORK_NAME = ?, LABWORK_X = ?," +
                    " LABWORK_Y = ?, LABWORK_DATAOFCREATE = ?, LABWORK_MINIMALPOINT = ?, LABWORK_DISCRIPTION = ?," +
                    " LABWORK_DIFFICULTY = ?, LABWORK_PERSONNAME = ?, LABWORK_HEIGHT = ?, LABWORK_WEIGHT = ? WHERE id = ?";
        } else {
            query = "UPDATE LABWORK_TABLE SET LABWORK_TIMEOFCREATE = ?, LABWORK_NAME = ?, LABWORK_X = ?," +
                    " LABWORK_Y = ?, LABWORK_DATAOFCREATE = ?, LABWORK_MINIMALPOINT = ?, LABWORK_DISCRIPTION = ?," +
                    " LABWORK_DIFFICULTY = ?, LABWORK_PERSONNAME = ?, LABWORK_HEIGHT = ?, LABWORK_WEIGHT = ?, LABWORK_BIRTHDAY = ?, LABWORK_BIRTHDAYTIME =? WHERE id = ?";
        }
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setTime(1, Time.valueOf(labWork.getCreationDate().toLocalTime()));
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
            if (labWork.getAuthor().getBirthday() == null) {
                pst.setInt(12, id);
            } else {
                pst.setDate(12, Date.valueOf(labWork.getAuthor().getBirthday().toLocalDate()));
                pst.setTime(13, Time.valueOf(labWork.getAuthor().getBirthday().toLocalTime()));
                pst.setInt(14, id);
            }
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

    public static boolean removeAccess(int id) {
        String query = "DELETE from THING_TABLE where THING_ID = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, id);
            int row = pst.executeUpdate();
            return row > 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}

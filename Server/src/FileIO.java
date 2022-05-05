import java.io.*;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FileIO {
    public static ConcurrentHashMap<String, LabWork> Read(String name, char del) {
        ConcurrentHashMap<String, LabWork> collection = new ConcurrentHashMap<>();
        InputStreamReader reader;
        try {
            reader = new InputStreamReader(
                    new FileInputStream(name));
        } catch (FileNotFoundException e) {
            System.out.println("Не получилось открыть файл");
            return null;
        }
        int c;
        ArrayList<String> st = new ArrayList<>();
        String tmp = "";

        while (true) {
            try {
                if ((c = reader.read()) == -1) break;
            } catch (IOException e) {
                System.out.println("Ошибка при чтении");
                return null;
            }
            if (c == '\n') {
                st.add(tmp);
                tmp = "";
            }
            else
                tmp += (char) c;
        }

        for (String cur : st) {
            String[] element = cur.split(String.valueOf(del));

            if (checkLabWork(element)) {
                Month[] months = Month.values();

                if (element[15].equals("null")) {
                    Coordinates coordinates = new Coordinates(Float.parseFloat(element[3]), Integer.parseInt(element[4]));

                    ZoneId zone = ZoneId.of("Europe/Moscow");
                    LocalDateTime creation = LocalDateTime.of(Integer.parseInt(element[5]),
                            months[Integer.parseInt(element[6]) - 1],
                            Integer.parseInt(element[7]),
                            Integer.parseInt(element[8]),
                            Integer.parseInt(element[9]),
                            Integer.parseInt(element[10]));

                    java.time.ZonedDateTime labCreation = ZonedDateTime.of(creation, zone);

                    Person person = new Person(element[14],
                            null,
                            Integer.parseInt(element[16]),
                            Integer.parseInt(element[17]));

                    LabWork labWork = new LabWork(Integer.parseInt(element[1]), element[2],
                            coordinates, labCreation,
                            Integer.parseInt(element[11]),
                            element[12],
                            Difficulty.valueOf(element[13]), person);

                    collection.put(element[0], labWork);
                }
                else {
                    Coordinates coordinates = new Coordinates(Float.parseFloat(element[3]), Integer.parseInt(element[4]));

                    ZoneId zone = ZoneId.of("Europe/Moscow");
                    LocalDateTime time = LocalDateTime.of(Integer.parseInt(element[15]),
                            months[Integer.parseInt(element[16]) - 1],
                            Integer.parseInt(element[17]),
                            Integer.parseInt(element[18]),
                            Integer.parseInt(element[19]));

                    java.time.ZonedDateTime birthday = ZonedDateTime.of(time, zone);

                    LocalDateTime creation = LocalDateTime.of(Integer.parseInt(element[5]),
                            months[Integer.parseInt(element[6]) - 1],
                            Integer.parseInt(element[7]),
                            Integer.parseInt(element[8]),
                            Integer.parseInt(element[9]),
                            Integer.parseInt(element[10]));

                    java.time.ZonedDateTime labcreation = ZonedDateTime.of(creation, zone);

                    Person person = new Person(element[14],
                            birthday,
                            Integer.parseInt(element[20]),
                            Integer.parseInt(element[21]));

                    LabWork labWork = new LabWork(Integer.parseInt(element[1]), element[2], coordinates,
                            labcreation,
                            Integer.parseInt(element[11]),
                            element[12],
                            Difficulty.valueOf(element[13]), person);

                    collection.put(element[0], labWork);
                }
            } else {
                System.out.println("В базе есть некорректно записанный элемент!");
            }

        }

        try {
            reader.close();
        } catch (IOException e) {
            System.out.println("Произошла ошибка закрытия файла");
        }

        return collection;
    }

    public static void Write(ConcurrentHashMap<String, LabWork> collection, String name, char del) throws IOException {
        OutputStreamWriter writer;
        try {
            writer = new OutputStreamWriter(
                    new FileOutputStream(name));
        } catch (FileNotFoundException e) {
            System.out.println("Не получилось открыть файл");
            return;
        }
        List<Map.Entry<String, LabWork>> entries =
                new ArrayList<>(collection.entrySet());

        for (Map.Entry<String, LabWork> entry : entries) {
            writer.write(entry.getKey() + del + entry.getValue().parse(del) + '\n');
        }
        writer.close();
    }

    public static boolean checkLabWork(String[] element) {
        String a = Arrays.toString(Difficulty.values());

        if (element.length == 18) {
            if (element[0].isEmpty()
                    || !Useful.isInteger(element[1])
                    || element[2].isEmpty()
                    || !Useful.isFloat(element[3])
                    || !Useful.isInteger(element[4])
                    || !Useful.isInteger(element[5]) || Integer.parseInt(element[5]) <= 1970 || Integer.parseInt(element[5]) >= 2023
                    || !Useful.isInteger(element[6]) || Integer.parseInt(element[6]) <= 0 || Integer.parseInt(element[6]) >= 13
                    || !Useful.isInteger(element[7]) || Integer.parseInt(element[7]) <= 0 || Integer.parseInt(element[7]) >= 32
                    || !Useful.isInteger(element[8]) || Integer.parseInt(element[8]) < 0 || Integer.parseInt(element[8]) >= 24
                    || !Useful.isInteger(element[9]) || Integer.parseInt(element[9]) < 0 || Integer.parseInt(element[9]) >= 60
                    || !Useful.isInteger(element[10]) || Integer.parseInt(element[10]) < 0 || Integer.parseInt(element[10]) >= 60
                    || !Useful.isInteger(element[11]) || Integer.parseInt(element[11]) <= 0
                    || element[12].isEmpty() || element[12].length() >= 5207 || element[12].contains(";")
                    || !a.contains(element[13])
                    || element[14].isEmpty()
                    || element[15].isEmpty() || !element[15].equals("null")
                    || !Useful.isInteger(element[16]) || Integer.parseInt(element[16]) <= 0
                    || !Useful.isInteger(element[17]) || Integer.parseInt(element[17]) <= 0) {
                return false;
            }
        } else if (element.length == 22){
            if (element[0].isEmpty()
                    || !Useful.isInteger(element[1])
                    || element[2].isEmpty()
                    || !Useful.isFloat(element[3])
                    || !Useful.isInteger(element[4])
                    || !Useful.isInteger(element[5]) || Integer.parseInt(element[5]) <= 1970 || Integer.parseInt(element[5]) >= 2023
                    || !Useful.isInteger(element[6]) || Integer.parseInt(element[6]) <= 0 || Integer.parseInt(element[6]) >= 13
                    || !Useful.isInteger(element[7]) || Integer.parseInt(element[7]) <= 0 || Integer.parseInt(element[7]) >= 32
                    || !Useful.isInteger(element[8]) || Integer.parseInt(element[8]) < 0 || Integer.parseInt(element[8]) >= 24
                    || !Useful.isInteger(element[9]) || Integer.parseInt(element[9]) < 0 || Integer.parseInt(element[9]) >= 60
                    || !Useful.isInteger(element[10]) || Integer.parseInt(element[10]) < 0 || Integer.parseInt(element[10]) >= 60
                    || !Useful.isInteger(element[11]) || Integer.parseInt(element[11]) <= 0
                    || element[12].isEmpty() || element[12].length() >= 5207 || element[12].contains(";")
                    || !a.contains(element[13])
                    || element[14].isEmpty()
                    || !Useful.isInteger(element[15]) || Integer.parseInt(element[15]) <= 1970 || Integer.parseInt(element[15]) >= 2023
                    || !Useful.isInteger(element[16]) || Integer.parseInt(element[16]) <= 0 || Integer.parseInt(element[16]) >= 13
                    || !Useful.isInteger(element[17]) || Integer.parseInt(element[17]) <= 0 || Integer.parseInt(element[17]) >= 32
                    || !Useful.isInteger(element[18]) || Integer.parseInt(element[18]) < 0 || Integer.parseInt(element[18]) >= 24
                    || !Useful.isInteger(element[19]) || Integer.parseInt(element[19]) < 0 || Integer.parseInt(element[19]) >= 60
                    || !Useful.isInteger(element[20]) || Integer.parseInt(element[20]) <= 0
                    || !Useful.isInteger(element[21]) || Integer.parseInt(element[21]) <= 0) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    public static boolean CheckedWrite(String name) {
        File file;

        file = new File(name);
        if (file == null || !file.canWrite()) {
            return false;
        }
        return true;
    }

    public static boolean CheckedRead(String name) {
        File file;

        file = new File(name);
        if (file == null || !file.canRead()) {
            return false;
        }
        return true;
    }
}


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Script {
    public static ArrayList<String> scriptHistory = new ArrayList<>();
    ArrayList<String> answer = new ArrayList<>();

    public ConcurrentHashMap<String, LabWork> makeScript(ConcurrentHashMap<String, LabWork> collection, String script, String saveFile) {
        if (scriptHistory.contains(script)) {
            answer.add("Образовался цикл из команд");
            scriptHistory.remove(script);
            return collection;
        } else {
            scriptHistory.add(script);
        }

        InputStreamReader reader;
        try {
            reader = new InputStreamReader(new FileInputStream(script));
        } catch (FileNotFoundException e) {
            answer.add("Не получилось открыть файл");
            scriptHistory.remove(script);
            return collection;
        }

        int c;
        ArrayList<String> strScript = new ArrayList<>();
        String tmp = "";

        while (true) {
            try {
                if ((c = reader.read()) == -1)
                    break;
            } catch (IOException e) {
                answer.add("Ошибка при чтении");
                return null;
            }

            if (c == '\n') {
                strScript.add(tmp);
                tmp = "";
            } else if (c != '\r')
                tmp += (char) c;
        }

        ArrayDeque<String> history = new ArrayDeque<>();
        for (int i = 0; i < strScript.size(); i++) {
            String[] current = strScript.get(i).split(" ");
            Menu menu = new Menu();

            if (current[0].isEmpty())
                continue;

            history.addLast(current[0]);
            switch (current[0]) {
                case ("help"):
                    menu.help();
                    break;
                case ("show"):
                    menu.show(collection);
                    break;
                case ("info"):
                    menu.info(collection);
                    break;
                case ("insert"):
                    if (current.length < 2 || current[1].isEmpty()) {
                        menu.answer.add("Неправильно введена команда в скрипте");
                        scriptHistory.remove(script);
                        return collection;
                    }
                    int x = checkScript(strScript, i);

                    if (x == 0) {
                        menu.answer.add("Неправильно введена команда в скрипте");
                        scriptHistory.remove(script);
                        return collection;
                    }

                    collection = menu.insert(collection, current[1], Script.insertScript(strScript, i));
                    collection = Useful.lhmSort(collection);
                    i += x;
                    break;
                case ("remove_key"):
                    if (current.length < 2 || current[1].isEmpty()) {
                        System.out.println("Неправильно введена команда в скрипте");
                        scriptHistory.remove(script);
                        return collection;
                    }
                    collection = menu.removeKey(collection, current[1]);
                    break;
                case ("clear"):
                    collection.clear();
                    menu.answer.add("Коллекция очищена");
                    break;
                case ("history"):
                    menu.history(history);
                    break;
                case ("update"):
                    if (current.length < 2 || !Useful.isInteger(current[1])) {
                        menu.answer.add("Неправильно введена команда в скрипте");
                        scriptHistory.remove(script);
                        return collection;
                    }

                    int x1 = checkScript(strScript, i);

                    if (x1 == 0) {
                        menu.answer.add("Неправильно введена команда в скрипте");
                        scriptHistory.remove(script);
                        return collection;
                    }

                    collection = menu.update(collection, current[1], insertScript(strScript, i));

                    i += x1;
                    break;
                case ("sum_of_minimal_point"):
                    menu.answer.add(String.valueOf(menu.sumOfMinimalPoint(collection)));
                    break;
                case ("max_by_name"):
                    menu.answer.add(String.valueOf(menu.maxByName(collection)));
                    break;
                case ("count_by_minimal_point"):
                    if (current.length < 2 || !Useful.isInteger(current[1])) {
                        menu.answer.add("Неправильно введена команда в скрипте");
                        scriptHistory.remove(script);
                        return collection;
                    }
                    menu.answer.add(String.valueOf(menu.countByMinimalPoint(collection, current[1])));
                    break;
                case ("remove_lower_key"):
                    if (current.length < 2 || current[1].isEmpty()) {
                        menu.answer.add("Неправильно введена команда в скрипте");
                        scriptHistory.remove(script);
                        return collection;
                    }
                    collection = menu.removeLowerKey(collection, current[1]);
                    break;
                case ("replace_if_greater"):
                    int x2 = checkScript(strScript, i);

                    if (x2 == 0) {
                        menu.answer.add("Неправильно введена команда в скрипте");
                        scriptHistory.remove(script);
                        return collection;
                    }

                    LabWork labWork = Script.insertScript(strScript, i);

                    if (current.length < 2 || current[1].isEmpty()) {
                        menu.answer.add("Неправильно введена команда в скрипте");
                        scriptHistory.remove(script);
                        return collection;
                    }

                    collection = menu.replaceIfGreater(collection, current[1], labWork);

                    i += x2;
                    break;
                case ("save"):
                    menu.savetoFile(collection, saveFile, ';');
                    break;
                case ("execute_script"):
                    if (current.length < 2 || current[1].isEmpty()) {
                        menu.answer.add("Неправильно введена команда в скрипте");
                        scriptHistory.remove(script);
                        return collection;
                    }
                    collection = menu.executeScript(collection, current[1], saveFile);
                    break;
                default:
                    answer.add("Неправильно введена команда в скрипте");
                    scriptHistory.remove(script);
                    return collection;
            }
            if (history.size() > 12)
                history.poll();
            answer.addAll(menu.answer);
        }
        scriptHistory.remove(script);
        return collection;
    }

    public static LabWork insertScript(ArrayList<String> strScript, int position) {
        Coordinates coordinates = new Coordinates(Float.parseFloat(strScript.get(position + 2)), Integer.parseInt(strScript.get(position + 3)));
        Person person;

        if (strScript.get(position + 8).isEmpty())
            person = new Person(strScript.get(position + 7), null, Integer.parseInt(strScript.get(position + 9)), Integer.parseInt(strScript.get(position + 10)));
        else {
            Month[] months = Month.values();
            ZoneId zone = ZoneId.of("Europe/Moscow");
            LocalDateTime time = LocalDateTime.of(Integer.parseInt(strScript.get(position + 8)),
                    months[Integer.parseInt(strScript.get(position + 9)) - 1],
                    Integer.parseInt(strScript.get(position + 10)),
                    Integer.parseInt(strScript.get(position + 11)),
                    Integer.parseInt(strScript.get(position + 12)));
            java.time.ZonedDateTime birthday = ZonedDateTime.of(time, zone);

            person = new Person(strScript.get(position + 7), birthday, Integer.parseInt(strScript.get(position + 13)), Integer.parseInt(strScript.get(position + 14)));
        }

        return new LabWork(strScript.get(position + 1), coordinates, Integer.parseInt(strScript.get(position + 4)), strScript.get(position + 5), Difficulty.valueOf(strScript.get(position + 6)), person);
    }

    public static int checkScript(ArrayList<String> strScript, int position) {
        if (strScript.size() - position - 1 < 10)
            return 0;
        else {
            String a = Arrays.toString(Difficulty.values());
            if (strScript.get(position + 8).isEmpty()) {
                if (strScript.get(position + 1).isEmpty()
                        || !Useful.isFloat(strScript.get(position + 2))
                        || !Useful.isInteger(strScript.get(position + 3))
                        || !Useful.isInteger(strScript.get(position + 4)) || Integer.parseInt(strScript.get(position + 4)) <= 0
                        || strScript.get(position + 5).isEmpty() || strScript.get(position + 5).length() >= 5207 || strScript.get(position + 5).contains(";")
                        || !a.contains(strScript.get(position + 6))
                        || strScript.get(position + 7).isEmpty()
                        || !strScript.get(position + 8).isEmpty()
                        || !Useful.isInteger(strScript.get(position + 9)) || Integer.parseInt(strScript.get(position + 9)) <= 0
                        || !Useful.isInteger(strScript.get(position + 10)) || Integer.parseInt(strScript.get(position + 10)) <= 0) {
                    return 0;
                }
                else {
                    return 10;
                }
            } else if (strScript.size() - position - 1 >= 14) {
                if (strScript.get(position + 1).isEmpty()
                        || !Useful.isFloat(strScript.get(position + 2))
                        || !Useful.isInteger(strScript.get(position + 3))
                        || !Useful.isInteger(strScript.get(position + 4)) || Integer.parseInt(strScript.get(position + 4)) <= 0
                        || strScript.get(position + 5).isEmpty() || strScript.get(position + 5).length() >= 5207 || strScript.get(position + 5).contains(";")
                        || !a.contains(strScript.get(position + 6))
                        || strScript.get(position + 7).isEmpty()
                        || !Useful.isInteger(strScript.get(position + 8)) || Integer.parseInt(strScript.get(position + 8)) <= 1970 || Integer.parseInt(strScript.get(position + 8)) >= 2023
                        || !Useful.isInteger(strScript.get(position + 9)) || Integer.parseInt(strScript.get(position + 9)) <= 0 || Integer.parseInt(strScript.get(position + 9)) >= 13
                        || !Useful.isInteger(strScript.get(position + 10)) || Integer.parseInt(strScript.get(position + 10)) <= 0 || Integer.parseInt(strScript.get(position + 10)) >= 32
                        || !Useful.isInteger(strScript.get(position + 11)) || Integer.parseInt(strScript.get(position + 11)) < 0 || Integer.parseInt(strScript.get(position + 11)) >= 24
                        || !Useful.isInteger(strScript.get(position + 12)) || Integer.parseInt(strScript.get(position + 12)) < 0 || Integer.parseInt(strScript.get(position + 12)) >= 60
                        || !Useful.isInteger(strScript.get(position + 13)) || Integer.parseInt(strScript.get(position + 13)) <= 0
                        || !Useful.isInteger(strScript.get(position + 14)) || Integer.parseInt(strScript.get(position + 14)) <= 0) {
                    return 0;
                } else {
                    return 14;
                }
            } else {
                return 0;
            }
        }
    }
}
//"C:\Users\egorn\Documents\ИТМО\1_курс\Програмировани\Lab6\Server\src\pot.txt"
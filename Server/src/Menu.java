import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Menu {
    ArrayList<String> answer;
    private String login = "";

    Menu() {
        this.answer = new ArrayList<>();
    }

    Menu(String login) {
        this.answer = new ArrayList<>();
        this.login = login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void help() {
        answer.add("help: вывести справку по доступным командам");
        answer.add("info : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)");
        answer.add("show : вывести в стандартный поток вывода все элементы коллекции в строковом представлении");
        answer.add("insert null {element} : добавить новый элемент с заданным ключом");
        answer.add("update id {element} : обновить значение элемента коллекции, id которого равен заданному");
        answer.add("remove_key null : удалить элемент из коллекции по его ключу");
        answer.add("clear : очистить коллекцию");
        answer.add("execute_script file_name : считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.");
        answer.add("exit : завершить программу (без сохранения в файл)");
        answer.add("history : вывести последние 12 команд (без их аргументов)");
        answer.add("replace_if_greater null {element} : заменить значение по ключу, если новое значение больше старого");
        answer.add("remove_lower_key null : удалить из коллекции все элементы, ключ которых меньше, чем заданный");
        answer.add("sum_of_minimal_point : вывести сумму значений поля minimalPoint для всех элементов коллекции");
        answer.add("max_by_name : вывести любой объект из коллекции, значение поля name которого является максимальным");
        answer.add("count_by_minimal_point minimalPoint : вывести количество элементов, значение поля minimalPoint которых равно заданному");
    }

    public void info(ConcurrentHashMap<String, LabWork> collection) {
        if (collection == null || collection.isEmpty()) {
            answer.add("Коллекция пуста");
        } else {
            answer.add("Размер коллекции: " + collection.size());
        }

        answer.add("Тип коллекции: ConcurrentHashMap<String, LabWork>");
        answer.add("LabWork: \n" +
                "int id" + "\n" +
                "String name" + "\n" +
                "Coordinates coordinates" + "\n" +
                "java.time.ZonedDateTime creationDate" + "\n" +
                "Integer minimalPoint" + "\n" +
                "String description" + "\n" +
                "Difficulty difficulty" + "\n" +
                "Person author");
        answer.add("Coordinates:\n" +
                "Float x\n" +
                "Integer y");
        answer.add("Person\n" +
                "String name\n" +
                "java.time.ZonedDateTime birthday\n" +
                "Integer height\n" +
                "long weight");
    }

    public void show(ConcurrentHashMap<String, LabWork> collection) {
        if (collection == null || collection.isEmpty()) {
            answer.add("Коллекция пуста");
            return;
        }

        collection.forEach((key, value) -> answer.add(key + " - " + value));
    }

    public ConcurrentHashMap<String, LabWork> insert(ConcurrentHashMap<String, LabWork> collection, String lhmKey, LabWork labWork) {
        if (lhmKey.isEmpty() || lhmKey.contains(";") || Useful.isOnlyTab(lhmKey)) {
            answer.add("Не правильный аргумент");
            return collection;
        }
        int id = DB.insertLabWork(lhmKey, labWork);

        if (id > 0) {
            labWork.setId(id);
            if (DB.addThing(login, labWork.getId())) {
                collection.put(lhmKey, labWork);
                answer.add("Добавлено");
            } else {
                answer.add("Произошла ошибка");
            }
        } else {
            answer.add("Произошла ошибка");
        }
        return collection;
    }

    public ConcurrentHashMap<String, LabWork> removeKey(ConcurrentHashMap<String, LabWork> collection, String lhmKey) {
        if (lhmKey.isEmpty() || !collection.containsKey(lhmKey)) {
            answer.add("Такого ключа нет");
            return collection;
        }
        if (DB.accessCheck(login, collection.get(lhmKey).getId())) {
            if (DB.removeLabwork(collection.get(lhmKey).getId()) && DB.removeAccess(collection.get(lhmKey).getId())) {
                collection.remove(lhmKey);
                answer.add("Удалено " + lhmKey);
            } else {
                answer.add("Произошла ошибка "  + lhmKey);
            }
        } else {
            answer.add("У вас нет прав "  + lhmKey);
        }
        return collection;
    }

    public void history(ArrayDeque<String> history) {
        if (history.isEmpty())
            answer.add("История пуста");
        else {
            answer.add("Последние 12 команд: ");
            answer.addAll(history);
        }
    }

    public ConcurrentHashMap<String, LabWork> update(ConcurrentHashMap<String, LabWork> collection, String id, LabWork labWork) {
        if (!Useful.isInteger(id)) {
            answer.add("Не корректное id");
            return collection;
        }

        labWork.setId(Integer.parseInt(id));
        
        String[] key = {""};
        collection.entrySet().stream().filter((s)-> s.getValue().getId() == Integer.parseInt(id)).forEach(s -> key[0] = s.getKey());

        if (key[0].isEmpty()) {
            answer.add("Нет элемента с таким id");
        } else {
            if (DB.accessCheck(login, Integer.parseInt(id))) {
                if (DB.updateLabWork(Integer.parseInt(id), labWork)) {
                    answer.add("Заменено");
                    collection.replace(key[0], labWork);
                } else {
                    answer.add("Произошла ошибка");
                }
            } else {
                answer.add("У вас нет прав");
            }
        }
        return collection;
    }

    public int sumOfMinimalPoint(ConcurrentHashMap<String, LabWork> collection) {
        int res = 0;

        for (LabWork labWork : collection.values()) {
            res += labWork.getMinimalPoint();
        }

        return res;
    }

    public LabWork maxByName(ConcurrentHashMap<String, LabWork> collection) {
        return collection.entrySet().stream().max(Comparator.comparing(s -> s.getValue().getName())).get().getValue();
    }

    public long countByMinimalPoint(ConcurrentHashMap<String, LabWork> collection, String minimalPoint) {
        if (!Useful.isInteger(minimalPoint)) {
            answer.add("Не правильный вид аргумента");
            return 0;
        }
        int mp = Integer.parseInt(minimalPoint);

        return collection.entrySet().stream().filter(s -> s.getValue().getMinimalPoint() == mp).count();
    }

    public ConcurrentHashMap<String, LabWork> removeLowerKey(ConcurrentHashMap<String, LabWork> collection, String lhmKey) {
        if (lhmKey.isEmpty()) {
            answer.add("Нет ключа");
            return collection;
        }

        ArrayList<String> key = new ArrayList<>();
        collection.entrySet().stream().filter(s -> s.getKey().length() < lhmKey.length()).forEach(s -> key.add(s.getKey()));

        for (String cur: key) {
            collection = removeKey(collection, cur);
        }
        
        return collection;
    }

    public ConcurrentHashMap<String, LabWork> replaceIfGreater(ConcurrentHashMap<String, LabWork> collection, String lhmKey, LabWork labWork) {
        if (lhmKey.isEmpty() || !collection.containsKey(lhmKey)) {
            answer.add("Не такого ключа");
            return collection;
        }

        if (collection.get(lhmKey).compareTo(labWork) < 0) {
            if (DB.accessCheck(login, collection.get(lhmKey).getId())) {
                if (DB.updateLabWork(collection.get(lhmKey).getId(), labWork)) {
                    collection.replace(lhmKey, collection.get(lhmKey), labWork);
                    answer.add("Заменено");
                } else {
                    answer.add("Произошла ошибка");
                }
            } else {
                answer.add("У вас нет прав");
            }
        } else {
            answer.add("Замены не было");
        }

        return collection;
    }

    public void savetoFile(ConcurrentHashMap<String, LabWork> collection, String name, char del) {
        try {
            FileIO.Write(collection, name, del);
            answer.add("Сохранено");
        } catch (IOException e) {
            answer.add("Произошла ошибка");
        }
    }

    public ConcurrentHashMap<String, LabWork> executeScript(ConcurrentHashMap<String, LabWork> collection, String script, String saveFile) {
        Script script1 = new Script();
        collection = script1.makeScript(collection, script, saveFile);
        answer.addAll(script1.answer);
        return collection;
    }

    public boolean signUp(String login, String password) {
        if (!checkLogin(login)) {
            String salt = Useful.getRandomString(5);
            String pass = Useful.generatePassword(password, salt);
            if (DB.signUp(login, pass, salt)) {
                answer.add("Пользователь зарегистрирован");
                return true;
            } else {
                answer.add("Пользователь не зарегистрирован");
                return false;
            }
        }
        return false;
    }

    public boolean logIn(String login, String password) {
        if (checkLogin(login)) {
            if (DB.logIn(login, password)) {
                answer.add("Вы вошли");
                return true;
            } else {
                answer.add("Неправильный пароль");
                return false;
            }
        }
        return false;
    }

    public boolean checkLogin(String login) {
        if (!DB.checkLogin(login)) {
            answer.add("Такого пользователя не существует");
            return false;
        }
        answer.add("Пользователь найден");
        return true;
    }
}

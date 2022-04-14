import java.io.IOException;
import java.util.*;

public class Menu {
    ArrayList<String> answer;
    Menu() {
        this.answer = new ArrayList<>();
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

    public void info(LinkedHashMap<String, LabWork> collection) {
        if (collection == null || collection.isEmpty()) {
            answer.add("Коллекция пуста");
        } else {
            answer.add("Размер коллекции: " + collection.size());
        }

        answer.add("Тип коллекции: LinkedHashMap<String, LabWork>");
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

    public void show(LinkedHashMap<String, LabWork> collection) {
        if (collection == null || collection.isEmpty()) {
            answer.add("Коллекция пуста");
            return;
        }

        collection.forEach((key, value) -> answer.add(key + " - " + value));
    }

    public LinkedHashMap<String, LabWork> insert(LinkedHashMap<String, LabWork> collection, String lhmKey, LabWork labWork) {
        if (lhmKey.isEmpty() || lhmKey.contains(";") || Useful.isOnlyTab(lhmKey)) {
            answer.add("Не правильный аргумент");
            return collection;
        }

        collection.put(lhmKey, labWork);
        answer.add("Добавлено");
        return collection;
    }

    public LinkedHashMap<String, LabWork> removeKey(LinkedHashMap<String, LabWork> collection, String lhmKey) {
        if (lhmKey.isEmpty() || !collection.containsKey(lhmKey)) {
            answer.add("Такого ключа нет");
            return collection;
        }
        collection.remove(lhmKey);
        answer.add("Удалено");
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

    public LinkedHashMap<String, LabWork> update(LinkedHashMap<String, LabWork> collection, String id, LabWork labWork) {
        if (!Useful.isInteger(id)) {
            answer.add("Не корректное id");
            return collection;
        }

        String[] key = {""};
        collection.entrySet().stream().filter((s)-> s.getValue().getId() == Integer.parseInt(id)).forEach(s -> key[0] = s.getKey());

        if (key[0].isEmpty()) {
            answer.add("Нет элемента с таким id");
        }
        else {
            answer.add("Заменено");
            collection.replace(key[0], labWork);
        }
        return collection;
    }

    public int sumOfMinimalPoint(LinkedHashMap<String, LabWork> collection) {
        int res = 0;

        for (LabWork labWork : collection.values()) {
            res += labWork.getMinimalPoint();
        }

        return res;
    }

    public LabWork maxByName(LinkedHashMap<String, LabWork> collection) {
        return collection.entrySet().stream().max(Comparator.comparing(s -> s.getValue().getName())).get().getValue();
    }

    public long countByMinimalPoint(LinkedHashMap<String, LabWork> collection, String minimalPoint) {
        if (!Useful.isInteger(minimalPoint)) {
            answer.add("Не правильный вид аргумента");
            return 0;
        }
        int mp = Integer.parseInt(minimalPoint);

        return collection.entrySet().stream().filter(s -> s.getValue().getMinimalPoint() == mp).count();
    }

    public LinkedHashMap<String, LabWork> removeLowerKey(LinkedHashMap<String, LabWork> collection, String lhmKey) {
        if (lhmKey.isEmpty()) {
            answer.add("Нет ключа");
            return collection;
        }

        LinkedHashMap<String, LabWork> result = new LinkedHashMap<>();
        collection.entrySet().stream().filter(s -> s.getKey().length() >= lhmKey.length()).forEach(s -> result.put(s.getKey(), s.getValue()));

        answer.add("Удалено");

        return result;
    }

    public LinkedHashMap<String, LabWork> replaceIfGreater(LinkedHashMap<String, LabWork> collection, String lhmKey, LabWork labWork) {
        if (lhmKey.isEmpty() || !collection.containsKey(lhmKey)) {
            answer.add("Не такого ключа");
            return collection;
        }

        if (collection.get(lhmKey).compareTo(labWork) < 0) {
            collection.replace(lhmKey, collection.get(lhmKey), labWork);
            answer.add("Заменено");
        } else {
            answer.add("Замены не было");
        }

        return collection;
    }

    public void savetoFile(LinkedHashMap<String, LabWork> collection, String name, char del) {
        try {
            IO.Write(collection, name, del);
            answer.add("Сохранено");
        } catch (IOException e) {
            answer.add("Произошла ошибка");
        }
    }

    public LinkedHashMap<String, LabWork> executeScript(LinkedHashMap<String, LabWork> collection, String script, String saveFile) {
        Script script1 = new Script();
        collection = script1.makeScript(collection, script, saveFile);
        answer.addAll(script1.answer);
        return collection;
    }

}

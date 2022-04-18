import java.io.Serializable;
import java.time.*;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Scanner;

public class Person implements Serializable {
    private String name; //Поле не может быть null, Строка не может быть пустой
    private java.time.ZonedDateTime birthday; //Поле может быть null
    private int height; //Поле не может быть null, Значение поля должно быть больше 0
    private long weight; //Значение поля должно быть больше 0

    public Person(String name, ZonedDateTime birthday, int height, long weight) {
        this.name = name;
        this.birthday = birthday;
        this.height = height;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public ZonedDateTime getBirthday() {
        return birthday;
    }

    public Integer getHeight() {
        return height;
    }

    public long getWeight() {
        return weight;
    }

    public static Person insert() {
        java.time.ZonedDateTime birthday = null;
        Scanner in = new Scanner(System.in);
        String input = "", name;
        int h;
        long w;

        System.out.println("Введите данные автора: ");

        do {
            System.out.print("Введите имя автора: ");
            if (in.hasNextLine())
                input = in.nextLine();
            else {
                System.out.println("Плохой символ");
                System.exit(0);
            }
        } while(input.isEmpty() || input.contains(";") || Useful.isOnlyTab(input));
        name = input;

        do {
            System.out.print("Введите рост автора: ");
            if (in.hasNextLine())
                input = in.nextLine();
            else {
                System.out.println("Плохой символ");
                System.exit(0);
            }
        } while(!Useful.isInteger(input) || Integer.parseInt(input) <= 0);
        h = Integer.parseInt(input);

        do {
            System.out.print("Введите вес автора: ");
            if (in.hasNextLine())
                input = in.nextLine();
            else {
                System.out.println("Плохой символ");
                System.exit(0);
            }
        } while(!Useful.isInteger(input) || Integer.parseInt(input) <= 0);
        w = Integer.parseInt(input);

        do {
            System.out.println("Хотите ввести дату рождения автора? (Y/N)");
            if (in.hasNextLine())
                input = in.nextLine();
            else {
                System.out.println("Плохой символ");
                System.exit(0);
            }
        } while(!input.equals("Y") && !input.equals("N"));

        if (input.equals("Y")) {
            ZoneId zone = ZoneId.of("Europe/Moscow");
            int year, minute, day, hour;
            Month month;

            do {
                System.out.print("Введите год: ");
                if (in.hasNextLine())
                    input = in.nextLine();
                else {
                    System.out.println("Плохой символ");
                    System.exit(0);
                }
            } while(!Useful.isInteger(input) || Integer.parseInt(input) <= 1970 || Integer.parseInt(input) >= 2023);
            year = Integer.parseInt(input);

            String a = Arrays.toString(Month.values());
            do {
                System.out.println(a);
                System.out.print("Выберите месяц: ");
                if (in.hasNextLine())
                    input = in.nextLine();
                else {
                    System.out.println("Плохой символ");
                    System.exit(0);
                }
            } while(!a.contains(input));
            month = Month.valueOf(input);

            Calendar myCalendar = (Calendar) Calendar.getInstance().clone();
            myCalendar.set(year, month.getValue(), 1);
            int max_date = myCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            do {
                System.out.print("Введите день: ");
                if (in.hasNextLine())
                    input = in.nextLine();
                else {
                    System.out.println("Плохой символ");
                    System.exit(0);
                }
            } while(!Useful.isInteger(input) || Integer.parseInt(input) < 0 || Integer.parseInt(input) > max_date);
            day = Integer.parseInt(input);

            do {
                System.out.print("Введите час: ");
                if (in.hasNextLine())
                    input = in.nextLine();
                else {
                    System.out.println("Плохой символ");
                    System.exit(0);
                }
            } while(!Useful.isInteger(input) || Integer.parseInt(input) < 0 || Integer.parseInt(input) > 23);
            hour = Integer.parseInt(input);

            do {
                System.out.print("Введите минуты: ");
                if (in.hasNextLine())
                    input = in.nextLine();
                else {
                    System.out.println("Плохой символ");
                    System.exit(0);
                }
            } while(!Useful.isInteger(input) || Integer.parseInt(input) < 0 || Integer.parseInt(input) >= 60);
            minute = Integer.parseInt(input);

            LocalDateTime time = LocalDateTime.of(year, month, day, hour, minute);
            birthday = ZonedDateTime.of(time, zone);
        }

        return new Person(name, birthday, h, w);
    }

    public String parse(char del) {
        if (this.birthday == null) {
            return name +
                    del + null +
                    del + height +
                    del + weight;
        } else {
            return name +
                    del + birthday.getYear() +
                    del + birthday.getMonthValue() +
                    del + birthday.getDayOfMonth() +
                    del + birthday.getHour() +
                    del + birthday.getMinute() +
                    del + height +
                    del + weight;
        }
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", birthday=" + birthday +
                ", height=" + height +
                ", weight=" + weight +
                '}';
    }
}

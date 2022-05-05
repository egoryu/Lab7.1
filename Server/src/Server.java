import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

import static java.lang.System.exit;

public class Server {
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        Analise analyst = new Analise();
        DatagramChannel server = null;
        try {
            server = DatagramChannel.open();
        } catch (IOException e) {
            System.out.println("Канал не создан");
            exit(0);
        }

        InetSocketAddress iAdd = new InetSocketAddress(MyConstant.HOST, MyConstant.PORT);
        server.bind(iAdd);
        System.out.println("Server Started: " + iAdd);
        server.configureBlocking(false);

        boolean flag = false;
        while (!flag) {
            String login, password;
            System.out.println("Введите логин:");
            if (!sc.hasNextLine()) {
                System.exit(0);
            }
            login = sc.nextLine();
            System.out.println("Введите пароль:");
            if (!sc.hasNextLine()) {
                System.exit(0);
            }
            password = sc.nextLine();
            flag = DB.connect(login, password);
        }
        analyst.loadBase();

        Exit exit = new Exit(analyst);
        exit.start();

        //Поток на ввод
        ThreadPoolExecutor executor1 = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        //Поток на обработку
        ThreadPoolExecutor executor2 = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
        //Поток на вывод
        ThreadPoolExecutor executor3 = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);

        while (!analyst.exit) {
            //Список на будущие полученные значения
            List<Future<Answer>> resultList = new ArrayList<>();
            //Полученные значения
            List<Answer> getter = new ArrayList<>();
            //Список на будущие обработанные значения
            List<Future<Request>> resultList1 = new ArrayList<>();
            //Обработанные значения
            List<Request> getter1 = new ArrayList<>();

            //Посылка задач на ввод
            DatagramChannel finalServer = server;
            Callable<Answer> input = () -> analyst.getLetter(finalServer);
            for(int i = 0; i < 10; i++) {
                Future<Answer> result = executor1.submit(input);
                resultList.add(result);
            }

            //Получения результатов с ввода
            for (Future<Answer> result : resultList) {
                Answer current = null;
                try {
                    current = result.get();
                } catch (InterruptedException | ExecutionException e) {
                    System.out.println(e.getMessage());
                }
                if (current == null)
                    continue;
                getter.add(current);
            }

            //Посылка задач на выполнение
            for (Answer request : getter) {
                DatagramChannel finalServer1 = server;
                Callable<Request> execute = () -> analyst.startAnalise(finalServer1, request);

                Future<Request> ans = executor2.submit(execute);
                resultList1.add(ans);
            }

            //Получение результатов обработки
            for (Future<Request> request:resultList1) {
                Request temp = null;
                try {
                    temp = request.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                if (temp == null)
                    continue;
                getter1.add(temp);
            }

            //Отправка ответа
            for (Request cur:getter1) {
                Runnable menu = () -> {
                    try {
                        analyst.sendLetter(cur, finalServer);
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                };
                executor3.submit(menu);
            }
        }
        executor3.shutdownNow();
        executor2.shutdownNow();
        executor1.shutdownNow();

        server.close();
    }
}

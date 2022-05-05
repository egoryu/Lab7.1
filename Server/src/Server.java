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
        Analise analyst = Analise.getAnalise();
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

        Exit exit = new Exit();
        exit.start();

        while (!Analise.getAnalise().isExit()) {
            //Посылка задач на ввод
            DatagramChannel finalServer = server;
            Runnable input = () -> {
                try {
                    analyst.getLetter(finalServer);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            };
            analyst.executor1.submit(input);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Analise.getAnalise().close();
        server.close();
    }
}

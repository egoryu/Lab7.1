import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Transfer {
    SocketAddress address;
    DatagramSocket server;
    private String userLogin;
    private String userPassword;

    static File file = new File("trans.ser");

    Transfer(SocketAddress address, DatagramSocket server) {
        this.address = address;
        this.server = server;
    }

    public void Start() throws IOException, ClassNotFoundException {
        Scanner in = new Scanner(System.in);

        while(true) {
            int mistake = 0;
            System.out.print("Введите команду: ");
            String input;
            if (!in.hasNextLine()) {
                System.out.println("Плохой символ");
                System.exit(0);
            }
            input = in.nextLine();


            String[] current = input.split(" ");

            if (current.length == 0)
                current = new String[]{" "};

            Request answer = null;
            Request request = null;

            switch (current[0]) {
                case ("exit"):
                    request = new Request(current[0]);
                    request.setInfo(userLogin, userPassword);
                    sendLetter(request);
                    System.exit(0);
                    break;
                case ("help"):
                case ("show"):
                case ("info"):
                case ("history"):
                case ("clear"):
                case ("sum_of_minimal_point"):
                case ("max_by_name"):
                    while (mistake < 4) {
                        request = new Request(current[0]);
                        request.setInfo(userLogin, userPassword);
                        sendLetter(request);
                        answer = getLetter(server);
                        if (answer == null)
                            mistake++;
                        else
                            break;
                    }
                    break;
                case ("insert"):
                case ("replace_if_greater"):
                case ("update"):
                    if (current.length < 2) {
                        System.out.println("Не введен аргумент");
                        continue;
                    }
                    while (mistake < 4) {
                        request = new Request(current[0], current[1], LabWork.insert());
                        request.setInfo(userLogin, userPassword);
                        sendLetter(request);
                        answer = getLetter(server);
                        if (answer == null)
                            mistake++;
                        else
                            break;
                    }
                    break;
                case ("remove_key"):
                case ("remove_lower_key"):
                case ("count_by_minimal_point"):
                case ("execute_script"):
                    if (current.length < 2) {
                        System.out.println("Не введен аргумент");
                        continue;
                    }
                    while (mistake < 4) {
                        request = new Request(current[0], current[1]);
                        request.setInfo(userLogin, userPassword);
                        sendLetter(request);
                        answer = getLetter(server);
                        if (answer == null)
                            mistake++;
                        else
                            break;
                    }
                    break;
                default:
                    System.out.println("Неправильно введена команда");
                    continue;
            }
            if (answer == null) {
                System.exit(0);
            }
            output(answer.getAnswer());
        }
    }

    public void authorization() throws IOException, ClassNotFoundException {
        Scanner in = new Scanner(System.in);
        boolean flag = true;

        while (flag) {
            int mistake = 0;
            String login;
            String password;

            System.out.println("Введите login, чтобы войти");
            System.out.println("Введите signup, чтобы зарегистрироваться");
            System.out.println("Введите exit, чтобы выйти");

            String input;
            if (!in.hasNextLine()) {
                System.out.println("Плохой символ");
                System.exit(0);
            }
            input = in.nextLine();

            Request answer = null;
            switch (input) {
                case ("signup"):
                case ("login"):
                    System.out.print("Введите логин:");
                    if (!in.hasNextLine()) {
                        System.out.println("Плохой символ");
                        System.exit(0);
                    }
                    login = in.nextLine();

                    System.out.print("Введите пароль:");
                    if (!in.hasNextLine()) {
                        System.out.println("Плохой символ");
                        System.exit(0);
                    }
                    password = in.nextLine();

                    while (mistake < 4) {
                        sendLetter(new Request(input, login, password));
                        answer = getLetter(server);
                        if (answer == null)
                            mistake++;
                        else
                            break;
                    }
                    if (answer == null) {
                        System.exit(0);
                    }
                    output(answer.getAnswer());
                    if (answer.isTrigger()) {
                        flag = false;
                        userLogin = answer.getLogin();
                        userPassword = answer.getPassword();
                    }
                    break;
                case ("exit"):
                    System.exit(0);
                    break;
                default:
                    System.out.println("Неправильная команда");
            }
        }
    }

    public void sendLetter(Request send) throws IOException {
        byte[] request;
        FileOutputStream fileOutput;
        FileInputStream fileInput;
        ObjectOutputStream objectOut;

        try {
            fileOutput = new FileOutputStream(file);
            objectOut = new ObjectOutputStream(fileOutput);

            objectOut.writeObject(send);

            fileInput = new FileInputStream(file);

            request = new byte[(int)file.length()];
            fileInput.read(request, 0, request.length);
        } catch (Exception e) {
            System.out.println("Проблема с файлом");
            return;
        }

        byte[] letterSize = Useful.convertToByte(request.length);
        DatagramPacket i = new DatagramPacket(letterSize, letterSize.length, address);
        server.send(i);

        DatagramPacket o = new DatagramPacket(request, request.length, address);
        server.send(o);

        objectOut.close();
        fileOutput.close();
        fileInput.close();
    }

    public Request getLetter(DatagramSocket datagramSocket) throws IOException, ClassNotFoundException {
        FileOutputStream fileOutput;
        FileInputStream fileInput;
        ObjectInputStream objectInput;

        byte[] length = new byte[MyConstant.SIZE];
        DatagramPacket letterSize = new DatagramPacket(length, length.length);

        try {
            datagramSocket.receive(letterSize);
        }
        catch (SocketTimeoutException e) {
            System.out.println("Сервер не отвечает");
            return null;
        }

        byte[] req = new byte[Useful.convertToInt(length)];
        DatagramPacket inputRequest = new DatagramPacket(req, req.length);
        datagramSocket.receive(inputRequest);

        fileOutput = new FileOutputStream(file);

        fileOutput.write(req);

        fileInput = new FileInputStream(file);
        objectInput = new ObjectInputStream(fileInput);

        Request request = (Request) objectInput.readObject();

        objectInput.close();
        fileOutput.close();
        fileInput.close();

        return request;
    }

    public void output(ArrayList<String> output) {
        for (String u: output)
            System.out.println(u);
    }
}

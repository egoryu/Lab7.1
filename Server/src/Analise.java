//import jline.internal.NonBlockingInputStream;

import java.io.*;
import java.net.SocketAddress;
        import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayDeque;
        import java.util.concurrent.ConcurrentHashMap;
import java.util.Scanner;

public class Analise {
     volatile ConcurrentHashMap<String, LabWork> collection = new ConcurrentHashMap<>();
    Scanner in = new Scanner(System.in);
    String nameOfSaveFile = "";
    ArrayDeque<String> history = new ArrayDeque<>();
    volatile public boolean exit = false;
    SocketAddress client;
    static File file;

    static {
        file = new File("analise1.ser");
    }

    public void loadFile(String nameOfFile) {
        while (nameOfFile.isEmpty() || !FileIO.CheckedWrite(nameOfFile) || !FileIO.CheckedRead(nameOfFile)) {
            System.out.print("Введите путь к файлу для загрузки и сохранения: ");
            if (!in.hasNextLine()) {
                System.out.println("Принудительный выход");
                System.exit(0);
            }
            nameOfFile = in.nextLine();
        }
        nameOfSaveFile = nameOfFile;
        collection = FileIO.Read(nameOfSaveFile, ';');


        if (collection == null) {
            collection = new ConcurrentHashMap<>();
        }

        collection = Useful.lhmSort(collection);
    }

    public void loadBase() {
        collection = DB.readLabWork();
    }

    public Request startAnalise(DatagramChannel datagramSocket, Answer answer) throws IOException, ClassNotFoundException {
        Menu menu = new Menu();
        boolean trigger = false;

        //askServer(menu);

        //Request request = getLetter(datagramSocket);
        if (answer == null)
            return null;
        Request request = answer.getRequest();
        client = answer.getClient();

        if (request == null)
            return null;
        if (request.getTarget() != null) {
            request.getTarget().setId();
        }
        menu.setLogin(request.getLogin());

        history.addLast(request.getCommand());
        switch (request.getCommand()) {
            case ("signup"):
                trigger = menu.signUp(request.getLogin(), request.getPassword());
                break;
            case ("login"):
                trigger = menu.logIn(request.getLogin(), request.getPassword());
                break;
            case ("help"):
                menu.help();
                break;
            case ("exit"):
                menu.savetoFile(collection, nameOfSaveFile, ';');
                Scanner in = new Scanner(System.in);
                String input;
                do {
                    System.out.println("Отключить сервер? (Y/N)");
                    if (!in.hasNextLine()) {
                        input = "Y";
                        break;
                    }
                    input = in.nextLine();
                } while(!input.equals("Y") && !input.equals("N"));

                if (input.equals("Y"))
                    exit = true;
                else
                    System.out.println("Продолжается работа");

                break;
            case ("show"):
                menu.show(collection);
                break;
            case ("info"):
                menu.info(collection);
                break;
            case ("insert"):
                collection = menu.insert(collection, request.getArgument(), request.getTarget());

                collection = Useful.lhmSort(collection);
                break;
            case ("remove_key"):
                collection = menu.removeKey(collection, request.getArgument());
                break;
            case ("clear"):
                collection.clear();
                menu.answer.add("Коллекция очищена");
                break;
            case ("history"):
                menu.history(history);
                break;
            case ("update"):
                collection = menu.update(collection, request.getArgument(), request.getTarget());
                break;
            case ("sum_of_minimal_point"):
                menu.answer.add(String.valueOf(menu.sumOfMinimalPoint(collection)));
                break;
            case ("max_by_name"):
                menu.answer.add(String.valueOf(menu.maxByName(collection)));
                break;
            case ("count_by_minimal_point"):
                menu.answer.add(String.valueOf(menu.countByMinimalPoint(collection, request.getArgument())));
                break;
            case ("remove_lower_key"):
                collection = menu.removeLowerKey(collection, request.getArgument());
                break;
            case ("replace_if_greater"):
                collection = menu.replaceIfGreater(collection, request.getArgument(), request.getTarget());
                break;
            case ("save"):
                menu.savetoFile(collection, nameOfSaveFile, ';');
                break;
            case ("execute_script"):
                collection = menu.executeScript(collection, request.getArgument(), nameOfSaveFile);
                break;
            default:
                menu.answer.add("Неправильно введена команда");
                history.pollLast();
        }
        if (history.size() >= 12)
            history.poll();

        if (!exit) {
            Request send = new Request(menu.answer, trigger);
            send.setInfo(request.getLogin(), request.getPassword());
            //sendLetter(send, datagramSocket);
            return send;
        } else {
            return null;
        }
    }

   /*public Answer getLetter(DatagramChannel datagramChannel) throws IOException, ClassNotFoundException {
       FileOutputStream fileOutput;
       FileInputStream fileInput;
       ObjectInputStream objectInput = null;

       ByteBuffer buffer1 = ByteBuffer.allocate(MyConstant.SIZE);
       SocketAddress client = datagramChannel.receive(buffer1);
       if (client == null)
           return null;
       buffer1.flip();
       int limits = buffer1.limit();
       byte bytes1[] = new byte[limits];
       buffer1.get(bytes1, 0, limits);
       int len = Useful.convertToInt(bytes1);

       if (len == 0) {
           return null;
       }

       if (len < 0) {
           System.out.println(Arrays.toString(bytes1));
           return null;
       }
       System.out.println();

       ByteBuffer buffer2;
       buffer2 = ByteBuffer.allocate(len);
       byte[] bytes2 = new byte[buffer2.limit()];
       datagramChannel.receive(buffer2);
       buffer2.flip();
       try {
           buffer2.get(bytes2, 0, buffer2.limit());
       } catch (Exception e) {
           System.out.println(buffer2);
           return null;
       }

       fileOutput = new FileOutputStream(file);

       fileOutput.write(bytes2);

       fileInput = new FileInputStream(file);
       if (file.length() == 0) {
           return null;
       }
       try {
           objectInput = new ObjectInputStream(fileInput);
       } catch (Exception e) {
           return null;
       }

       Request request = (Request) objectInput.readObject();

       objectInput.close();
       fileInput.close();
       fileOutput.close();

       return new Answer(request, client);
   }*/

    public Answer getLetter(DatagramChannel datagramChannel) throws IOException, ClassNotFoundException {
        FileOutputStream fileOutput;
        FileInputStream fileInput;
        ObjectInputStream objectInput = null;

        ByteBuffer buffer1 = ByteBuffer.allocate(MyConstant.SIZE * 1000);
        SocketAddress client = datagramChannel.receive(buffer1);
        if (client == null)
            return null;
        buffer1.flip();
        int limits = buffer1.limit();
        byte[] bytes1 = new byte[limits];
        buffer1.get(bytes1, 0, limits);

        fileOutput = new FileOutputStream(file);

        fileOutput.write(bytes1);

        fileInput = new FileInputStream(file);
        if (file.length() == 0) {
            return null;
        }
        try {
            objectInput = new ObjectInputStream(fileInput);
        } catch (Exception e) {
            return null;
        }

        Request request = (Request) objectInput.readObject();

        objectInput.close();
        fileInput.close();
        fileOutput.close();

        return new Answer(request, client);
    }

    public void sendLetter(Request send, DatagramChannel datagramChannel) throws IOException {
        FileOutputStream fileOutput;
        FileInputStream fileInput;
        ObjectOutputStream objectOut;

        fileOutput = new FileOutputStream(file);
        objectOut = new ObjectOutputStream(fileOutput);

        objectOut.writeObject(send);

        fileInput = new FileInputStream(file);
        byte[] request = new byte[(int)file.length()];
        fileInput.read(request);

        byte[] letterSize = Useful.convertToByte(request.length);

        datagramChannel.send(ByteBuffer.wrap(letterSize), client);

        datagramChannel.send(ByteBuffer.wrap(request), client);

        objectOut.close();
        fileInput.close();
        fileOutput.close();
    }
}

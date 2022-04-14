import com.sun.org.apache.xml.internal.utils.res.XResources_ja_JP_A;
//import jline.internal.NonBlockingInputStream;

import java.io.*;
import java.net.SocketAddress;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayDeque;
import java.util.LinkedHashMap;
import java.util.Scanner;

public class Analise {
    LinkedHashMap<String, LabWork> collection = new LinkedHashMap<>();
    Scanner in = new Scanner(System.in);
    String nameOfSaveFile = "";
    ArrayDeque<String> history = new ArrayDeque<>();
    public boolean exit = false;

    static File file;

    static {
        file = new File("analise1.ser");
    }

    SocketAddress client;

    public void loadFile(String nameOfFile) {
        while (nameOfFile.isEmpty() || !IO.CheckedWrite(nameOfFile) || !IO.CheckedRead(nameOfFile)) {
            System.out.print("Введите путь к файлу для загрузки и сохранения: ");
            if (!in.hasNextLine()) {
                System.out.println("Принудительный выход");
                System.exit(0);
            }
            nameOfFile = in.nextLine();
        }
        nameOfSaveFile = nameOfFile;
        collection = IO.Read(nameOfSaveFile, ';');


        if (collection == null) {
            collection = new LinkedHashMap<>();
        }

        collection = Useful.lhmSort(collection);
    }

    public void startAnalise(DatagramChannel datagramSocket) throws IOException, ClassNotFoundException {
        Menu menu = new Menu();

        //askServer(menu);

        Request request = getLetter(datagramSocket);

        if (request == null)
            return;
        if (request.getTarget() != null) {
            request.getTarget().setId();
        }

        history.addLast(request.getCommand());
        switch (request.getCommand()) {
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

        if (!exit)
            sendLetter(new Request(menu.answer), datagramSocket);
    }

   public Request getLetter(DatagramChannel datagramChannel) throws IOException, ClassNotFoundException {
       FileOutputStream fileOutput;
       FileInputStream fileInput;
       ObjectInputStream objectInput;

       ByteBuffer buffer1 = ByteBuffer.allocate(MyConstant.SIZE);
       client = datagramChannel.receive(buffer1);
       buffer1.flip();
       int limits = buffer1.limit();
       byte bytes1[] = new byte[limits];
       buffer1.get(bytes1, 0, limits);
       int len = Useful.convertToInt(bytes1);

       if (len <= 0)
           return null;
       ByteBuffer buffer2;
       byte[] bytes2 = new byte[len];
       try {
           buffer2 = ByteBuffer.allocate(len);
           datagramChannel.receive(buffer2);
           buffer2.flip();
           buffer2.get(bytes2, 0, len);
       } catch (Exception e) {
           System.out.println("Не дошло");
           return null;
       }

       fileOutput = new FileOutputStream(file);

       fileOutput.write(bytes2);

       fileInput = new FileInputStream(file);
       if (file.length() == 0) {
           return null;
       }
       objectInput = new ObjectInputStream(fileInput);

       Request request = (Request) objectInput.readObject();

       fileInput.close();
       objectInput.close();
       fileOutput.close();

       return request;
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
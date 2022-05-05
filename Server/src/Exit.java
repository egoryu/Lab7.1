import java.util.Scanner;

public class Exit extends Thread{
    @Override
    public void run() {
        Scanner sc = new Scanner(System.in);

        while(true) {
            if (!sc.hasNextLine()) {
                Analise.getAnalise().setExit(true);
                break;
            }
            String input = sc.nextLine();
            if (input.equals("exit")) {
                Analise.getAnalise().setExit(true);
                break;
            } else {
                System.out.println("Неправильная команда");
            }
        }
    }
}

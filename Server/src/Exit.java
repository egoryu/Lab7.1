import java.util.Scanner;

public class Exit extends Thread{
    Analise analise;

    Exit(Analise analise) {
        this.analise = analise;
    }
    @Override
    public void run() {
        //setDaemon(true);
        Scanner sc = new Scanner(System.in);

        while(true) {
            if (!sc.hasNextLine()) {
                analise.exit = true;
                break;
            }
            String input = sc.nextLine();
            if (input.equals("exit")) {
                analise.exit = true;
                break;
            } else {
                System.out.println("Неправильная команда");
            }
        }
    }
}

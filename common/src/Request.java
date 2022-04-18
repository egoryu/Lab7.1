import java.io.*;
import java.util.ArrayList;

public class Request implements Serializable {
    private String command = null;
    private String argument = null;
    private LabWork target = null;
    private ArrayList<String> answer = null;
    private String login = null;
    private String password = null;
    private boolean trigger = false;

    Request(String command) {
        this.command = command;
    }

    Request(String command, String argument) {
        this.command = command;
        this.argument = argument;
    }

    Request(String command, String login, String password) {
        this.command = command;
        this.login = login;
        this.password = password;
    }

    Request(String command, String argument, LabWork target) {
        this.command = command;
        this.argument = argument;
        this.target = target;
    }

    Request(ArrayList<String> answer, boolean trigger) {
        this.answer = answer;
        this.trigger = trigger;
    }

    public void setInfo(String login, String password) {
        this.password = password;
        this.login = login;
    }

    public String getCommand() {
        return command;
    }

    public String getArgument() {
        return argument;
    }

    public LabWork getTarget() {
        return target;
    }

    public ArrayList<String> getAnswer() {
        return answer;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public boolean isTrigger() {
        return trigger;
    }

    @Override
    public String toString() {
        return "Request{" +
                "command='" + command + '\'' +
                ", argument='" + argument + '\'' +
                ", target=" + target +
                '}';
    }
}

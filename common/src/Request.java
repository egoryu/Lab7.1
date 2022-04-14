import java.io.*;
import java.util.ArrayList;

public class Request implements Serializable {
    private String command;
    private String argument;
    private LabWork target;
    private ArrayList<String> answer;

    Request() {
        this.command = null;
        this.argument = null;
        this.target = null;
    }

    Request(String command) {
        this.command = command;
        this.argument = null;
        this.target = null;
    }

    Request(String command, String argument) {
        this.command = command;
        this.argument = argument;
        this.target = null;
    }

    Request(String command, String argument, LabWork target) {
        this.command = command;
        this.argument = argument;
        this.target = target;
    }

    Request(ArrayList<String> answer) {
        this.answer = answer;
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

    @Override
    public String toString() {
        return "Request{" +
                "command='" + command + '\'' +
                ", argument='" + argument + '\'' +
                ", target=" + target +
                '}';
    }
}

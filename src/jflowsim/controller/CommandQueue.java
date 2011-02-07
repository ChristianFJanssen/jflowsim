package jflowsim.controller;

import jflowsim.controller.commands.Command;
import java.util.LinkedList;

public class CommandQueue {

    private static CommandQueue instance;
    private LinkedList<Command> commandQueue;
    private int max_size = 1000;
    private int current_pos;

    private CommandQueue() {
        super();
        this.commandQueue = new LinkedList<Command>();
        this.current_pos = -1;
    }

    public static CommandQueue getInstance() {
        if (instance == null) {
            instance = new CommandQueue();
        }
        return (CommandQueue) instance;
    }

    public void add(Command command) {

        if (this.commandQueue.size() - 1 != this.current_pos) {
            for (int i = this.current_pos + 1; i < this.commandQueue.size(); i++) {
                this.commandQueue.remove(i);
                i--;
            }
        }

        if (this.commandQueue.size() < this.max_size) {
            this.commandQueue.add(command);
            this.current_pos++;
        } else {
            this.commandQueue.remove(0);
            this.commandQueue.add(command);
        }
    }

    public void undo() {
        if (current_pos > -1) {
            //System.out.println("undo:"+current_pos);
            this.commandQueue.get(current_pos).undo();
            this.current_pos--;
        }
    }

    public void redo() {
        if (this.current_pos < this.commandQueue.size() - 1) {
            //System.out.println("redo:"+current_pos);
            this.current_pos++;
            this.commandQueue.get(current_pos).redo();
        }
    }
}

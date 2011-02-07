package jflowsim.controller.commands;

import jflowsim.controller.CommandQueue;

public class RedoCommand extends Command{

    public void execute() {
        CommandQueue.getInstance().redo();
    }
    
    public void undo() {        
    }
    
    public void redo() {        
    }
}

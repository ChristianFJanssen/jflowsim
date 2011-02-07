package jflowsim.controller.commands;

import jflowsim.controller.CommandQueue;

public class UndoCommand extends Command{

    public void execute() {
        CommandQueue.getInstance().undo();
    }
    
    public void undo() {        
    }
    
    public void redo() {        
    }
}

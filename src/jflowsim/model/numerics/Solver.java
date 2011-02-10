package jflowsim.model.numerics;

import java.util.Observable;
import java.util.concurrent.CyclicBarrier;

public abstract class Solver extends Observable implements Runnable {

    protected Thread thread;

    protected int num_of_threads;    
    protected CyclicBarrier barrier;

    public Solver() {
        this.num_of_threads = Runtime.getRuntime().availableProcessors();
        
        this.barrier = new CyclicBarrier(num_of_threads);

        System.out.println(this.getClass().getSimpleName()+" num_of_threads:"+num_of_threads);
    }

    public void startSimulation() {
        this.thread = new Thread(this);
        this.thread.start();
    }

    abstract public void interrupt();

    public void update(){
        super.setChanged();
        super.notifyObservers();
    }
}

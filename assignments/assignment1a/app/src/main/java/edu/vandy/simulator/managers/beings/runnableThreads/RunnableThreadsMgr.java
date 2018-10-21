package edu.vandy.simulator.managers.beings.runnableThreads;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;

import edu.vandy.simulator.managers.beings.Being;
import edu.vandy.simulator.managers.beings.BeingManager;

import static java.util.stream.Collectors.toList;

/**
 * This BeingManager implementation manually creates a pool of Java
 * threads to run the being simulations.
 */
public class RunnableThreadsMgr
       extends BeingManager<SimpleBeingRunnable> {
    /**
     * Used for Android debugging.
     */
    private final static String TAG =
        RunnableThreadsMgr.class.getName();

    /**
     * The list of beings (implemented as concurrent Java threads)
     * that are attempting to acquire palantiri for gazing.
     */
    private List<Thread> mBeingThreads;

    /**
     * A factory method that returns a new SimpleBeingRunnable instance.
     *
     * @return A new SimpleBeingRunnable instance
     */
    @Override
    protected SimpleBeingRunnable newBeing() {
        // Return a new SimpleBeingRunnable instance.
        // TODO -- you fill in here, replacing null with the
        // appropriate code.
        return new SimpleBeingRunnable(this);
    }

    /**
     * This entry point method is called by the Simulator framework to
     * start the being gazing simulation.
     */
    @Override
    public void runSimulation() {
        // Call a method to create and start a thread for each being.
        // TODO -- you fill in here.
        beginBeingThreads();

        // Call a method to start a thread to wait for all the being
        // threads to finish.
        // TODO -- you fill in here.
        waitForBeingThreads();

        // Block until the waiter thread has finished.
        // TODO -- you fill in here.
        shutdownNow();
    }

    /**
     * Create/start a list of threads that represent the beings in
     * this simulation.
     */
    private void beginBeingThreads() {
        // All STUDENTS:
        // Call the BeingManager.getBeings() method to iterate through
        // the beings, create a new Thread object for each one, and
        // add it to the list of mBeingThreads.
        //
        // GRADUATE STUDENTS:
        // Set an "UncaughtExceptionHandler" for each being thread
        // that calls the BeingManager.error() method to indicate an
        // unexpected exception "ex" occurred for thread "thr".
        // Undergraduates do not need to set this exception handler
        // (though they are free to do to if they choose).
        //
        // TODO -- you fill in here.
        List<SimpleBeingRunnable> beings = getBeings();
        mBeingThreads = new ArrayList<>(beings.size());
        for (SimpleBeingRunnable being : beings) {
            Thread beingThread = new Thread(being);
            mBeingThreads.add(beingThread);

            beingThread.setUncaughtExceptionHandler((t, e) -> {
                RunnableThreadsMgr.this.error("Unexpected exception" + e + "occurred for thread " + beingThread.getName());
            });
        }

        // Start all the threads in the List of Threads.
        // TODO -- you fill in here.
        mBeingThreads.forEach(Thread::start);
    }

    /**
     * Start a thread to wait for all the being threads to finish and
     * return that thread to the caller.
     *
     * @return Thread that is waiting for all the beings to complete.
     */
    private Thread waitForBeingThreads() {
        // Create a Java thread that waits for all being threads to
        // finish gazing.  If an interruption or exception occurs
        // while waiting for the threads to finish, call the
        // BeingManager.error() helper method with the exception in
        // the catch clause, which trigger the simulator to generate a
        // shutdownNow() request.
        // TODO -- you fill in here.
        Thread waitThread = new Thread(() -> {
                for (Thread thread : mBeingThreads) {
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        RunnableThreadsMgr.this.error(e);
                    }
                }

        });


        // Start running the thread.
        // TODO -- you fill in here.
        waitThread.start();

        // Return the thread.
        // TODO -- you fill in here, replacing null with the thread that was created.
        return waitThread;
    }

    /**
     * Called to run to error the simulation and should only return
     * after all threads have been terminated and all resources
     * cleaned up.
     */
    @Override
    public void shutdownNow() {
        // No special handling required for this manager since the all
        // beings will have already been asked to stop by the base
        // class before calling this method.
    }
}


package edu.vandy.simulator.managers.beings.runnableThreads;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;

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
    private List<Thread> mBeingThreads = new ArrayList<>();

    /**
     * A factory method that returns a new SimpleBeingRunnable instance.
     *
     * @return A new SimpleBeingRunnable instance
     */
    @Override
    public SimpleBeingRunnable newBeing() {
        // Return a new SimpleBeingRunnable instance.
        // DONE -- you fill in here, replacing null with the
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
        // DONE -- you fill in here.
        beginBeingThreads();
        // Call a method that creates and starts a thread that's then
        //  used to wait for all the being threads to finish and
        //  return that thread to the caller.
        // DONE -- you fill in here.
        Thread sentry = createAndStartWaiterForBeingThreads();

        // Block until the waiter thread has finished.
        // DONE -- you fill in here.
        try {
            sentry.join();
        } catch (InterruptedException ie) {
        }

        shutdownNow();
    }

    /**
     * Create/start a list of threads that represent the beings in
     * this simulation.
     */
    public void beginBeingThreads() {
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
        // DONE -- you fill in here.
        List<SimpleBeingRunnable> beings = getBeings();
        for (SimpleBeingRunnable being : beings) {
            Thread thread = new Thread(being);
            thread.setUncaughtExceptionHandler((t, e) -> error("An exception " + e + " occured in thread " + thread.getName()));
            mBeingThreads.add(thread);
        }

        mBeingThreads.forEach(Thread::run);
    }

    /**
     * Create and start a thread that can be used to wait for all the
     * being threads to finish and return that thread to the caller.
     *
     * @return Thread that is waiting for all the beings to complete.
     */
    public Thread createAndStartWaiterForBeingThreads() {
        // Create a Java thread that waits for all being threads to
        // finish gazing.  If an interruption or exception occurs
        // while waiting for the threads to finish, call the
        // BeingManager.error() helper method with the exception in
        // the catch clause, which trigger the simulator to generate a
        // shutdownNow() request.
        // DONE -- you fill in here.
        Thread sentry = new Thread(() -> {
            for (Thread thread : mBeingThreads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    RunnableThreadsMgr.this.error(e);
                }
            }
        });

        // Start running the thread.
        // DONE -- you fill in here.
        sentry.start();

        // Return the thread.
        // DONE -- you fill in here, replacing null with the thread that was created.
        return sentry;
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


/**
 * Copyright (C) 2003 - 2009
 * Computational Intelligence Research Group (CIRG@UP)
 * Department of Computer Science
 * University of Pretoria
 * South Africa
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.sourceforge.cilib.simulator;

import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.cilib.algorithm.AbstractAlgorithm;

import net.sourceforge.cilib.algorithm.Algorithm;
import net.sourceforge.cilib.algorithm.ProgressEvent;
import net.sourceforge.cilib.algorithm.ProgressListener;
import net.sourceforge.cilib.problem.Problem;

/**
 * <p>
 * This class represents an instance of a "simulator" that may or may not contain multiple
 * individual simulations.
 * </p>
 * <p>
 * Each simulation experiment is repeated based on the
 * number of samples that the measurement suite requires (although this is subject to change
 * in a future version of the library). In this implementation each experiment is
 * execute in its own thread. Thus, each experiment is execute in parallel for a given simulation.
 * </p>
 * <p>
 * The primary purpose of running simulations is to measure the performance of the given algorithm
 * on a given problem. For that reason, a simulation accepts a measurement suite which it uses to
 * record the performace.
 * </p>
 * @author Edwin Peer
 */
class Simulator {

    private static final long serialVersionUID = 8987667794610802908L;
    private final Simulation[] simulations;
    private final Vector<ProgressListener> progressListeners;
    private final HashMap<Simulation, Double> progress;
    private final ExecutorService executor;
    private final XMLObjectFactory algorithmFactory;
    private final XMLObjectFactory problemFactory;
    private final XMLObjectFactory measurementFactory;
    private final int samples;

    /**
     * Creates a new instance of Simulator given an algorithm factory, a problem factory and a
     * measurement suite.
     * @see net.sourceforge.cilib.XMLObjectFactory
     * @param algorithmFactory The algorithm factory.
     * @param problemFactory The problem factory.
     * @param measurementFactory The measurement suite.
     */
    Simulator(ExecutorService executor, XMLObjectFactory algorithmFactory, XMLObjectFactory problemFactory, XMLObjectFactory measurementFactory, int samples) {
        this.executor = executor;
        this.algorithmFactory = algorithmFactory;
        this.problemFactory = problemFactory;
        this.measurementFactory = measurementFactory;
        this.samples = samples;
        this.progressListeners = new Vector<ProgressListener>();
        this.progress = new HashMap<Simulation, Double>();
        this.simulations = new Simulation[samples];
    }

    /**
     * Perform the initialization of the {@code Simulator} by creating the required
     * {@code Simulation} instances and executing the threads.
     */
    void init() {
        for (int i = 0; i < samples; ++i) {
            simulations[i] = new Simulation(this, (Algorithm) algorithmFactory.newObject(), (Problem) problemFactory.newObject(), (MeasurementSuite) measurementFactory.newObject());
            progress.put(simulations[i], 0.0);
        }
    }

    /**
     * Executes all the experiments for this simulation. The measurement suite will
     * be closed once this method completes.
     */
    void execute() {
        CompletionService<Simulation> completionService = new ExecutorCompletionService<Simulation>(executor);
        for (int i = 0; i < samples; ++i) {
            completionService.submit(simulations[i], simulations[i]); // The return value is explicitly null.
        }

        final Simulation[] completedSimulations = new Simulation[samples];
        try {
            for (int i = 0; i < samples; i++) {
                Future<Simulation> simulation = completionService.take();
                completedSimulations[i] = simulation.get();
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(Simulator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            throw new RuntimeException(ex);
        }

        executor.shutdown();
    }

    /**
     * Terminates all the experiments.
     */
    void terminate() {
        for (int i = 0; i < samples; ++i) {
            simulations[i].terminate();
        }
    }

    /**
     * Adds a listener for progress events. A progress is fired periodically based on the resolution
     * of the measurements. {@see ProgressEvent} {@see ProgressListener}
     * @param listener The event listener
     */
    void addProgressListener(ProgressListener listener) {
        progressListeners.add(listener);
    }

    /**
     * Removes a listener for progress events.
     * @param listener The event listener
     */
    void removeProgressListener(ProgressListener listener) {
        progressListeners.remove(listener);
    }

    private synchronized void notifyProgress() {
        double ave = 0;
        for (Double tmp : progress.values()) {
            ave += tmp.doubleValue();
        }

        ave /= progress.size();

        for (ProgressListener listener : progressListeners) {
            listener.handleProgressEvent(new ProgressEvent(ave));
        }
    }

    void updateProgress(Simulation simulation, double percentageComplete) {
        progress.put(simulation, new Double(((AbstractAlgorithm) simulation.getAlgorithm()).getPercentageComplete()));
        notifyProgress();
    }
}

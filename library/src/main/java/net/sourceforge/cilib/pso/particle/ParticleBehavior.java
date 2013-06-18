/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.pso.particle;

import net.sourceforge.cilib.util.Cloneable;
import net.sourceforge.cilib.pso.guideprovider.GuideProvider;
import net.sourceforge.cilib.pso.guideprovider.NBestGuideProvider;
import net.sourceforge.cilib.pso.guideprovider.PBestGuideProvider;
import net.sourceforge.cilib.pso.positionprovider.PositionProvider;
import net.sourceforge.cilib.pso.positionprovider.StandardPositionProvider;
import net.sourceforge.cilib.pso.velocityprovider.StandardVelocityProvider;
import net.sourceforge.cilib.pso.velocityprovider.VelocityProvider;

/**
 * A {@link ParticleBehavior} object encapsulates the {@link PositionProvider}
 * and {@link VelocityProvider} that a particle uses.
 *
 */
public class ParticleBehavior implements Comparable<ParticleBehavior>, Cloneable {

    private PositionProvider positionProvider;
    private VelocityProvider velocityProvider;

    private int successCounter;
    private int selectedCounter;

    /**
     * Default constructor assigns standard position and velocity provider
     * to particles.
     */
    public ParticleBehavior() {
        this.positionProvider = new StandardPositionProvider();
        this.velocityProvider = new StandardVelocityProvider();

        this.successCounter = 0;
        this.selectedCounter = 0;
    }

    /**
     * Constructor that assigns a given position and velocity update strategy
     * to a particle.
     *
     * @param p The {@link PositionProvider} to use.
     * @param v The {@link VelocityProvider} to use.
     */
    public ParticleBehavior(PositionProvider p, VelocityProvider v) {
        this.positionProvider = p;
        this.velocityProvider = v;
        this.successCounter = 0;
        this.selectedCounter = 0;
    }

    /**
     * Copy Constructor.
     *
     * @param copy The {@link ParticleBehavior} object to copy.
     */
    public ParticleBehavior(ParticleBehavior copy) {
        this.positionProvider = copy.positionProvider.getClone();
        this.velocityProvider = copy.velocityProvider.getClone();
        this.selectedCounter = copy.selectedCounter;
        this.successCounter = copy.successCounter;
    }

    /**
     * {@inheritDoc}
     */
    public ParticleBehavior getClone() {
        return new ParticleBehavior(this);
    }

    /**
     * Get the currently set {@link PositionProvider}.
     *
     * @return The current {@link PositionProvider}.
     */
    public PositionProvider getPositionProvider() {
        return positionProvider;
    }

    /**
     * Set the {@link PositionProvider}.
     *
     * @param positionProvider The {@link PositionProvider} to set.
     */
    public void setPositionProvider(PositionProvider positionProvider) {
        this.positionProvider = positionProvider;
    }

    /**
     * Get the currently set {@link VelocityProvider}.
     *
     * @return The current {@link VelocityProvider}.
     */
    public VelocityProvider getVelocityProvider() {
        return this.velocityProvider;
    }

    /**
     * Set the {@link VelocityProvider}.
     *
     * @param velocityProvider The {@link VelocityProvider} to set.
     */
    public void setVelocityProvider(VelocityProvider velocityProvider) {
        this.velocityProvider = velocityProvider;
    }

    /**
     * Increment the number of times this behavior was successful
     */
    public void incrementSuccessCounter() {
        successCounter++;
    }

    /**
     * Increment the number of times this behavior has been selected
     */
    public void incrementSelectedCounter() {
        selectedCounter++;
    }

    /**
     * Get the number of times this behavior has been selected
     */
    public int getSelectedCounter() {
        return selectedCounter;
    }

    /**
     * Get the number of times this behavior was successful
     */
    public int getSuccessCounter() {
        return successCounter;
    }

    /**
     * Reset the selected counter to zero.
     */
    public void resetSelectedCounter() {
        selectedCounter = 0;
    }

    /**
     * Reset the success counter to zero.
     */
    public void resetSuccessCounter() {
        successCounter = 0;
    }

    /**
     * Get the number of times this behavior has been selected
     */
    public void setSelectedCounter(int n) {
        selectedCounter = n;
    }

    /**
     * Get the number of times this behavior was successful
     */
    public void setSuccessCounter(int n) {
        successCounter = n;
    }

    /**
     * Compare two behaviors with regards to how successful they were in finding
     * better fitness values.
     * @param o The {@link ParticleBehavior} object to compare with this object.
     * @return -1 if this behavior was less successful, 0 if the two behaviors were equally successful, 1 otherwise.
     */
    @Override
    public int compareTo(ParticleBehavior o) {
        int mySuccesses = this.successCounter;
        int otherSuccesses = o.successCounter;
        return(mySuccesses < otherSuccesses ? -1 : (mySuccesses == otherSuccesses ? 0 : 1));
    }
}

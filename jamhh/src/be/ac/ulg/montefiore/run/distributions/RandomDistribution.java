/*
 * Copyright (c) 2004-2009, Jean-Marc François. All Rights Reserved.
 * Licensed under the New BSD license.  See the LICENSE file.
 */
package be.ac.ulg.montefiore.run.distributions;

import java.io.Serializable;

/**
 * This interface must be implemented by all the package's classes implementing
 * a mono-variate random distribution. Distributions are not mutable.
 */
public interface RandomDistribution
        extends Serializable {

    /**
     * Generates a pseudo-random number. The numbers generated by this function
     * are drawn according to the pseudo-random distribution described by the
     * object that implements it.
     *
     * @return A pseudo-random number.
     */
    public double generate();

    /**
     * Returns the probability (density) of a given number.
     *
     * @param n A number.
     * @return
     */
    public double probability(double n);
}

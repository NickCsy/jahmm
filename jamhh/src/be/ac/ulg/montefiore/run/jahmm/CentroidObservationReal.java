/*
 * Copyright (c) 2004-2009, Jean-Marc François. All Rights Reserved.
 * Licensed under the New BSD license.  See the LICENSE file.
 */
package be.ac.ulg.montefiore.run.jahmm;

import static java.lang.Math.abs;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class represents the centroid of a set of {@link ObservationReal
 * ObservationReal}.
 */
public class CentroidObservationReal
        implements Centroid<ObservationReal> {

    private double value;

    /**
     *
     * @param o
     */
    public CentroidObservationReal(ObservationReal o) {
        this.value = o.value;
    }

    @Override
    public void reevaluateAdd(ObservationReal e,
            List<? extends ObservationReal> v) {
        value = (value * (double) v.size() + e.value) / (v.size() + 1.);
    }

    @Override
    public void reevaluateRemove(ObservationReal e,
            List<? extends ObservationReal> v) {
        value = ((value * (double) v.size()) - e.value) / (v.size() - 1.);
    }

    /**
     * Returns the distance from this centroid to a given element. This distance
     * is the absolute value of the difference between the value of this
     * centroid and the value of the argument.
     *
     * @param e The element, which must be an {@link ObservationReal
     *          ObservationReal}.
     * @return The distance to the centroid.
     */
    @Override
    public double distance(ObservationReal e) {
        return abs(e.value - value);
    }
    private static final Logger LOG = Logger.getLogger(CentroidObservationReal.class.getName());
}
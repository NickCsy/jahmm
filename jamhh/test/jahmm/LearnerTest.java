/*
 * Copyright (c) 2004-2009, Jean-Marc François. All Rights Reserved.
 * Licensed under the New BSD license.  See the LICENSE file.
 */
package jahmm;

import jahmm.learn.BaumWelchLearner;
import jahmm.learn.BaumWelchScaledLearner;
import jahmm.learn.KMeansLearner;
import jahmm.toolbox.KullbackLeiblerDistanceCalculator;
import jahmm.toolbox.MarkovGenerator;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;

/**
 *
 * @author kommusoft
 */
public class LearnerTest
        extends TestCase {

    final static private double DELTA = 5.E-3;

    private Hmm<ObservationInteger> hmm;
    private List<List<ObservationInteger>> sequences;
    private KullbackLeiblerDistanceCalculator klc;

    @Override
    protected void setUp() {
        hmm = new Hmm<>(3, new OpdfIntegerFactory(10));
        hmm.getOpdf(0).fit(new ObservationInteger(1), new ObservationInteger(2));

        MarkovGenerator<ObservationInteger> mg
                = new MarkovGenerator<>(hmm);

        sequences = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            sequences.add(mg.observationSequence(100));
        }

        klc = new KullbackLeiblerDistanceCalculator();
    }

    /**
     *
     */
    public void testBaumWelch() {
        /* Model sequences using BW algorithm */

        BaumWelchLearner bwl = new BaumWelchLearner();

        Hmm<ObservationInteger> bwHmm = bwl.learn(hmm, sequences);

        assertEquals(0., klc.distance(bwHmm, hmm), DELTA);

        /* Model sequences using the scaled BW algorithm */
        BaumWelchScaledLearner bwsl = new BaumWelchScaledLearner();
        bwHmm = bwsl.learn(hmm, sequences);

        assertEquals(0., klc.distance(bwHmm, hmm), DELTA);
    }

    /**
     *
     * @throws java.lang.CloneNotSupportedException
     */
    public void testKMeans() throws CloneNotSupportedException {
        KMeansLearner<ObservationInteger> kml
                = new KMeansLearner<>(5,
                        new OpdfIntegerFactory(10), sequences);
        assertEquals(0., klc.distance(kml.learn(), hmm), DELTA);
    }
}
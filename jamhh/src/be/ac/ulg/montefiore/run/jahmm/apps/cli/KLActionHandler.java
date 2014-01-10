/*
 * Copyright (c) 2004-2009, Jean-Marc François. All Rights Reserved.
 * Licensed under the New BSD license.  See the LICENSE file.
 */
package be.ac.ulg.montefiore.run.jahmm.apps.cli;

import be.ac.ulg.montefiore.run.jahmm.CentroidFactory;
import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.Observation;
import be.ac.ulg.montefiore.run.jahmm.apps.cli.CommandLineArguments.Arguments;
import static be.ac.ulg.montefiore.run.jahmm.apps.cli.CommandLineArguments.checkArgs;
import static be.ac.ulg.montefiore.run.jahmm.apps.cli.Types.relatedObjs;
import be.ac.ulg.montefiore.run.jahmm.io.FileFormatException;
import be.ac.ulg.montefiore.run.jahmm.io.HmmReader;
import static be.ac.ulg.montefiore.run.jahmm.io.HmmReader.read;
import be.ac.ulg.montefiore.run.jahmm.toolbox.KullbackLeiblerDistanceCalculator;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.EnumSet;
import static java.util.EnumSet.of;
import java.util.logging.Logger;

/**
 * This class implements an action that computes the Kullback-Leibler distance
 * between two HMMs.
 */
public class KLActionHandler extends ActionHandler {

    /**
     *
     * @throws FileNotFoundException
     * @throws IOException
     * @throws FileFormatException
     * @throws AbnormalTerminationException
     */
    @Override
    public void act() throws FileNotFoundException, IOException,
            FileFormatException, AbnormalTerminationException {
        EnumSet<Arguments> args = of(
                Arguments.OPDF,
                Arguments.IN_HMM,
                Arguments.IN_KL_HMM);
        checkArgs(args);

        InputStream st = Arguments.IN_KL_HMM.getAsInputStream();
        Reader reader1 = new InputStreamReader(st);
        st = Arguments.IN_HMM.getAsInputStream();
        Reader reader2 = new InputStreamReader(st);

        distance(relatedObjs(), reader1, reader2);
    }

    private <O extends Observation & CentroidFactory<O>> void
            distance(RelatedObjs<O> relatedObjs, Reader reader1, Reader reader2)
            throws IOException, FileFormatException {
        Hmm<O> hmm1 = read(reader1, relatedObjs.opdfReader());
        Hmm<O> hmm2 = read(reader2, relatedObjs.opdfReader());

        KullbackLeiblerDistanceCalculator kl
                = new KullbackLeiblerDistanceCalculator();
        System.out.println(kl.distance(hmm1, hmm2));
    }
    private static final Logger LOG = Logger.getLogger(KLActionHandler.class.getName());
}
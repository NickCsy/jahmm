/*
 * Copyright (c) 2004-2009, Jean-Marc François. All Rights Reserved.
 * Licensed under the New BSD license.  See the LICENSE file.
 */
package jahmm.calculators;

import jahmm.Hmm;
import jahmm.observables.Observation;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import jutlis.tuples.Tuple3;
import jutlis.tuples.Tuple3Base;

/**
 * This class can be used to compute the probability of a given observations
 * sequence for a given HMM. Once the probability has been computed, the object
 * holds various information such as the <i>alpha</i> (and possibly
 * <i>beta</i>) array, as described in <i>Rabiner</i> and <i>Juang</i>.
 * <p>
 * Computing the <i>beta</i> array requires a O(1) access time to the
 * observation sequence to get a theoretically optimal performance.
 */
public class ForwardBackwardCalculator extends ForwardBackwardCalculatorBase<double[][], double[][]> {

    public static final ForwardBackwardCalculator Instance = new ForwardBackwardCalculator();

    /**
     *
     */
    protected ForwardBackwardCalculator() {
    }

    protected <O extends Observation> double computeProbability(List<O> oseq, Hmm<? super O> hmm, Collection<ComputationType> flags, double[][] alpha, double[][] beta) {
        double probability = 0.;
        int n = hmm.nbStates();
        double[] tmp;
        if (flags.contains(ComputationType.ALPHA)) {
            tmp = alpha[oseq.size() - 1];
            for (int i = 0; i < n; i++) {
                probability += tmp[i];
            }
        } else {
            tmp = beta[0x00];
            O observation = oseq.get(0x00);
            for (int i = 0; i < n; i++) {
                probability += hmm.getPi(i) * hmm.getOpdf(i).probability(observation) * tmp[i];
            }
        }
        return probability;
    }

    /**
     * Computes the probability of occurrence of an observation sequence given a
     * Hidden Markov Model.
     *
     * @param <O>
     * @param hmm A Hidden Markov Model;
     * @param oseq An observation sequence.
     * @param flags How the computation should be done. See the
     * {@link ComputationType ComputationType} enum.
     * @return
     */
    @Override
    public <O extends Observation> double computeProbability(Hmm<O> hmm, Collection<ComputationType> flags, List<? extends O> oseq) {
        if (oseq.isEmpty()) {
            throw new IllegalArgumentException("Invalid empty sequence");
        }

        double[][] alpha = null, beta = null;

        if (flags.contains(ComputationType.ALPHA)) {
            alpha = computeAlpha(hmm, oseq);
        }

        if (flags.contains(ComputationType.BETA)) {
            beta = computeBeta(hmm, oseq);
        }

        return computeProbability(oseq, hmm, flags, alpha, beta);
    }

    /**
     * Computes the content of the alpha array
     *
     * @param <O>
     * @param hmm
     * @param oseq
     * @return alpha[t][i] = P(O(1), O(2),..., O(t+1), i(t+1) = i+1 | hmm), that
     * is the probability of the beginning of the state sequence (up to time
     * t+1) with the (t+1)th state being i+1.
     */
    @Override
    public <O extends Observation> double[][] computeAlpha(Hmm<? super O> hmm, Collection<O> oseq) {
        double[][] alpha = new double[oseq.size()][hmm.nbStates()];

        Iterator<O> seqIterator = oseq.iterator();
        O observation;
        if (seqIterator.hasNext()) {
            observation = seqIterator.next();

            for (int i = 0; i < hmm.nbStates(); i++) {
                alpha[0][i] = hmm.getPi(i) * hmm.getOpdf(i).probability(observation);
            }

            for (int t = 1; t < oseq.size(); t++) {
                observation = seqIterator.next();

                for (int i = 0; i < hmm.nbStates(); i++) {
                    double sum = 0.;
                    for (int j = 0; j < hmm.nbStates(); j++) {
                        sum += alpha[t - 1][j] * hmm.getAij(j, i);
                    }
                    alpha[t][i] = sum * hmm.getOpdf(i).probability(observation);
                }
            }
        }
        return alpha;
    }

    /* Computes the content of the beta array.  Needs a O(1) access time
     to the elements of oseq to get a theoretically optimal algorithm. */
    @Override
    public <O extends Observation> double[][] computeBeta(Hmm<? super O> hmm, List<O> oseq) {
        double[][] beta = new double[oseq.size()][hmm.nbStates()];
        O observation;

        for (int i = 0; i < hmm.nbStates(); i++) {
            beta[oseq.size() - 1][i] = 1.;
        }

        for (int t = oseq.size() - 2; t >= 0; t--) {
            for (int i = 0; i < hmm.nbStates(); i++) {
                observation = oseq.get(t + 1);
                double sum = 0.;
                for (int j = 0; j < hmm.nbStates(); j++) {
                    sum += beta[t + 1][j] * hmm.getAij(i, j)
                            * hmm.getOpdf(j).probability(observation);
                }
                beta[t][i] = sum;
            }
        }
        return beta;
    }

    @Override
    public <O extends Observation> Tuple3<double[][], double[][], Double> computeAll(Hmm<? super O> hmm, List<O> oseq) {
        double[][] alpha = computeAlpha(hmm, oseq);
        double[][] beta = computeBeta(hmm, oseq);
        double probability = computeProbability(oseq, hmm, EnumSet.of(ComputationType.ALPHA), alpha, beta);
        return new Tuple3Base<>(alpha, beta, probability);
    }
}
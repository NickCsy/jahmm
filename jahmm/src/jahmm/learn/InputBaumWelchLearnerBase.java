package jahmm.learn;

import jahmm.InputHmm;
import jahmm.calculators.ForwardBackwardCalculator;
import jahmm.calculators.InputForwardBackwardCalculatorBase;
import jahmm.observables.InputObservationTuple;
import jahmm.observables.Observation;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import jutlis.tuples.Tuple3;

/**
 *
 * @author kommusoft
 * @param <TObservation> The type of observations regarding the Hidden Markov
 * Model.
 * @param <TInteraction> The type of interactions regarding the Hidden Markov
 * Model.
 */
public class InputBaumWelchLearnerBase<TObservation extends Observation, TInteraction extends Enum<TInteraction>> extends BaumWelchLearnerGammaBase<TObservation, InputObservationTuple<TInteraction, TObservation>, InputHmm<TObservation, TInteraction>, double[][], double[][], double[][]> implements InputBaumWelchLearner<TObservation, TInteraction> {

    private static final Logger LOG = Logger.getLogger(InputBaumWelchLearnerBase.class.getName());

    @Override
    @SuppressWarnings("unchecked")
    protected ForwardBackwardCalculator<double[][], double[][], TObservation, InputObservationTuple<TInteraction, TObservation>, InputHmm<TObservation, TInteraction>> getCalculator() {
        return InputForwardBackwardCalculatorBase.Instance;
    }

    @Override
    protected double[][][] estimateXi(List<? extends InputObservationTuple<TInteraction, TObservation>> sequence, Tuple3<double[][], double[][], Double> abp, InputHmm<TObservation, TInteraction> hmm) {
        if (sequence.size() <= 1) {
            throw new IllegalArgumentException("Observation sequence too short");
        }
        double[][] a = abp.getItem1();
        double[][] b = abp.getItem2();
        double pinv = 1.0d / abp.getItem3();
        double[][][] xi = new double[sequence.size() - 1][hmm.nbStates()][hmm.nbStates()];
        Iterator<? extends InputObservationTuple<TInteraction, TObservation>> seqIterator = sequence.iterator();
        seqIterator.next();
        for (int t = 0; t < sequence.size() - 1; t++) {
            InputObservationTuple<TInteraction, TObservation> interaction = seqIterator.next();
            for (int i = 0; i < hmm.nbStates(); i++) {
                for (int j = 0; j < hmm.nbStates(); j++) {
                    xi[t][i][j] = a[t][i] * hmm.getAixj(i, interaction.getInput(), j) * hmm.getOpdf(j).probability(interaction.getObservation()) * b[t + 1][j] * pinv;
                }
            }
        }
        return xi;
    }

    @Override
    public InputHmm<TObservation, TInteraction> iterate(InputHmm<TObservation, TInteraction> hmm, List<? extends List<? extends InputObservationTuple<TInteraction, TObservation>>> sequences) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected double[][] createADenominator(InputHmm<TObservation, TInteraction> hmm) {
        return new double[hmm.nbStates()][hmm.nbInput()];
    }

    @Override
    protected double[][][] createANumerator(InputHmm<TObservation, TInteraction> hmm) {
        return new double[hmm.nbStates()][hmm.nbInput()][hmm.nbStates()];
    }

    @Override
    protected void updateAbarXiGamma(InputHmm<TObservation, TInteraction> hmm, List<? extends InputObservationTuple<TInteraction, TObservation>> obsSeq, double[][][] xi, double[][] gamma, double[][][] aijNum, double[][] aijDen) {
        int I = aijDen.length;
        int T = xi.length;
        for (int i = 0; i < I; i++) {
            for (int t = 0; t < T; t++) {
                int k = hmm.getInputIndex(null);//TODO: find observation
                aijDen[i][k] += gamma[t][i];

                for (int j = 0; j < I; j++) {
                    aijNum[i][k][j] += xi[t][i][j];
                }
            }
        }
    }

    @Override
    protected void setAValues(InputHmm<TObservation, TInteraction> hmm, double[][][] aijNum, double[][] aijDen) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void setPdfValues(InputHmm<TObservation, TInteraction> nhmm, List<? extends List<? extends InputObservationTuple<TInteraction, TObservation>>> sequences, double[][][] allGamma) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void setPiValues(InputHmm<TObservation, TInteraction> nhmm, double[][][] allGamma) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

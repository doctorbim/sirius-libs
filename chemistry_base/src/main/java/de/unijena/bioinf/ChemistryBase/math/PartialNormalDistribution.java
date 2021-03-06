package de.unijena.bioinf.ChemistryBase.math;

/**
 * A normal distribution which is flatten in the middle such that everything within
 * mean +- tolerance has exactly the same probability density.
 */
public class PartialNormalDistribution extends RealDistribution {

    protected NormalDistribution innerDistribution;
    protected double tolerance;
    protected double start, end, maximum;
    protected double rescale;


    public PartialNormalDistribution(double mean, double variance, double tolerance) {
        this.innerDistribution = new NormalDistribution(mean, variance);
        this.tolerance = tolerance;
        this.maximum = innerDistribution.getDensity(mean+tolerance);
        this.start = mean-tolerance;
        this.end = mean+tolerance;
        this.rescale = 1d/(1d - (innerDistribution.getProbability(start, end) - (end-start)*maximum ));
    }

    @Override
    public double getDensity(double x) {
        if (x >= start && x <= end) return maximum*rescale;
        else return innerDistribution.getDensity(x)*rescale;
    }

    @Override
    public double getCumulativeProbability(double x) {
        final double x1 = innerDistribution.getCumulativeProbability(Math.min(x, start))*rescale;
        final double x2 = (x < start) ? 0 : maximum*(Math.min(end, x)-start)*rescale;
        final double x3 = (x < end) ? 0 : (innerDistribution.getCumulativeProbability(x)-innerDistribution.getCumulativeProbability(end))*rescale;
        return x1+x2+x3;
    }

    @Override
    public double getVariance() {
        return innerDistribution.getVariance();
    }

    @Override
    public double getMean() {
        return innerDistribution.getMean();
    }
}

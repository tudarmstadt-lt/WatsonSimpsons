package answerProcessing.types;

import java.util.Comparator;

/**
 * Created by dath on 16.07.15.
 */
public class ScoringComparator implements Comparator<Sentence> {

    String scoreName;
    boolean ascendingOrder = false;

    public ScoringComparator(String scoreName) {
        this.scoreName = scoreName;
    }

    /**
     *
     * @param scoreName
     * @param ascendingOrder true for ascending ordering (min to max)
     */
    public ScoringComparator(String scoreName, boolean ascendingOrder) {
        this.scoreName = scoreName;
        this.ascendingOrder = ascendingOrder;
    }

    @Override
    public int compare(Sentence s1, Sentence s2) {
        if(Math.abs(s1.getScore(scoreName)-s2.getScore(scoreName)) < 0.000001)
            return 0;

        // ascending order (from min to max)
        if(ascendingOrder)
            return Double.compare(s1.getScore(scoreName), s2.getScore(scoreName));

        // descending order (from max to min)
        return Double.compare(s2.getScore(scoreName), s1.getScore(scoreName));
    }

}

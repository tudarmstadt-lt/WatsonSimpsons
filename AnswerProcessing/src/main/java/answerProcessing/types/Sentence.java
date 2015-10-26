package answerProcessing.types;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sentence {


    int index;
    String parse;
    String rawText;
    
    // Text that this sentence is part of
    private Text parentText;

    @SerializedName("tokens")
    List<Token> tokens;

    @SerializedName("basic-dependencies")
    List<Dependency> basicDependencies;

    @SerializedName("collapsed-dependencies")
    List<Dependency> collapsedDependencies;

    @SerializedName("collapsed-ccprocessed-dependencies")
    List<Dependency> collapsedCcprocessedDependencies;

    MachineReading machineReading;

    // stores scoring values for answer selection
    Map<String, Double> scores;

    // stores type of ngram-feature and score for distance to question
    private Map<String, Double> ngramDistances;

    public Sentence() {
        this.tokens = new ArrayList<Token>();
        this.basicDependencies = new ArrayList<Dependency>();
        this.collapsedDependencies = new ArrayList<Dependency>();
        this.collapsedCcprocessedDependencies = new ArrayList<Dependency>();
        this.machineReading = new MachineReading();
        this.ngramDistances = new HashMap<String, Double>();
        this.scores = new HashMap<String, Double>();
        this.parentText= null;
    }

    public Sentence(int index, String parse, String rawText, List<Token> tokens,
                    List<Dependency> basicDependencies,
                    List<Dependency> collapsedDependencies,
                    List<Dependency> collapsedCcprocessedDependencies) {
        super();
        this.index = index;
        this.parse = parse;
        this.rawText = rawText;
        this.tokens = tokens;
        this.basicDependencies = basicDependencies;
        this.collapsedDependencies = collapsedDependencies;
        this.collapsedCcprocessedDependencies = collapsedCcprocessedDependencies;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getParse() {
        return parse;
    }

    public void setParse(String parse) {
        this.parse = parse;
    }

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Dependency> getBasicDependencies() {
        return basicDependencies;
    }

    public void setBasicDependencies(List<Dependency> basicDependencies) {
        this.basicDependencies = basicDependencies;
    }

    public List<Dependency> getCollapsedDependencies() {
        return collapsedDependencies;
    }

    public void setCollapsedDependencies(List<Dependency> collapsedDependencies) {
        this.collapsedDependencies = collapsedDependencies;
    }

    public List<Dependency> getCollapsedCcprocessedDependencies() {
        return collapsedCcprocessedDependencies;
    }

    public void setCollapsedCcprocessedDependencies(
            List<Dependency> collapsedCcprocessedDependencies) {
        this.collapsedCcprocessedDependencies = collapsedCcprocessedDependencies;
    }

    public MachineReading getMachineReading() {
        return machineReading;
    }

    public void setMachineReading(MachineReading machineReading) {
        this.machineReading = machineReading;
    }

    public Double getNgramDistance(String nGramType) {
        return ngramDistances.get(nGramType);
    }

    public Double[] getNGramDistances() {
        Collection<Double> distances = ngramDistances.values();
        return distances.toArray(new Double[distances.size()]);
    }

    @Override
    public String toString() {
        return rawText;
    }

    public String getConcatTokens() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tokens.size(); ++i) {
            sb.append(tokens.get(i).getWord() + " ");
        }
        return sb.toString().trim();
    }

    public void addNgramDistance(String description, Double distance) {
        this.ngramDistances.put(description, distance);
    }

    public Map<String, Double> getScores() {
        return scores;
    }

    public void setScores(Map<String, Double> scores) {
        this.scores = scores;
    }

    /**
     * Adds a score associated with given name to this sentence
     *
     * @param scoreName    name of scoring for later access
     * @param scoringValue computed scoring value for this sentence
     */
    public void addScore(String scoreName, double scoringValue) {
        this.scores.put(scoreName, scoringValue);
    }

    /**
     * @param scoreName the name associated with scoring computation
     * @return the computed scoring value for this sentence
     */
    public double getScore(String scoreName) {
        if (this.scores.containsKey(scoreName))
            return this.scores.get(scoreName);
        else
            return 0.0;
    }

	public Text getParentText() {
		return parentText;
	}

	public void setParentText(Text parentText) {
		this.parentText = parentText;
	}
    

	
}

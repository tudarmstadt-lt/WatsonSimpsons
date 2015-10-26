package answerProcessing.types;

public class Dependency {

	/*
		 "dep": "ROOT",
          "governor": "0",
          "governorGloss": "ROOT",
          "dependent": "5",
          "dependentGloss": "wife"
	 */
	
	String dep;
	int governor;	
	String governorGloss;
	int dependent;
	String dependentGloss;
	
	public Dependency(String dep, int govenor, String governorGloss,
			int dependent, String dependentGloss) {
		super();
		this.dep = dep;
		this.governor = govenor;
		this.governorGloss = governorGloss;
		this.dependent = dependent;
		this.dependentGloss = dependentGloss;
	}
	
	public String getDep() {
		return dep;
	}
	public void setDep(String dep) {
		this.dep = dep;
	}
	public int getGovenor() {
		return governor;
	}
	public void setGovenor(int govenor) {
		this.governor = govenor;
	}
	public String getGovernorGloss() {
		return governorGloss;
	}
	public void setGovernorGloss(String governorGloss) {
		this.governorGloss = governorGloss;
	}
	public int getDependent() {
		return dependent;
	}
	public void setDependent(int dependent) {
		this.dependent = dependent;
	}
	public String getDependentGloss() {
		return dependentGloss;
	}
	public void setDependentGloss(String dependentGloss) {
		this.dependentGloss = dependentGloss;
	}

	@Override
	public String toString() {
		return "Dependency [dep="+dep+", governor="+governor+", govGloss="+governorGloss+", dependent="+dependent+", depGloss=\""+dependentGloss+"\"]";
	}
	
	
	
}

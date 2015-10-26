package answerProcessing.types;

import java.util.ArrayList;
import java.util.List;

public class MachineReading {
	
	List<EntityMention> entities;
	List<Relation> relations;
	
	public MachineReading() {
		entities = new ArrayList<EntityMention>();
		relations = new ArrayList<MachineReading.Relation>();
	}
	
	public MachineReading(List<EntityMention> entities, List<Relation> relations) {
		super();
		this.entities = entities;
		this.relations = relations;
	}
	

	public List<EntityMention> getEntities() {
		return entities;
	}

	public void setEntities(List<EntityMention> entities) {
		this.entities = entities;
	}

	public List<Relation> getRelations() {
		return relations;
	}

	public void setRelations(List<Relation> relations) {
		this.relations = relations;
	}


	public class Relation {

		String id, type;
		
		List<EntityMention> arguments;		
		List<Probabilities> probabilities;
		
		public Relation() {
			arguments = new ArrayList<EntityMention>();
			probabilities = new ArrayList<MachineReading.Probabilities>();
		}
		
		public Relation(String id, String type, List<EntityMention> arguments,
				List<Probabilities> probabilities) {
			super();
			this.id = id;
			this.type = type;
			this.arguments = arguments;
			this.probabilities = probabilities;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public List<EntityMention> getArguments() {
			return arguments;
		}

		public void setArguments(List<EntityMention> arguments) {
			this.arguments = arguments;
		}

		public List<Probabilities> getProbabilities() {
			return probabilities;
		}

		public void setProbabilities(List<Probabilities> probabilities) {
			this.probabilities = probabilities;
		}
		
		
	}
	
	public class Probabilities {
		String label;
		double value;
		
		public Probabilities(String label, double value) {
			super();
			this.label = label;
			this.value = value;
		}
		
		public String getLabel() {
			return label;
		}
		public void setLabel(String label) {
			this.label = label;
		}
		public double getValue() {
			return value;
		}
		public void setValue(double value) {
			this.value = value;
		}
		
		@Override
		public String toString() {
			return "Prob [label="+label+", value="+value+"]";
		}
		
		
	}
}

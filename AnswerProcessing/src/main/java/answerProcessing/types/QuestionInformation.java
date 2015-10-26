package answerProcessing.types;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jwatson.answer.Focuslist;
import jwatson.answer.Latlist;
import jwatson.answer.Qclasslist;

public class QuestionInformation {
	List<Qclasslist> qClassList;
	List<Latlist> latList;
	List<Focuslist> focusList;
	Questiontype questionType;
	String question;
	private List<Sentence> sentences;
	private Set<Token> tokens = new HashSet<Token>();
	private static final String[] colorDic = new String[] { "color", "colour" };
	private static final String[] animalDic = new String[] { "ant", "ape",
			"seal", "dog", "elephant", "sheep", "cobra", "cow", "coyote",
			"python", "snake", "monkey", "pony", "horse", "mouse", "bird",
			"dolphin", "duck", "falcon", "gorilla", "hamster", "lobster",
			"octopus", "woodpecker", "ostrich", "rabbit", "squirrel", "parrot",
			"rat", "terrier", "raven", "pigeon", "tortoise", "toucan",
			"tree lizard", "volture", "vulture", "insect", "cat", "animal",
			"pet", "raccoon", "racoon", "bear", "woodpecker", "fish",
			"creature", "racehorse", "frog" };
	private static final String[] familyDic = new String[] { "family name",
			"last name", "family" };
	private static final String[] organizationDic = new String[] {
			"organization", "organisation", "institution", "activity",
			"association", "shop", "restaurant", "hotel", "business",
			"facility", "theme park", "school", "store", "firm" };
	private static final String[] jobDic = new String[] { "job" };
	private static final String[] femaleDic = new String[] { "mother",
			"sister", "niece", "girlfriend", "daughter", "wife", "girl",
			"woman", "hostess", "nurse", "mom", "waitress", "nanny", "lady" };
	private static final String[] maleDic = new String[] { "brother", "father",
			"cousin", "husband", "son", "boyfriend", "man", "guy", "boy" };
	private static final String[] personDic = new String[] { "alias", "child",
			"assistant", "friend", "person", "siblings", "teacher",
			"executive", "student", "member", "ranger", "officer", "prisoner",
			"psychologist", "classmate", "affair", "twins", "partner", "date",
			"character", "he", "she" };
	private static final String[] locationDic = new String[] { "house", "town",
			"GPE", "address", "country" };
	private static final String[] moneyDic = new String[] { "income", "money",
			"price", "cost", "dollar", "bill" };
	private static final String[] durationDic = new String[] { "how old",
			"how long" };
	private static final String[] durationLATDic = new String[] { "age" };
	private static final String[] numDic = new String[] { "how many" };
	private static final String[] heightDic = new String[] { "how tall" };
	private static final String[] weightDic = new String[] { "weight" };
	private static final String[] numLATDic = new String[] { "i.q.", "iq" };
	private static final String[] personWhatDic = new String[] { "name",
			"rename", "alias", "called, call" };
	private static final String[] locationVerbs = new String[] { "found",
			"destroy", "build", "rebuild" };

	public static enum Questiontype {
		femalePerson, malePerson, animalNE, person, location, season, episode, date, number, unknown, family,animal, ordinal, duration, money, organization, percentage, color, distance, weight, location_organization;
	};

	public QuestionInformation(List<Qclasslist> qClassList,
			List<Latlist> latList, List<Focuslist> focusList,
			List<Sentence> sentences, String questionText) {
		this.qClassList = qClassList;
		this.latList = latList;
		this.focusList = focusList;
		this.sentences = sentences;
		this.question = questionText.toLowerCase();
		this.questionType = computeQuestionType();
	}

	Questiontype computeQuestionType() {
		for (Sentence sentence : sentences) {
			tokens.addAll(sentence.getTokens());
		}
		if (findIn_focusList("how much") || findIn_focusList("what")) {
			for (String word : moneyDic) {
				if (findIn_QuestionText(word))
					return Questiontype.money;
			}
		}
		for (String word : durationDic) {
			if (findIn_focusList(word))
				return Questiontype.duration;
		}
		for (String word : durationLATDic) {
			if (findIn_latList(word))
				return Questiontype.duration;
		}
		for (String word : heightDic) {
			if (findIn_focusList(word))
				return Questiontype.distance;
		}
		for (String word : weightDic) {
			if (findIn_latList(word))
				return Questiontype.weight;
		}
		if (findIn_latList("percentage") || findIn_latList("percent"))
			return Questiontype.percentage;
		if (findIn_qClassList("DATE") || findIn_latList("when")
				|| findIn_focusList("when"))
			return Questiontype.date;
		if (findIn_qClassList("NUMBER"))
			return Questiontype.number;
		if (findIn_focusList("where"))
			return Questiontype.location_organization;

		for (String word : numDic) {
			if (findIn_focusList(word))
				return Questiontype.number;
		}
		if (findIn_latList("episode"))
			return Questiontype.episode;
		if (findIn_latList("season"))
			return Questiontype.season;
		for (String word : animalDic) {
			if (findIn_latList(word)) {
				if (findIn_focusList("whose")
						|| findIn_focusList("whose" + " " + word)
						|| findIn_focusList("who")
						&& startsIn_QuestionText("own"))
					return Questiontype.person;

				else
					return Questiontype.animalNE;
			}
		}
		for (String word : familyDic) {
			if (findIn_latList(word))
				return Questiontype.family;
		}

		for (String word : femaleDic) {
			if (findIn_latList(word))
				return Questiontype.femalePerson;
		}

		for (String word : maleDic) {
			if (findIn_latList(word))
				return Questiontype.malePerson;
		}

		for (String word : personDic) {
			if (findIn_latList(word))
				return Questiontype.person;
		}
		for (String word : moneyDic) {
			if (findIn_latList(word))
				return Questiontype.money;
		}

		for (String word : locationDic) {
			if (findIn_latList(word))
				return Questiontype.location;
		}

		if (findIn_focusList("why"))
			return Questiontype.unknown;

		for (String word : organizationDic) {
			if (findIn_latList(word))
				return Questiontype.organization;
		}
		for (String word : colorDic) {
			if (findIn_latList(word))
				return Questiontype.color;
		}

		for (String word : jobDic) {
			if (findIn_latList(word))
				return Questiontype.unknown;
		}

		for (String word : numLATDic) {
			if (findIn_latList(word))
				return Questiontype.number;
		}

		if (question.contains("how many"))
			return Questiontype.number;

		for (String word : femaleDic) {
			if (((findIn_focusList("what") && findIn_QuestionText("name"))
					|| (findIn_focusList("how") && (startsIn_QuestionText("call") || startsIn_QuestionText("name"))) || findIn_focusList("who"))
					&& findIn_QuestionText(word))
				return Questiontype.femalePerson;
		}

		for (String word : maleDic) {
			if (((findIn_focusList("what") && findIn_QuestionText("name"))
					|| (findIn_focusList("how") && (startsIn_QuestionText("call") || startsIn_QuestionText("name"))) || findIn_focusList("who"))
					&& findIn_QuestionText(word))
				return Questiontype.malePerson;
		}
		for (String word : personDic) {
			if (((findIn_focusList("what") && (findIn_QuestionText("name") || findIn_QuestionText("rename")))
					|| (findIn_focusList("how") && (startsIn_QuestionText("call") || startsIn_QuestionText("name"))) || findIn_focusList("who"))
					&& findIn_QuestionText(word))
				return Questiontype.person;
		}
		for (String word : animalDic) {
			if (((findIn_focusList("what") && findIn_QuestionText("name"))
					|| (findIn_focusList("how") && (startsIn_QuestionText("call") || startsIn_QuestionText("name"))) || (findIn_focusList("who") && !startsIn_QuestionText("own")))
					&& findIn_QuestionText(word))
				return Questiontype.animalNE;
		}
		if (findIn_focusList("who") || findIn_focusList("whose"))
			return Questiontype.person;

		if (findIn_QuestionText("where"))
			return Questiontype.location_organization;

		for (String word : locationDic) {
			if (((findIn_focusList("what") && findIn_QuestionText("name")) || (findIn_focusList("how")
					&& startsIn_QuestionText("call") || startsIn_QuestionText("name")))
					&& findIn_QuestionText(word))
				return Questiontype.location;
		}
		for (String word : organizationDic) {
			if (((findIn_focusList("what") && findIn_QuestionText("name")) || (findIn_focusList("how")
					&& startsIn_QuestionText("call") || startsIn_QuestionText("name")))
					&& findIn_QuestionText(word))
				return Questiontype.organization;
		}
		if (question.startsWith("when"))
			return Questiontype.date;

		for (String word : organizationDic) {
			if (findIn_QuestionText(word))
				return Questiontype.organization;
		}
		for (String word : locationDic) {
			if (findIn_QuestionText(word))
				return Questiontype.location;
		}
		if (findIn_QuestionText("who"))
			return Questiontype.person;
		if (findIn_focusList("what")) {
			for (String word : personWhatDic) {
				if (findIn_QuestionText(word))
					return Questiontype.person;
			}
			for (String word : locationVerbs) {
				if (findIn_QuestionText(word))
					return Questiontype.location_organization;
			}
		}
		if (findIn_focusList("what")
				&& findIn_QuestionText("name")
				|| findIn_focusList("how")
				&& (startsIn_QuestionText("call") || startsIn_QuestionText("name")))
			return Questiontype.person;

		return Questiontype.unknown;
	}

	private boolean startsIn_QuestionText(String value) {
		for (Token token : tokens) {
			if (token.word.toLowerCase().startsWith(value))
				return true;
		}
		return false;

	}

	private boolean findIn_QuestionText(String value) {
		for (Token token : tokens) {
			if (token.word.toLowerCase().equals(value))
				return true;
		}
		return false;

	}

	private boolean findIn_latList(java.lang.String value) {
		for (Latlist element : latList) {
			if (element.getValue().toLowerCase().equals(value))
				return true;
		}
		return false;
	}

	private boolean findIn_focusList(java.lang.String value) {
		for (Focuslist element : focusList) {
			if (element.getValue().toLowerCase().equals(value))
				return true;
		}
		return false;
	}

	private boolean findIn_qClassList(String value) {
		for (Qclasslist element : this.qClassList) {

			if (element.getValue().equals(value))
				return true;
		}
		return false;
	}

	public void setQuestionType(Questiontype type) {
		this.questionType = type;

	}

	public List<Qclasslist> getqClassList() {
		return qClassList;
	}

	public void setqClassList(List<Qclasslist> qClassList) {
		this.qClassList = qClassList;
	}

	public List<Latlist> getLatList() {
		return latList;
	}

	public void setLatList(List<Latlist> latList) {
		this.latList = latList;
	}

	public List<Focuslist> getFocusList() {
		return focusList;
	}

	public void setFocusList(List<Focuslist> focusList) {
		this.focusList = focusList;
	}

	public List<String> getNEtype() {
		List<String> result = new ArrayList<String>();
		switch (questionType) {
		case femalePerson:
		case malePerson:
		case person:
			result.add("PERSON");
			result.add("MISC");
			result.add("PARTOFPERSON");
			result.add("FAMILY");
			result.add("APOSTROPHE");
			break;
		case family:
			result.add("PERSON");
			result.add("APOSTROPHE");
			result.add("FAMILY");
			break;
		case animalNE:
			result.add("PERSON");
			result.add("APOSTROPHE");
			break;
		case location:
		case organization:
		case location_organization:
			result.add("LOCATION");
			result.add("PARTOFLOCATION");
			result.add("PARTOFPERSON");
			result.add("DATE");
			result.add("PERSON");
			result.add("NUMBER");
			result.add("ORDINAL");
			result.add("ORGANIZATION");
			result.add("BINDING");// of,&
			result.add("MONEY");
			result.add("AND");// "and"
			result.add("MISC");
			result.add("APOSTROPHE");// ','s
			result.add("FAMILY");// "family"
			break;
		case color:
			result.add("COLOR");
			break;
		case date:
			result.add("DATE");
			break;
		case ordinal:
			result.add("ORDINAL");
			break;
		case number:
			result.add("NUMBER");
			break;
		case duration:
			result.add("DURATION");
			break;
		case money:
			result.add("MONEY");
			break;
		case percentage:
			result.add("PERCENTAGE");
			break;
		case distance:
			result.add("NUMBER");
			result.add("AND");
			result.add("DISTANCE");
			break;
		case weight:
			result.add("NUMBER");
			result.add("AND");
			result.add("WEIGHT");
			break;
		default:
			result.add("UNKNOWN");
			break;

		}
		return result;
	}

	public Questiontype getQuestionType() {
		return questionType;
	}
}
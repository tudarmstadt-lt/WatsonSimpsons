package answerProcessing.modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import answerProcessing.EntityCollection;
import answerProcessing.types.NeOccurrence;
import answerProcessing.types.Question;
import answerProcessing.types.QuestionInformation.Questiontype;
import answerProcessing.types.QuizAnswer;
import answerProcessing.types.Sentence;
import answerProcessing.types.Token;

public class NamedEntityProcessing {
	/**
	 * Extract the NamedEntity occurrences of a sentence
	 * 
	 * @param sentence
	 * @param neTypes
	 * @param concatNes
	 * @return NamedEntity occurrences
	 */
	public static Map<String, NeOccurrence> getNamedEntitiesOccurences(
			Sentence sentence, List<String> neTypes, boolean concatNes) {
		Map<String, NeOccurrence> neOccurences = new HashMap<String, NeOccurrence>();
		List<Token> tokens = sentence.getTokens();
		Token currentToken, nextToken;
		String currentNe;
		String neText = "";
		List<String> usedNeTypes = new ArrayList<String>();
		for (int i = 0; i < tokens.size() - 1; i++) {
			currentToken = tokens.get(i);
			if (neTypes.contains("COLOR"))
				setColorEntities(currentToken);
			currentNe = currentToken.getNer();
			if (neTypes.contains(currentNe)) {
				if (concatNes) {
					nextToken = tokens.get(i + 1);
					String nextNe = nextToken.getNer();
					neText = concatNeTexts(neText,currentToken);
					usedNeTypes.add(currentNe);
					if (!neTypes.contains(nextNe)) {
						addNeOccurence(neOccurences, neText, usedNeTypes);
						neText = "";
						usedNeTypes = new ArrayList<String>();
					}
				} else {
					addNeOccurence(neOccurences, currentToken.getWord(),
							usedNeTypes);
				}
			}
		}
		// add last token
		if (!tokens.isEmpty()) {
			currentToken = tokens.get(tokens.size() - 1);
			currentNe = currentToken.getNer();
			usedNeTypes.add(currentNe); 
			if (currentNe.equals(neTypes)) {
				if (concatNes) {
					neText = concatNeTexts(neText,currentToken);
				} else
					neText = currentToken.getWord();
				addNeOccurence(neOccurences, neText, usedNeTypes);
			}
		}
		return neOccurences;
	}

	private static String concatNeTexts(String neText, Token currentToken) {
		String whitespace=" ";
		if(currentToken.getWord().equals("'s")||currentToken.getWord().equals("'"))
			whitespace= "";
		if(neText.endsWith("'") && currentToken.getNer()=="NUMBER"){
			whitespace= "";
			neText = neText.substring(0, neText.length()-1).concat("'");
		}
		return (neText + whitespace + currentToken.getWord()).trim();
	}

	/**
	 * increments NeOccurence in HashMap
	 * 
	 * @param neOccurences
	 * @param neText
	 * @param usedNeTypes
	 */
	protected static void addNeOccurence(
			Map<String, NeOccurrence> neOccurences, String neText,
			List<String> usedNeTypes) {
		if (neOccurences.containsKey(neText)) {
			neOccurences.get(neText).addOccurrence();
		} else {
			neOccurences.put(neText, new NeOccurrence(1, usedNeTypes));
		}
	}

	/**
	 * get all NamedEntitys of a sentence
	 * 
	 * @param sentence
	 * @param neTypes
	 * @param concatNes
	 * @return NamedEntitys of a sentence
	 */
	public static List<String> getNamedEntities(Sentence sentence,
			List<String> neTypes, boolean concatNes) {
		List<String> namedEntities = new ArrayList<String>();
		namedEntities.addAll(getNamedEntitiesOccurences(sentence, neTypes,
				concatNes).keySet());

		return namedEntities;
	}

	/**
	 * Extract the most common named entities for every given possible answer
	 * from all sentences
	 * 
	 * @param question
	 * @param sentences
	 *            most likely sentences (corresponding to texts from the given
	 *            answers)
	 * @param range
	 *            number of sentences to be considered around the candidate
	 *            sentence (e.g. 1-> previous and next sentence)
	 * @param concatNes
	 *            decides if named entities span more than one word
	 * @return named entity texts (for every sentence)
	 */
	public static List<QuizAnswer> extractMostCommonNamedEntitiesMultipleSetences(
			Question question, List<Sentence> sentences, int range,
			boolean concatNes) {
		List<String> neType = question.getQuestionInformation().getNEtype();
		// extract ne texts from question (should not be in answer)
		// extract most common ne text from every possible answer
		Map<String, NeOccurrence> neOccurrences = new LinkedHashMap<String, NeOccurrence>();
		for (int i = 0; i < sentences.size(); i++) { 
			Map<String, NeOccurrence> tempNeOccurrences = getNamedEntitiesOccurences(
					sentences.get(i), neType, concatNes);
			for (String ne : tempNeOccurrences.keySet()) {
				if (neOccurrences.containsKey(ne)) {
					neOccurrences.get(ne).addOccurrence(
							tempNeOccurrences.get(ne));
				} else {

					neOccurrences.put(ne, tempNeOccurrences.get(ne));
					tempNeOccurrences.get(ne).setSentence(sentences.get(i));
				}
			}
		}
		return filter_sort(neOccurrences, question, neType, concatNes);
	}

	/**
	 * filter NE-extractions with mandatory NE-types, score results and sort
	 * them
	 * 
	 * @param neOccurrences
	 * @param question
	 * @param neType
	 * @param concatNes
	 * @return
	 */
	private static List<QuizAnswer> filter_sort(
			Map<String, NeOccurrence> neOccurrences, Question question,
			List<String> neType, boolean concatNes) {
		List<Sentence> questionSents = question.getAnnotatedText()
				.getSentences();
		List<String> questionNes = new ArrayList<String>();
		for (Sentence questionSent : questionSents) {
			questionNes
					.addAll(getNamedEntities(questionSent, neType, concatNes));
		}
		neOccurrences.remove("``");
		if (question.getQuestionInformation().getNEtype()
				.contains("ORGANIZATION")) {
			List<String> minimumNes = new ArrayList<String>();
			minimumNes.add("ORGANIZATION");
			minimumNes.add("LOCATION");
			// remove question NEs from answer NEs
			neOccurrences = filterNeTypes(neOccurrences, minimumNes);
			neOccurrences = filterOrganizationNEs(neOccurrences, questionNes);
		}
		Questiontype qtype = question.getQuestionInformation()
				.getQuestionType();
		if (qtype.equals(Questiontype.animalNE)
				|| qtype.equals(Questiontype.family)) {
			List<String> minimumNes = new ArrayList<String>();
			minimumNes.add("PERSON");
			neOccurrences = filterNeTypes(neOccurrences, minimumNes);
		}
		if (qtype.equals(Questiontype.femalePerson)
				|| qtype.equals(Questiontype.malePerson)
				|| qtype.equals(Questiontype.person)) {
			List<String> minimumNes = new ArrayList<String>();
			minimumNes.add("PERSON");
			minimumNes.add("MISC");
			neOccurrences = filterNeTypes(neOccurrences, minimumNes);
		}
		if (qtype.equals(Questiontype.weight)) {
			List<String> minimumNes = new ArrayList<String>();
			minimumNes.add("WEIGHT");
			neOccurrences = filterNeTypes(neOccurrences, minimumNes);
		}
		if (qtype.equals(Questiontype.distance)) {
			List<String> minimumNes = new ArrayList<String>();
			minimumNes.add("DISTANCE");
			neOccurrences = filterNeTypes(neOccurrences, minimumNes);
		}

		if (qtype == Questiontype.family)
			neOccurrences = filterFamilyNEs(neOccurrences, questionNes);
		else if (qtype == Questiontype.animalNE || qtype == Questiontype.person
				|| qtype == Questiontype.malePerson
				|| qtype == Questiontype.femalePerson)
			neOccurrences = filterPersonNEs(neOccurrences, question,
					questionNes);
		HashSet<Integer> occSet = new HashSet<Integer>();
		for (NeOccurrence neOccurrence : neOccurrences.values()) {
			occSet.add(neOccurrence.getOccurrence());
		}
		final int max = (occSet.isEmpty()) ? 1 : Collections.max(occSet);
		List<QuizAnswer> nes = sortByValue(neOccurrences, max);
		return nes;
	}

	/**
	 * annotation for manual NE-type color
	 * 
	 * @param currentToken
	 */
	private static void setColorEntities(Token currentToken) {
		String[] color = new String[] { "blue", "red", "white", "gold",
				"silver", "yellow", "orange", "black", "purple", "magenta",
				"green", "bronze", "gray", "brown", "violet", "cyan" };
		for (int i = 0; i < color.length; i++) {
			if (currentToken.getWord().contains(color[i])) {
				currentToken.setNer("COLOR");
				break;
			}
		}
	}

	/**
	 * filters person NEs deletes most-common male or female names
	 * 
	 * @param neOccurrences
	 * @param neType
	 * @return filtered neOccurences
	 */
	private static Map<String, NeOccurrence> filterPersonNEs(
			Map<String, NeOccurrence> neOccurrences, Question question,
			List<String> questionNesUnfiltered) {
		List<String> questionNes = new ArrayList<>();
		// remove apostrophes in questionNes
		for (String ne : questionNesUnfiltered) {
			if (ne.endsWith("'s"))
				questionNes.add(ne.substring(0, ne.length() - 2));
			else {
				if (ne.endsWith("s"))
					questionNes.add(ne.substring(0, ne.length() - 1));
				if (ne.endsWith("'"))
					questionNes.add(ne.substring(0, ne.length() - 1));
				else
					questionNes.add(ne);
			}

		}
		// filter mostCommon male/female in answerNes
		String male = "Milhouse, Bart Simpson, Homer, Seymour Skinner, "
				+ "Moe, Apu, Waylon Smithers, Ned Flanders, Montgomery Burns, Carl, Lenny, Abe, Grampa, "
				+ "Martin, Nelson, Willie, Matt Groening, Ralph, Timothy, Todd, Otto, Barney, Mayor Quimby, Wiggum, Arnie, Kent";
		String female = "Lisa Simpson, Marge, Edna, Helen, Patty, Selma, Maggie, Cartwright, Emily, Manjula";
		List<String> mostCommonMalePerson = new ArrayList<String>();
		List<String> mostCommonFemalePerson = new ArrayList<String>();
		mostCommonMalePerson = Arrays.asList(male.split(", "));
		mostCommonFemalePerson = Arrays.asList(female.split(", "));
		if (question.getQuestionType() == Questiontype.animalNE
				|| question.getQuestionType() == Questiontype.femalePerson)
			questionNes.addAll(mostCommonMalePerson);
		if (question.getQuestionType() == Questiontype.animalNE
				|| question.getQuestionType() == Questiontype.malePerson)
			questionNes.addAll(mostCommonFemalePerson);
		Set<String> deleteNes = filterPersonQuestionNEs(questionNes, neOccurrences.keySet());
		// filter MISC entries
		for (Entry<String, NeOccurrence> ne : neOccurrences.entrySet()) {
			if (ne.getValue().getNeTypes().contains("MISC")
					&& !ne.getValue().getNeTypes().contains("PERSON")) {
				for (String episode : EntityCollection.getEpisodes()) {
					String misc = ne.getKey().toLowerCase();
					boolean isPersonMisc = misc.contains("guy")
							|| misc.contains("man") || misc.contains("boy")
							|| misc.contains("girl") || misc.contains("mom")
							|| misc.contains("dad") || misc.contains("texan")
							|| misc.contains("lady");
					if (episode.split(" - ")[3].equals(ne.getKey())
							|| !isPersonMisc)
						deleteNes.add(ne.getKey());
				}
			}
		}
		for (String ne : deleteNes) {
			neOccurrences.remove(ne);
		}

		return neOccurrences;
	}

	/**
	 * filter NE-extractions (Mandatory NE-types)
	 * 
	 * @param neOccurrences
	 * @param question
	 * @param neType
	 * @param concatNes
	 * @return filtered neOccurences
	 */
	private static Map<String, NeOccurrence> filterNeTypes(
			Map<String, NeOccurrence> neOccurrences, List<String> mNes) {
		Set<String> deletes = new HashSet<String>();
		Map<String, NeOccurrence> modify = new HashMap<String, NeOccurrence>();
		// remove binding on the beginning an end of concatenated token
		ArrayList<String> bindings = new ArrayList<String>();
		bindings.add("BINDING");// &,of
		bindings.add("AND"); // and
		bindings.add("APOSTROPHE");// 's,'
		for (Entry<String, NeOccurrence> ne : neOccurrences.entrySet()) {
			boolean wrongNe = true;
			for (String mNe : mNes) {
				if (ne.getValue().getNeTypes().contains(mNe)) {
					wrongNe = false;
					break;
				}

			}
			if (wrongNe)
				deletes.add(ne.getKey());
			else {
				boolean changed = false;
				String key = ne.getKey();
				int size = ne.getValue().getNeTypes().size();
				String lastNEtype = ne.getValue().getNeTypes().get(size - 1);
				if (bindings.contains(lastNEtype)) {
					deletes.add(ne.getKey());
					switch (lastNEtype) {
					case "BINDING":
						if(ne.getKey().endsWith("&"))
							key = key.substring(0, key.length() - 2);
						else // of
							key = key.substring(0, key.length() - 3);
						break;
					case "AND":
							key = key.substring(0, key.length() - 4);
						break;

					default:
						if(ne.getKey().endsWith("'"))
							key = key.substring(0, key.length() - 1);
						else//'s
							key = key.substring(0, key.length() - 2);
					}
					changed = true;
				}
				if (bindings.contains(ne.getValue().getNeTypes().get(0))) {
					deletes.add(ne.getKey());
					int newbegin = key.split(" ")[0].length();
					key = key.substring(newbegin + 1);
					changed = true;
				}
				if (changed)
					modify.put(key, ne.getValue());

			}
		}
		for (String key : deletes) {
			neOccurrences.remove(key);
		}
		// modify old entries
		for (Entry<String, NeOccurrence> ne : modify.entrySet()) {
			if (!neOccurrences.containsKey(ne.getKey()))
				neOccurrences.put(ne.getKey(), ne.getValue());
			else {
				// combine two entries
				neOccurrences.get(ne.getKey()).addOccurrence(ne.getValue());
				if (ne.getValue().getSentence().getScore("default") > neOccurrences
						.get(ne.getKey()).getSentence().getScore("default"))
					neOccurrences.get(ne.getKey()).setSentence(
							ne.getValue().getSentence());
			}
		}
		return neOccurrences;
	}

	/**
	 * filter Organization/Location NEs
	 * 
	 * @param neOccurrences
	 * @param questionNes
	 * @return filtered Organization/Location NEs
	 */
	private static Map<String, NeOccurrence> filterOrganizationNEs(
			Map<String, NeOccurrence> neOccurrences, List<String> questionNes) {

		String mostCommonPerson = "Bart, Milhouse, Homer, Seymour, Moe, Apu, Waylon, Ned, Montgomery,"
				+ " Carl, Lenny, Abe, Grampa, Martin, Nelson, Willie, Otto, Santa, "
				+ "Lisa, Marge, Edna, Helen, Patty, Selma, Maggie, Emily, Timothy Lovejoy, Krusty, Barney, "
				+ "Bart Simpson, Milhouse Van Houten, Homer Simpson, Seymour Skinner, Moe Szyslak, Apu Nahasapeemapetilon, Waylon Smithers, Ned Flanders, Montgomery Burns,"
				+ "Carl Carlson, Lenny Leonard, Abe Simpson, Grampa Simpson, Martin Prince, Nelson Muntz, Barney Gumble"
				+ "Lisa Simpson, Marge Simpson, Edna Krabappel, Helen Lovejoy, Patty Bouvier, Selma Bouvier, Maggie Simpson, Emily, "
				+ "Lovejoy, Simpson, Van Houten, Smithers, Burns, Flanders, Skinner, Bouvier, Szyslak, Muntz, Carlson, Gumble, Leonard, Nahasapeemapetilon, Burns";
		String locations = "House, Home, Apartment, Treehouse, Hell, Heaven";
		List<String> mostCommonPersonList = new ArrayList<String>();
		List<String> locationsList = new ArrayList<String>();
		mostCommonPersonList = Arrays.asList(mostCommonPerson.split(", "));
		locationsList = Arrays.asList(locations.split(", "));
		// remove most-common person Nes
		for (String ne : mostCommonPersonList) {
			neOccurrences.remove(ne);
		}
		// remove most-common person Nes
		for (String ne : locationsList) {
			neOccurrences.remove(ne);
			neOccurrences.remove("The " + ne);
			neOccurrences.remove("the " + ne);
			neOccurrences.remove(ne.toLowerCase());
			neOccurrences.remove("The " + ne.toLowerCase());
			neOccurrences.remove("the " + ne.toLowerCase());
		}
		// remove too short location NEs
		for (String ne : questionNes) {
			neOccurrences.remove(ne);
			neOccurrences.remove("The " + ne);
		}
		return neOccurrences;

	}

	/**
	 * filters family NEs deletes most-common first names
	 * 
	 * @param neOccurrences
	 * @param question
	 * @return filtered neOccurences
	 */
	private static Map<String, NeOccurrence> filterFamilyNEs(
			Map<String, NeOccurrence> neOccurrences, List<String> questionNes) {

		String nonFamily = "Bart, Milhouse, Homer, Seymour, Moe, Apu, Waylon, Ned, Montgomery,"
				+ " Carl, Lenny, Abe, Grampa, Martin, Nelson, Willie, Timothy, Barney"
				+ "Lisa, Marge, Edna, Helen, Patty, Selma, Maggie, Emily, Otto, Santa, Krusty, Kent, Arnie, Manjula";
		List<String> mostCommonNonFamily = new ArrayList<String>();
		mostCommonNonFamily = Arrays.asList(nonFamily.split(", "));
		for (String ne : mostCommonNonFamily) {
			neOccurrences.remove(ne);
		}
		for (String ne : questionNes) {
			neOccurrences.remove(ne);
		}
		return neOccurrences;

	}

	/**
	 * Compares answer Person NamedEntities with question NEs
	 * adds NE to delete list if f.i.: Ned matches Ned Flanders 
	 * adds NE not to delete list if f.i.: Ned doesn't match Nediana
	 * 
	 * @param notNeededNEs
	 *            list of not needed NEs
	 * @param nes
	 *            NE answersList
	 * @return list of NEs, which should be removed from answersList
	 */
	public static Set<String> filterPersonQuestionNEs(List<String> notNeededNEs,
			Set<String> nes) {
		Set<String> deleteNes = new HashSet<String>();
		for (String qNe : notNeededNEs) {
			for (String aNe : nes) {
				// questionNe is equal with answerNe
				if (aNe.equals(qNe)
						|| person1containsperson2(qNe, aNe)
						|| person1containsperson2(aNe, qNe))
					deleteNes.add(aNe);

			}
		}

		return deleteNes;
	}

	/**
	 * Ne2 contains Ne1 -> both Nes have same first name or same last name
	 * 
	 * @param person2
	 * @param ne2
	 * @return true if Ne2 contains Ne1
	 */
	private static boolean person1containsperson2(String person1, String person2) {
		if (!person1.contains(person2))
			return false;
		// Ne1 is equal with first name of question Ne2
		boolean sameFirstName = person2.startsWith(person1.substring(0, 2))
				& person1.charAt(person2.length()) == ' ';
		// Ne1 is equal with last name of question Ne2
		boolean sameLastName = (person2.endsWith(person1.substring(
				person1.length() - 2, person1.length())) & person1
				.charAt(person1.length() - person2.length() - 1) == ' ');
		return sameFirstName | sameLastName;

	}

	/**
	 * Sum up named entity occurrences from several sentences
	 * @param neTypes
	 *            named entity type that is considered
	 * @param contextSents
	 *            list of sentences that contain the considered one and the
	 *            context sentences
	 * @param concatNes
	 *            decides if named entities span more than one word

	 */
	public static Map<String, NeOccurrence> addNamedEntitiesOccurrences(
			List<String> neTypes, List<Sentence> contextSents, boolean concatNes) {

		Map<String, NeOccurrence> neOccurrences = new HashMap<String, NeOccurrence>();

		for (Sentence currentSent : contextSents) {
			Map<String, NeOccurrence> sentOcc = getNamedEntitiesOccurences(
					currentSent, neTypes, concatNes);
			for (String neText : sentOcc.keySet()) {
				if (neOccurrences.containsKey(neText)) {
					neOccurrences.get(neText)
							.addOccurrence(sentOcc.get(neText));
				} else {
					neOccurrences.put(neText,
							new NeOccurrence(sentOcc.get(neText)
									.getOccurrence(), neTypes));

				}
			}
		}
		return neOccurrences;

	}

	/**
	 * computes score of NE and sorts NE list best QuizAnswer is head of the
	 * list
	 * 
	 * @param neOccurrences
	 * @return list of QuizAnswers with NE, score and metadata
	 */
	public static List<QuizAnswer> sortByValue(
			Map<String, NeOccurrence> neOccurrences, final int max) {

		Map<String, Double> scoredNes = new HashMap<String, Double>();
		// computes score for each NE in answer list
		// QuizAnswer.score = 10% relative neOccurrence + 90% score of
		// bestSentence
		for (Entry<String, NeOccurrence> nes : neOccurrences.entrySet()) {
			double oc = (double) nes.getValue().getOccurrence();
			double sc = nes.getValue().getSentence().getScore("default");
			double score = 0.15 * (oc / max) + 0.85 * sc;
			scoredNes.put(nes.getKey(), score);
		}
		List<Entry<String, Double>> sortedEntries = new LinkedList<Entry<String, Double>>(
				scoredNes.entrySet());

		Collections.sort(sortedEntries,
				new Comparator<Map.Entry<String, Double>>() {
					@Override
					public int compare(Entry<String, Double> o1,
							Entry<String, Double> o2) {
						return Double.compare(o1.getValue(), o2.getValue());
					}
				});
		List<QuizAnswer> lst = new ArrayList<QuizAnswer>();
		for (Entry<String, Double> nes : sortedEntries) {
			QuizAnswer qa = new QuizAnswer(nes.getKey(), nes.getValue(),
					neOccurrences.get(nes.getKey()));

			lst.add(0, qa);
		}
		return lst;
	}
}

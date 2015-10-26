package answerProcessing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import answerProcessing.modules.NamedEntityProcessing;
import answerProcessing.modules.answerGen.DateGenerator;
import answerProcessing.modules.answerGen.NumberConverter;
import answerProcessing.types.PossibleAnswer;
import answerProcessing.types.Question;
import answerProcessing.types.QuestionInformation.Questiontype;
import answerProcessing.types.QuizAnswer;
import answerProcessing.types.Sentence;
import answerProcessing.EntityCollection;

public class QuizPipeline {


	private static NumberConverter numberConverter = new NumberConverter();
	private static DateGenerator dateGenerator = new DateGenerator();

	
	/**
	 * Executes the QA-Pipeline without generating more quiz answers and returns correct answer followed by less likely ones
	 * 
	 * @param q
	 *            the question to ask
	 * @param possibleAnswers
	 *            the possible Answers to select from
	 * @param serverUrl
	 *            the URL of the Remote-NLP-Server
	 * @param maxNumOfAnswers
	 *            maximum number of returned answers
	 * @throws IOException
	 *             if connection to Remote-NLP-Server fails
	 */
	public static List<QuizAnswer> executeQAPipeline(Question q,
			List<PossibleAnswer> possibleAnswers, String serverUrl) throws IOException {
		// execute watson pipeline for sentence answers
		List<Sentence> allAnswers = Pipeline .executePipeline(q,
						possibleAnswers, serverUrl);
		// extract named entities etc. if possible
		List<QuizAnswer>  answers = extractShorterAnswers(q, allAnswers);
		if (answers.isEmpty()){
			// generate default sentence long answers if no shorter ones were found
			answers= new ArrayList<QuizAnswer>();
			QuizAnswer answer;
			for (Sentence sent: allAnswers){
				answer= new QuizAnswer(sent);
				answers.add(answer);
			}
		}
		return answers;
	}
	
	/**
	 * Executes the Pipeline and returns possible answers
	 * 
	 * @param q
	 *            the question to ask
	 * @param possibleAnswers
	 *            the possible Answers to select from
	 * @param serverUrl
	 *            the URL of the Remote-NLP-Server
	 * @param numberOfRequestedAnswers
	 *            maximum number of returned answers
	 * @throws IOException
	 *             if connection to Remote-NLP-Server fails
	 */
	public static List<QuizAnswer> executePipeline(Question q,
			List<PossibleAnswer> possibleAnswers, String serverUrl,
			int numberOfRequestedAnswers) throws IOException {
		// execute watson pipeline for sentence answers
		List<Sentence> allAnswers = Pipeline .executePipeline(q,
				possibleAnswers, serverUrl);
		// extract named entities etc. if possible
		List<QuizAnswer>  answers = extractShorterAnswers(q, allAnswers);
		// generate more alternative answers if necessary
		answers = generateMoreAnswers(q, numberOfRequestedAnswers,
					allAnswers, answers, q.getQuestionInformation().getNEtype());

		int numOfFoundAnswers= answers.size();
		if (numOfFoundAnswers >= numberOfRequestedAnswers)
			answers = answers.subList(0, Math.min(numOfFoundAnswers, numberOfRequestedAnswers));
		
		return answers;
	}

	/**
	 * Extract answers that are only a few words long from given sentence long answers to a given question
	 * @param q
	 * @param allAnswers
	 * @return
	 */
	private static List<QuizAnswer> extractShorterAnswers(Question q, List<Sentence> allAnswers) {
		List<QuizAnswer> answers= new ArrayList<QuizAnswer>();
		if (q.getQuestionType() == Questiontype.episode
				|| q.getQuestionType() == Questiontype.season) {
			// extract answers for season or episode questions
			answers  = generateEpisodesOrSeasons(q, allAnswers , answers);
		} else {
			// extract possible one or more word answers
			answers = NamedEntityProcessing.extractMostCommonNamedEntitiesMultipleSetences(q,
							allAnswers, 0, true);
			answers = maybeConvertToNumbers(q.getQuestionType(), answers);
		}

		answers = filterAnswers(q.getQuestionType(), answers);
		return answers;
	}

	/**
	 * Filter out equal answers
	 */
	private static List<QuizAnswer> filterAnswers(Questiontype qType,
			List<QuizAnswer> answers) {
		QuizAnswer currentAnswer;
		List<QuizAnswer> answersRest;
		for (int i = answers.size() - 1; i > 0; i--) {
			currentAnswer = answers.get(i);
			answersRest = answers.subList(0, i);
				if (contains(answersRest, currentAnswer.getAnswer()))
					answers.remove(i);
		}
		return answers;
	}
	
	/**
	 * Checks if any of the given quiz answers contains the given answer
	 */
	private static boolean contains(List<QuizAnswer> answers, String queryStr){
		String answerStr;
		for(QuizAnswer answer: answers){
			answerStr= answer.getAnswer();
			if(answerStr.equalsIgnoreCase(queryStr))
				return true;
		}
		return false;
	}

	/**
	 * Generate season-episode information for given possible answers if
	 * available
	 * 
	 * @param q
	 *            question
	 * @param allAnswers
	 *            possible answers
	 * @param answers
	 *            list of result answers
	 * @return season episode information as answers
	 */
	private static List<QuizAnswer> generateEpisodesOrSeasons(Question q,
			List<Sentence> allAnswers, List<QuizAnswer> answers) {
		Set<String> episodesOrSeasons = new HashSet<String>();
		String bestAnswer = null;
		for (Sentence answer : allAnswers) {
			String title = answer.getParentText().getTitle();
			if (title.contains("Episode") && title.contains("Season")) {
				title = title.split(":")[0].trim();
				if (q.getQuestionType() == Questiontype.season)
					title = title.split("-")[1].trim();
				if (!episodesOrSeasons.contains(title)) { // make sure to add
															// correct answer as
															// first element to
															// the result list
					episodesOrSeasons.add(title);
					answers.add(new QuizAnswer(title, answer));
				} else {
					if (!title.equals(bestAnswer))
						episodesOrSeasons.add(title);
				}

			}
		}
		return answers;
	}

	/**
	 * Check if given list contains written numbers, convert to textual digit
	 * numbers if possible
	 * 
	 * @param qType
	 *            determines if list should contain numbers
	 * @param answers
	 *            list that could be numbers
	 * @return list of textual digit numbers and entities that could not be
	 *         converted
	 */
	private static List<QuizAnswer> maybeConvertToNumbers(Questiontype qType,
			List<QuizAnswer> answers) {
		boolean isNumberType = qType.equals(Questiontype.number)
				|| qType.equals(Questiontype.ordinal)
				|| qType.equals(Questiontype.duration);
		boolean isPercentage = qType.equals(Questiontype.percentage);
		boolean isMoney = qType.equals(Questiontype.money);
		if (isNumberType || isPercentage || isMoney) {
			String ne;
			for (QuizAnswer quizAnswer : answers) {
				ne = quizAnswer.getAnswer();
				ne = ne.replaceAll("\\s*(\\$|(dollars)|(Dollars)|%)\\s*", "");
				Integer correctNumber = numberConverter.convertWordToNumber(ne);
				if (correctNumber != null) {
					String correctNumberStr = String.valueOf(correctNumber);
					if (isPercentage)
						correctNumberStr = correctNumberStr + "%";
					else if (isMoney)
						correctNumberStr = "$" + correctNumberStr;
					quizAnswer.setAnswer(correctNumberStr);
				}
			}
		}

		return answers;

	}

	/**
	 * Expand given answer list with new answers
	 * 
	 * @param q
	 *            question type of generated answers
	 * @param numberOfRequestedAnswers
	 *            final number of answers after generating more
	 * @param bestAnswers
	 *            original sentence long answers
	 * @param answers
	 *            alternative answers that are expanded
	 * @return altAnswers
	 */
	private static List<QuizAnswer> generateMoreAnswers(Question q,
			int numberOfRequestedAnswers, List<Sentence> bestAnswers,
			List<QuizAnswer> answers, List<String> neTypes) {
		if (answers.size() < numberOfRequestedAnswers) {
			int numOfNeededAnswers = numberOfRequestedAnswers
					- answers.size();
			// index is used to extract default sentence long answer
			int bestAnswersIndex= 0;
			while(numOfNeededAnswers> 0){
				Sentence bestSentence = new Sentence();
				bestSentence.setRawText("D'Oh!"); 
				if (bestAnswersIndex < bestAnswers.size())
					bestSentence = bestAnswers.get(bestAnswersIndex);
				if(q.getQuestionType()==Questiontype.unknown&&!bestSentence.getRawText().equals("D'Oh!")){
					answers.add(new QuizAnswer(bestSentence));
				}
				else{
					List<String> generatedNeTypes = new ArrayList<>();
					generatedNeTypes.addAll(neTypes);
				answers.add(new QuizAnswer(generateAltAnswer(q, bestSentence,
						answers, numOfNeededAnswers), 0.0, generatedNeTypes));}
				bestAnswersIndex++;
				numOfNeededAnswers--;
			}
		}
		return answers;
	}


	/**
	 * Generate an alternative answer of a given type
	 * 
	 * @param q
	 *            question that this answer is generated for
	 * @param answer
	 *            original sentence long answer
	 * @param answers
	 *            already found answers
	 * @param numNeeded
	 *            number of still needed answers
	 * @return generated answer or original sentence answer if none could be
	 *         generated
	 */
	private static String generateAltAnswer(Question q, Sentence answer,
			List<QuizAnswer> answers, int numNeeded) {
		String correctAnswer = null;
		if (!answers.isEmpty())
			correctAnswer = answers.get(0).getAnswer();
		Questiontype questionType = q.getQuestionType();
		switch (questionType) {
		case femalePerson: {
			return generateFromKnownEntities(answers, EntityCollection.getFemaleCharacters(), true);
		}
		case malePerson: {
			return generateFromKnownEntities(answers, EntityCollection.getMaleCharacters(), true);
		}
		case animalNE: {
			return generateFromKnownEntities(answers, EntityCollection.getAnimalCharacters(), true);
		}
		case family: {
			return generateFromKnownEntities(answers, EntityCollection.getFamilies(), false);
		}
		case person: {
			String character = (answers.size() % 2 == 0) ? generateFromKnownEntities(
					answers, EntityCollection.getFemaleCharacters(), true)
					: generateFromKnownEntities(answers, EntityCollection.getMaleCharacters(), true);
			return character;
		}
		case location:
			return generateFromKnownEntities(answers, EntityCollection.getLocations(), false);
		case organization:
		case location_organization:
			String location = (answers.size() % 2 == 0) ? generateFromKnownEntities(
					answers, EntityCollection.getLocations(), false) : generateFromKnownEntities(
					answers, EntityCollection.getOrganizations(), false);
			return location;
		case animal:
			return generateFromKnownEntities(answers, EntityCollection.getAnimals(), false);
		case number:
			return generateNumber(answers, numNeeded + answers.size() + 1,
					correctAnswer);
		case duration:
			return generateNumber(answers, numNeeded + answers.size() + 1,
					correctAnswer).concat(" years old");
		case ordinal:
			return generateNumber(answers, numNeeded + answers.size() + 1,
					correctAnswer);
		case percentage: {
			int percentage= getRandomNumberUpToLimit(answers, 101);
			return percentage + "%";
		}
		case date:
			return generateDate(answers, correctAnswer);
		case color:
			return generateFromKnownEntities(answers, EntityCollection.getColors(), false);
		case season: {
			int seasonNum= getRandomNumberUpToLimit(answers, 26);
			return "Season " + (1+seasonNum);
		}
		case episode:
			return generateFromKnownEntities(answers, EntityCollection.getEpisodes(), false);
		case money:
			return generateMoney(answers, numNeeded + answers.size() + 1,
					correctAnswer);
		case distance: 
			return generateNumber(answers, numNeeded + answers.size() + 1,
					correctAnswer).concat(" meter");
		case weight: 
			return generateNumber(answers, numNeeded + answers.size() + 1,
					correctAnswer).concat(" pound");
		default:
			return answer.getRawText();
		}
	}

	/**
	 * Generate a random number between 0 and a limit
	 * 
	 * @param answers
	 *            list of exceptions
	 * @param limit
	 * 			  the limit
	 * @return new number if possible 
	 * 		   (i.e. the limit is high enough to accommodate a new one 
	 * 		   in addition to given answers)            
	 */
	private static int getRandomNumberUpToLimit(List<QuizAnswer> answers, int limit) {
		Random rand = new Random();
		int number= rand.nextInt(limit);
		if(answers.size()<limit){
			do{
				number= rand.nextInt(limit);
			}while(contains(answers, String.valueOf("Season " +number))
					|| contains(answers, String.valueOf(number+ "%")));
		}
		return number;
	}

	/**
	 * Generate date from given correct one
	 * 
	 * @param correctDate
	 *            the correct answer (or null)
	 * @param answers
	 *            list of exceptions
	 * @return date
	 */
	private static String generateDate(List<QuizAnswer> answers,
			String correctDate) {
		String date;
		do {
			date = dateGenerator.generateDate(correctDate);
		} while (contains(answers, date)); 
		return date;
	}

	/**
	 * Generate number somewhere around the given correct one
	 * 
	 * @param correctNum
	 *            the correct answer (or null)
	 * @param answers
	 *            list of exceptions
	 * @param minNum
	 *            range around the correct number
	 * @return number
	 */
	private static String generateNumber(List<QuizAnswer> answers, int minNum,
			String correctNum) {
		Random rand = new Random();
		// find seed number in answers
		Iterator<QuizAnswer> notThoseIt = answers.iterator();
		Integer seedNumber = numberConverter.convertWordToNumber(correctNum);
		while (notThoseIt.hasNext() && seedNumber == null) {
			seedNumber = numberConverter.convertWordToNumber(notThoseIt.next()
					.getAnswer());
		}
		// generate new number
		String numberStr;
		if (seedNumber != null) {
			numberStr = getRandomNumber(answers, minNum, rand, seedNumber);
			return numberStr;
		} else {
			do {
				numberStr = String.valueOf(rand.nextInt(100));
			} while (contains(answers, numberStr));
			return numberStr;
		}
	}

	/**
	 * Generate a random number around a seed number that is not one of the
	 * given numbers
	 * 
	 * @param answers
	 *            list of exceptions
	 * @param minNum
	 *            range around the seed number
	 * @param rand
	 *            random number generator
	 * @param seedNumber
	 *            the seed number
	 * @return number
	 */
	private static String getRandomNumber(List<QuizAnswer> answers, int minNum,
			Random rand, Integer seedNumber) {
		int number;
		String numberStr = null;
		do {
			int factor = rand.nextInt(minNum);
			number = (rand.nextBoolean()) ? seedNumber + factor : seedNumber
					- factor;
			numberStr = String.valueOf(number);
		} while (contains(answers, numberStr)
				|| number < 0
				|| contains(answers, "$" + numberStr));
		return numberStr;
	}

	/**
	 * Generate amount of money somewhere around the given correct one
	 * 
	 * @param correctAnswer
	 *            the correct answer (or null)
	 * @param altAnswers
	 *            list of exceptions
	 * @return money ;)
	 */
	private static String generateMoney(List<QuizAnswer> altAnswers,
			int minNum, String correctAnswer) {
		Random rand = new Random();
		String moneyStr;
		if (correctAnswer != null) {
			Pattern moneyPattern = Pattern
					.compile("(.*(\\$\\s*(.*)))|((.*)\\s*(dollars).*)");
			Matcher matcher = moneyPattern.matcher(correctAnswer);
			if (matcher.matches()) {
				String valStr = matcher.group(3);
				if (valStr == null)
					valStr = matcher.group(5);
				Integer val = numberConverter.convertWordToNumber(valStr);
				if (val != null) {
					String newVal = getRandomNumber(altAnswers, minNum, rand,
							val);
					if (newVal != null)
						return "$" + newVal;
				}
			}
		}
		do {
			moneyStr = "$" + rand.nextInt(1000);
		} while (contains(altAnswers, moneyStr));
		return moneyStr;
	}

	/**
	 * Select random entity from given entities that is not an exception
	 * 
	 * @param answers
	 *            list of exceptions
	 * @param entities
	 *            list of entities
	 * @param checkParts
	 *            determines if the parts of the exceptions also count as
	 *            exceptions
	 * @return entity name
	 */
	private static String generateFromKnownEntities(List<QuizAnswer> answers,
			List<String> entities, boolean checkParts) {
		Random rand = new Random();
		int len = entities.size();
		int index = rand.nextInt(len);
		String entity = entities.get(index);
		while (contains(answers, entity)){
			index = rand.nextInt(len);
			entity = entities.get(index);
		}
		return entity;
	}


}

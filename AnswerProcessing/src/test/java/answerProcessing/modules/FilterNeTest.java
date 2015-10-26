package answerProcessing.modules;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class FilterNeTest {

	@Test
	public void test() {
		List<String> questionNes = new ArrayList<String>();
		Set<String> nes = new HashSet<String>();
		Set<String> deletes;		
		questionNes.add("Bart");
		questionNes.add("Carla");
		questionNes.add("Ned Flanders");
		nes.add("Carl");
		nes.add("Nediana Flanders");
		nes.add("Bart Simpson");
		nes.add("Nediana");
		nes.add("Ned Flanders");
		nes.add("Nediana");
		nes.add("Ned");
		nes.add("Flanders");
		deletes =NamedEntityProcessing.filterPersonQuestionNEs(questionNes, nes);
		assertTrue(deletes.contains("Flanders"));
		assertTrue(deletes.contains("Bart Simpson"));
		assertTrue(deletes.contains("Ned"));
		assertTrue(deletes.contains("Ned Flanders"));
		assertEquals(4,deletes.size());
	}
	
	@Test
	public void test2() {
		List<String> questionNes = new ArrayList<String>();
		Set<String> nes = new HashSet<String>();
		Set<String> deletes;
		questionNes.add("Bart Simpson");
		nes.add("Bart");
		nes.add("Carla");
		questionNes.add("Carl");
		questionNes.add("Nediana Flanders");
		nes.add("Ned Flanders");
		questionNes.add("Nediana");
		questionNes.add("Ned Flanders");
		questionNes.add("Nediana");
		questionNes.add("Ned");
		questionNes.add("Flanders");
		deletes =NamedEntityProcessing.filterPersonQuestionNEs(questionNes, nes);
		assertTrue(deletes.contains("Ned Flanders"));
		assertTrue(deletes.contains("Bart"));
		assertEquals(2,deletes.size());
	}
	@Test
	public void test3() {
		List<String> questionNes = new ArrayList<String>();
		Set<String> nes = new HashSet<String>();
		Set<String> deletes;		
		questionNes.add("Flanders");
		nes.add("Ned Flanders");
		nes.add("Flanders");
		deletes =NamedEntityProcessing.filterPersonQuestionNEs(questionNes, nes);
		assertTrue(deletes.contains("Flanders"));
		assertTrue(deletes.contains("Ned Flanders"));
	}
}

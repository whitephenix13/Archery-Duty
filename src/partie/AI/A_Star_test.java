package partie.AI;

import static org.junit.Assert.*;

import org.junit.Test;

public class A_Star_test {

	@Test
	public void test() {
		A_Star astar= new A_Star();
		assertTrue(astar.TEST_RemovePathPoint_neighborTest());
		assertTrue(astar.TEST_RemovePathPoint_removeInChain());
		assertTrue(astar.TEST_RemovePathPoint_removeTConfig());
		assertTrue(astar.TEST_RemovePathPoint_removeSquareConfig());
	}

}

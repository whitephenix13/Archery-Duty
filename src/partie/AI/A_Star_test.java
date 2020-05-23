package partie.AI;

import static org.junit.Assert.*;

import org.junit.Test;

public class A_Star_test {

	@Test
	public void test() {
		A_Star astar= new A_Star();
		String error1 = astar.TEST_RemovePathPoint_neighborTest();
		assertTrue(error1,error1.equals(""));
		assertTrue(astar.TEST_RemovePathPoint_removeInChain());
		assertTrue(astar.TEST_RemovePathPoint_removeTConfig());
		assertTrue(astar.TEST_RemovePathPoint_removeSquareConfig());
	}

}

package partie.collision;

import static org.junit.Assert.*;

import org.junit.Test;

import partie.AI.A_Star;

public class Hitbox_test {
	@Test
	public void test() {
		assertTrue(Hitbox.TEST_getSlidedHitbox());
	}
}

package me.jezza.jc.creators;

import java.io.IOException;

import me.jezza.jc.interfaces.Command;

/**
 * @author Jezza
 */
public class TestCreator {

	@Command("test-creator")
	public void create(String[] args) {
	}

	@Command("internal thingy")
	public void lol() {
		class Test {
			public void create() throws IOException {
			}
		}
	}
}

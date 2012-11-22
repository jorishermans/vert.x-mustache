package com.rtforce.template;

import org.junit.Test;
import org.vertx.java.framework.TestBase;

public class MustacheTest extends TestBase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		startApp(MustacheTestClient.class.getName());
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void testMustacheParsing() throws Exception {
		startTest(getMethodName());
	}
}

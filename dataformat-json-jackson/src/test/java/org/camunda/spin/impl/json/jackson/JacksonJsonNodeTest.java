package org.camunda.spin.impl.json.jackson;

import org.camunda.spin.Spin;
import org.camunda.spin.json.SpinJsonNode;

import junit.framework.TestCase;

public class JacksonJsonNodeTest extends TestCase {
	public void testName() {
		SpinJsonNode node = Spin.JSON("[\"foo\", \"bar\", \"\\\"foo\\\"\"]");
		System.out.println(node.toString());
		SpinJsonNode node1 = node.elements().get(1);
		System.out.println(node.equals("foo"));
		System.out.println(node.hasProp("foo"));
		//System.out.println(node.lastIndexOf("baz"));
	}
}
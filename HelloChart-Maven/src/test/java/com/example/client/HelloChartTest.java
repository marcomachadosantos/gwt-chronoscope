package com.example.client;

import com.google.gwt.junit.client.GWTTestCase;

/**
 * GWT JUnit tests must extend GWTTestCase.
 */
public class HelloChartTest extends GWTTestCase {

  /**
   * Must refer to a valid module that sources this class.
   */
  public String getModuleName() {
    return "com.example.HelloChartJUnit";
  }

  public void testSomething() {
    assertTrue(true);
  }

}
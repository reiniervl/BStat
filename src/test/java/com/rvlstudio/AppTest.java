package com.rvlstudio;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.*;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName ) {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {
        EventDAO em = new SqlEventDAO();
        em.addEvent(new Event<Integer>(740, "Drinken", Calendar.getInstance(), Unit.MILILITER));
        em.addEvent(new Event<Integer>(840, "Drinken", Calendar.getInstance(), Unit.MILILITER));
        em.addEvent(new Event<Double>(1.4, "Slurpen", Calendar.getInstance(), Unit.LITER));

        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        start.add(Calendar.MONTH, -1);
        end.add(Calendar.MONTH, 1);
        System.out.format("Start: %s\nEnd: %s\n", start.getTime().toString(), end.getTime().toString());

        for(Event<?> e : em.getEvents(start, end)) {
            System.out.println(e);
        }
        assertTrue( true );
    }
}
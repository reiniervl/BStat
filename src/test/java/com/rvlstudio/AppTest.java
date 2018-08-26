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
        /* 
        EventDAO em = SqlEventDAO.getDAO();
        em.addEvent(new Event<Integer>(100, "Melk", Calendar.getInstance(), Unit.MILILITER));
        em.addEvent(new Event<Integer>(150, "Melk", Calendar.getInstance(), Unit.MILILITER));
        em.addEvent(new Event<Double>(0.04, "Water", Calendar.getInstance(), Unit.LITER));

        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        start.add(Calendar.MONTH, -1);
        end.add(Calendar.MONTH, 1);

        for(Event<?> e : em.getEvents(start, end)) {
            System.out.println(e);
        }
         */

         EventDAO dao = new RestEventDAO(ResourceBundle.getBundle("com.rvlstudio.test"));
        
         Calendar start = Calendar.getInstance();
         Calendar end = Calendar.getInstance();
         start.add(Calendar.MONTH, -1);
         end.add(Calendar.MONTH, 1);
        
        
         for(Event<?> e : dao.getEvents(start, end)) {
             System.out.println(e);
         }
        
        assertTrue( true );
    }
}

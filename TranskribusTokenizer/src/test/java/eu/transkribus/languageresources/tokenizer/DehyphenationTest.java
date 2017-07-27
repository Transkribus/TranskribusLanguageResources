/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.tokenizer;

import eu.transkribus.tokenizer.dehyphenator.DehyphenatorComplex;
import eu.transkribus.tokenizer.dehyphenator.DehyphenatorSimple;
import java.util.LinkedList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author max
 */
public class DehyphenationTest
{

    public DehyphenationTest()
    {
    }

    @BeforeClass
    public static void setUpClass()
    {
    }

    @AfterClass
    public static void tearDownClass()
    {
    }

    @Before
    public void setUp()
    {
    }

    @After
    public void tearDown()
    {
    }

    @Test
    public void testDehyphenation()
    {
        assertEquals("test", new DehyphenatorSimple("¬", "", true, true, true).process("te¬\nst"));
        assertEquals("test", new DehyphenatorSimple("¬:=", ":=", true, true, true).process("te:\n:st"));
        assertEquals("te:\n:st", new DehyphenatorSimple("¬=", "=", true, true, true).process("te:\n:st"));
        assertEquals("te:st", new DehyphenatorSimple("¬=:", "", true, true, false).process("te:\n:st"));
        assertEquals("te:\n:st", new DehyphenatorSimple("¬=:", "", true, true, true).process("te:\n:st"));
    }
    
    @Test
    public void testComplexDehyphenation()
    {
        List<DehyphenatorSimple> dehyphenators = new LinkedList<>();
        dehyphenators.add(new DehyphenatorSimple("=", "=", false, false, true));
        dehyphenators.add(new DehyphenatorSimple(":", ":", false, false, true));
        dehyphenators.add(new DehyphenatorSimple("¬", "", false, false, true));
        
        DehyphenatorComplex dehyphenatorComplex = new DehyphenatorComplex(dehyphenators);
        
        assertEquals("test", dehyphenatorComplex.process("te¬\nst"));
        assertEquals("test", dehyphenatorComplex.process("te:\n:st"));
        assertEquals("te:\n=st", dehyphenatorComplex.process("te:\n=st"));
        assertEquals("te=\n:st", dehyphenatorComplex.process("te=\n:st"));
    }
}

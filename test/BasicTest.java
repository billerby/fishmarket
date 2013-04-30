import org.junit.*;
import java.util.*;
import play.test.*;
import models.*;

public class BasicTest extends UnitTest {

    @Test
    public void aVeryImportantThingToTest() {
        assertEquals(2, 1 + 1);
    }
    
    @Test 
    public void createAndRetrieveFish(){
    	new Fish("VITLING 2").save();
    	
    	Fish vitling2 = Fish.find("byName", "VITLING 2").first();
    	
    	// test
    	assertNotNull(vitling2);
    	assertEquals("VITLING 2", vitling2.name);
    }

}

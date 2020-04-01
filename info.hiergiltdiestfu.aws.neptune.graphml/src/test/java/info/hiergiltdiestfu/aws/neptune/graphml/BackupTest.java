package info.hiergiltdiestfu.aws.neptune.graphml;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.amazonaws.services.s3.model.S3ObjectSummary;

import info.hiergiltdiestfu.aws.neptune.graphml.Aws.AWSBackupEditor;
/**
 * 
 * @author LUNOACK
 * Test the BackUp if graphml\AWS\AWSBackupEditor.java works and deletes the correct files.
 */

@SpringBootTest
public class BackupTest {

	@Autowired
	private AWSBackupEditor backup;
	
	private static S3ObjectSummary obj;
	
	/**
	 * This are the Dates which are Parameters for the BackupService
	 */
	private Calendar cal;
	private Calendar cal7days;
	private Calendar cal6weeks;
	
	/**
	 * Create a S3Object to work with it and to simulate
	 */
	@BeforeAll
	static void setup() {
		obj = new S3ObjectSummary();
	}
	
	/**
	 * Create an example calendar before each test.  
	 */
	@BeforeEach
	void setupCalender(){
		cal = getRandomCalendar();
		cal7days = backup.getLast7Days(cal);
		cal6weeks = backup.getLast6Weeks(cal);
	}
	
	/**
	 * Creates a Random Calendar-Date.
	 * @return
	 */
	Calendar getRandomCalendar() {
		Calendar cal = Calendar.getInstance();
		int year = randBetween(1990, Calendar.getInstance().get(Calendar.YEAR));
		cal.set(Calendar.YEAR, year);
		int dayofyear = randBetween(1, cal.getActualMaximum(Calendar.DAY_OF_YEAR));
		cal.set(Calendar.DAY_OF_YEAR, dayofyear);
		
		return cal;
	}
	/**
	 * Creates a random year.
	 * @param start
	 * @param end
	 * @return
	 */
	int randBetween(int start, int end) {
        return start + (int)Math.round(Math.random() * (end - start));
    }
	/**
	 * Tests if the Backup from this day will be stored.
	 */
	@Test
	void thisDaysTest() {
		cal.add(Calendar.DATE, - 0);
		Date lastmodiefied = cal.getTime();
		obj.setLastModified(lastmodiefied);
		assertFalse(backup.shoulddeleteBackup(obj,cal7days,cal6weeks));
	}
	
	/**
	 * Tests if a Backup before 8 days will be stored.
	 * If its a Sunday it will else not.
	 */
	@Test
	void before8daysDaysTest() {
		cal.add(Calendar.DATE, -8);
		Date lastmodiefied = cal.getTime();
		obj.setLastModified(lastmodiefied);
		if(cal.get( Calendar.DAY_OF_WEEK ) != Calendar.SUNDAY)
			assertTrue(backup.shoulddeleteBackup(obj,cal7days,cal6weeks));
		else
			assertFalse(backup.shoulddeleteBackup(obj,cal7days,cal6weeks));
	}
	
	/**
	 * Tests if the Backup from the Last 7 days will be stored-
	 */
	@Test
	void iterateLast7Days() {
		int zaehler = 7;
		while (zaehler != 0) {
			cal.add( Calendar.DAY_OF_WEEK, -1 );
			Date lastmodiefied = cal.getTime();
			obj.setLastModified(lastmodiefied);
			assertFalse(backup.shoulddeleteBackup(obj,cal7days,cal6weeks));
			zaehler--;
		}	
		cal.add( Calendar.DAY_OF_WEEK, -1 );
		Date lastmodiefied = cal.getTime();
		obj.setLastModified(lastmodiefied);
		if(cal.get( Calendar.DAY_OF_WEEK ) != Calendar.SUNDAY)
			assertTrue(backup.shoulddeleteBackup(obj,cal7days,cal6weeks));
		else
			assertFalse(backup.shoulddeleteBackup(obj,cal7days,cal6weeks));
	}
	
	/**
	 * Tests if the Backup from the Last 6 weeks will be stored.
	 * Without checking the Last 7 days.
	 * Only Sunday Backups should be stored others will be deleted.
	 * So 35 Days are checked.
	 */
	@Test
	void iterateLast42Days() {
		cal.add( Calendar.DAY_OF_WEEK, -7 );
		int zaehler = 35;
		while (zaehler != 0) {
			cal.add( Calendar.DAY_OF_WEEK, -1 );
			Date lastmodiefied = cal.getTime();
			obj.setLastModified(lastmodiefied);
			if(cal.get( Calendar.DAY_OF_WEEK ) != Calendar.SUNDAY)
				assertTrue(backup.shoulddeleteBackup(obj,cal7days,cal6weeks));
			else
				assertFalse(backup.shoulddeleteBackup(obj,cal7days,cal6weeks));
			zaehler--;
		}
	}
	
	/**
	 * Negative Test. 
	 * Looks if Backups before the 6 weeks will be stored.
	 * Every Backup should be deleted.
	 * Checks only 35 days before the 6 weeks because normally they should already be deleted.
	 */
	void iterateAfter42Days() {
		cal.add( Calendar.DAY_OF_WEEK, -42 );
		int zaehler = 35;
		while (zaehler != 0) {
			cal.add( Calendar.DAY_OF_WEEK, -1 );
			Date lastmodiefied = cal.getTime();
			obj.setLastModified(lastmodiefied);
			assertTrue(backup.shoulddeleteBackup(obj,cal7days,cal6weeks));
			zaehler--;
		}
	}
}

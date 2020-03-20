package info.hiergiltdiestfu.aws.neptune.graphml;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.amazonaws.services.s3.model.S3ObjectSummary;

import info.hiergiltdiestfu.aws.neptune.graphml.AWS.AWSBackupEditor;
/**
 * 
 * @author LUNOACK
 * Test the BackUp if graphml\AWS\AWSBackupEditor.java works and deletes the correct files.
 */
public class BackupTest {

	@BeforeAll
	static void setup() {
		backup = new AWSBackupEditor();
		obj = new S3ObjectSummary();
	}
	
	@BeforeEach
	void setupCalender(){
		cal = Calendar.getInstance();
	}
	
	private static AWSBackupEditor backup;
	private static S3ObjectSummary obj;
	private Calendar cal;
	
	@Test
	void thisDaysTest() {
		cal.add(Calendar.DATE, - 0);
		Date lastmodiefied = cal.getTime();
		obj.setLastModified(lastmodiefied);
		assertFalse(backup.keepBackup(obj));
	}
	
	@Test
	void before8daysDaysTest() {
		cal.add(Calendar.DATE, -8);
		Date lastmodiefied = cal.getTime();
		obj.setLastModified(lastmodiefied);
		if(cal.get( Calendar.DAY_OF_WEEK ) != Calendar.SUNDAY)
			assertTrue(backup.keepBackup(obj));
		else
			assertFalse(backup.keepBackup(obj));
	}
	
	@Test
	void iterateLast7Days() {
		int zaehler = 7;
		while (zaehler != 0) {
			cal.add( Calendar.DAY_OF_WEEK, -1 );
			Date lastmodiefied = cal.getTime();
			obj.setLastModified(lastmodiefied);
			assertFalse(backup.keepBackup(obj));
			zaehler--;
		}	
		cal.add( Calendar.DAY_OF_WEEK, -1 );
		Date lastmodiefied = cal.getTime();
		obj.setLastModified(lastmodiefied);
		if(cal.get( Calendar.DAY_OF_WEEK ) != Calendar.SUNDAY)
			assertTrue(backup.keepBackup(obj));
		else
			assertFalse(backup.keepBackup(obj));
	}
	
	@Test
	void iterateLast42Days() {
		cal.add( Calendar.DAY_OF_WEEK, -7 );
		int zaehler = 35;
		while (zaehler != 0) {
			cal.add( Calendar.DAY_OF_WEEK, -1 );
			Date lastmodiefied = cal.getTime();
			obj.setLastModified(lastmodiefied);
			if(cal.get( Calendar.DAY_OF_WEEK ) != Calendar.SUNDAY)
				assertTrue(backup.keepBackup(obj));
			else
				assertFalse(backup.keepBackup(obj));
			zaehler--;
		}
	}
	
	void iterateAfter42Days() {
		cal.add( Calendar.DAY_OF_WEEK, -42 );
		int zaehler = 35;
		while (zaehler != 0) {
			cal.add( Calendar.DAY_OF_WEEK, -1 );
			Date lastmodiefied = cal.getTime();
			obj.setLastModified(lastmodiefied);
			assertTrue(backup.keepBackup(obj));
			zaehler--;
		}
	}
}

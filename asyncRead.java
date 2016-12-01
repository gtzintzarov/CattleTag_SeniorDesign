/*
 * Sample program that shows how to create a
 * simpleReadPlan that uses a list of antennas as passed by the user
 * and prints the tags found.
 */
//package samples;
/*
import com.thingmagic.Reader;
import com.thingmagic.SimpleReadPlan;
import com.thingmagic.TMConstants;
import com.thingmagic.TagProtocol;
import com.thingmagic.TagReadData;
import com.thingmagic.TransportListener;
*/
import com.thingmagic.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 *
 * @author qvantel
 */





public class asyncRead
{

	
	static PrintWriter pw;
	static StringBuilder sb;
        static File f;
        static String timeStamp;
        static String timeStamp2;
        static SimpleDateFormat format;
	
	public static void main(String argv[]) throws FileNotFoundException
    	{
                int counter = 1;
                Date d1, d2;
                long diffMinutes; 
    	        format = new SimpleDateFormat("dd.MM.yyyy" + "HH.mm.ss"); 
		
		try
		{
			// INITIATE VARIABLES
			System.out.printf("INITIATING VARIABLES...\n");
			Reader r = null;
			int[] antennaList = null;
			TagReadData[] tagReads;
			TagReadData[] tagReads2;
			int nextarg = 1;
			boolean trace = false;
			//System.out.println("Jesse WOO Is here...");		
			Reader.GpioPin[] state;
			antennaList = parseAntennaList(argv, nextarg);
			String readerURI = "tmr:///dev/ttyACM0";
			r = Reader.create(readerURI);
			boolean keepGoing = false;
			// END INITIATE VARIABLES

			// MAKE CONNECTION -- SETUP PARAMS -- SELECT REGION OF OPERATION
			System.out.printf("\n\nConnecting...\n");
			r.connect();
			
			if (Reader.Region.UNSPEC == (Reader.Region) r.paramGet(TMConstants.TMR_PARAM_REGION_ID))
		    	{
		        	Reader.Region[] supportedRegions = (Reader.Region[]) r.paramGet(TMConstants.TMR_PARAM_REGION_SUPPORTEDREGIONS);
		        	if (supportedRegions.length < 1) {
		            		throw new Exception("Reader doesn't support any regions");
		        	} else {
		            		r.paramSet(TMConstants.TMR_PARAM_REGION_ID, supportedRegions[0]);
		        	}
		    	}
		    // END MAKE CONNECTION -- SETUP PARAMS -- SELECT REGION OF OPERATION

			// SELECT ANTENNA -- ADJUST OUTPUT POWER
		    System.out.printf("\n\nSelecting Antenna & Setting Output Power\n");
			String arg = "2";
			antennaList[0] = Integer.parseInt(arg);
			SimpleReadPlan plan = new SimpleReadPlan(antennaList, TagProtocol.GEN2, null, null, 1000);
		    	r.paramSet(TMConstants.TMR_PARAM_READ_PLAN, plan);
		    	// Read tags
			int power = 3000;
			r.paramSet(TMConstants.TMR_PARAM_RADIO_READPOWER, power);
			// END SELECT ANTENNA -- ADJUST OUTPUT POWER
			

			// TESTING GPI STATUS
			r.paramSet(TMConstants.TMR_PARAM_GPIO_INPUTLIST, new int[] {1,2} );
			System.out.printf("\n\nSTATUS of GPI pins:\n");
			state = r.gpiGet();
        	for (Reader.GpioPin gp : state)
        	{
        		System.out.printf("Pin %d: %s\n", gp.id, gp.high ? "High" : "Low");
	        }
	        // END TESTING GPI STATU
	        

			// CREATE ASYNCHRONOUS READER
			System.out.printf("\n\nCreating Asynchronous Reader\n");
			ReadExceptionListener exceptionListener = new TagReadExceptionReceiver();
			r.addReadExceptionListener(exceptionListener);
	        // Create and add tag listener
	        ReadListener rl = new PrintListener();
	        r.addReadListener(rl);
	        // END CREAT ASYNCHRONOUS READER

	        // CHECK INITIAL TEMP
	        System.out.printf("\n\nInitial Temperature: %d\n\n",r.paramGet("/reader/radio/temperature"));
	        // END CHECK INITIAL TEMP



			// int counter = 1; // This counter should be incorporated in the *.csv file
			boolean masterKeepGoing = true;
			while(masterKeepGoing)
			{
				System.out.printf("BEGIN RUN #%d\n",counter);
                                
                                
	            // BEGIN GPI 1: This chunk of code checks for the first gpi pin to be pressed
	                  user_setGPI(r, new boolean[] {true,false});
		          r.paramSet(TMConstants.TMR_PARAM_GPIO_INPUTLIST, new int[] {1,2} ); // this just has to be written before any gpi reads
		        while(true)
		        {
		        	state = r.gpiGet();
		        	if (state[0].high)
		        		continue;
		        	else
		        	{
		        		keepGoing = true;
		        		break;
		        	}
		        }
		        user_setGPI(r, new boolean[] {false,true});
		        // END GPI 1
		        
			    //THIS PART INITIALIZE OUR NEW CSV FILE - IT INCLUDES THE STARTING TIME
                            //THE HEADERS, THE STOPPING TIME AND THE TOTAL RUNNING TIME OF THE SESSION
                                sb = new StringBuilder();
<<<<<<< HEAD
                                f = new File("/mnt/usb/cows_" + counter + ".csv");
=======
                                f = new File("cows_" + counter + ".csv");
>>>>>>> b1f2fca948e9d67073011744733bd4b060b8c5f4
                                pw = new PrintWriter(f);
                                timeStamp = new SimpleDateFormat("MM.dd.yyyy " + "hh.mm.ss.a").format(new Date());
                                timeStamp2 = new SimpleDateFormat("dd.MM.yyyy" + "HH.mm.ss").format(new Date());
                                d1 = format.parse(timeStamp2);
                                sb.append("Reading Starting Time"); sb.append(','); sb.append(timeStamp);  sb.append('\n');
                                sb.append("EPC"); sb.append(','); sb.append("ANTENNA");sb.append(','); sb.append("READ COUNT"); sb.append(','); 
	                        sb.append("READ TIME"); sb.append('\n');
                            

		        // BEGIN READ LOOP
		        while(keepGoing)
		        {
                   
		        	r.startReading();
		        	Thread.sleep(500);
		        	r.stopReading();

		        	System.out.printf("Temperature2: %d\n",r.paramGet("/reader/radio/temperature"));
		        	r.paramSet(TMConstants.TMR_PARAM_GPIO_INPUTLIST, new int[] {1,2} );
		        	state = r.gpiGet();
		        	System.out.printf("%s\n",state[0].high ? "High" : "Low");
		        	if (!state[1].high)
		        	{
		        		keepGoing = false;
                                        // FINALIZE THE CSV FILE
                                        timeStamp = new SimpleDateFormat("MM.dd.yyyy " + "hh.mm.ss.a").format(new Date());
                                        timeStamp2 = new SimpleDateFormat("dd.MM.yyyy" + "HH.mm.ss").format(new Date());
                                        d2 = format.parse(timeStamp2);
                                        sb.append("Reading Stopping Time"); sb.append(','); sb.append(timeStamp);  sb.append('\n');
                                        diffMinutes = (d2.getTime() - d1.getTime()) / (60 * 1000) % 60; 
                                        sb.append("Session Time: " + String.valueOf(diffMinutes) + " minutes");
                                        // WRITE DATA TO CSV FILE AND FLUSH THE STREAM
		        		pw.write(sb.toString());
		        		pw.close();
		        		System.out.printf("DONE: RUN #%d\n",counter);
		        		System.out.printf("TO TURN OFF HOLD BUTTON 1\n");
		        	}
		        }
		        user_setGPI(r, new boolean[] {false,false});
		        counter++;
		        // END READ LOOP

		        // CHECK BOTH BUTTONS TO SEE IF EXIT
		        Thread.sleep(5000);
		        r.paramSet(TMConstants.TMR_PARAM_GPIO_INPUTLIST, new int[] {1,2} );
		        state = r.gpiGet();
		        if (!state[0].high)
	        	{
	        		masterKeepGoing = false;
	        		System.out.printf("TURNING OFF!!!!! BYE BYE\n");
	        		user_setGPI(r, new boolean[] {true,true});
	        		Thread.sleep(200);
	        		user_setGPI(r, new boolean[] {false,false});
	        		Thread.sleep(200);
	        		user_setGPI(r, new boolean[] {true,true});
	        		Thread.sleep(200);
	        		user_setGPI(r, new boolean[] {false,false});
	        		Thread.sleep(200);
	        		user_setGPI(r, new boolean[] {true,true});
	        		Thread.sleep(200);
	        		user_setGPI(r, new boolean[] {false,false});
	        		Thread.sleep(200);
	        		user_setGPI(r, new boolean[] {true,true});
	        		Thread.sleep(200);
	        		user_setGPI(r, new boolean[] {false,false});
	        		
	        	}

	    	}

	        // TURN REEADER OFF
		    r.destroy();
		} 
		catch (Exception ex) {
            ex.printStackTrace();
        }
	}

// ----------------- END PROGRAM ----------------------





	public static void user_setGPI(Reader r, boolean lightArray[])
	{
		try
		{
			r.paramSet(TMConstants.TMR_PARAM_GPIO_OUTPUTLIST, new int[] {1,2} ); // I think the move is to put this before everything
            for(int iii=1; iii < 3; iii++)
            {
                try
                {
                    r.gpoSet(new Reader.GpioPin[]{new Reader.GpioPin(iii, lightArray[iii-1])});
                }
                catch (IndexOutOfBoundsException iobe)
                {
                    //System.out.println("Missing argument after args " + argv[nextarg]);
                    //usage();
                    System.out.println("GPO SET is just not working");
                }
            }
        }
        catch (Exception ex) {
        	ex.printStackTrace();
    	}
    }

	static class TagReadExceptionReceiver implements ReadExceptionListener
	{
		String strDateFormat = "M/d/yyyy h:m:s a";
		SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
		public void tagReadException(com.thingmagic.Reader r, ReaderException re)
		{
			String format = sdf.format(Calendar.getInstance().getTime());
			System.out.println("Reader Exception: " + re.getMessage() + " Occured on :" + format);
			if(re.getMessage().equals("Connection Lost"))
			{
				System.exit(1);
			}
		}
	}


	static class PrintListener implements ReadListener
	{
		public void tagRead(Reader r, TagReadData tr) 
		{
			//System.out.println("Background read: " + tr.toString());
			System.out.println("Background reads: " + tr.toString());
			sb.append(tr.epcString()); sb.append(','); sb.append(tr.getAntenna());sb.append(','); sb.append(tr.getReadCount()); sb.append(','); 
			sb.append(tr.getTime()); sb.append('\n');
		}

	}

	static  int[] parseAntennaList(String[] args,int argPosition)
    	{
        	int[] antennaList = null;
        	try
		{
		    String argument = args[argPosition + 1];
		    String[] antennas = argument.split(",");
		    int i = 0;
		    antennaList = new int[antennas.length];
		    for (String ant : antennas)
		    {
		        antennaList[i] = Integer.parseInt(ant);
		        i++;
		    }
		}
		catch (IndexOutOfBoundsException ex)
		{
		    System.out.println("Missing argument after " + args[argPosition]);
		    //usage();
		}
		catch (Exception ex)
		{
		    System.out.println("Invalid argument at position " + (argPosition + 1) + ". " + ex.getMessage());
		    //usage();
		}
        	return antennaList;
	}
}


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



/**
 *
 * @author qvantel
 */





public class asyncRead
{

	
	static PrintWriter pw;
	static StringBuilder sb;
    
	
	public static void main(String argv[]) throws FileNotFoundException
    	{
    		sb = new StringBuilder();
            pw = new PrintWriter(new File("cows.csv"));
    		sb.append("EPC"); sb.append(','); sb.append("ANTENNA");sb.append(','); sb.append("READ COUNT"); sb.append(','); 
			sb.append("READ TIME"); sb.append('\n');
		try
		{
			
			Reader r = null;
			int[] antennaList = null;
			TagReadData[] tagReads;
			TagReadData[] tagReads2;
			int nextarg = 1;
			boolean trace = false;
			System.out.println("Jesse WOO Is here...");		
			Reader.GpioPin[] state;
			antennaList = parseAntennaList(argv, nextarg);

			String readerURI = "tmr:///dev/ttyACM0";
			r = Reader.create(readerURI);
			r.connect();
			r.paramSet(TMConstants.TMR_PARAM_GPIO_INPUTLIST, new int[] {1,2} );
			r.paramSet(TMConstants.TMR_PARAM_GPIO_OUTPUTLIST, new int[] {1,2} );
		

			if (Reader.Region.UNSPEC == (Reader.Region) r.paramGet(TMConstants.TMR_PARAM_REGION_ID))
		    	{
		        	Reader.Region[] supportedRegions = (Reader.Region[]) r.paramGet(TMConstants.TMR_PARAM_REGION_SUPPORTEDREGIONS);
		        	if (supportedRegions.length < 1) {
		            		throw new Exception("Reader doesn't support any regions");
		        	} else {
		            		r.paramSet(TMConstants.TMR_PARAM_REGION_ID, supportedRegions[0]);
		        	}
		    	}
			
			System.out.println("before parse int");
			String arg = "2";
			antennaList[0] = Integer.parseInt(arg);
			System.out.println("Matt Williams Is here...");
			SimpleReadPlan plan = new SimpleReadPlan(antennaList, TagProtocol.GEN2, null, null, 1000);
		    	r.paramSet(TMConstants.TMR_PARAM_READ_PLAN, plan);
		    	// Read tags
			int power = 3000;
			r.paramSet(TMConstants.TMR_PARAM_RADIO_READPOWER, power);

			//playing with GPIO
			//int something = [1,2];
			
			state = r.gpiGet();
        	for (Reader.GpioPin gp : state)
        	{
        		System.out.printf("Pin %d: %s\n", gp.id, gp.high ? "High" : "Low");
	        }
	        

			// NEW CODE JB and GT
			ReadExceptionListener exceptionListener = new TagReadExceptionReceiver();
			r.addReadExceptionListener(exceptionListener);
	        // Create and add tag listener
	        ReadListener rl = new PrintListener();
	        r.addReadListener(rl);
	        // search for tags in the background
	        //r.startReading();   
	        //System.out.println("Do other work here");

	        //Other work here
	        boolean keepGoing = false;
	        //while(keepGoing)
	        //{
	        System.out.printf("Temperature1: %d\n",r.paramGet("/reader/radio/temperature"));
	        	//state = r.gpiGet();
	        	//System.out.printf("Pin %d: %s\n",state[0].id, state[0].high ? "High" : "Low");
	        	//System.out.printf("George is Here\n");
	        	
	        	//System.out.printf("%s\n",r.gpiGet());
	        	//Thread.sleep(3000);
	        //}


	        //End other work here

	        //Thread.sleep(8000);

	        for(int iii=1, iii < 3; iii++)
            {
				r.gpoSet(new Reader.GpioPin[]{new Reader.GpioPin(iii, true)});
            
            }

	        int counter = 0;
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
	        while(keepGoing)
	        {
	        	r.startReading();
	        	//System.out.printf("Reading...\n");
	        	//System.out.printf("Temperature2: %d\n",r.paramGet("/reader/radio/temperature"));
	        	Thread.sleep(1000);
	        	//System.out.printf("Temperature3: %d\n",r.paramGet("/reader/radio/temperature"));
	        	
	        	r.stopReading();
	        	System.out.printf("Temperature2: %d\n",r.paramGet("/reader/radio/temperature"));
	        	state = r.gpiGet();
        		//for (Reader.GpioPin gp : state)
        		//{
        		//	System.out.printf("Pin %d: %s\n", gp.id, gp.high ? "High" : "Low");
	        	//}
	        	System.out.printf("%s\n",state[0].high ? "High" : "Low");
	        	counter = counter + 1;
	        	if (!state[1].high)
	        	{
	        		keepGoing = false;
	        		pw.write(sb.toString());
	        		pw.close();
	        	}
	        }

			/*	
		    tagReads = r.read(1000);
		    for (TagReadData tr : tagReads)
		    {
		       	System.out.println("Tag : " + tr.toString());
		    }
			tagReads2 = r.read(10000);
			for (TagReadData tr : tagReads2)
			{
				System.out.println("Tag : " + tr.toString());
			}
			System.out.println(r.paramGet(TMConstants.TMR_PARAM_RADIO_READPOWER));
		    	// Shut down reader
		    */
			
			//r.reboot();
		    r.destroy();
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
			System.out.println("Background read: " + tr.toString());
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

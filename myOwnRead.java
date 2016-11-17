/*
 * Sample program that shows how to create a
 * simpleReadPlan that uses a list of antennas as passed by the user
 * and prints the tags found.
 */
//package samples;

import com.thingmagic.Reader;
import com.thingmagic.SimpleReadPlan;
import com.thingmagic.TMConstants;
import com.thingmagic.TagProtocol;
import com.thingmagic.TagReadData;
import com.thingmagic.TransportListener;

/**
 *
 * @author qvantel
 */

public class myOwnRead
{
	public static void main(String argv[])
    	{
		try
		{
			Reader r = null;
			int[] antennaList = null;
			TagReadData[] tagReads;
			TagReadData[] tagReads2;
			int nextarg = 1;
			boolean trace = false;
			System.out.println("Jesse WOO Is here...");		
			antennaList = parseAntennaList(argv, nextarg);

			String readerURI = "tmr:///dev/ttyACM0";
			r = Reader.create(readerURI);
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
			
			System.out.println("before parse int");
			String arg = "2";
			antennaList[0] = Integer.parseInt(arg);
			System.out.println("Matt Williams Is here...");
			SimpleReadPlan plan = new SimpleReadPlan(antennaList, TagProtocol.GEN2, null, null, 1000);
		    	r.paramSet(TMConstants.TMR_PARAM_READ_PLAN, plan);
		    	// Read tags
			int power = 3000;
			r.paramSet(TMConstants.TMR_PARAM_RADIO_READPOWER, power);
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
			
			r.reboot();
			
		    	r.destroy();
		} catch (Exception ex) {
            		ex.printStackTrace();
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

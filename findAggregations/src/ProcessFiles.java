import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
 
public class ProcessFiles {
 
  public static void main(String[] args) {
 
	ProcessFiles obj = new ProcessFiles();
	obj.run();
 
  }
 
  public void run() {
 
	String csvFile = "/Users/Rezart/Documents/ProjectsCsvFiles/ApacheIvy/MultiplicityIvyNoDuplicates.csv";
	
	BufferedReader br = null;
	BufferedReader br2 = null;
	int counterBothSides=0;
	String line = "";
	String cvsSplitBy = ",";
	 int multiplicity0to1 = 0;
     int multilpicity1toM=0;
     int bothSides =0;
    int counter =0;
    int counter5=0;
    ArrayList<String> firstClass = new ArrayList<String>();
	ArrayList<String> vars = new ArrayList<String>();
	ArrayList<String> secondClass = new ArrayList<String>();
	ArrayList<String> multiplicity = new ArrayList<String>();
	ArrayList<String> generics = new ArrayList<String>();
	int BothSides =0;
	try {
 
		Map<String, String> maps = new HashMap<String, String>();
		Map<String, String> maps2 = new HashMap<String, String>();
		br = new BufferedReader(new FileReader(csvFile));
		
		
		while ((line = br.readLine()) != null) {
 
			// use comma as separator
			String[] classes = line.split(cvsSplitBy);
              firstClass.add(classes[0]);
			  vars.add(classes[2]);
			  secondClass.add(classes[3]);
			  generics.add(classes[4]);
			  multiplicity.add(classes[5]);
	     }
		ArrayList<Boolean> isAggregation = new ArrayList<Boolean>();
		for(int i = 0 ; i<firstClass.size();i++)
		{
			isAggregation.add(true);
		}
		for(int i = 0 ; i<firstClass.size();i++)
		{
			for(int j=0;j<firstClass.size();j++)
			{
				if(j!=i)
				{
				if((firstClass.get(i).equals(firstClass.get(j)) &&  secondClass.get(i).equals(secondClass.get(j))))
						{
					        isAggregation.set(i, false);
						}
				}
			}
		}
		
	    
		for(int i = 0 ; i<firstClass.size();i++)
		{
			if(isAggregation.get(i).equals(true))
			{
				counter++;
			}
		}
		
 
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	} finally {
		if (br != null) {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
 
	System.out.println( "The number of  aggregations is : " + counter  );
  }
 
}
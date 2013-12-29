import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
 
public class ProcessFiles {
 
  public static void main(String[] args) {
 
	ProcessFiles obj = new ProcessFiles();
	obj.run();
 
  }
 
  public void run() {
 
	  String csvFile = "/Users/Rezart/Documents/ProjectsCsvFiles/Ant-1.9.1/FindBothDirectionsAnt.csv";
		
		ArrayList<String> listFirstClasses =  new ArrayList<String>();
		ArrayList<String> listSecondClasses = new ArrayList<String>();
		ArrayList<String> listMultiplicity = new ArrayList<String>();
		int zerotoone=0;
		int onetomany=0;
		BufferedReader br = null;
		BufferedReader br2 = null;
	String line = "";
	String cvsSplitBy = ",";
    
	try {
 
		br = new BufferedReader(new FileReader(csvFile));
		while ((line = br.readLine()) != null) {
 
		        // use comma as separator
			String[] classes = line.split(cvsSplitBy);
            listFirstClasses.add(classes[0]);
         
            listSecondClasses.add(classes[3]);
         

 
		}
	    int counterBothSides =0;
		for(int i=0;i<listFirstClasses.size();i++)
		{
			for(int j=i+1;j<listFirstClasses.size();j++)
			{
				if(listFirstClasses.get(i).equals(listSecondClasses.get(j)) && listFirstClasses.get(j).equals(listSecondClasses.get(i)))
				{
					counterBothSides++;
					System.out.println(listFirstClasses.get(i)+" "+listSecondClasses.get(i));
				}
			}
		}
		System.out.println("Bi-Directional:" + counterBothSides + " general:" + (listFirstClasses.size()-counterBothSides*2));
		
		
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
 
	System.out.println("Done");
  }
 
}
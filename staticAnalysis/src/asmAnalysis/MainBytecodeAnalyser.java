package asmAnalysis;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.objectweb.asm.ClassVisitor;

import asmAnalysis.ClassVisitorFinder;
import asmAnalysis.ClassFileOpener;
import asmAnalysis.MainBytecodeAnalyser;
import asmAnalysis.MethodVisitorFinder;



public class MainBytecodeAnalyser {

	

	/**
	 * @param args  A list of class/jar files to process
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException 
	{
		if (args.length == 0) {
			System.err.println("MainBytecodeAnalyser error: run with some .class files as arguments.");
			return;
		}
		MainBytecodeAnalyser instance = new MainBytecodeAnalyser();
		instance.visitAllFiles(System.out, args);
		//System.out.println(MethodComplexityCounter.graph.toString());
		FileWriter fw = new FileWriter("WriteInfo.csv");
		FileWriter fw1 = new FileWriter("MethodInvocations.csv");
		FileWriter fw2 = new FileWriter("Generalizations.csv");
		FileWriter fw3 = new FileWriter("Multiplicity.csv");
		
		   PrintWriter out = new PrintWriter(fw);
		   PrintWriter out1 = new PrintWriter(fw1);
		   PrintWriter out2 = new PrintWriter(fw2);
		   PrintWriter out3 = new PrintWriter(fw3);
		   Iterator iterator = ClassVisitorFinder.attributesReferenced.keySet().iterator();  
		   
		   while (iterator.hasNext()) {  
		      String key = iterator.next().toString();  
		      String value =ClassVisitorFinder.attributesReferenced .get(key).toString();  
		      
		      //out.println(key + "," + value);  
		   }  
		   
		   
           Iterator iterator2 = MethodVisitorFinder.localReferenced.keySet().iterator();  
		   
		   while (iterator2.hasNext()) {  
		      String key = iterator2.next().toString();  
		      String value =MethodVisitorFinder.localReferenced.get(key).toString();  
		      
		      //out.println(key + "," + value);  
		   }  
		   
		   out.println("Multipicity");
		   out3.println("Multipicity");
		   out.println("Current Class,Accessability,varName,Type,Generics,Multiplicity");
		   out3.println("Current Class,Accessability,varName,Type,Generics,Multiplicity");
		   for(int i =0; i<ClassVisitorFinder.attReferenced.size();++i)
		   {
			   
			   out.println(ClassVisitorFinder.attReferenced.get(i));
			   out3.println(ClassVisitorFinder.attReferenced.get(i));
		   }
		   for(int i =0; i<MethodVisitorFinder.locReferenced.size();++i)
		   {
			   out.println(MethodVisitorFinder.locReferenced.get(i));
			   out3.println(MethodVisitorFinder.locReferenced.get(i));
		   }
		   out.println();
		   out.println("Generalizations");
		   out2.println("Generalizations");
		   out.println("Class,Superclass");
		   out2.println("Class,Superclass");
		   for(int i =0; i<ClassVisitorFinder.generalizations.size();++i)
		   {
			   out.println(ClassVisitorFinder.generalizations.get(i));
			   out2.println(ClassVisitorFinder.generalizations.get(i));
		   }
		   
		   out.println("Calling property");
		   out1.println("Calling property");
		   out.println("Type of Invocation,Method name,Class Belonging,Method owner");
		   out1.println("Type of Invocation,Method name,Class Belonging,Method owner");
		   for(int i =0; i<MethodVisitorFinder.methodCalls.size();++i)
		   {
			   
			   out.println(MethodVisitorFinder.methodCalls.get(i));
			   out1.println(MethodVisitorFinder.methodCalls.get(i));
		   } 
		   
		   out1.flush();
		   out1.close();
		   out2.flush();
		   out2.close();
		   out3.flush();
		   out3.close();
		   out.flush();
		   out.close();
		  
		   
		   
		   
	}
	

	protected void visitAllFiles(PrintStream outfile, String[] filesToAnalyse) 
	{
		ClassVisitor visitor = new ClassVisitorFinder(outfile);
		outfile.printf("Date, \"%s\"\n", new Date());
		ClassFileOpener opener = new ClassFileOpener(visitor);
		for (String fileName : filesToAnalyse) {
			opener.open(fileName);
		}
	}


}

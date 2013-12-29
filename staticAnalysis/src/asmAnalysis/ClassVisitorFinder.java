package asmAnalysis;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import asmAnalysis.MethodVisitorFinder;


public class ClassVisitorFinder extends ClassVisitor 
{

	static String currentClassName;
	static   ParentClass p1 = new ParentClass();
	Map<String, Integer> methodCounts;
	static Map<String, String> attributesReferenced = new HashMap<String,String>();
	static ArrayList<String> attReferenced = new ArrayList<String>();
	static ArrayList<String> generalizations = new ArrayList<String>();
	PrintStream outfile;

    public ClassVisitorFinder(PrintStream outfile) {
        super(Opcodes.ASM4);
    	this.methodCounts = new HashMap<String, Integer>();
    	
    	this.outfile = outfile;
    	
    }

    @Override
    public void visit(int version, int access, String name,
                    String signature, String superName, String[] interfaces) 
    {
    	this.currentClassName  = name;
    	
    	p1.setParentClass(name);
    	if(!superName.contains("java/lang/Object"))
    	{
    	System.out.println("////***** We are in class"+name +" is a subclass of "+superName);
    	generalizations.add("Class:"+name+",Class:"+superName);
    	}
    	else
    	{
    	System.out.println("////***** We are in class"+name +" it doesnt have superclass");	
    	generalizations.add("Class:"+name+",No");
    	}
    	this.methodCounts.clear();
    }
    
    @Override
    public void visitSource(String source, String debug)
    {
    	System.out.println("!!!!!!!!!! " + source+"+++ "+debug);
    	
    }
    
    @Override
    public FieldVisitor visitField(int access, String name, String desc,
            String signature, Object value) 
    {
    	attributesReferenced.put(currentClassName, desc);
    	
    	int m = currentClassName.lastIndexOf('/');
    	int m2= desc.lastIndexOf('/');
    	/*if(!name.equals("this") && (!(desc.contains("java/lang/")) && (!(desc.contains("java/io/"))) && (!(desc.equals("I")))&& (!(desc.equals("[I"))) && (!(desc.equals("Z"))) && (!(desc.equals("[Z")))))
    	{
    	System.out.println("field//////"+ " " + access+" " +  currentClassName.substring(m+1)+ " "+"  " + name+ "  "+desc+"  "+signature+ "  ");
    	attReferenced.add(currentClassName+","+desc);
    	} */
    	if(desc.startsWith("L") && !name.equals("this") && (!(desc.contains("java/lang/")) && (!(desc.contains("java/io/")))) && ((!(signature == null) && (signature.contains("java/io/")))&& (!(signature.contains("java/lang/")))))
    	{
    		System.out.println("field//////"+ " " + access+" " +  currentClassName.substring(m+1)+ " "+"  " + name+ "  "+desc+"  "+signature+ "  ");
    		if(access == 2 && !(signature == null) )
    		{
    			if(desc.contains("java/util") || desc.startsWith("["))
    			{
        	     attReferenced.add(currentClassName+","+"private"+","+name+","+desc +"," + signature+","+"1..M");
    			} else 
    			{
    			 attReferenced.add(currentClassName+","+"private"+","+name+","+desc +"," + signature+"," +"0..1");	
    			}
    		}
    		if(access == 2 && signature == null )
    		{

    			if(desc.contains("java/util") || desc.startsWith("["))
    			{
        	     attReferenced.add(currentClassName+","+"private"+","+name+","+desc +"," + "Not using generics"+","+"1..M");
    			} else
    			{
    				 attReferenced.add(currentClassName+","+"private"+","+name+","+desc +"," + "Not using generics"+","+"0..1");
    			}
    		}
    		if(access == 0  && !(signature == null)  )
    		{
    			if(desc.contains("java/util") || desc.startsWith("[") )
    			{
    			attReferenced.add(currentClassName+","+"public"+","+name+","+desc +"," + signature+","+"1..M");
    			} else
    			{
    				attReferenced.add(currentClassName+","+"public"+","+name+","+desc +"," + signature+","+"0..1");	
    			}
    		}
    		if(access == 0  && signature == null )
    		{
    			if(desc.contains("java/util") || desc.startsWith("["))
    			{
    			attReferenced.add(currentClassName+","+"public"+","+name+","+desc +"," + "Not using generics"+","+"1..M");
    			} else 
    			{
    				attReferenced.add(currentClassName+","+"public"+","+name+","+desc +"," + "Not using generics"+","+"0..1");	
    			}
    		}
    		if(access == 1 && !(signature == null)  )
    		{
    			if(desc.contains("java/util") || desc.startsWith("["))
    			{
    			attReferenced.add(currentClassName+","+"protected"+","+name+","+desc +"," + signature+","+"1..M");
    			} else 
    			{
    				attReferenced.add(currentClassName+","+"protected"+","+name+","+desc +"," + signature+","+"0..1");	
    			}
    		}
    		if(access == 1 && signature == null )
    		{
    			if(desc.contains("java/util") || desc.startsWith("["))
    			{
    			attReferenced.add(currentClassName+","+"protected"+","+name+","+desc +"," +"Not using generics"+","+"1..M");
    			} else
    			{
    				attReferenced.add(currentClassName+","+"protected"+","+name+","+desc +"," +"Not using generics"+","+"0..1");	
    			}
    		}
    		
    	}
    	
    	return null;
    }
    

    @Override
    public MethodVisitor visitMethod(int access, String methodName,
            String methodDesc, String signature, String[] exceptions)
    {
    	MethodVisitorFinder methodVisitor = new MethodVisitorFinder(methodName+methodDesc, this);
        return methodVisitor;
    }

    public void setMethodCount(String currentMethod, int predicateCount) {
		this.methodCounts.put(currentMethod, predicateCount);
	}

    @Override
    public void visitEnd()
    {
    	this.outfile.print(this);
    }
    
   

	@Override
	public String toString() {
		StringBuilder results = new StringBuilder();
    	int classTotal = 0;
    	for (Entry<String,Integer> pair : this.methodCounts.entrySet()) {
    		//results.append(String.format("Method,%s.%s,%d\n", this.currentClassName, pair.getKey(), pair.getValue()));
    		//classTotal += pair.getValue();
    	}
    	results.append(String.format("Class,%s,%d\n", this.currentClassName, classTotal));
		return results.toString();
	}

   
}

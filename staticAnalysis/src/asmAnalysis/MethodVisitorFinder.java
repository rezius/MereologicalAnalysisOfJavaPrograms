package asmAnalysis;

//import org.objectweb.asm.Label;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.*;

import asmAnalysis.ClassVisitorFinder;
//import org.objectweb.asm.MethodVisitor;
//import org.objectweb.asm.Opcodes;


public class MethodVisitorFinder extends MethodVisitor {

	int predicateCount;
	String currentMethod;
	static Map<String, String> localReferenced = new HashMap<String,String>();
	static ArrayList<String> locReferenced = new ArrayList<String>();
	static ArrayList<String> methodCalls = new ArrayList<String>();
	ClassVisitorFinder parentClass;
	//static DotGraph graph = new DotGraph("Method Calls");
	public MethodVisitorFinder(String methodName, ClassVisitorFinder parentClass) 
	{
		super(Opcodes.ASM4);
		this.currentMethod = methodName;
		this.parentClass = parentClass;
		//this.predicateCount = 1;  // NB - always predicates+1
	}

	@Override
	public void visitJumpInsn(int opcode, Label label) {
		if (opcode != Opcodes.GOTO)
			this.predicateCount ++;
	}

	@Override
	public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
		this.predicateCount += labels.length;
	}

	@Override
	public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
		this.predicateCount += labels.length;
	}

	@Override
	public void visitEnd() {
		this.parentClass.setMethodCount(this.currentMethod, this.predicateCount);
		//System.out.println(graph.toString());
	}
	
	@Override
	public void visitMethodInsn(int opcode, String owner,String name, String desc)
	{
		
		String opcodeName="";
		if(opcode==182)
		{
			opcodeName="INVOKEVIRTUAL";
		}
		
		if(opcode==183)
		{
			
			opcodeName="INVOKESPECIAL";
			
		}
		
		if(opcode==184)
		{
			
			opcodeName = "INVOKESTATIC";
			
		}
		
		if(opcode==185)
		{
			
			opcodeName = "INVOKEINTERFACE";
			
		}
		if((!owner.contains("java/io/")) && (!owner.contains("java/util/")) && (!owner.contains("java/lang/")) && ((owner!= ClassVisitorFinder.p1.getParentClass())))
				{
			
		methodCalls.add(opcodeName+","+name+","+ClassVisitorFinder.p1.getParentClass()+","+owner);
				}
		//dotgraph.Node source = graph.addNode(owner);
		//dotgraph.Node target =  graph.addNode(name);
		//graph.addEdge(source, target);
		//graph.addEdge(owner, name);
	}
    
	@Override
	public void visitLocalVariable(String name,
            String desc,
            String signature,
            Label start,
            Label end,
            int index)
	{
		localReferenced.put(ClassVisitorFinder.p1.getParentClass(), desc);
		
		int m1 = desc.lastIndexOf('/');
		int m2 = currentMethod.lastIndexOf(')');
		/*if(!name.equals("this") && (!(desc.contains("java/lang/")) && (!(desc.contains("java/io/"))) && (!(desc.equals("I"))) && (!(desc.equals("Z")))))
		{
		System.out.println(ClassComplexityCounter.p1.getParentClass()+ "   " +   currentMethod.substring(0, m2+1)+"  "+ name+"  "+desc+ "  ");
		locReferenced.add(ClassComplexityCounter.p1.getParentClass()+","+desc);
		} */
		if(desc.startsWith("L") && !name.equals("this") && (!(desc.contains("java/lang/")) && (!(desc.contains("java/io/")))) )
		{
			
			System.out.println("-----------" + ClassVisitorFinder.p1.getParentClass()+ "   " +   currentMethod.substring(0, m2+1)+"  "+ name+"  "+desc+ "  ");
			if(!(signature==null) && (!(signature.contains("java/util")))&& (!(signature.contains("java/io"))) && (!desc.contains("java/util")) && (!desc.startsWith("[")))
			locReferenced.add(ClassVisitorFinder.p1.getParentClass()+",private"+","+name+","+desc+","+signature+","+"0..1");
			if(!(signature==null) && (!(signature.contains("java/util")))&& (!(signature.contains("java/io"))) && (!desc.contains("java/util")) && (((desc.contains("java/util"))|| desc.startsWith("["))))
				locReferenced.add(ClassVisitorFinder.p1.getParentClass()+",private"+","+name+","+desc+","+signature+","+"1..M");
			if((signature==null) && (!desc.contains("java/util")) && (!desc.startsWith("[")) )
				locReferenced.add(ClassVisitorFinder.p1.getParentClass()+",private"+","+name+","+desc+",Not using generics"+","+"0..1");
			if((signature==null) && (((desc.contains("java/util"))|| desc.startsWith("["))) )
				locReferenced.add(ClassVisitorFinder.p1.getParentClass()+",private"+","+name+","+desc+",Not using generics"+","+"1..M");	
			
		}
		
	}

	
	
}

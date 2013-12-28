package branches;

import java.lang.instrument.UnmodifiableClassException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * This class does the job of instrumenting a single method. Since we're
 * interested in coverage, we instrument instructions to do with branches and
 * switch. We also look out for "line number" instructions so we can keep in
 * synch with the original Java file. Remember: in this class, "super.mv" is the
 * next method visitor in the visitor-chain.
 * 
 * @author jpower
 * 
 */
public class InstrumenterMethodVisitor extends MethodVisitor {

	private String methodName;
	private String className;
	private String description;
	private static ProbeIndexer probeIndexer;

	public InstrumenterMethodVisitor(ProbeIndexer probeIndexer,
			MethodVisitor mv, String methodName, String className,
			String description) {
		super(Opcodes.ASM4, mv);
		this.probeIndexer = probeIndexer;
	}

	@Override
	public void visitLineNumber(int line, Label start) {
		this.probeIndexer.setCurrentLine(line);
		super.visitLineNumber(line, start);
	}

	@Override
	public void visitTypeInsn(int opcode, String type) {
		if (opcode == Opcodes.NEW) {
			super.mv.visitCode();
			try {
				super.mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System",
						"err", "Ljava/io/PrintStream;");
				super.mv.visitLdcInsn(type.toString());
				super.mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
						"java/io/PrintStream", "print", "(Ljava/lang/String;)V");
				// record data in CSV
				Object o = new Object();
				int index = probeIndexer.registerObjectCreate(type.toString(),
						o);
			} catch (Exception e) {

			}

			// add method to the existing class
			try {
				super.mv.visitVarInsn(Opcodes.ALOAD, 0);
				super.mv.visitMethodInsn(Opcodes.INVOKESTATIC,
						"RuntimeMonitor", "hitObjectCreate",
						"(Ljava/lang/Object;)V");
				super.mv.visitTypeInsn(opcode, type);
			} catch (Exception e) {
			}

			if (opcode == Opcodes.INVOKESPECIAL
					|| opcode == Opcodes.INVOKESTATIC
					|| opcode == Opcodes.INVOKEVIRTUAL) {

			}
		}
	}

	@Override
	public void visitMaxs(int maxStack, int maxLocals) {
		super.mv.visitMaxs(maxStack + 2, maxLocals);
	}
}

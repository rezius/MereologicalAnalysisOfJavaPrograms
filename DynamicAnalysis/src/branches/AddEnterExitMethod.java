package branches;

import static org.objectweb.asm.Opcodes.ACC_INTERFACE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;

import java.util.List;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

class AddEnterExitMethod extends AdviceAdapter {
	private String name;
	private String desc;
	private String className;
	private ProbeIndexer probeIndexer;

	public AddEnterExitMethod(ProbeIndexer probeIndexer, int access,
			String name, String desc, MethodVisitor mv, String className) {
		super(ASM4, mv, access, name, desc);
		this.name = name;
		this.desc = desc;
		this.className = className;
		this.probeIndexer = probeIndexer;
	}

	protected void onMethodEnter() {
		super.mv.visitFieldInsn(GETSTATIC, "java/lang/System", "err",
				"Ljava/io/PrintStream;");
		super.mv.visitLdcInsn("Entering " + className + "." + name.toString()
				+ "()");
		super.mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream",
				"println", "(Ljava/lang/String;)V");

		// register in file
		this.probeIndexer.registerMethodEnter(className + "." + name.toString()
				+ "()");
	}

	protected void onMethodExit(int opcode) {
		if (opcode != ATHROW) {
			onFinally(opcode);
		}
	}

	public void visitMaxs(int maxStack, int maxLocals) {
		super.visitMaxs(maxStack + 4, maxLocals);
	}

	private void onFinally(int opcode) {
		super.mv.visitFieldInsn(GETSTATIC, "java/lang/System", "err",
				"Ljava/io/PrintStream;");
		super.mv.visitLdcInsn("Exiting " + className + "." + name.toString()
				+ "()");
		super.mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream",
				"println", "(Ljava/lang/String;)V");

		// register in file
		this.probeIndexer.registerMethodExit(className + "." + name.toString()
				+ "()");
	}
}
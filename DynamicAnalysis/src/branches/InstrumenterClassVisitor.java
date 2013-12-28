package branches;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.LocalVariablesSorter;
import org.objectweb.asm.util.CheckClassAdapter;

/**
 * A Class Visitor that instruments a single class file. Actually, there's
 * little work done here: it's mainly delegated to a Method Visitor.
 * 
 * @author jpower
 */
public class InstrumenterClassVisitor extends ClassVisitor {

	private String name;
	private boolean isInterface;
	ProbeIndexer probeIndex;

	public InstrumenterClassVisitor(ProbeIndexer probeIndex,
			CheckClassAdapter cv, String name) {
		super(Opcodes.ASM4, cv);
		this.probeIndex = probeIndex;
		this.name = name;
	}

	@Override
	public void visit(int version, int access, String name, String signature,
			String superName, String[] interfaces) {
		super.cv.visit(version, access, name, signature, superName, interfaces);
		this.name = name;
	}

	@Override
	public void visitSource(String source, String debug) {
		super.visitSource(source, debug);
		probeIndex.setCurrentFile(source);
	}

	@Override
	public MethodVisitor visitMethod(int access, String methodName,
			String methodDesc, String signature, String[] exceptions) {

		MethodVisitor mv = super.cv.visitMethod(access, methodName, methodDesc,
				signature, exceptions);

		if (mv != null) {
			mv = new InstrumenterMethodVisitor(probeIndex, mv, methodName,
					this.name, methodDesc);
			mv = new AddEnterExitMethod(probeIndex, access, methodName,
					methodDesc, mv, this.name);
		}
		return mv;

	}

	@Override
	public void visitEnd() {
		super.cv.visitEnd();
		this.probeIndex.resetNames();
	}

}

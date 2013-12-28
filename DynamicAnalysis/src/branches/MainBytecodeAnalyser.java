package branches;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;

/**
 * Insert probes into a list of class files to implement branch coverage.
 * It also generates a probe index listing the locations of all the probes it inserted.
 * @author jpower
 *
 */
/**
 * @author jpower
 * 
 */
public class MainBytecodeAnalyser {

	private static String probeIndexFilename = "probeIndex.csv";
	private File outputDirectory;
	private final ProbeIndexer probeIndex;
	private boolean noisy = false;

	/**
	 * @param args
	 *            The output directory, followed by a list of class files to
	 *            process. Both the probe index and the instrumented class files
	 *            will be writte to the output directory.
	 */
	public static void main(String[] args) {
		if (args.length < 2) {
			usageMessage("insufficient arguments (" + args.length + ")");
			return;
		}
		MainBytecodeAnalyser instance = new MainBytecodeAnalyser(true);
		instance.notify("Running in directory: "
				+ System.getProperty("user.dir"));
		try {
			instance.openOutDir(args[0]);
			instance.openProbeIndex();
			for (int i = 1; i < args.length; i++)
				instance.visitClassFile(args[i]);
		} catch (IOException e) {
			e.printStackTrace();
		}
		instance.probeIndex.closeStream();
	}

	public MainBytecodeAnalyser() {
		this.outputDirectory = null;
		this.probeIndex = new ProbeIndexer();
	}

	public MainBytecodeAnalyser(boolean noisy) {
		this();
		this.noisy = true;
	}

	/**
	 * Simple method to print some progress information, if desired.
	 * 
	 * @param msg
	 *            Print this message if 'noisy' is set to true.
	 */
	private void notify(String msg) {
		if (noisy)
			System.out.println(msg);
	}

	/**
	 * Print a usage/error message
	 * 
	 * @param errMsg
	 *            The message to print, giving details of what went wrong.
	 */
	static void usageMessage(String errMsg) {
		System.err.println("MainBytecodeAnalyser error with: " + errMsg);
		System.err
				.println("\tUsage: java MainBytecodeAnalyser outDir inFile1.class inFile2.class .....");

	}

	/**
	 * Visit (and this instrument) a single class file
	 * 
	 * @param classfilename
	 *            The class file to be read from
	 * @throws IOException
	 *             if I can't open the class file.
	 */
	private void visitClassFile(String classfilename) throws IOException {
		if (!classfilename.endsWith(".class"))
			classfilename += ".class";
		String className = classfilename.substring(0,
				classfilename.length() - 6);
		notify("Reading class file " + classfilename);
		// Set up the transformation chain: writer <- tracer <- checker <-
		// changer <- reader
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		ClassVisitor tracer = cw;
		if (noisy)
			tracer = new TraceClassVisitor(cw, new PrintWriter(System.out));
		CheckClassAdapter checker = new CheckClassAdapter(tracer);
		InstrumenterClassVisitor instrumenter = new InstrumenterClassVisitor(
				probeIndex, checker, className);
		ClassReader cr = makeReaderFrom(classfilename);
		// Perform the visit:
		cr.accept(instrumenter, ClassReader.SKIP_FRAMES);
		// Dump the instrumented class to a file:
		this.writeNewClassFile(classfilename, cw.toByteArray());

	}

	/**
	 * The visit is over, so write the instrumented class to a file
	 * 
	 * @param classfilename
	 *            The file to write to
	 * @param byteArray
	 *            The byte code of the instrumented class
	 * @throws IOException
	 *             If there's a problem while writing the class to a file.
	 */
	private void writeNewClassFile(String classfilename, byte[] byteArray)
			throws IOException {
		File infile = new File(classfilename);
		File outfile = new File(outputDirectory, infile.getName());
		notify("Writing instrumented class file " + outfile.getPath());
		FileOutputStream newclassfile = new FileOutputStream(outfile);
		newclassfile.write(byteArray);
		newclassfile.close();
	}

	/**
	 * Create a (ASM) class reader object from a given filename
	 * 
	 * @param classfilename
	 *            The (relative) file name of a .class file
	 * @return An ASM object that can read this class
	 * @throws IOException
	 *             if there's a problem opening the .class file
	 */
	private ClassReader makeReaderFrom(String classfilename) throws IOException {
		ClassReader cr = null;
		try {
			FileInputStream is = new FileInputStream(classfilename);
			cr = new ClassReader(is);
		} catch (IOException e) {
			usageMessage("cannot read class file " + classfilename);
			throw (e);
		}
		return cr;
	}

	/**
	 * Open a file for writing the probe index to
	 * 
	 * @throws FileNotFoundException
	 *             if we can't open the file
	 */
	private void openProbeIndex() throws FileNotFoundException {
		String fullPath = outputDirectory.getPath() + File.separator
				+ probeIndexFilename;
		notify("Writing probe index to " + fullPath);
		File probeIndexFile = new File(fullPath);
		try {
			PrintStream ps = new PrintStream(probeIndexFile);
			this.probeIndex.setStream(ps);
		} catch (FileNotFoundException e) {
			usageMessage("problem opening hit-table file " + fullPath);
			throw (e);
		}
	}

	/**
	 * Check that the user-specified output dirextory is valid.
	 * 
	 * @param filename
	 *            The output directory (for probe index and instrumented files)
	 * @throws IOException
	 *             if this is not a valid directory.
	 */
	private void openOutDir(String filename) throws IOException {
		notify("Setting output directory to to " + filename);
		this.outputDirectory = new File(filename);
		if (!this.outputDirectory.isDirectory()) {
			usageMessage(filename + " is not a valid directory");
			throw new IOException();
		}
	}

}

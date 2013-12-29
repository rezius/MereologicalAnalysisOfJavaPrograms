package asmAnalysis;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;

/** 
 * This class is used to recursively process directories and JAR files, 
 * looking for .class files.  Whenever it finds a .class file, it will
 * invoke the accept() method of the class visitor for it.
 * @author rezart
 */

public class ClassFileOpener
{
	private ClassVisitor visitor;
	
	
	/** 
	 * Create an object that will open class files
	 * @param visitor This handler will be given each class file in turn
	 */
	public ClassFileOpener(ClassVisitor visitor) {
		this.visitor = visitor;
	}

	public ClassVisitor getVisitor() {
		return this.visitor;
	}
	
	public void setVisitor(ClassVisitor visitor) {
		this.visitor = visitor;
	}

	
	/** 
	 * Open a class file, JAR file or directory and process all the classes
	 * @param filename The name of the file/directory to process
	 */
	public void open(String filename)
	{
		if (filename.endsWith(".class")) 
			readClassFile(filename);
		else if (filename.endsWith(".jar"))
			readJarFile(filename);
		else {
			File dir = new File(filename);
			if (dir.isDirectory()) 
				readDirectory(dir);
			else
				System.err.printf("Error opening file \"%s\" (not class/jar/dir)\n", filename);
		}
	}


	/** 
	 * Read a directory and search it and (recursively) its sub-directories for class and jar files.
	 * Will ultimately try to visit any class files it finds.
	 * @param dir The name of the directory.
	 */
	protected void readDirectory(File dir) 
	{
		if (dir.isDirectory()) {
			for (String child : dir.list()) 
			{
				if (child.endsWith(".class")) 
					readClassFile(dir.getPath()+File.separator+child);
				else if (child.endsWith(".jar"))
					readJarFile(dir.getPath()+File.separator+child);
				else
					readDirectory(new File(dir, child));
			}
		}
	}


	/** 
	 * Open the file, which must be a ".class" file, and then try to visit it. 
	 * @param filename The name of the .class file
	 */ 
	protected void readClassFile(String filename)
	{
		try {
			this.visitOneClass(filename, new FileInputStream(filename));
		} catch (FileNotFoundException e) {
			System.err.println("Cannot find class file "+filename);
			e.printStackTrace();
		}
	}


	/** 
	 * Read a ".jar" file and process each ".class" file it contains.
	 * @param jarfilename The name of the jar file to process.
	 */
	protected void readJarFile(String jarfilename)
	{
		try {
			JarFile jar = new JarFile(jarfilename);
			Enumeration<JarEntry> jenum = jar.entries();
			while (jenum.hasMoreElements()) {
				JarEntry je = jenum.nextElement();
				String filename = je.getName();
				this.visitOneClass(filename, jar.getInputStream(je));
			}
		}
		catch (java.io.IOException e) {
			System.err.println("Error with JAR file "+jarfilename);
			e.printStackTrace();
		}
	}


	/**
	 * @param filename The (qualified) name of the .class file
	 * @param is An opened InputStream corresponding to the class file
	 */
	protected void visitOneClass(String filename, InputStream is)
	{
		if (! filename.endsWith(".class")) 
			return;
		try {
			ClassReader cr = new ClassReader(is);
			cr.accept(this.visitor, 0);
		} catch (IOException e) {
			System.err.println("Error opening class file "+filename);
			e.printStackTrace();
		}
	}

	
	
}

package branches;

import java.io.PrintStream;

/**
 * A class to represent the probe index file: simply a list of the probes that have been inserted.
 * As the visitors work they register the current context (class/method/line) with this class.
 * Whenever a probe is generated it is registered with this class to get its unique index number.
 * @author jpower
 *
 */
public class ProbeIndexer 
{

	private PrintStream ps;
	private String currentClass;
	private String currentMethod;
	private int currentLine;
	private String currentFile;

	/** Each probe gets a unique index as it is created.
	 */
	private static int probeCounter = 0;
	
	public ProbeIndexer() {
		this.ps = System.out;
	}

	public void setStream(PrintStream ps) {
		this.ps = ps;
	}

	public void closeStream() {
		if (this.ps != System.out)
			this.ps.close();
	}

	/**
	 * Prints a single line of the probe index - i.e. the information about one probe.
	 * Called each time a new probe is created, and returns a unique index for it to the caller.
	 * @param probeData The kind of probe this is (and maybe some more info)
	 * @return the unique index for this probe (must match the probe index file)
	 */
	public int printInContext(String probeData) {
		probeCounter ++;
		String sourceContext="-,-";
		if (this.currentFile != null && this.currentLine>0)
			sourceContext = String.format("%s,%d", this.currentFile, this.currentLine);
		ps.printf("%d%s", probeCounter,",");	
		ps.printf(probeData);
		ps.println();
		return probeCounter;
	}
	
	public int registerObjectCreate(String type, Object o) {
		StringBuilder sb = new StringBuilder();
		sb.append("Object Create");
		sb.append("," + type);
		sb.append("," + o.hashCode());
		return printInContext(sb.toString());
	}
	
	public int registerMethodEnter(String method) {
		StringBuilder sb = new StringBuilder();
		sb.append("Entering,"+method);
		return printInContext(sb.toString());
	}
	
	public int registerMethodExit(String method) {
		StringBuilder sb = new StringBuilder();
		sb.append("Exiting,"+method);
		return printInContext(sb.toString());
	}
	
	public void setCurrentFile(String currentFile) {
		this.currentFile = currentFile;	
	}
	
	public void setCurrentClass(String currentClass) {
		this.currentClass = currentClass;	
		this.setCurrentMethod(null);
	}

	public void setCurrentMethod(String currentMethod) {
		this.currentMethod = currentMethod;
		this.setCurrentLine(0);
	}

	public void setCurrentLine(int currentLine) {
		this.currentLine = currentLine;
	}

	public void resetNames() {
		this.setCurrentClass(null);
	}
	
	public String getCurrentMethod(){
		return this.currentMethod;
	}
	public String getCurentClass(){
		return this.currentClass;
	}

}

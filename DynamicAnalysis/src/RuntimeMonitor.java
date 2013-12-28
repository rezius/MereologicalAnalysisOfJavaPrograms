
/** This class implements the methods that are called by instrumented code <b>at run-time</b>.
 * Make sure it's in your class path when running instrumented code.
 * This is a simple implementation that just prints the "hit" information to System.out.
 * @author jpower
 *
 */
public class RuntimeMonitor 
{		
	public static void hitObjectCreate(Object o)
	{		
		o = new Object();
		System.out.printf("%s",","+o.hashCode());
		System.out.println();
	}
}

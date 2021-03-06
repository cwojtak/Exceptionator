import java.io.File;
import java.util.ArrayList;

public class Main {
	
	public static ArrayList<Class<? extends Throwable>> EXCEPTIONS = new ArrayList<Class<? extends Throwable>>();
	public static int CLASSNUMS = 0;
	public static int THROWNNUM = 0;
	
	public static void main(String[] args) throws Throwable {
		populateExceptions();
		if(EXCEPTIONS.size() < 1) return;
		try {
			exceptionRecurser(EXCEPTIONS.size() - 1);
		} catch(Throwable e) {
			e.printStackTrace();
		}
		System.out.println(CLASSNUMS + " compilation units were searched; " + (EXCEPTIONS.size() - 1) + " exceptions were found for use; " + THROWNNUM + " exceptions were actually thrown.");
	}

	public static void populateExceptions() {
		//Field f;
	    try {
	        /*ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	        Class CL_class = classLoader.getClass();
	        while (CL_class != java.lang.ClassLoader.class) {
	            CL_class = CL_class.getSuperclass();
	        }
	    	//f = classLoader.getClass().getDeclaredField("classes");
	        //f.setAccessible(true);
	        //Vector<Class> classes =  (Vector<Class>) f.get(classLoader);
	        Reflections r = new Reflections("java.io", new SubTypesScanner(false));
	        Set<Class<? extends Object>> classes = r.getSubTypesOf(Object.class);*/
	    	
	    	File directory = new File(System.getProperty("java.class.path") + "\\..\\lib\\");
	        final String[] files = directory.list();
	        for (final String file : files) {
	        	checkDirectory(new File(directory, file), "", EXCEPTIONS);
	        }
	    } catch (Throwable e) {
	    	//e.printStackTrace();
	    }
}
	
	@SuppressWarnings("deprecation")
	public static void exceptionRecurser(int i) throws Throwable {
		if(i > 0) {
			try {
				exceptionRecurser(i - 1);
			} catch(Throwable e) {
				e.printStackTrace();
				THROWNNUM += 1;
			}
		}
		boolean illegal = false;
		try {
			EXCEPTIONS.get(i).newInstance();
		} catch(Throwable e) {
			illegal = true;
		}
		if(illegal) return;
		System.out.println("-----------------------------------------------------------------------------------------");
		throw EXCEPTIONS.get(i).newInstance();
	}
	
	/**
	 * Private helper method
	 * 
	 * @param directory
	 *            The directory to start with
	 * @param pckgname
	 *            The package name to search for. Will be needed for getting the
	 *            Class object.
	 * @param classes
	 *            if a file isn't loaded but still is in the directory
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void checkDirectory(File directory, String pckgname, ArrayList<Class<? extends Throwable>> classes) throws ClassNotFoundException {
	    File tmpDirectory;
	    if (directory.exists() && directory.isDirectory()) {
	        final String[] files = directory.list();
	        for (final String file : files) {
	            if (file.endsWith(".java") && !file.startsWith("module-info") && !file.startsWith("package-info")) {
	                try {
	                	try {
	                		if(pckgname.contains("sun.tools.jconsole")) continue;
	                		if(pckgname.length() > 1)
	                			if(pckgname.charAt(0) == '.') pckgname = pckgname.substring(1, pckgname.length());
	                		Class org = Class.forName(pckgname + '.' + file.substring(0, file.length() - 5));
	                		Class here = org;
	                		CLASSNUMS += 1;
		                	if(here.getSuperclass() == null) continue; //for interfaces
	        	        	while(!here.getSuperclass().equals(Object.class)) {
	        		        	if(here.getSuperclass() == Throwable.class) {
	        		        		classes.add(org);
	        		        		break;
	        		        	}
	        		        	here = here.getSuperclass();
	        	        	}
	                	} catch(ClassNotFoundException | UnsatisfiedLinkError e) {
	                	}
	                } catch (Throwable e) {
	                	//e.printStackTrace();
	                    // do nothing. this class hasn't been found by the
	                    // loader, and we don't care.
	                }
	            } else if ((tmpDirectory = new File(directory, file)).isDirectory()) {
	            	checkDirectory(tmpDirectory, pckgname + "." + file, classes);
	            }
	        }
	    }
	}
}

package app;

public class Main {
	static App app;
	static String databasePath = "database.db"; // Sqlite database name/path.
	static boolean isGUI = true; // Whether the application will start in GUI mode or Console mode.
	public static void main(String[] args) {
		// Parse arguments passed in.
		for(String s:args) {
			// If there is a "c" flag then start in Console mode. 
			if(s.equals("-c"))
				isGUI = false;
		}
		
		// Choose which object to instantiate.
		if(isGUI) {
			app = new GuiApp(databasePath);
		}
		else {
			app = new ConsoleApp(databasePath);
		}
		
		// Run the application.
		app.run();
	}

}

package app;

import java.io.File;

public abstract class App extends DatabaseManager {
	// Will hold ClientManager instance.
	protected IManager client;
	// This will hold currently logged in account.
	protected Teammate loggedAccount;
	
	App(String databasePath) {
		// Set up database manager.
		super(databasePath);
		// This will hold the loaded teammates from database.
		Teammate[] teammates = null;
		
        // Check if the specified database exists or not.
		File f = new File(databasePath);  
        if (f.exists()) {
        	// Load teammates from database if available.
        	System.out.println("Sqlite Database \"" + databasePath + "\" exists. Loading database..." );
        	teammates = loadDatabase();
        } 
        else {
        	// Create database otherwise.
        	System.out.println("Sqlite Database \"" + databasePath + "\" does not exist. Creating database...");
        	createDatabase();
        }
        
        // Set up ClientManager instance.
        client = new ClientManager(teammates, lastUserId);
	}
	
	// This will be defined in ConsoleApp and GuiApp.
	abstract void run();
	
	// Save database before application closes.
	void close() {
		// Get the available teammates from client and save to sqlite database.
		saveDatabase(((ClientManager) client).getTeammates());
	}
}

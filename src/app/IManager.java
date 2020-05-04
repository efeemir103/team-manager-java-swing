package app;

import java.util.ArrayList;

public interface IManager {
	// Will be used to instantiate Teammate array to maximum size in child classes.
	int maxTeammates = 100;
	
	// Adds a teammate to system that is being managed.
	void addTeammate(Teammate t);
	// Removes a teammate from system that is being managed.
	void removeTeammate(Teammate t);
	// Assigns a task to a teammate in system that is being managed.
	void assignTask(Task t);
	// Removes a task from a teammate in system that is being managed.
	void removeTask(Task t);
	// Returns array of tasks of teammate "t".
	ArrayList<Task> getTasks(Teammate t);
}

package app;

import java.util.ArrayList;

public class Teammate {
	private int id;
	private String name;
	private String username;
	private String password;
	private boolean isAssignor;
	private ArrayList<Task> tasks; // Array of tasks assigned to this intance Teammate.
	private int taskNumber;
	 
	Teammate(int id, String name, String username, String password) {
		this.id = id;
		this.name = name;
		this.username = username;
		this.password = password;
		// Initialize assigned tasks to 0.
		tasks = new ArrayList<Task>();
		taskNumber = 0;
	}
		
	int getId() {
		return id;
	}

	void setId(int id) {
		this.id = id;
	}



	String getName() {
		return name;
	}

	void setName(String s) {
		name = s;
	}
	
	String getUsername() {
		return username;
	}

	void setUsername(String username) {
		this.username = username;
	}

	String getPassword() {
		return password;
	}

	void setPassword(String password) {
		this.password = password;
	}

	boolean isAssignor() {
		return isAssignor;
	}

	void setAssignor(boolean isAssignor) {
		this.isAssignor = isAssignor;
	}

	ArrayList<Task> getTasks() {
		return tasks;
	}
	
	// Assign a task to this instance.
	void addTask(Task task) throws TaskException {
		if(tasks.contains(task)) {
			// Do not add same task to task array.
			// Throws custom error.
			throw new TaskException("Task already exists.");
		} else {
			// Otherwise add it.
			tasks.add(task);
			taskNumber++;
		}
	}

	// Remove a task from this instance.
	void removeTask(Task task) throws TaskException {
		if(tasks.contains(task)) {
			// Task has to be in array to be removed.
			tasks.remove(task);
			taskNumber--;
		} else {
			// Otherwise throw custom error.
			throw new TaskException("Task is not on the list.");
		}
	}

	int getTaskNumber() {
		return taskNumber;
	}

	void setTaskNumber(int taskNumber) {
		this.taskNumber = taskNumber;
	}
}

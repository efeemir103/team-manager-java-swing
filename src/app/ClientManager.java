package app;

import java.util.ArrayList;

public class ClientManager implements IManager {
	// To give incremental id to every teammate we have to store the last id.
	protected int lastTeammateId;
	// Store all teammates available.
	private Teammate[] teammates;
	
	ClientManager(Teammate[] teammates, int lastIndex) {
		if(teammates == null) {
			// If teammate is empty then just instantiate array and set first element to null.
			this.teammates = new Teammate[maxTeammates];
			this.teammates[0] = null;
		} else {
			// Otherwise assign passed in array.
			this.teammates = teammates;
		}
		this.lastTeammateId = lastIndex;
	}
	
	@Override
	public void addTeammate(Teammate t) {
		// Add new teammate then increment last teammate id.
		teammates[lastTeammateId++] = t;  
	}
	
	@Override
	public void removeTeammate(Teammate t) {
		// Remove the teammate passed in from array.
		if(lastTeammateId == t.getId()) {
			// If the lastTeammate is removing then just set the last element null and decrement last id.
			teammates[--lastTeammateId] = null;
		} else {
			// Otherwise set the teammate with same id to null.
			int last = maxTeammates;
			for(int i = 0;i < maxTeammates;i++) {
				if(teammates[i].getId() == t.getId()) {
					last = i;
					break;
				}
			}
			// Then shift all teammates to fill emptied element.
			for(int i=last;i < maxTeammates;i++) {
				if(teammates[i] == null) {
					break;
				}
				else {
					teammates[i] = teammates[i+1];
				}
			}
		}
	}
	
	@Override
	public void assignTask(Task t) {
		// Assign a task to corresponding teammate.
		int id = t.getAssigneeId();
		for(int i = 0;i < maxTeammates; i++) {
			// Avoid empty elements.
			if(teammates[i] != null) {
				if(id == teammates[i].getId()) {
					try {
						teammates[i].addTask(t);
					} catch (TaskException e) {
						e.printStackTrace();
					}
					break;
				}
			}
		}
	}
	
	@Override
	public void removeTask(Task t) {
		// Remove task from corresponding teammate.
		int id = t.getAssigneeId();
		for(int i = 0;i < maxTeammates; i++) {
			// Avoid empty elements.
			if(teammates[i] != null) {
				if(id == teammates[i].getId()) {
					try {
						teammates[i].removeTask(t);
					} catch (TaskException e) {
						e.printStackTrace();
					}
					break;
				}
			}
		}
	}
	
	@Override
	public ArrayList<Task> getTasks(Teammate t) {
		// Get tasks of teammate.
		return t.getTasks();
	}
	
	public boolean usernameExists(String username) {
		// Search for username. If found return true. Otherwise false.
		for(int i = 0;i < maxTeammates;i++) {
			if(teammates[i] != null) {
				if(teammates[i].getUsername().equals(username)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public Teammate getTeammate(String username) {
		// Get the teammate instance with same username from array.
		for(int i = 0;i < maxTeammates; i++) {
			if(teammates[i] != null) {
				if(teammates[i].getUsername().equals(username)) {
					return teammates[i];
				}	
			}
		}
		return null;
	}
	
	public Teammate getTeammate(int id) {
		// Find the teammate with same id.
		for(int i = 0;i < maxTeammates; i++) {
			if(teammates[i] != null) {
				if(teammates[i].getId() == id) {
					return teammates[i];
				}	
			}
		}
		return null;
	}
	
	public boolean containsTeammate(Teammate t) {
		// Check if the teammate with same id exists.
		for(int i = 0;i < maxTeammates; i++) {
			if(teammates[i] != null) {
				if(teammates[i].getId() == t.getId()) {
					return true;
				}	
			}
		}
		return false;
	}
	
	public int getLastTaskId(Teammate t) {
		// Get the last task id of spesific teammate.
		int max = 0;
		for(Task task: t.getTasks()) {
			if(task.getId() > max) {
				max = task.getId();
			}
		}
		return max;
	}
	
	public Teammate[] getTeammates() {
		// Get the array of teammates.
		return teammates;
	}
}

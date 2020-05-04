package app;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class ConsoleApp extends App {
	private enum state{
		Menu,
		Create,
		Login,
		User,
		Assignor,
		Close
	};
	
	// State which app is in.
	private state stateOfApp;
	
	// Global console input.
	private Scanner input;
	
	ConsoleApp(String databasePath) {
		// Set up as a application first.
		super(databasePath);
		
		// Initialize attributes.
		stateOfApp = state.Menu;
		input = new Scanner(System.in);
	}
	
	private void menu() {
		// Menu Screen.
		while(stateOfApp == state.Menu) {
			System.out.print("Please Login or Create account: (Enter \"L\" for login, \"C\" for create account)\n"
						+    "Enter \"q\" or \"Q\" to quit the app.\t"
					);
			char answer;
			answer = input.nextLine().charAt(0);
			
			if(answer == 'C' || answer == 'c') {
				stateOfApp = state.Create;
			} else if(answer == 'L' || answer == 'l') {
				stateOfApp = state.Login;
			} else if(answer == 'Q' || answer == 'q') {
				stateOfApp = state.Close;
			} else {
				System.out.println("Option \"" + answer + "\" does not exist.");
			}
		}
	}
	
	private void create() {
		// Create Account Screen.
		String username, password, name;
		boolean assignor;
		do {
			System.out.print("Username:\t");
			username = input.nextLine();
		} while(((ClientManager) client).usernameExists(username));
		System.out.print("Password:\t");
		password = input.nextLine();
		do {
			System.out.print("Password Again:\t");
		} while(!input.nextLine().equals(password));
		System.out.print("Name:\t");
		name = input.nextLine();
		System.out.print("Are you a assignor: (Enter \"Y\" for yes \"N\" for no)\t");
		char answer = input.nextLine().charAt(0);
		assignor = (answer == 'Y' ||  answer == 'y') ? true: false;
		
		Teammate t = new Teammate(lastUserId, name, username, password);
		t.setAssignor(assignor);
		
		client.addTeammate(t);
		addTeammate(t);
		
		System.out.println("Account Created now you can login:");
		stateOfApp = state.Login;
	}
	
	private void login() {
		// Login Account Screen.
		String username, password;
		System.out.print("Username:\t");
		username = input.nextLine();
		Teammate t = ((ClientManager) client).getTeammate(username);
		
		if(t != null) {
			System.out.print("Password:\t");
			password = input.nextLine();
			if(password.equals(t.getPassword())) {
				System.out.println("Logged in as \"" + t.getName() + "\"");
				loggedAccount = t;
				stateOfApp = t.isAssignor() ? state.Assignor: state.User;
			} else {
				System.out.println("Password incorrect. Try again later.");
			}
		} else {
			System.out.println("User \"" + username + "\" does not exist.");
			stateOfApp = state.Menu;
		}
	}
	
	private void user() {
		// When a normal user is logged in.
		System.out.println("=====================================================\n"
						+  "Please enter a command:\n"
						+  "logout\n"
						+  "complete -- Complete a task assigned to you.\n"
						+  "change -- Change importance of a task assigned to you.\n"
						+  "show -- Show all tasks assigned to you in order of importance.\n"
						+  "nearby -- Show all tasks assigned to you in order of closest.\n"
						+  "help -- Print this information."
				);
		
		// User Screen should always show while user is logged in.
		while(stateOfApp == state.User) {
			System.out.println("=====================================================");
			ArrayList<Task> tasks = loggedAccount.getTasks();
			Task selected;
			String answer;
			int id;
			answer = input.nextLine();
			switch(answer) {
				case "logout":
					// Logging out.
					loggedAccount = null;
					stateOfApp = state.Menu;
					break;
				case "complete":
					// Completing a task.
					System.out.println("Enter the id of the task you want to complete and remove:");
					for(Task t: tasks) {
						System.out.println("Id: "+ t.getId() + " Description: " + t.getDescription() + " Deadline: " + t.getDeadline() 
										+ " Importance: " + t.getImportance());
					}
					id = input.nextInt();
					input.nextLine();
					selected = null;
					for(Task t: tasks) {
						if(t.getId() == id) {
							selected = t;
						}
					}
					if(selected != null) {
						
						client.removeTask(selected);
						removeTask(selected);
					} else {
						System.out.println("Task with id:" + id + " does not exist.");
					}
					break;
				case "change":
					// Changing a task importance.
					System.out.println("Enter the id of the task you want to update importance of:");
					for(Task t: tasks) {
						System.out.println("Id: "+ t.getId() + " Description: " + t.getDescription() + " Deadline: " + t.getDeadline() 
										+ " Importance: " + t.getImportance());
					}
					id = input.nextInt();
					input.nextLine();
					selected = null;
					for(Task t: tasks) {
						if(t.getId() == id) {
							selected = t;
						}
					}
					if(selected != null) {
						short importance;
						System.out.println("Enter the new importance");
						importance = input.nextShort();
						input.nextLine();
						client.removeTask(selected);
						selected.setImportance(importance);
						client.assignTask(selected);
						assignTask(selected);
					} else {
						System.out.println("Task with id:" + id + " does not exist.");
					}
					break;
				case "show":
					// Show tasks in importance order.
					tasks.sort(Task.ImportanceComparator);
					System.out.println("--- Tasks ---");
					for(Task t: tasks) {
						System.out.println(t.getDescription() + "\nDeadline: " + t.getDeadline() 
										+ "\nImportance: " + t.getImportance() + "\n");
					}
					break;
				case "nearby":
					// Show tasks in deadline time closest order.
					tasks.sort(Task.DeadlineComparator);
					System.out.println("--- Tasks ---");
					for(Task t: tasks) {
						System.out.println(t.getDescription() + "\nDeadline: " + t.getDeadline() 
										+ "\nImportance: " + t.getImportance() + "\n");
					}
					break;
				case "help":
					// Show help if needed.
					System.out.println("=====================================================\n"
									+  "Please enter a command:\n"
									+  "logout\n"
									+  "complete -- Complete a task assigned to you.\n"
									+  "change -- Change importance of atask assigned to you.\n"
									+  "show -- Show all tasks assigned to you in order of importance.\n"
									+  "nearby -- Show all tasks assigned to you in order of closest.\n"
									+  "help -- Print this information."
							);
					break;
				default:
					// Show help if unknown command entered.
					System.out.println("Command \"" + answer + "\" does not exist. Try this ones:\n"
									+  "=====================================================\n"
									+  "Please enter a command:\n"
									+  "logout\n"
									+  "complete -- Complete a task assigned to you.\n"
									+  "change -- Change importance of atask assigned to you.\n"
									+  "show -- Show all tasks assigned to you in order of importance.\n"
									+  "nearby -- Show all tasks assigned to you in order of closest.\n"
									+  "help -- Print this information."
					);
			}
			
		}
	}
	
	void assignor() {
		// When a assignor user is logged in.
		System.out.println("=====================================================\n"
				+  "Please enter a command:\n"
				+  "logout\n"
				+  "complete -- Complete a task assigned to you.\n"
				+  "change -- Change importance of a task assigned to you.\n"
				+  "show -- Show all tasks assigned to you in order of importance.\n"
				+  "nearby -- Show all tasks assigned to you in order of closest.\n"
				+  "help -- Print this information.\n"
				+  "Assignee Commands:\n"
				+  "assign -- Assign some teammate a job.\n"
				+  "remove -- Remove a teammate."
		);

		// Assignor Screen should always show while assignor is logged in.
		while(stateOfApp == state.Assignor) {
			System.out.println("=====================================================");
			ArrayList<Task> tasks = loggedAccount.getTasks();
			Teammate[] teammates = ((ClientManager) client).getTeammates();
			Teammate selectedTeammate;
			Task selected;
			String answer, description, deadline;
			int id, taskId;
			short importance;
			answer = input.nextLine();
			switch(answer) {
				case "logout":
					// Logging out.
					loggedAccount = null;
					stateOfApp = state.Menu;
					break;
				case "complete":
					// Completing a task.
					System.out.println("Enter the id of the task you want to complete and remove:");
					for(Task t: tasks) {
						System.out.println("Id: "+ t.getId() + " Description: " + t.getDescription() + " Deadline: " + t.getDeadline() 
										+ " Importance: " + t.getImportance());
					}
					id = input.nextInt();
					input.nextLine();
					selected = null;
					for(Task t: tasks) {
						if(t.getId() == id) {
							selected = t;
						}
					}
					if(selected != null) {
						
						client.removeTask(selected);
						removeTask(selected);
					} else {
						System.out.println("Task with id:" + id + " does not exist.");
					}
					break;
				case "change":
					// Changing a task importance.
					System.out.println("Enter the id of the task you want to update importance of:");
					for(Task t: tasks) {
						System.out.println("Id: "+ t.getId() + " Description: " + t.getDescription() + " Deadline: " + t.getDeadline() 
										+ " Importance: " + t.getImportance());
					}
					id = input.nextInt();
					input.nextLine();
					selected = null;
					for(Task t: tasks) {
						if(t.getId() == id) {
							selected = t;
						}
					}
					if(selected != null) {
						System.out.println("Enter the new importance");
						importance = input.nextShort();
						input.nextLine();
						client.removeTask(selected);
						selected.setImportance(importance);
						client.assignTask(selected);
						assignTask(selected);
					} else {
						System.out.println("Task with id:" + id + " does not exist.");
					}
					break;
				case "show":
					// Show tasks in importance order.
					tasks.sort(Task.ImportanceComparator);
					for(Task t: tasks) {
						System.out.println(t.getDescription() + "\nDeadline: " + t.getDeadline() 
										+ "\nImportance: " + t.getImportance());
					}
					break;
				case "nearby":
					// Show tasks in deadline time closest order.
					tasks.sort(Task.DeadlineComparator);
					for(Task t: tasks) {
						System.out.println(t.getDescription() + "\nDeadline: " + t.getDeadline() 
										+ "\nImportance: " + t.getImportance());
					}
					break;
				case "help":
					// Show help if needed.
					System.out.println("=====================================================\n"
									+  "Please enter a command:\n"
									+  "logout\n"
									+  "complete -- Complete a task assigned to you.\n"
									+  "change -- Change importance of atask assigned to you.\n"
									+  "show -- Show all tasks assigned to you in order of importance.\n"
									+  "nearby -- Show all tasks assigned to you in order of closest.\n"
									+  "help -- Print this information."
									+  "Assignee Commands:\n"
									+  "assign -- Assign some teammate a job.\n"
									+  "remove -- Remove a teammate."
							);
					break;
				case "assign":
					// Assign a task to a teammate.
					System.out.println("Enter the id of the teammate you want to assign job to:");
					for(int i=0;i < maxTeammates;i++) {
						if(teammates[i] == null) {
							continue;
						}
						System.out.println("Id: " + teammates[i].getId() + " Name: " + teammates[i].getName() 
									+ " Username: " + teammates[i].getUsername() 
									+ " Number of Tasks Assigned: " + teammates[i].getTaskNumber());
					}
					
					id = input.nextInt();
					input.nextLine();
					
					selectedTeammate = ((ClientManager) client).getTeammate(id);
					if(selectedTeammate != null) {
						taskId = ((ClientManager) client).getLastTaskId(((ClientManager) client).getTeammate(id)) + 1;
						System.out.print("Description of task:\t");
						description = input.nextLine();
						DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");
						System.out.print("Deadline of task: (In format like " + dtf.format(LocalDateTime.now()) + ")\t");
						deadline = input.nextLine();
						System.out.print("Importance of task: (An integer)\t");
						importance = input.nextShort();
						input.nextLine();
						
						client.assignTask(new Task(taskId, description, deadline, importance, id));
						assignTask(new Task(taskId, description, deadline, importance, id));
						System.out.println("Task assigned successfully.");
					} else {
						System.out.println("Teammate with id:" + id + " does not exist.");
					}
					break;
				case "remove":
					// Remove a teammate.
					System.out.println("Enter the id of the teammate you want to remove:");
					for(int i=0;i < maxTeammates;i++) {
						if(teammates[i] == null) {
							continue;
						}
						System.out.println("Id: " + teammates[i].getId() + "\tName: " + teammates[i].getName() 
									+ "\tUsername: " + teammates[i].getUsername() 
									+ "\tNumber of Tasks Assigned: " + teammates[i].getTaskNumber());
					}
					id = input.nextInt();
					input.nextLine();
					selectedTeammate = new Teammate(id, "", "", "");
					client.removeTeammate(selectedTeammate);
					removeTeammate(selectedTeammate);
					break;
				default:
					// Show help if unknown command entered.
					System.out.println("Command \"" + answer + "\" does not exist. Try this ones:\n"
									+  "=====================================================\n"
									+  "Please enter a command:\n"
									+  "logout\n"
									+  "complete -- Complete a task assigned to you.\n"
									+  "change -- Change importance of atask assigned to you.\n"
									+  "show -- Show all tasks assigned to you in order of importance.\n"
									+  "nearby -- Show all tasks assigned to you in order of closest.\n"
									+  "help -- Print this information."
									+  "Assignee Commands:\n"
									+  "assign -- Assign some teammate a job.\n"
									+  "remove -- Remove a teammate."
					);
			}
			
		}
	}
	
	void run() {
		// The main run loop.
		System.out.println("\n\nWelcome to Teammate  Manager\n"
				+  "====================================================="
		);
		// As long as app state is not close loop should run. Otherwise "close()" function handles exiting app.  
		while(true) {
			switch(stateOfApp) {
				case Menu: 
					menu();
					break;
				case Create:
					create();
					break;
				case Login:
					login();
					break;
				case User:
					user();
					break;
				case Assignor:
					assignor();
					break;
				case Close:
					close();
					break;
			}
		}
	}
	
	void close() {
		// Close console input.
		input.close();
		// Save database.
		super.close();
		// Exit the process.
		System.exit(0);
	}
}

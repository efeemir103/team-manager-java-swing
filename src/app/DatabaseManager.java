package app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DatabaseManager implements IManager {
	// This will hold the max id of teammates.
	protected int lastUserId;
	private String databaseName;
	// Global connection to sqlite database.
	private Connection connection;
	
	DatabaseManager(String databaseName) {
		// Set the name of database, set the connection to null.
		connection = null;
		this.databaseName = databaseName;
		// Assume there is no teammate.
		lastUserId = 0;
	}
	
	protected void createDatabase() {
		// Initialize tables in sqlite.
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + databaseName);
			Statement statement = connection.createStatement();
	        
			// Table that will hold the users.
	        statement.execute("CREATE TABLE IF NOT EXISTS users(\n"
					+ "    id integer PRIMARY KEY,\n"
	                + "    name text NOT NULL,\n"
	                + "    username text NOT NULL,\n"
	                + "    password text NOT NULL,\n"
	                + "    isAssignor integer DEFAULT 0,\n"
	                + "    taskNumber integer DEFAULT 0\n"
	                + ");");
			// Table that will hold the tasks.	        
	        statement.execute("CREATE TABLE IF NOT EXISTS tasks(\n"
					+ "    id integer PRIMARY KEY,\n"
	                + "    description text NOT NULL,\n"
	                + "    deadline text,\n"
	                + "    importance integer DEFAULT 0,\n"
	                + "    assignee integer NOT NULL\n"
	                + ");");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(connection != null) {
	        		connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	protected Teammate[] loadDatabase() {
		// Load database to "users" array that will be returned after.
		Teammate[] users = new Teammate[maxTeammates];
		int i = 0;
        try {
            // Create a connection to the database.
            connection = DriverManager.getConnection("jdbc:sqlite:" + databaseName);
            
            Statement statement = connection.createStatement();
            
            ResultSet usersTable = statement.executeQuery("SELECT * FROM users");
            ResultSet tasks;
            
            int id;
            String name;
            String username;
            String password;
            boolean isAssignor;
            int taskNumber;
            
            // First get all users from table.
            while(usersTable.next()) {
            	users[i] = null;
            	id = usersTable.getInt("id");
            	name = usersTable.getString("name");
            	username = usersTable.getString("username");
            	password = usersTable.getString("password");
            	isAssignor = (usersTable.getInt("isAssignor") == 1) ? true : false;
            	taskNumber = usersTable.getInt("taskNumber");
            	Teammate t = new Teammate(id, name, username, password);
            	t.setAssignor(isAssignor);
            	t.setTaskNumber(taskNumber);
            	if(id >= lastUserId) {
            		lastUserId = id+1;
            	}
            	users[i++] = t;
            }
            
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM tasks WHERE assignee = ?");
            
            i--;
            // Then get tasks assigned to them from tasks table in database.
            for(;i>=0;i--) {
            	id = users[i].getId();
            	preparedStatement.setInt(1, id);
            	tasks = preparedStatement.executeQuery();
            	
            	while(tasks.next()) {
            		try{
            			
            			users[id].addTask((new Task(tasks.getInt("id"),
                				tasks.getString("description"), 
                				tasks.getString("deadline"),
                				(short) tasks.getInt("importance"),
                				tasks.getInt("assignee")
                				)));	
            		} catch(TaskException e) {
            			e.printStackTrace();
            		}
            	}
            }
            
            System.out.println("Data has been successfully loaded from SQLite database \""+ databaseName +"\".");
            
        } catch (SQLException e) {
			e.printStackTrace();
        } finally {
        	try {
				if(connection != null) {
	        		connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
        
        // Return null if the first element of loaded array is null.
        return (users[0] != null) ? users:null;
	}
	
	protected void saveDatabase(Teammate[] users) {
		// Save everything in passed array to database.
		String[] sql = {"REPLACE INTO users VALUES(?,?,?,?,?,?)", 
		"REPLACE INTO tasks VALUES(?,?,?,?,?)"};
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + databaseName);
			PreparedStatement preparedStatement = connection.prepareStatement(sql[0]);
			
			// First just update users table.
	        for(int i=0;i < maxTeammates;i++) {
	        	if(users[i] == null) {
	        		continue;
	        	}
	        	
	        	preparedStatement.setInt(1, users[i].getId());
	        	preparedStatement.setString(2, users[i].getName());
	        	preparedStatement.setString(3, users[i].getUsername());
	        	preparedStatement.setString(4, users[i].getPassword());
	            preparedStatement.setInt(5, users[i].isAssignor() ? 1:0);
	            preparedStatement.setInt(6, users[i].getTaskNumber());
	            preparedStatement.executeUpdate();
	        }
	        
	        preparedStatement = connection.prepareStatement(sql[1]);
	        
	        // Then update tasks table.
	        for(int i=0;i < maxTeammates;i++) {
	        	if(users[i] == null) {
	        		continue;
	        	}
	        	
	        	for(Task task:users[i].getTasks()) {
	            	preparedStatement.setInt(1, task.getId());
	            	preparedStatement.setString(2, task.getDescription());
	            	preparedStatement.setString(3, task.getDeadline());
	            	preparedStatement.setInt(4, task.getImportance());
	                preparedStatement.setInt(5, task.getAssigneeId());
	            }
	        }
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(connection != null) {
	        		connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	@Override
	public void addTeammate(Teammate t) {
		// Add just one teammate to database.
		String[] sql = {"REPLACE INTO users VALUES(?,?,?,?,?,?)", 
				"REPLACE INTO tasks VALUES(?,?,?,?,?)"};
        try {
        	connection = DriverManager.getConnection("jdbc:sqlite:" + databaseName);
        	PreparedStatement preparedStatement = connection.prepareStatement(sql[0]);
        	preparedStatement.setInt(1, t.getId());
        	preparedStatement.setString(2, t.getName());
        	preparedStatement.setString(3, t.getUsername());
        	preparedStatement.setString(4, t.getPassword());
            preparedStatement.setInt(5, t.isAssignor() ? 1:0);
            preparedStatement.setInt(6, t.getTaskNumber());
            preparedStatement.executeUpdate();
            
            // Add assigned tasks to database too.
            for(Task task:t.getTasks()) {
            	preparedStatement = connection.prepareStatement(sql[1]);
            	preparedStatement.setInt(1, task.getId());
            	preparedStatement.setString(2, task.getDescription());
            	preparedStatement.setString(3, task.getDeadline());
            	preparedStatement.setInt(4, task.getImportance());
                preparedStatement.setInt(5, task.getAssigneeId());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
			try {
				if(connection != null) {
	        		connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void removeTeammate(Teammate t) {
		String[] sql = {"DELETE FROM users WHERE id = ?",
				"DELETE FROM tasks WHERE assignee = ?"
		};
		 
        try {
        	// Remove a teammate from database.
        	connection = DriverManager.getConnection("jdbc:sqlite:" + databaseName);
        	PreparedStatement preparedStatement = connection.prepareStatement(sql[0]);
 
            // Set the corresponding parameter.
            preparedStatement.setInt(1, t.getId());
            // Execute the delete statement.
            preparedStatement.executeUpdate();
            
            // Remove corresponding tasks too.
            preparedStatement = connection.prepareStatement(sql[1]);
            
            // Set the corresponding parameter.
            preparedStatement.setInt(1, t.getId());
            // Execute the delete statement.
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
			try {
				if(connection != null) {
	        		connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void assignTask(Task t) {
		// Add a task to table. 
		String sql = "REPLACE INTO tasks VALUES(?,?,?,?,?)";
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + databaseName);
			PreparedStatement preparedStatement;
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, t.getId());
	    	preparedStatement.setString(2, t.getDescription());
	    	preparedStatement.setString(3, t.getDeadline());
	    	preparedStatement.setInt(4, t.getImportance());
	        preparedStatement.setInt(5, t.getAssigneeId());
	        preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(connection != null) {
	        		connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void removeTask(Task t) {
		// Remove one task from table.
		String sql = "DELETE FROM tasks WHERE id = ?";
		 try {
			 // Create a connection to the database.
	            connection = DriverManager.getConnection("jdbc:sqlite:" + databaseName);
	            PreparedStatement preparedStatement = connection.prepareStatement(sql);
	            preparedStatement.setInt(1, t.getId());
	 
	            // Execute the delete statement.
	            preparedStatement.executeUpdate();
	 
	        } catch (SQLException e) {
	            e.printStackTrace();
	        } finally {
				try {
					if(connection != null) {
		        		connection.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
	}
	
	@Override
	public ArrayList<Task> getTasks(Teammate t) {
		// Get tasks assigned to spesific teammate from database.
		ArrayList<Task> result = new ArrayList<Task>();
		String sql = "SELECT FROM tasks WHERE assignee = ?";
		try {
			// Create a connection to the database
	        connection = DriverManager.getConnection("jdbc:sqlite:" + databaseName);
	        
	        PreparedStatement preparedStatement = connection.prepareStatement(sql);
	        preparedStatement.setInt(1, t.getId());
	        
	        ResultSet tasks = preparedStatement.executeQuery();
	        while(tasks.next()) {
	        	result.add(new Task(tasks.getInt("id"),
        				tasks.getString("description"), 
        				tasks.getString("deadline"),
        				(short) tasks.getInt("importance"),
        				tasks.getInt("assignee")
        				));
	        }
	        
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(connection != null) {
	        		connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		if(result.isEmpty()) {
        	return null;
        } else {
        	return result;
        }
	}
}

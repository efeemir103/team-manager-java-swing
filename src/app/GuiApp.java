package app;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableModel;

public class GuiApp extends App {
	// Global color palette.
	private final int darkBlueColor = 0x0097a7;
	private final int blueColor = 0x00bcd4;
	private final int lightBlueColor = 0xb2ebf2;
	private final int whiteColor = 0xffffff;
	private final int dirtyWhiteColor = 0xf6f6f6;
	private final int backgroundColor = 0xd8d8d8;
	private final int orangeColor = 0xff5722;
	private final int lightOrangeColor = 0xff6332;
	private final int greyColor = 0x757575;
	private final int lightGreyColor = 0x838383;
	private final int redColor = 0xdc1a3c;
	private final int fumeWhite = 0xe0e0e0;
	
	// Get the default system font. Will be used as global font for every GUI element.
	private Font font = new JLabel().getFont(); 
	
	// Main window.
	private JFrame frame = new JFrame();
	
	// CardLayout panels.
	private JPanel views = new JPanel();
	private JPanel menuView = new JPanel();
	private JPanel createView = new JPanel();
	private JPanel loginView = new JPanel();
    
	// Setting a buttons UI look, colors.
	private void setBtnUI(JButton btn, int backgroundColor, int foregroundColor, int fontSize, String iconPath, int hoverColor) {
		// Set colors.
		btn.setBackground(new Color(backgroundColor));
		btn.setForeground(new Color(foregroundColor));
		// Set font size by deriving it.
		btn.setFont(btn.getFont().deriveFont(fontSize));
		// Set a icon for button.
		btn.setIcon(new ImageIcon(iconPath));
		// Hovering effect.
		btn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent me) {
				btn.setBackground(new Color(hoverColor));
			}
			
			@Override
			public void mouseExited(MouseEvent me) {
				btn.setBackground(new Color(backgroundColor));
			}
		});
	}
	
	// A nested class for the layout when a normal teammate logins.
	private class UserView extends JPanel {
		private static final long serialVersionUID = -5907052262175253840L;

		// List of tasks assigned to logged user.
		protected ArrayList<Task> tasks;
		
		// Will be shown on top left corner of app. Name of logged teammate.
		protected JLabel nameLabel = new JLabel();
		
		// Action buttons related to logged user.
		protected JButton logoutBtn = new JButton("Logout");

		// Remove selected task from table.
		protected JButton completeBtn = new JButton("Complete Task");
		
		// Change importance of selected task from table.
		protected JButton changeBtn = new JButton("Change Importance");
		
		// Sort tasks on table by either importance or closest (deadline).   
		protected JButton sortBtn = new JButton("Sort by Closest");

		// Will hold action buttons on a horizontal layout.
		protected JPanel editButtons = new JPanel();
		
		// Used for JTable. To make every cell non-editable, the method "isCellEditable" is overridden.
		protected DefaultTableModel model = new DefaultTableModel(new String[] {
				"Task Id", "Description", "Deadline", "Importance"
		}, 0) {
			private static final long serialVersionUID = -8290888012737743114L;

			@Override
		    public boolean isCellEditable(int row, int column) {
		       return false;
		    }
		};
		
		// Create the JTable for tasks.
		protected JTable tasksTable = new JTable(model);
		
		UserView() {
			nameLabel.setIcon(new ImageIcon("icons/outline_account_circle_white.png"));
			
			nameLabel.setOpaque(true);
			nameLabel.setBackground(new Color(lightBlueColor));
			nameLabel.setForeground(new Color(whiteColor));
			nameLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			
			setBtnUI(logoutBtn, greyColor, whiteColor, 12, "icons/outline_exit_to_app_white.png", lightGreyColor);
			
			setBtnUI(completeBtn, orangeColor, whiteColor, 12, "icons/outline_done_white.png", lightOrangeColor);
			
			setBtnUI(changeBtn, greyColor, whiteColor, 12, "icons/outline_edit_white.png", lightGreyColor);
			
			setBtnUI(sortBtn, orangeColor, whiteColor, 12, "icons/outline_import_export_white.png", lightOrangeColor);
			
			logoutBtn.addActionListener(e -> {
				cards.show(views, "menu");
				loggedAccount = null;
			});
			
			tasksTable.setBackground(new Color(fumeWhite));
			
			setBackground(new Color(backgroundColor));
			
			completeBtn.addActionListener(e -> {
				int row = tasksTable.getSelectedRow();
				if(row != -1) {
					int id = (int) model.getValueAt(row, 0);
					ArrayList<Task> tasks = loggedAccount.getTasks();
					Task selected = null;
					for(Task t: tasks) {
						if(t.getId() == id) {
							selected = t;
						}
					}
					if(selected != null) {
						
						model.removeRow(row);
						client.removeTask(selected);
						removeTask(selected);
					}	
				}
			});
			
			changeBtn.addActionListener(e -> {
				int row = tasksTable.getSelectedRow();
				if(row != -1) {
					int id = (int) model.getValueAt(row, 0);
					ArrayList<Task> tasks = loggedAccount.getTasks();
					Task selected = null;
					for(Task t: tasks) {
						if(t.getId() == id) {
							selected = t;
						}
					}
					if(selected != null) {
						String answer = JOptionPane.showInputDialog(frame,"Enter new importance:");
						if(answer != "") {
							short importance = Short.parseShort(answer);
							model.setValueAt(importance, row, 3);
							client.removeTask(selected);
							selected.setImportance(importance);
							client.assignTask(selected);
							assignTask(selected);
						}
					}	
				}
			});
			
			sortBtn.addActionListener(e -> {
				String s = sortBtn.getText();
				if(s.equals("Sort by Closest")) {
					tasks.sort(Task.DeadlineComparator);
					sortBtn.setText("Sort by Importance");
				}
				if(s.equals("Sort by Importance")) {
					tasks.sort(Task.ImportanceComparator);
					sortBtn.setText("Sort by Closest");
				}
				
				model.setRowCount(0);
				for(Task t: tasks) {
					model.addRow(new Object[] {
						t.getId(), t.getDescription(), t.getDeadline(), t.getImportance()
					});
				}
			});
			
			JPanel userCard = new JPanel();
			userCard.add(nameLabel);
			userCard.add(logoutBtn);
			userCard.setLayout(new FlowLayout(FlowLayout.LEFT, 50, 0));
			userCard.setBackground(new Color(backgroundColor));
			
			editButtons.add(completeBtn);
			editButtons.add(changeBtn);
			editButtons.add(sortBtn);
			editButtons.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 0));
			editButtons.setBackground(new Color(backgroundColor));
			
			setLayout(new BorderLayout(20, 20));
			add(userCard, BorderLayout.NORTH);
			add(new JScrollPane(tasksTable), BorderLayout.CENTER);
			add(editButtons, BorderLayout.SOUTH);
		}
		
		// Will be used to refresh model whenever needed. (When new user logins, model changes.)
		public void render() {
			nameLabel.setText(loggedAccount.getName());
			tasks = ((ClientManager) client).getTasks(loggedAccount);
			tasks.sort(Task.ImportanceComparator);
			model.setRowCount(0); // Empty the table.
			
			// Build the table from tasks assigned to logged account.
			for(Task t: tasks) {
				model.addRow(new Object[] {
					t.getId(), t.getDescription(), t.getDeadline(), t.getImportance()
				});
			}
		}
	}
	
	// A nested class for the layout when a assignor teammate logins.
	// All the normal user functionality except there is actions such as:
	// - Assigning a teammate a task.
	// - Removing a teammate.
	// This is why it extends UserView.
	private class AssignorView extends UserView {
		private static final long serialVersionUID = 5419985131438157861L;
		
		// Holds all of the teammates available on the application.
		private Teammate[] teammates;

		// This buttons are for the mentioned added functionality.
		// They will be added to inherited button group "editButtons".
		private JButton assignBtn = new JButton("Assign a task");
		private JButton removeBtn = new JButton("Remove a teammate");
		
		// Both assigning a teammate a task and removing a teammate should be done on a separate window than main window.
		private JFrame assignFrame = new JFrame();
		private JFrame removeFrame = new JFrame();
		
		// Table model for teammates that will be show in frames above.
		// Same overriden for making cells non-editable.
		private DefaultTableModel teammateModel = new DefaultTableModel(new String[] {
				"Id", "Name", "Username", "Assignor", "Number of Tasks"
		}, 0) {
			private static final long serialVersionUID = 4060675525315431400L;

			@Override
		    public boolean isCellEditable(int row, int column) {
		       return false;
		    }
		};
		
		// Separate tables for showing available teammates from model.
		private JTable assignTable = new JTable(teammateModel);
		private JTable removeTable = new JTable(teammateModel);
		
		AssignorView() {
			// Initialize same GUI elements from UserView.
			super();
			
			// Prepare Assign Frame 
			JLabel assignHeader = new JLabel("Assign a task to a teammate");
			
			// Set the header label UI look.
			assignHeader.setOpaque(true);
			assignHeader.setForeground(new Color(darkBlueColor));
			assignHeader.setBackground(new Color(backgroundColor));
			assignHeader.setHorizontalAlignment(JLabel.CENTER);
			assignHeader.setFont(assignHeader.getFont().deriveFont(24f));
			assignHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
			
			// Set the button UI's.
			setBtnUI(assignBtn, greyColor, whiteColor, 12, "icons/outline_assignment_white.png", lightGreyColor);
			setBtnUI(removeBtn, orangeColor, whiteColor, 12, "icons/outline_delete_white.png", lightOrangeColor);
			
			// Create a simple form for new task to be assigned.
			JPanel form = new JPanel();
			JLabel taskDescriptionLabel = new JLabel("Task Description:");
			JTextField taskDescriptionField = new JTextField();
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");
			JLabel taskDeadlineLabel = new JLabel("Task Deadline (In format like \"" + dtf.format(LocalDateTime.now()) + "\"):");
			JTextField taskDeadlineField = new JTextField();
			JLabel taskImportanceLabel = new JLabel("Task Importance:");
			JTextField taskImportanceField = new JTextField();
			JLabel taskAssigneeIdLabel = new JLabel("Task Assignee Id:");
			JTextField taskAssigneeIdField = new JTextField();
			form.add(taskDescriptionLabel);
			form.add(taskDescriptionField);
			form.add(taskDeadlineLabel);
			form.add(taskDeadlineField);
			form.add(taskImportanceLabel);
			form.add(taskImportanceField);
			form.add(taskAssigneeIdLabel);
			form.add(taskAssigneeIdField);
			form.setLayout(new GridLayout(4, 2, 10, 10));
			
			JButton assignConfirmBtn = new JButton("Assign");
			
			setBtnUI(assignConfirmBtn, orangeColor, whiteColor, 12, "icons/outline_done_white.png", lightOrangeColor);
			
			assignConfirmBtn.addActionListener(e -> {
				String answer = taskAssigneeIdField.getText();
				if(answer != "") {
					int id = Integer.parseInt(answer);
					
					Teammate selectedTeammate = ((ClientManager) client).getTeammate(id);
					if(selectedTeammate != null) {
						int taskId = ((ClientManager) client).getLastTaskId(((ClientManager) client).getTeammate(id)) + 1;
						String description = taskDescriptionField.getText();
						
						String deadline = taskDeadlineField.getText();
						short importance = Short.parseShort(taskImportanceField.getText());
						
						render();
						client.assignTask(new Task(taskId, description, deadline, importance, id));
						assignTask(new Task(taskId, description, deadline, importance, id));
					}
					
					taskDescriptionField.setText("");
					taskDeadlineField.setText("");
					taskImportanceField.setText("");
					taskAssigneeIdField.setText("");
					render();
				}
			});
			
			// Create a new panel so we can give margins for assign window.
			JPanel assignPanel = new JPanel(); 
			assignPanel.setLayout(new GridLayout(4, 1, 0, 20));
			assignPanel.setBorder(BorderFactory.createEmptyBorder(100, 100, 100, 100));
			assignPanel.add(assignHeader);
			assignPanel.add(new JScrollPane(assignTable));
			assignPanel.add(form);
			assignPanel.add(assignConfirmBtn);
			
			assignFrame.add(assignPanel);
        	assignFrame.setMinimumSize(new Dimension(1400, 900));
        	assignFrame.setTitle("Team Manager - Assign Task");
        	assignFrame.setIconImage(new ImageIcon("icons/outline_people_white.png").getImage());
        	
        	// Additional functionality buttons for pop-up frames.
			assignBtn.addActionListener(e -> {
				assignFrame.setVisible(true);
			});
			
			removeBtn.addActionListener(e -> {
				removeFrame.setVisible(true);
			});
			
			assignFrame.addWindowListener(new WindowAdapter() {
        	    public void windowClosing(WindowEvent e) {
        	    	// Clear every input when window closes.
        	    	assignFrame.dispose();
        	    	taskDescriptionField.setText("");
    				taskDeadlineField.setText("");
    				taskImportanceField.setText("");
    				taskAssigneeIdField.setText("");
        	    }
        	});
			
			// Prepare Remove Frame
			
			JLabel removeHeader = new JLabel("Assign a task to a teammate");
			
			JButton removeConfirmBtn = new JButton("Remove selected teammate");
			
			removeHeader.setOpaque(true);
			removeHeader.setForeground(new Color(darkBlueColor));
			removeHeader.setBackground(new Color(backgroundColor));
			removeHeader.setHorizontalAlignment(JLabel.CENTER);
			removeHeader.setFont(assignHeader.getFont().deriveFont(24f));
			removeHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
			
			BorderLayout layout = (BorderLayout)getLayout();
			remove(layout.getLayoutComponent(BorderLayout.SOUTH));
			add(editButtons, BorderLayout.SOUTH);
			
			setBtnUI(removeConfirmBtn, orangeColor, whiteColor, 12, "icons/outline_done_white.png", lightOrangeColor);
			
			removeConfirmBtn.addActionListener(e -> {
				// Get the selected teammate and remove it from model, ClientManager and sqlite database.
				int row = removeTable.getSelectedRow();
				if(row != -1) {
					int id = (int) teammateModel.getValueAt(row, 0);
					Teammate selectedTeammate = new Teammate(id, "", "", "");
					
					teammateModel.removeRow(row);
					client.removeTeammate(selectedTeammate);
					removeTeammate(selectedTeammate);
					
					// If account deleted is currently logged account show menu in main window.
					if(id == loggedAccount.getId()) {
						cards.show(views, "menu");
					}
				}
			});
			
			// Create a panel so we can add border to remove window.
			JPanel removePanel = new JPanel();
			removePanel.setLayout(new BoxLayout(removePanel, BoxLayout.Y_AXIS));
			removePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
			removePanel.add(removeHeader);
			removePanel.add(new JScrollPane(removeTable));
			removePanel.add(removeConfirmBtn);
			
			removeFrame.add(removePanel);
			removeFrame.setMinimumSize(new Dimension(1400, 900));
        	removeFrame.setTitle("Team Manager - Remove Teammate");
        	removeFrame.setIconImage(new ImageIcon("icons/outline_people_white.png").getImage());
        	
        	// Place added functionality buttons of assignor account to the "editButton" button group inherited. 
			editButtons.add(assignBtn);
			editButtons.add(removeBtn);
		}
		
		// Will be used to refresh model whenever needed. (When new user logins, model changes.)
		public void render() {
			// First of all call the method in super class to load necessary updates.
			super.render();
			teammates = ((ClientManager) client).getTeammates();
			
			teammateModel.setRowCount(0); // Empty the table.
			
			// Build model from all the teammates in system.
			for(int i = 0; i < maxTeammates; i++) {
				if(teammates[i] == null) {
					continue;
				}
				teammateModel.addRow(new Object[] {
					teammates[i].getId(), teammates[i].getName(), teammates[i].getUsername(), teammates[i].isAssignor(), teammates[i].getTaskNumber()
				});
			}
		}
	}
	
	// Will hold instances to classes above that we extended from JPanel.
	private JPanel userView;
	private JPanel assignorView;
	
	// Global CardLayout of main window.
	private CardLayout cards = new CardLayout();
	
	GuiApp(String databasePath) {
		// Super class constructor call loads database as intended.
		super(databasePath);
		
		// Try to load custom font from file.
		try {
			File f = new File("fonts/Montserrat-Regular.ttf");
			font = Font.createFont(Font.TRUETYPE_FONT, f);	
		} catch(IOException e) {
			System.out.println("Couldn't load custom fonts.");
			e.printStackTrace();
		} catch(FontFormatException e) {
			System.out.println("Custom font format did not match.");
			e.printStackTrace();
		}
		
		// Change default font.
		Enumeration<Object> keys = UIManager.getDefaults().keys();
		FontUIResource fontResource = new FontUIResource(font.deriveFont(14f));
	    // Change fonts of all Text elements in Swing.
		while (keys.hasMoreElements()) {
	      Object key = keys.nextElement();
	      Object value = UIManager.get (key);
	      if (value instanceof javax.swing.plaf.FontUIResource)
	        UIManager.put(key, fontResource);
	    }
	    
		// Instantiate JPanel subclasses.
	    userView = new UserView();
    	assignorView = new AssignorView();
	}
	
	// Set up menu view. (Login or Create Account)
	void setMenuView() {
		JLabel header = new JLabel("Welcome to Team Manager");
		JButton loginBtn = new JButton("Login");
		JButton createBtn = new JButton("Create an Account");
		
		header.setOpaque(true);
		header.setForeground(new Color(whiteColor));
		header.setBackground(new Color(darkBlueColor));
		header.setHorizontalAlignment(JLabel.CENTER);
		header.setFont(header.getFont().deriveFont(24f));
		header.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		setBtnUI(loginBtn, orangeColor, whiteColor, 14, "icons/outline_exit_to_app_white.png", lightOrangeColor);
		
		setBtnUI(createBtn, greyColor, whiteColor, 14, "icons/outline_person_add_white.png", lightGreyColor);
		
		// Change cards when a option is choosen.
		loginBtn.addActionListener(e -> cards.show(views, "login"));
		createBtn.addActionListener(e -> cards.show(views, "create"));
		
		menuView.add(header);
		
		// Create a button group for both login and create buttons.
		// Used to set a border around buttons.
		JPanel buttons = new JPanel();
		buttons.add(loginBtn);
		buttons.add(createBtn);
		buttons.setLayout(new GridLayout(2, 1, 0, 20));
		buttons.setBorder(BorderFactory.createEmptyBorder(60, 400, 60, 400));
		buttons.setBackground(new Color(backgroundColor));
		
		menuView.add(buttons);
		
		menuView.setLayout(new GridLayout(2, 1, 100, 100));
		menuView.setBackground(new Color(backgroundColor));
	}
	
	// Set up new account creating view.
	void setCreateView() {
		JLabel header = new JLabel("Create an Account");
		
		// Label to show errors.
		JLabel status = new JLabel();
		
		// Necessary form fields.
		JLabel nameLabel = new JLabel("Name:");
		JTextField nameField = new JTextField();
		JLabel usernameLabel = new JLabel("Username:");
		JTextField usernameField = new JTextField();
		JLabel passwordLabel = new JLabel("Password:");
		JPasswordField passwordField = new JPasswordField();
		JLabel passwordAgainLabel = new JLabel("Password Again:");
		JPasswordField passwordAgainField = new JPasswordField();
		JLabel assignorLabel = new JLabel("Are you an assignor?");
		JCheckBox assignorCheck = new JCheckBox();
		JButton createBtn = new JButton("Create");
		JButton cancelBtn = new JButton("Cancel");
		
		header.setOpaque(true);
		header.setForeground(new Color(darkBlueColor));
		header.setBackground(new Color(backgroundColor));
		header.setHorizontalAlignment(JLabel.CENTER);
		header.setFont(header.getFont().deriveFont(24f));
		header.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		status.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		status.setOpaque(true);
		status.setVisible(false);
		status.setBackground(new Color(redColor));
		status.setForeground(new Color(whiteColor));
		status.setFont(status.getFont().deriveFont(16f));
		
		nameField.setBackground(new Color(dirtyWhiteColor));
		nameField.setBorder(BorderFactory.createLineBorder(new Color(blueColor)));
		nameField.setToolTipText("Enter your name...");
		
		usernameField.setBackground(new Color(dirtyWhiteColor));
		usernameField.setBorder(BorderFactory.createLineBorder(new Color(blueColor)));
		usernameField.setToolTipText("Enter your username...");
		
		passwordField.setBackground(new Color(dirtyWhiteColor));
		passwordField.setBorder(BorderFactory.createLineBorder(new Color(blueColor)));
		passwordField.setToolTipText("Enter your password...");
		
		passwordAgainField.setBackground(new Color(dirtyWhiteColor));
		passwordAgainField.setBorder(BorderFactory.createLineBorder(new Color(blueColor)));
		passwordAgainField.setToolTipText("Enter your password again...");
		
		assignorCheck.setBackground(new Color(backgroundColor));
		
		setBtnUI(createBtn, orangeColor, whiteColor, 14, "icons/outline_person_add_white.png", lightOrangeColor);
		createBtn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		setBtnUI(cancelBtn, greyColor, whiteColor, 14, "icons/outline_cancel_white.png", lightGreyColor);
		cancelBtn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		createBtn.addActionListener(e -> {
			String username, password, name;
			boolean assignor;
			
			// Form validation on new account.
			username = usernameField.getText();
			
			if(!((ClientManager) client).usernameExists(username)) {
				// If username is not taken continue.
				password = new String(passwordField.getPassword());
				if((new String(passwordAgainField.getPassword())).equals(password)) {
					// If passwords are same then create new account. 
					name = nameField.getText();
					assignor = assignorCheck.isSelected();
					
					Teammate t = new Teammate(((ClientManager) client).lastTeammateId, name, username, password);
					t.setAssignor(assignor);
					
					client.addTeammate(t);
					addTeammate(t);
					
					// Clear fields if validated and account added.
					nameField.setText("");
					usernameField.setText("");
					passwordField.setText("");
					passwordAgainField.setText("");
					assignorCheck.setSelected(false);
					status.setText("");
					status.setVisible(false);
					
					// Change view to main menu.
					cards.show(views, "menu");
				} else {
					// Otherwise password check fails.
					status.setText("Passwords do not match!");
					status.setVisible(true);
				}
			} else {
				status.setText("Username already taken!");
				status.setVisible(true);
			}
		});
		
		cancelBtn.addActionListener(e -> {
			// Cancels creating account and changes view to main menu.
			cards.show(views, "menu");
			// Clear fields.
			nameField.setText("");
			usernameField.setText("");
			passwordField.setText("");
			passwordAgainField.setText("");
			assignorCheck.setSelected(false);
			status.setText("");
			status.setVisible(false);
		});
		
		// Group all fields in a form.
		JPanel form = new JPanel();
		form.add(nameLabel);
		form.add(nameField);
		form.add(usernameLabel);
		form.add(usernameField);
		form.add(passwordLabel);
		form.add(passwordField);
		form.add(passwordAgainLabel);
		form.add(passwordAgainField);
		form.add(assignorLabel);
		form.add(assignorCheck);
		form.add(createBtn);
		form.add(cancelBtn);
		form.setLayout(new GridLayout(8, 2, 20, 20));
		form.setBackground(new Color(backgroundColor));
		
		createView.add(header);
		createView.add(Box.createRigidArea(new Dimension(0, 10)));
		createView.add(status);
		createView.add(Box.createRigidArea(new Dimension(0, 10)));
		createView.add(form);
		
		createView.setLayout(new BoxLayout(createView, BoxLayout.Y_AXIS));
		createView.setBorder(BorderFactory.createEmptyBorder(150, 200, 200, 200));
		createView.setBackground(new Color(backgroundColor));
	}
	
	// Set up login view.
	void setLoginView() {
		JLabel header = new JLabel("Login");
		JLabel status = new JLabel();
		
		// Necessary form fields.
		JLabel usernameLabel = new JLabel("Username:");
		JTextField usernameField = new JTextField();
		JLabel passwordLabel = new JLabel("Password:");
		JPasswordField passwordField = new JPasswordField();
		
		JButton loginBtn = new JButton("Login");
		JButton cancelBtn = new JButton("Cancel");
		
		header.setOpaque(true);
		header.setForeground(new Color(darkBlueColor));
		header.setBackground(new Color(backgroundColor));
		header.setHorizontalAlignment(JLabel.CENTER);
		header.setFont(header.getFont().deriveFont(24f));
		header.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		status.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		status.setOpaque(true);
		status.setVisible(false);
		status.setBackground(new Color(redColor));
		status.setForeground(new Color(whiteColor));
		
		usernameField.setBackground(new Color(dirtyWhiteColor));
		usernameField.setBorder(BorderFactory.createLineBorder(new Color(blueColor)));
		usernameField.setToolTipText("Enter your username...");
		
		passwordField.setBackground(new Color(dirtyWhiteColor));
		passwordField.setBorder(BorderFactory.createLineBorder(new Color(blueColor)));
		passwordField.setToolTipText("Enter your password...");
		
		setBtnUI(loginBtn, orangeColor, whiteColor, 14, "icons/outline_person_add_white.png", lightOrangeColor);
		
		setBtnUI(cancelBtn, greyColor, whiteColor, 14, "icons/outline_cancel_white.png", lightGreyColor);
		
		
		loginBtn.addActionListener(e -> {
			// Form validation
			String username = usernameField.getText();
			Teammate t = ((ClientManager) client).getTeammate(username);
			
			if(t != null) {
				// If teammate is in ClientManager then account exists.
				String password = new String(passwordField.getPassword());
				if(password.equals(t.getPassword())) {
					// Entered password matches password in database.
					loggedAccount = t;

					// Clear fields.
					usernameField.setText("");
					passwordField.setText("");
					status.setText("");
					status.setSize(0, 0);
					status.setVisible(false);
					
					// Select which type of user it is to show next view and then render it. 
					// Necessary data is filled before card changes.
					if(t.isAssignor()) {
						((AssignorView) assignorView).render();
						cards.show(views, "assignor");
					} else {
						((UserView) userView).render();
						cards.show(views, "user");
					}
				} else {
					// Password in client database and entered does not match.
					status.setText("Password incorrect!");
					status.setVisible(true);
				}
			} else {
				// Account doesn't exist.
				status.setText("User doesn't exist!");
				status.setVisible(true);
			}
		});
		
		cancelBtn.addActionListener(e -> {
			// Back to main menu.
			cards.show(views, "menu");
			
			// Clear fields.
			usernameField.setText("");
			passwordField.setText("");
			status.setText("");
			status.setVisible(false);
		});
		
		// Group fields in form.
		JPanel form = new JPanel();
		form.add(usernameLabel);
		form.add(usernameField);
		form.add(passwordLabel);
		form.add(passwordField);
		form.add(loginBtn);
		form.add(cancelBtn);
		form.setLayout(new GridLayout(3, 2, 20, 50));
		form.setBackground(new Color(backgroundColor));
		
		loginView.add(header);
		loginView.add(Box.createRigidArea(new Dimension(0, 10)));
		loginView.add(status);
		loginView.add(Box.createRigidArea(new Dimension(0, 10)));
		loginView.add(form);
		
		loginView.setLayout(new BoxLayout(loginView, BoxLayout.Y_AXIS));
		loginView.setBorder(BorderFactory.createEmptyBorder(200, 200, 200, 200));
		loginView.setBackground(new Color(backgroundColor));
	}
	
	@Override
	void run() {
		// Don't let items disappear on smaller sizes.
		frame.setMinimumSize(new Dimension(1400, 900));
    	
		// Title and Title Icon.
		frame.setTitle("Team Manager");
    	frame.setIconImage(new ImageIcon("icons/outline_people_white.png").getImage());
    	
    	// Set the card layout and main panel.
    	views.setLayout(cards);
    	views.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
    	views.setBackground(new Color(backgroundColor));
    	
    	// Add each one of the cards to panel.
    	views.add(menuView, "menu");
    	views.add(createView, "create");
    	views.add(loginView, "login");
    	views.add(userView, "user");
    	views.add(assignorView, "assignor");
    	
    	// Start application by showing menu.
    	cards.show(views, "menu");
    	
    	// Prepare GUI of each one of the cards.
    	setMenuView();
    	setCreateView();
    	setLoginView();
    	
    	// Set up main frame.
    	frame.add(views);
    	frame.addWindowListener(new WindowAdapter() {
    	    public void windowClosing(WindowEvent e) {
    	    	// Save database then end the process.
    	    	frame.dispose();
    	        close();
    	        System.exit(0);
    	    }
    	});
    	
    	// Show main window.
    	frame.setVisible(true);
	}
}

package app;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;

public class Task {
	private int id;
	private String description;
	private String deadline;
	private short importance;
	private int assigneeId; // Who this task is assigned to.
	
	Task(int id, String description, String deadline, short importance, int assigneeId) {
		this.id = id;
		this.description = description;
		this.deadline = deadline;
		this.importance = importance;
		this.assigneeId = assigneeId;
	}
	
	int getId() {
		return id;
	}
	
	void setId(int id) {
		this.id = id;
	}
	
	String getDescription() {
		return description;
	}
	
	void setDescription(String description) {
		this.description = description;
	}
	
	String getDeadline() {
		return deadline;
	}
	
	void setDeadline(String deadline) {
		this.deadline = deadline;
	}
	
	short getImportance() {
		return importance;
	}
	
	void setImportance(short importance) {
		this.importance = importance;
	}

	int getAssigneeId() {
		return assigneeId;
	}

	void setAssigneeId(int assigneeId) {
		this.assigneeId = assigneeId;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + assigneeId;
		result = prime * result + ((deadline == null) ? 0 : deadline.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Task other = (Task) obj;
		if (assigneeId != other.assigneeId)
			return false;
		if (deadline == null) {
			if (other.deadline != null)
				return false;
		} else if (!deadline.equals(other.deadline))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id != other.id)
			return false;
		return true;
	}

	// Compares 2 tasks; greater importance value will be before other.
	// Later will be used to sort by Comparators.
	public static Comparator<Task> ImportanceComparator = new Comparator<Task>() {
		@Override
        public int compare(Task t1, Task t2) {
            return (int) (t2.importance - t1.importance);
        }
	};
	
	// Compares 2 tasks; the one more closer to today will be before other.
	// Later will be used to sort by Comparators.
	public static Comparator<Task> DeadlineComparator = new Comparator<Task>() {
		@Override
        public int compare(Task t1, Task t2) {
			// Parse LocalDateTime.
			// Get time remains to deadlines.
			// Minutes will be the order of significance.
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");
			LocalDateTime time1 = LocalDateTime.parse(t1.deadline, dtf);
			LocalDateTime time2 = LocalDateTime.parse(t2.deadline, dtf);
			return (int) (time2.until(time1, ChronoUnit.MINUTES));
        }
	};
}

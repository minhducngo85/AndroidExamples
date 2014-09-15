package com.android.ch4todolist.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * the To-Do item class
 * 
 * @author minhducngo
 *
 */
public class ToDoItem {

    private String task;

    private Date createdOn;

    public ToDoItem(String task) {
        this(task, new Date(System.currentTimeMillis()));
    }

    public ToDoItem(String task, Date createdOn) {
        super();
        this.task = task;
        this.createdOn = createdOn;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
        String dateString = sdf.format(createdOn);
        return "ToDoItem [task=" + task + ", createdOn=" + dateString + "]";
    }

    /**
     * @return the task
     */
    public String getTask() {
        return task;
    }

    /**
     * @param task
     *            the task to set
     */
    public void setTask(String task) {
        this.task = task;
    }

    /**
     * @return the createdOn
     */
    public Date getCreatedOn() {
        return createdOn;
    }

    /**
     * @param createdOn
     *            the createdOn to set
     */
    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }
}

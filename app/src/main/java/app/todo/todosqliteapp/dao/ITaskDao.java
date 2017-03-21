package app.todo.todosqliteapp.dao;

import java.util.List;

public interface ITaskDao {

    public Task fetchTaskById(int userId);
    public List<Task> fetchAllTasks();
    public boolean addTask(Task task);
    public boolean deleteTask(Task task);
    public boolean deleteTaskById(int taskId);
    public boolean deleteTaskByTitle(String title);
    public boolean deleteAllUsers();

}
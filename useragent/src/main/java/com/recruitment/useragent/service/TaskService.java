package com.recruitment.useragent.service;

import com.recruitment.useragent.dto.TaskDto;
import com.recruitment.useragent.dto.UpdateTaskDto;
import com.recruitment.useragent.entity.Customer;
import com.recruitment.useragent.entity.Task;
import com.recruitment.useragent.exception.NotFoundException;
import com.recruitment.useragent.mapper.TaskMapper;
import com.recruitment.useragent.repository.TaskRepository;
import com.recruitment.useragent.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final CustomerRepository userRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository, CustomerRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public Page<TaskDto> getTasksForCustomer(String email, Pageable pageable, String searchQuery) {
        Customer customer = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User", "email", String.valueOf(email)));
        Page<Task> tasksPage;
        if (searchQuery != null && !searchQuery.isEmpty()) {
            tasksPage = taskRepository.findByCustomerIdAndSearchQuery(customer.getId(), searchQuery, pageable);
        } else {
            // If searchQuery is empty, retrieve all tasks for the customer
            tasksPage = taskRepository.findByCustomerId(customer.getId(), pageable);
        }

        return tasksPage.map(TaskMapper::mapToTaskDto);
    }

    public TaskDto createTaskForUser(String email, TaskDto taskDto) {
        Customer customer = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User", "email", String.valueOf(email)));

        Task task = TaskMapper.mapToTask(taskDto);

        task.setCustomer(customer);
        Task createdTask = taskRepository.save(task);

        return TaskMapper.mapToTaskDto(createdTask);
    }

    public TaskDto updateTask(Long taskId, UpdateTaskDto updateTaskDto) {
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task", "id", String.valueOf(taskId)));

        existingTask.setTitle(updateTaskDto.getTitle());
        existingTask.setDescription(updateTaskDto.getDescription());
        existingTask.setDueDate(updateTaskDto.getDueDate());
        existingTask.setCategory(updateTaskDto.getCategory());
        existingTask.setPriority(updateTaskDto.getPriority());
        existingTask.setStatus(updateTaskDto.getStatus());

        Task updatedTask = taskRepository.save(existingTask);
        return TaskMapper.mapToTaskDto(updatedTask);
    }

    public boolean deleteTask(Long taskId) {
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task", "id", String.valueOf(taskId)));

        taskRepository.delete(existingTask);

        return true;
    }

    public TaskDto getTaskDetails(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task", "id", String.valueOf(taskId)));
        return TaskMapper.mapToTaskDto(task);
    }
}

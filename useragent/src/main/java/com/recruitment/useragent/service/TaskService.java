package com.recruitment.useragent.service;

import com.recruitment.useragent.dto.TaskDto;
import com.recruitment.useragent.dto.UpdateTaskDto;
import com.recruitment.useragent.entity.Customer;
import com.recruitment.useragent.entity.Task;
import com.recruitment.useragent.exception.NotFoundException;
import com.recruitment.useragent.mapper.TaskMapper;
import com.recruitment.useragent.repository.TaskRepository;
import com.recruitment.useragent.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public Page<TaskDto> getTasksForCustomer(Long customerId, Pageable pageable) {
        userRepository.findById(customerId).orElseThrow(() -> new NotFoundException("User", "id", String.valueOf(customerId)));
        Page<Task> tasksPage = taskRepository.findByCustomerId(customerId, pageable);
        return tasksPage.map(TaskMapper::mapToTaskDto);
    }

    public TaskDto createTaskForUser(Long customerId, TaskDto taskDto) {
        Customer customer = userRepository.findById(customerId).orElseThrow(() -> new NotFoundException("User", "id", String.valueOf(customerId)));

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

        Task updatedTask = taskRepository.save(existingTask);
        return TaskMapper.mapToTaskDto(updatedTask);
    }

    public boolean deleteTask(Long taskId) {
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task", "id", String.valueOf(taskId)));

        taskRepository.delete(existingTask);

        return true;
    }
}

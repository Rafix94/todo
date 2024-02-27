package com.recruitment.useragent.service;

import com.recruitment.useragent.dto.TaskDto;
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

    public Page<TaskDto> getTasksForUser(Long userId, Pageable pageable) {
        Page<Task> tasksPage = taskRepository.findByCustomerId(userId, pageable);
        return tasksPage.map(TaskMapper::mapToTaskDto);
    }

    public TaskDto createTaskForUser(Long customerId, TaskDto taskDto) {
        Customer customer = userRepository.findById(customerId).orElseThrow(() -> new NotFoundException("User", "id", String.valueOf(customerId)));

        Task task = TaskMapper.mapToTask(taskDto);

        task.setCustomer(customer);
        Task createdTask = taskRepository.save(task);

        return TaskMapper.mapToTaskDto(createdTask);
    }
}

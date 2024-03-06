package com.recruitment.useragent.service;

import com.recruitment.useragent.dto.TaskDto;
import com.recruitment.useragent.dto.UpdateTaskDto;
import com.recruitment.useragent.entity.Customer;
import com.recruitment.useragent.entity.Task;
import com.recruitment.useragent.exception.NotFoundException;
import com.recruitment.useragent.mapper.TaskMapper;
import com.recruitment.useragent.repository.CustomerRepository;
import com.recruitment.useragent.repository.TaskRepository;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.randomizers.range.IntegerRangeRandomizer;
import org.jeasy.random.randomizers.range.LongRangeRandomizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    TaskRepository taskRepository;

    @Mock
    CustomerRepository customerRepository;

    @InjectMocks
    TaskService taskService;

    private EasyRandom generator;

    @BeforeEach
    public void setUp() {
        EasyRandomParameters parameters = new EasyRandomParameters();
        parameters.stringLengthRange(3, 3);
        parameters.randomize(Integer.class, new IntegerRangeRandomizer(1, 10));
        parameters.randomize(Long.class, new LongRangeRandomizer(1L, 10L));
        generator = new EasyRandom(parameters);
    }

    @Test
    void getTasksForCustomerThrowsExceptionWhenCustomerNotFound() {
        Customer customer = generateRandomCustomer();

        Pageable pageable = PageRequest.of(0, 10);

        when(customerRepository.findByEmail(customer.getEmail()))
                .thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(
                NotFoundException.class,
                () -> taskService.getTasksForCustomer(customer.getEmail(), pageable, null)
        );

        assertEquals("User not found with the given input data email : '" + customer.getEmail() + '\'', notFoundException.getMessage());
    }

    @Test
    void getTasksForCustomerReturnsTasksForCustomer() {
        Customer customer = generateRandomCustomer();

        Task task = generator.nextObject(Task.class);

        Pageable pageable = PageRequest.of(0, 10);

        String searchQuery = "searchQuery";

        when(customerRepository.findByEmail(customer.getEmail()))
                .thenReturn(Optional.of(customer));
        when(taskRepository.findByCustomerId(customer.getId(), pageable))
                .thenReturn(convertListToPage(List.of(task), pageable));
        when(taskRepository.findByCustomerIdAndSearchQuery(customer.getId(), searchQuery, pageable))
                .thenReturn(convertListToPage(List.of(task), pageable));

        Page<TaskDto> tasksWithoutSearch = taskService.getTasksForCustomer(customer.getEmail(), pageable, null);
        Page<TaskDto> tasksWithSearch = taskService.getTasksForCustomer(customer.getEmail(), pageable, searchQuery);

        assertEquals(1, tasksWithoutSearch.getTotalElements());
        assertEquals(1, tasksWithSearch.getTotalElements());

        assertInstanceOf(TaskDto.class, tasksWithoutSearch.getContent().get(0));
        assertInstanceOf(TaskDto.class, tasksWithSearch.getContent().get(0));
    }

    @Test
    void createTaskForUserThrowsExceptionWhenCustomerNotFound() {
        Customer customer = generateRandomCustomer();

        when(customerRepository.findByEmail(customer.getEmail()))
                .thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(
                NotFoundException.class,
                () -> taskService.createTaskForUser(customer.getEmail(), TaskDto.builder().build())
        );

        assertEquals("User not found with the given input data email : '" + customer.getEmail() + '\'', notFoundException.getMessage());
    }

    @Test
    void createTaskForUserCreatesTask() {
        Task task = Task.builder()
                .build();

        Customer customer = generateRandomCustomer();

        when(customerRepository.findByEmail(customer.getEmail()))
                .thenReturn(Optional.of(customer));

        when(taskRepository.save(Mockito.any(Task.class))).thenReturn(task);

        TaskDto taskDto = taskService.createTaskForUser(customer.getEmail(),
                TaskDto.builder().build());

        assertEquals(TaskMapper.mapToTaskDto(task), taskDto);
    }

    @Test
    void updateTaskThrowsExceptionWhenTaskNotFound() {
        when(taskRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.empty());

        long taskId = 1L;
        NotFoundException notFoundException = assertThrows(
                NotFoundException.class,
                () -> taskService.updateTask(taskId, UpdateTaskDto.builder().build())
        );

        assertEquals("Task not found with the given input data id : '" + taskId + '\'', notFoundException.getMessage());
    }


    @Test
    void updateTaskUpdatesTask() {
        Task task = Task.builder()
                .id(1L)
                .build();
        when(taskRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.of(task));

        when(taskRepository.save(Mockito.any(Task.class)))
                .thenReturn(task);

        TaskDto taskDto = taskService.updateTask(task.getId(),
                UpdateTaskDto.builder().build());

        assertEquals(TaskMapper.mapToTaskDto(task), taskDto);
    }

    @Test
    void deleteTaskThrowsExceptionWhenTaskNotFound() {
        when(taskRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.empty());

        long taskId = 1L;
        NotFoundException notFoundException = assertThrows(
                NotFoundException.class,
                () -> taskService.deleteTask(taskId)
        );

        assertEquals("Task not found with the given input data id : '" + taskId + '\'', notFoundException.getMessage());
    }

    @Test
    void deleteTaskDeletesTask() {
        Task task = Task.builder()
                .id(1L)
                .build();
        when(taskRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.of(task));

        boolean deleted = taskService.deleteTask(task.getId());

        assertTrue(deleted);
    }

    @Test
    void getTaskDetailsThrowsExceptionWhenTaskNotFound() {
        when(taskRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.empty());

        long taskId = 1L;
        NotFoundException notFoundException = assertThrows(
                NotFoundException.class,
                () -> taskService.getTaskDetails(taskId)
        );

        assertEquals("Task not found with the given input data id : '" + taskId + '\'', notFoundException.getMessage());
    }

    @Test
    void getTaskDetailsGetsTaskDetails() {
        Task task = Task.builder()
                .id(1L)
                .build();
        when(taskRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.of(task));

        TaskDto taskDto = taskService.getTaskDetails(task.getId());

        assertEquals(TaskMapper.mapToTaskDto(task), taskDto);
    }

    private Customer generateRandomCustomer() {
        return generator.nextObject(Customer.class);
    }
    public static <T> Page<T> convertListToPage(List<T> list, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());

        return new PageImpl<>(list.subList(start, end), pageable, list.size());
    }
}
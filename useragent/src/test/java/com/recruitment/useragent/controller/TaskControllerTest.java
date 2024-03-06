package com.recruitment.useragent.controller;

import com.recruitment.useragent.dto.TaskDto;
import com.recruitment.useragent.entity.Customer;
import com.recruitment.useragent.entity.Task;
import com.recruitment.useragent.exception.NotFoundException;
import com.recruitment.useragent.service.TaskService;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.randomizers.range.IntegerRangeRandomizer;
import org.jeasy.random.randomizers.range.LongRangeRandomizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

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
    void getTasksForUserGetsTasks() throws Exception {
        Customer customer = generateRandomCustomer();

        int pageSize = 10;
        Pageable pageable = PageRequest.of(0, pageSize);

        List<TaskDto> tasks = generator.objects(TaskDto.class, 20).toList();

        Page<TaskDto> t = convertListToPage(tasks, pageable);
        when(taskService.getTasksForCustomer(eq(customer.getEmail()), any(PageRequest.class), eq("")))
                .thenReturn(t);

        mockMvc.perform(get("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("email", customer.getEmail())
                        .param("searchQuery", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[*].id").exists())
                .andExpect(jsonPath("$.content[*].title").exists())
                .andExpect(jsonPath("$.content[*].description").exists())
                .andExpect(jsonPath("$.content[*].dueDate").exists())
                .andExpect(jsonPath("$.content[*].priority").exists())
                .andExpect(jsonPath("$.content[*].status").exists())
                .andExpect(jsonPath("$.content[*].category").exists());
    }

    @Test
    void getTasksForUserThrowsExceptionWhenCustomerNotFound() throws Exception {
        Customer customer = generateRandomCustomer();

        when(taskService.getTasksForCustomer(eq(customer.getEmail()), any(PageRequest.class), eq("")))
                .thenThrow(new NotFoundException("User", "email", String.valueOf(customer.getEmail())));

        mockMvc.perform(get("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("email", customer.getEmail())
                        .param("searchQuery", ""))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals(
                        "User not found with the given input data email : '" + customer.getEmail() + '\'',
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void getTaskDetails() throws Exception {
        Task task = generator.nextObject(Task.class);

        when(taskService.getTaskDetails(task.getId()))
                .thenReturn(TaskDto.builder().build());

        mockMvc.perform(get("/tasks/" + task.getId())).andExpect(status().isOk());
    }

    @Test
    void createTaskForUser() {
    }

    @Test
    void updateTask() {
    }

    @Test
    void deleteTask() {
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
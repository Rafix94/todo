package com.recruitment.useragent.repository;

import antlr.collections.impl.IntRange;
import com.recruitment.useragent.entity.Customer;
import com.recruitment.useragent.entity.Task;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.FieldPredicates;
import org.jeasy.random.randomizers.range.IntegerRangeRandomizer;
import org.jeasy.random.randomizers.range.LongRangeRandomizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles("test")
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private EasyRandom generator;

    @BeforeEach
    public void setUp() {
        EasyRandomParameters parameters = new EasyRandomParameters();
        parameters.stringLengthRange(3, 3);
        parameters.randomize(Integer.class, new IntegerRangeRandomizer(1, 10));
        parameters.randomize(Long.class, new LongRangeRandomizer(1L, 10L));
        parameters.excludeField(FieldPredicates.named("id"));
        generator = new EasyRandom(parameters);
    }

    @ParameterizedTest
    @ValueSource(ints = {5, 10, 15})
    void findByCustomerId(int pageSize) {
        Customer customer = customerRepository.save(generateRandomCustomer());

        List<Task> expectedTasks = generator.objects(Task.class, 20).toList();

        expectedTasks.forEach(task -> task.setCustomer(customer));

        taskRepository.saveAll(expectedTasks);

        String sortField = "title";

        Pageable pageable = PageRequest.of(0, pageSize, Sort.by(sortField).ascending());
        Page<Task> taskPage = taskRepository.findByCustomerId(customer.getId(), pageable);


        List<Task> actualTasks = IntStream.range(0, taskPage.getTotalPages())
                .mapToObj(pageNumber -> PageRequest.of(pageNumber, pageSize, Sort.by(sortField).ascending()))
                .map(pageable1 -> taskRepository.findByCustomerId(customer.getId(), pageable1).getContent())
                .flatMap(List::stream)
                .toList();

        expectedTasks = new ArrayList<>(expectedTasks);
        expectedTasks.sort(Comparator.comparing(Task::getTitle));

        assertEquals(expectedTasks.size(), actualTasks.size());
        assertEquals(expectedTasks.size(), taskPage.getTotalElements());

        for (int i = 0; i < expectedTasks.size(); i++) {
            assertEquals(expectedTasks.get(i).getTitle(), actualTasks.get(i).getTitle());
        }

    }

    @Test
    void findByCustomerIdAndSearchQuery() {
        Customer customer = customerRepository.save(generateRandomCustomer());

        List<Task> tasks = generator.objects(Task.class, 20).toList();

        tasks.forEach(task -> task.setCustomer(customer));

        //the only one containing 4 letters
        String uniqueSearchQuery = "Test";
        tasks.get(0).setTitle("prefix" + uniqueSearchQuery + "suffix");
        tasks.get(1).setDescription("prefix" + uniqueSearchQuery + "suffix");

        taskRepository.saveAll(tasks);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> byCustomerIdAndSearchQuery = taskRepository.findByCustomerIdAndSearchQuery(customer.getId(), uniqueSearchQuery, pageable);

        assertEquals(2, byCustomerIdAndSearchQuery.getTotalElements());
        byCustomerIdAndSearchQuery.getContent()
                .forEach(task -> assertTrue(
                        task.getTitle().contains(uniqueSearchQuery) ||
                                task.getDescription().contains(uniqueSearchQuery)));
    }

    private Customer generateRandomCustomer() {
        return generator.nextObject(Customer.class);
    }
}
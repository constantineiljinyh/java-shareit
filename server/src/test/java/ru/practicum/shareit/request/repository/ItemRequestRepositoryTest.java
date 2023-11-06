package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    private ItemRequest itemRequest;

    private ItemRequest request1;

    private ItemRequest request2;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("testUser")
                .email("testuser@example.com")
                .build();
        userRepository.save(user);

        itemRequest = ItemRequest.builder()
                .description("Test request")
                .requestor(user)
                .created(LocalDateTime.now())
                .build();

        request1 = ItemRequest.builder()
                .description("Request 1")
                .requestor(user)
                .created(LocalDateTime.now())
                .build();

        request2 = ItemRequest.builder()
                .description("Request 2")
                .requestor(user)
                .created(LocalDateTime.now())
                .build();
    }

    @AfterEach
    public void cleanup() {
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }

    @Test
    public void findByRequestorIdOrderByCreatedDescTest() {
        itemRequestRepository.save(itemRequest);

        List<ItemRequest> result = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(user.getId());
        assertEquals(1, result.size());
        assertTrue(result.contains(itemRequest));

    }


    @Test
    void findAllByOrderByCreatedDescTest() {
        itemRequestRepository.save(request1);
        itemRequestRepository.save(request2);

        Page<ItemRequest> result = itemRequestRepository.findAllByOrderByCreatedDesc(PageRequest.of(0, 10));

        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().contains(request1));
        assertTrue(result.getContent().contains(request2));
    }

    @Test
    void findByIdAndRequestorIdTest() {
        itemRequestRepository.save(itemRequest);
        ItemRequest foundRequest = itemRequestRepository.findByIdAndRequestorId(itemRequest.getId(), user.getId());
        assertEquals(foundRequest, itemRequest);
    }
}

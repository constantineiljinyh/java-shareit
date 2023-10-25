package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @AfterEach
    public void cleanup() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }

    @Test
    public void testFindAllByOwnerIdOrderById() {
        User user = User.builder()
                .name("username")
                .email("email@example.com")
                .build();
        userRepository.save(user);
        Item item1 = Item.builder()
                .owner(user)
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .build();
        Item item2 = Item.builder()
                .owner(user)
                .name("Item 2")
                .description("Description 2")
                .available(true)
                .build();
        itemRepository.save(item1);
        itemRepository.save(item2);

        Page<Item> result = itemRepository.findAllByOwnerIdOrderById(user.getId(), PageRequest.of(0, 10));

        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().contains(item1));
        assertTrue(result.getContent().contains(item2));
    }

    @Test
    public void testSearchItems() {
        User user = User.builder()
                .name("username")
                .email("email@example.com")
                .build();
        userRepository.save(user);

        Item item1 = Item.builder()
                .owner(user)
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .build();

        Item item2 = Item.builder()
                .owner(user)
                .name("Item 2")
                .description("Description 2")
                .available(true)
                .build();

        itemRepository.save(item1);
        itemRepository.save(item2);

        Page<Item> result = itemRepository.searchItems("Item", PageRequest.of(0, 10));
        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().contains(item1));
        assertTrue(result.getContent().contains(item2));
    }

    @Test
    public void testFindByRequestId() {
        User user = User.builder()
                .name("username")
                .email("email@example.com")
                .build();
        userRepository.save(user);

        ItemRequest request = ItemRequest.builder()
                .description("Request description")
                .requestor(user)
                .created(LocalDateTime.now())
                .build();
        itemRequestRepository.save(request);

        Item item1 = Item.builder()
                .owner(user)
                .request(request)
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .build();

        Item item2 = Item.builder()
                .owner(user)
                .request(request)
                .name("Item 2")
                .description("Description 2")
                .available(true)
                .build();

        itemRepository.save(item1);
        itemRepository.save(item2);

        List<Item> result = itemRepository.findByRequestId(request.getId());

        assertEquals(2, result.size());
        assertTrue(result.contains(item1));
        assertTrue(result.contains(item2));
    }
}
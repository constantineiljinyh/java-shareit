package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @AfterEach
    public void cleanup() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
        commentRepository.deleteAll();
    }

    @Test
    public void testFindAllByItemId() {
        User user = User.builder()
                .name("username")
                .email("email@example.com")
                .build();
        userRepository.save(user);

        Item item = Item.builder()
                .owner(user)
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .build();
        itemRepository.save(item);

        Comment comment1 = Comment.builder()
                .text("Comment 1")
                .item(item)
                .authorName(user)
                .created(LocalDateTime.now())
                .build();
        Comment comment2 = Comment.builder()
                .text("Comment 2")
                .item(item)
                .authorName(user)
                .created(LocalDateTime.now())
                .build();
        Comment comment3 = Comment.builder()
                .text("Comment 3")
                .item(item)
                .authorName(user)
                .created(LocalDateTime.now())
                .build();

        commentRepository.save(comment1);
        commentRepository.save(comment2);
        commentRepository.save(comment3);

        List<Comment> comments = commentRepository.findAllByItemId(item.getId());

        assertEquals(3, comments.size());
        assertTrue(comments.contains(comment1));
        assertTrue(comments.contains(comment2));
        assertTrue(comments.contains(comment3));
    }
}
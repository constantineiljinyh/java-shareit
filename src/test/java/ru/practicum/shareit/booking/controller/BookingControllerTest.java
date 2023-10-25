package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private final EasyRandom random = new EasyRandom();


    @Test
    void testAddBooking() throws Exception {
        int userId = 1;
        BookingDto bookingDto = random.nextObject(BookingDto.class);
        User user = random.nextObject(User.class);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setBooker(user);
        BookingFullDto bookingFullDto = BookingMapper.toBookingFullDto(booking);

        when(bookingService.createBooking(Mockito.anyInt(), Mockito.any(Booking.class))).thenReturn(bookingFullDto);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()));

        verify(bookingService).createBooking(Mockito.anyInt(), Mockito.any(Booking.class));
    }

    @Test
    void testApprovedOrRejectedBooking() throws Exception {
        int userId = 1;
        BookingDto bookingDto = random.nextObject(BookingDto.class);
        User user = random.nextObject(User.class);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setBooker(user);
        BookingFullDto bookingFullDto = BookingMapper.toBookingFullDto(booking);

        when(bookingService.updateBookingStatus(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyBoolean())).thenReturn(bookingFullDto);

        mvc.perform(patch("/bookings/{bookingId}", booking.getId())
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(bookingDto))
                        .param("approved", String.valueOf(true))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()));

        verify(bookingService).updateBookingStatus(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyBoolean());
    }

    @Test
    void testGetBookingById() throws Exception {
        int userId = 1;
        BookingDto bookingDto = random.nextObject(BookingDto.class);
        User user = random.nextObject(User.class);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setBooker(user);
        BookingFullDto bookingFullDto = BookingMapper.toBookingFullDto(booking);

        when(bookingService.getBookingById(Mockito.anyInt(), Mockito.anyInt())).thenReturn(bookingFullDto);

        mvc.perform(get("/bookings/{bookingId}", booking.getId())
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()));

        verify(bookingService).getBookingById(Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    void testGetBookingsByBookerId() throws Exception {
        int userId = 1;
        BookingDto bookingDto = random.nextObject(BookingDto.class);
        User user = random.nextObject(User.class);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setBooker(user);
        BookingFullDto bookingFullDto = BookingMapper.toBookingFullDto(booking);
        List<BookingFullDto> bookingFullDtoList = List.of(bookingFullDto);

        when(bookingService.getBookingsByBookerId(Mockito.anyInt(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(bookingFullDtoList);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingFullDtoList.get(0).getId()));

        verify(bookingService).getBookingsByBookerId(Mockito.anyInt(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    void testGetAllBookingsForItemsByOwnerId() throws Exception {
        int userId = 1;
        BookingDto bookingDto = random.nextObject(BookingDto.class);
        User user = random.nextObject(User.class);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setBooker(user);
        BookingFullDto bookingFullDto = BookingMapper.toBookingFullDto(booking);
        List<BookingFullDto> bookingFullDtoList = List.of(bookingFullDto);

        when(bookingService.getAllBookingsForItemsByOwnerId(Mockito.anyInt(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(bookingFullDtoList);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingFullDtoList.get(0).getId()));

        Mockito.verify(bookingService).getAllBookingsForItemsByOwnerId(Mockito.anyInt(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt());
    }
}
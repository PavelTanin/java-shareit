package ru.practicum.shareit.validator;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingIncomeInfo;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CustomValidatorTest {

    private CustomValidator customValidator = new CustomValidator();

    @Test
    void isUserValidWhenUserHaveEmptyOrNullNameThenThrowUserEmptyNameException() {
        UserDto testUser = new UserDto("test@test.ru", "");
        UserDto testUser2 = new UserDto("test@test.ru", null);

        assertThrows(UserEmptyNameException.class,
                () -> customValidator.isUserValid(testUser));
        assertThrows(UserEmptyNameException.class,
                () -> customValidator.isUserValid(testUser2));
    }

    @Test
    void isUserValidWhenUserHaveEmptyOrBlankOrNullEmailThenThrowUserEmailValidationException() {
        UserDto testUser = new UserDto("", "Test");
        UserDto testUser2 = new UserDto(" ", "Test");
        UserDto testUser3 = new UserDto(null, "Test");

        assertThrows(UserEmailValidationException.class,
                () -> customValidator.isUserValid(testUser));
        assertThrows(UserEmailValidationException.class,
                () -> customValidator.isUserValid(testUser2));
        assertThrows(UserEmailValidationException.class,
                () -> customValidator.isUserValid(testUser3));
    }

    @Test
    void isItemValidWhenItemEmptyOrNullNameThenThrowItemWrongNameException() {
        ItemDto testItem1 = new ItemDto(null, "Test", true);
        ItemDto testItem2 = new ItemDto("", "Test", true);

        assertThrows(ItemWrongNameException.class,
                () -> customValidator.isItemValid(testItem1));
        assertThrows(ItemWrongNameException.class,
                () -> customValidator.isItemValid(testItem2));
    }

    @Test
    void isItemValidWhenItemEmptyOrNullDescriptionThenThrowItemWrongDescriptionException() {
        ItemDto testItem1 = new ItemDto("Test", null, true);
        ItemDto testItem2 = new ItemDto("Test", "", true);

        assertThrows(ItemWrongDescriptionException.class,
                () -> customValidator.isItemValid(testItem1));
        assertThrows(ItemWrongDescriptionException.class,
                () -> customValidator.isItemValid(testItem2));
    }

    @Test
    void isItemValidWhenItemNullAvailableValueThenThrowEmptyAvailablePointException() {
        ItemDto testItem1 = new ItemDto("Test", "Test", null);

        assertThrows(EmptyAvailablePoint.class,
                () -> customValidator.isItemValid(testItem1));
    }

    @Test
    void isBookingValidWhenItemIdIsNullThenThrowIncorrectItemValueException() {
        BookingIncomeInfo testBooking = new BookingIncomeInfo();
        testBooking.setItemId(null);
        testBooking.setStart(LocalDateTime.now().plusDays(2));
        testBooking.setEnd(LocalDateTime.now().plusDays(5));

        assertThrows(IncorrectItemValueException.class,
                () -> customValidator.isBookingValid(testBooking));

    }

    @Test
    void isBookingValidWhenStartIsBeforeNowOrNullThenThrowIncorrectBookingTimeException() {
        BookingIncomeInfo testBooking = new BookingIncomeInfo();
        testBooking.setItemId(1L);
        testBooking.setStart(LocalDateTime.now().minusDays(2));
        testBooking.setEnd(LocalDateTime.now().plusDays(5));

        BookingIncomeInfo testBooking2 = new BookingIncomeInfo();
        testBooking2.setItemId(1L);
        testBooking2.setStart(null);
        testBooking2.setEnd(LocalDateTime.now().plusDays(5));

        assertThrows(IncorrectBookingTimeException.class,
                () -> customValidator.isBookingValid(testBooking));
        assertThrows(IncorrectBookingTimeException.class,
                () -> customValidator.isBookingValid(testBooking2));

    }

    @Test
    void isBookingValidWhenEndIsBeforeNowOrNullThenThrowIncorrectBookingTimeException() {
        BookingIncomeInfo testBooking = new BookingIncomeInfo();
        testBooking.setItemId(1L);
        testBooking.setStart(LocalDateTime.now().plusDays(1));
        testBooking.setEnd(LocalDateTime.now().minusDays(5));

        BookingIncomeInfo testBooking2 = new BookingIncomeInfo();
        testBooking2.setItemId(1L);
        testBooking2.setStart(LocalDateTime.now().plusDays(2));
        testBooking2.setEnd(null);

        assertThrows(IncorrectBookingTimeException.class,
                () -> customValidator.isBookingValid(testBooking));
        assertThrows(IncorrectBookingTimeException.class,
                () -> customValidator.isBookingValid(testBooking2));
    }

    @Test
    void isBookingValidWhenEndIsBeforeStartThenThrowIncorrectBookingTimeException() {
        BookingIncomeInfo testBooking = new BookingIncomeInfo();
        testBooking.setItemId(1L);
        testBooking.setStart(LocalDateTime.now().plusDays(2));
        testBooking.setEnd(LocalDateTime.now().plusDays(1));

        assertThrows(IncorrectBookingTimeException.class,
                () -> customValidator.isBookingValid(testBooking));
    }

    @Test
    void isRequestValidWhenDescriptionIsEmptyOrNullThenThrowEmptyRequestDescriptionException() {
        ItemRequestDto testRequest = new ItemRequestDto();
        testRequest.setDescription(null);
        ItemRequestDto testRequest2 = new ItemRequestDto();
        testRequest2.setDescription("");

        assertThrows(EmptyRequestDescriptionException.class,
                () -> customValidator.isRequestValid(testRequest));
        assertThrows(EmptyRequestDescriptionException.class,
                () -> customValidator.isRequestValid(testRequest2));
    }

    @Test
    void isPageableParamsCorrectWhenFromNegativeThenThrowIncorrectRequestParamsException() {
        Integer from = -1;
        Integer size = 1;

        assertThrows(IncorrectRequestParamsException.class,
                () -> customValidator.isPageableParamsCorrect(from, size));
    }

    @Test
    void isPageableParamsCorrectWhenSizeNegativeOrZeroThenThrowIncorrectRequestParamsException() {
        Integer from = 0;
        Integer size = 0;
        Integer size2 = -1;

        assertThrows(IncorrectRequestParamsException.class,
                () -> customValidator.isPageableParamsCorrect(from, size));
        assertThrows(IncorrectRequestParamsException.class,
                () -> customValidator.isPageableParamsCorrect(from, size2));
    }

    @Test
    void isCommentValidWhenCommentTextIsEmptyOrNullThenThrowEmptyCommentException() {
        CommentDto testComment = new CommentDto();
        testComment.setText(null);
        CommentDto testComment2 = new CommentDto();
        testComment2.setText("");

        assertThrows(EmptyCommentTextException.class,
                () -> customValidator.isCommentValid(testComment));
        assertThrows(EmptyCommentTextException.class,
                () -> customValidator.isCommentValid(testComment2));
    }
}
package com.ticket.shop.exception;

import com.ticket.shop.error.Error;
import com.ticket.shop.exception.company.CompanyNotFoundException;
import com.ticket.shop.exception.country.CountryNotFoundException;
import com.ticket.shop.exception.user.UserAlreadyExistsException;
import com.ticket.shop.exception.user.UserNotFoundException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

/**
 * Ticket Shop exception handler
 */
@ControllerAdvice
public class TicketShopExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handle "already exists" exceptions
     *
     * @param ex      exception
     * @param request http Servlet Request
     * @return {@link Error}
     */
    @ExceptionHandler(value = {
            UserAlreadyExistsException.class
    })
    public ResponseEntity<Error> handlerConflictException(Exception ex, HttpServletRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.CONFLICT);
    }

    /**
     * Handle "not found" exceptions
     *
     * @param ex      exception
     * @param request http Servlet Request
     * @return {@link Error}
     */
    @ExceptionHandler(value = {
            UserNotFoundException.class,
            CountryNotFoundException.class,
            CompanyNotFoundException.class
    })
    public ResponseEntity<Error> handlerNotFoundException(Exception ex, HttpServletRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle "database communication" exception
     *
     * @param ex      exception
     * @param request http Servlet Request
     * @return {@link Error}
     */
    @ExceptionHandler(value = {DatabaseCommunicationException.class})
    public ResponseEntity<Error> handlerBadRequestException(Exception ex, HttpServletRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle "access denied" exception
     * @param ex      exception
     * @param request http Servlet Request
     * @return {@link Error}
     */
    @ExceptionHandler(value = {AccessDeniedException.class})
    public ResponseEntity<Error> handlerForbiddenException(Exception ex, HttpServletRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.FORBIDDEN);
    }

    /**
     * Handle method other exception
     *
     * @param ex      exception
     * @param request http Servlet Request
     * @return {@link Error}
     */
    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Error> handlerAnyOtherException(Exception ex, HttpServletRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle method argument not valid
     *
     * @param ex         the exception to handle
     * @param headers    the headers to be written to the response
     * @param httpStatus the selected response status
     * @param request    the current request
     * @return {@link Error}
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus httpStatus, WebRequest request) {
        Error error = Error.builder()
                .timestamp(new Date())
                .message(ex.getBindingResult().getAllErrors().get(0).getDefaultMessage())
                .method(((ServletWebRequest) request).getRequest().getMethod())
                .path(((ServletWebRequest) request).getRequest().getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    /**
     * Build the error response
     *
     * @param ex         exception
     * @param request    http Servlet Request
     * @param httpStatus httpStatus
     * @return {@link Error}
     */
    public ResponseEntity<Error> buildErrorResponse(Exception ex, HttpServletRequest request, HttpStatus httpStatus) {
        Error error = Error.builder()
                .timestamp(new Date())
                .message(ex.getMessage())
                .method(request.getMethod())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(error, httpStatus);
    }
}

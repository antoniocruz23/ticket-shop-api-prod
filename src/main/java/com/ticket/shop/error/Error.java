package com.ticket.shop.error;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * Error class
 */
@Data
@Builder
public class Error {
    Date timestamp;
    String message;
    String method;
    String path;
    String exception;

}

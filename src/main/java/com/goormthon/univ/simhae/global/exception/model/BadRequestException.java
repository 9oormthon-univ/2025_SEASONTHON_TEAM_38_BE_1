package com.goormthon.univ.simhae.global.exception.model;


import com.goormthon.univ.simhae.global.exception.message.ErrorMessage;

public class BadRequestException extends SimhaeException {

    public BadRequestException(final ErrorMessage errorMessage) {
        super(errorMessage);
    }

}

package com.goormthon.univ.simhae.global.exception.model;


import com.goormthon.univ.simhae.global.exception.message.ErrorMessage;

public class NotFoundException extends SimhaeException {

    public NotFoundException(final ErrorMessage errorMessage) {
        super(errorMessage);
    }

}

package com.goormthon.univ.simhae.global.exception.model;

import com.goormthon.univ.simhae.global.exception.message.ErrorMessage;
import lombok.Getter;

@Getter
public class SimhaeException extends RuntimeException {

    private final ErrorMessage errorMessage;

    public SimhaeException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
        this.errorMessage = errorMessage;
    }

}

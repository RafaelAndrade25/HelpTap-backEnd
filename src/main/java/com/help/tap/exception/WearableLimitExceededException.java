package com.help.tap.exception;

public class WearableLimitExceededException extends RuntimeException {

    public WearableLimitExceededException(int userId, int limit) {
        super(String.format(
                "Usuário ID %d já atingiu o limite máximo de %d pulseira(s) cadastrada(s).",
                userId, limit
        ));
    }
}
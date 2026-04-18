package com.help.tap.dto.external;

public record ValidationRequestDTO (
        String credential,
        String uf,
        String type){}

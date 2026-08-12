package com.help.tap.exception;

import com.help.tap.dto.error.ErrorResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

        // -------------------------------------------------------------------------
        // 404 — Recurso não encontrado
        // -------------------------------------------------------------------------
        @ExceptionHandler(EntityNotFoundException.class)
        public ResponseEntity<ErrorResponseDTO> handleNotFound(
                        EntityNotFoundException ex, HttpServletRequest request) {

                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(ErrorResponseDTO.of(
                                                404, "Not Found",
                                                ex.getMessage(),
                                                request.getRequestURI()));
        }

        // -------------------------------------------------------------------------
        // 409 — Limite de pulseiras excedido
        // -------------------------------------------------------------------------
        @ExceptionHandler(WearableLimitExceededException.class)
        public ResponseEntity<ErrorResponseDTO> handleWearableLimit(
                        WearableLimitExceededException ex, HttpServletRequest request) {

                return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(ErrorResponseDTO.of(
                                                409, "Conflict",
                                                ex.getMessage(),
                                                request.getRequestURI()));
        }

        // -------------------------------------------------------------------------
        // 422 — Violação de regra de negócio
        // -------------------------------------------------------------------------
        @ExceptionHandler(BusinessRuleException.class)
        public ResponseEntity<ErrorResponseDTO> handleBusinessRule(
                        BusinessRuleException ex, HttpServletRequest request) {

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ErrorResponseDTO.of(
                                                422, "Unprocessable Entity",
                                                ex.getMessage(),
                                                request.getRequestURI()));
        }

        // -------------------------------------------------------------------------
        // 422 — Falhas de validação de campos (@Valid)
        // -------------------------------------------------------------------------
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponseDTO> handleValidation(
                        MethodArgumentNotValidException ex, HttpServletRequest request) {

                List<String> details = ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .map(err -> String.format("Campo '%s': %s", err.getField(), err.getDefaultMessage()))
                                .toList();

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ErrorResponseDTO.ofValidation(request.getRequestURI(), details));
        }

        // -------------------------------------------------------------------------
        // 401 — Credenciais inválidas
        // -------------------------------------------------------------------------
        @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<ErrorResponseDTO> handleBadCredentials(
                        BadCredentialsException ex, HttpServletRequest request) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(ErrorResponseDTO.of(
                                                401, "Unauthorized",
                                                ex.getMessage(),
                                                request.getRequestURI()));
        }

        // -------------------------------------------------------------------------
        // 409 — Conflito (CPF ou E-mail duplicado)
        // -------------------------------------------------------------------------
        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ErrorResponseDTO> handleIllegalArgument(
                        IllegalArgumentException ex, HttpServletRequest request) {

                return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(ErrorResponseDTO.of(
                                                409, "Conflict",
                                                ex.getMessage(),
                                                request.getRequestURI()));
        }

        // -------------------------------------------------------------------------
        // 500 — Fallback para erros inesperados
        // -------------------------------------------------------------------------
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponseDTO> handleGeneric(
                        Exception ex, HttpServletRequest request) {

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ErrorResponseDTO.of(
                                                500, "Internal Server Error",
                                                "Ocorreu um erro inesperado. Tente novamente mais tarde.",
                                                request.getRequestURI()));
        }
}
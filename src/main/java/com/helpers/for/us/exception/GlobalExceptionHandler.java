package com.you.and.me.exception;

import com.you.and.me.model.response.common.CommonResponse;
import com.you.and.me.model.response.ValidationFailedResponse;
import com.you.and.me.util.CommonResponseMessages;
import com.you.and.me.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.Instant;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(DataNotFoundErrorExceptionHandler.class)
    public ResponseEntity<CommonResponse<String>> handleDataNotFoundException(DataNotFoundErrorExceptionHandler e) {
        logger.error("{} Data Not Found Exception: {} {}", Constant.ERROR_DOTS_START, e.getMessage(), Constant.ERROR_DOTS_END);

        CommonResponse<String> response = new CommonResponse<>(
                CommonResponseMessages.DATA_NOT_FOUND_CODE,
                CommonResponseMessages.DATA_NOT_FOUND_STATUS,
                CommonResponseMessages.DATA_NOT_FOUND_MESSAGE,
                e.getMessage(),
                Instant.now()
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @ExceptionHandler(InternalServerErrorExceptionHandler.class)
    public ResponseEntity<CommonResponse<String>> handleInternalServerException(InternalServerErrorExceptionHandler e) {
        logger.error("{} Internal Server Error: {} {}", Constant.ERROR_DOTS_START, e.getMessage(), Constant.ERROR_DOTS_END);

        CommonResponse<String> response = new CommonResponse<>(
                CommonResponseMessages.INTERNAL_SERVER_ERROR_CODE,
                CommonResponseMessages.INTERNAL_SERVER_ERROR_STATUS,
                CommonResponseMessages.INTERNAL_SERVER_ERROR_MESSAGE,
                e.getMessage(),
                Instant.now()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(DataAccessErrorExceptionHandler.class)
    public ResponseEntity<CommonResponse<String>> handleDataAccessException(DataAccessErrorExceptionHandler e) {
        logger.error("{} Database Access Error: {} {}", Constant.ERROR_DOTS_START, e.getMessage(), Constant.ERROR_DOTS_END);

        CommonResponse<String> response = new CommonResponse<>(
                CommonResponseMessages.DATA_ACCESS_ERROR_CODE,
                CommonResponseMessages.DATA_ACCESS_ERROR_STATUS,
                CommonResponseMessages.DATA_ACCESS_ERROR_MESSAGE,
                e.getMessage(),
                Instant.now()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(UpdateFailedErrorExceptionHandler.class)
    public ResponseEntity<CommonResponse<String>> handleUpdateFailedErrorException(UpdateFailedErrorExceptionHandler e) {
        logger.error("{} Update Failed Error Exception: {} {}", Constant.ERROR_DOTS_START, e.getMessage(), Constant.ERROR_DOTS_END);

        CommonResponse<String> response = new CommonResponse<>(
                CommonResponseMessages.BAD_REQUEST_CODE,
                CommonResponseMessages.BAD_REQUEST_STATUS,
                CommonResponseMessages.BAD_REQUEST_MESSAGES,
                e.getMessage(),
                Instant.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(InsertFailedErrorExceptionHandler.class)
    public ResponseEntity<CommonResponse<String>> handleInsertFailedErrorException(InsertFailedErrorExceptionHandler e) {
        logger.error("{} Insert Failed Error Exception: {} {}", Constant.ERROR_DOTS_START, e.getMessage(), Constant.ERROR_DOTS_END);

        CommonResponse<String> response = new CommonResponse<>(
                CommonResponseMessages.BAD_REQUEST_CODE,
                CommonResponseMessages.BAD_REQUEST_STATUS,
                CommonResponseMessages.BAD_REQUEST_MESSAGES,
                e.getMessage(),
                Instant.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ValidationFailedErrorExceptionHandler.class)
    public ResponseEntity<CommonResponse<List<ValidationFailedResponse>>> handleValidationFailedErrorException(ValidationFailedErrorExceptionHandler e) {
        logger.error("{} Validation Failed Error Exception : {} {}", Constant.ERROR_DOTS_START, e.getMessage(), Constant.ERROR_DOTS_END);

        CommonResponse<List<ValidationFailedResponse>> response = new CommonResponse<>(
                CommonResponseMessages.BAD_REQUEST_CODE,
                CommonResponseMessages.BAD_REQUEST_STATUS,
                CommonResponseMessages.BAD_REQUEST_MESSAGES,
                e.getValidationFailedResponses(),
                Instant.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(UserRegisterFailedErrorExceptionHandler.class)
    public ResponseEntity<CommonResponse<String>> handleUserRegisterFailedErrorException(UserRegisterFailedErrorExceptionHandler e) {
        logger.error("{} Validation Failed Error Exception : {} {}", Constant.ERROR_DOTS_START, e.getMessage(), Constant.ERROR_DOTS_END);
        CommonResponse<String> response = new CommonResponse<>(
                CommonResponseMessages.BAD_REQUEST_CODE,
                CommonResponseMessages.BAD_REQUEST_STATUS,
                CommonResponseMessages.BAD_REQUEST_MESSAGES,
                e.getMessage(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(DataRetrieveFailedErrorExceptionHandler.class)
    public ResponseEntity<CommonResponse<String>> handleDataRetrieveFailedErrorException(DataRetrieveFailedErrorExceptionHandler e) {
        logger.error("{} Data Retrieve Failed Error Exception : {} {}", Constant.ERROR_DOTS_START, e.getMessage(), Constant.ERROR_DOTS_END);
        CommonResponse<String> response = new CommonResponse<>(
                CommonResponseMessages.DATA_RETRIEVE_FAILED_CODE,
                CommonResponseMessages.DATA_RETRIEVE_FAILED_STATUS,
                CommonResponseMessages.DATA_RETRIEVE_FAILED_MESSAGE,
                e.getMessage(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(VerificationFailedErrorExceptionHandle.class)
    public ResponseEntity<CommonResponse<String>> handleVerificationFailedErrorException(VerificationFailedErrorExceptionHandle e) {
        logger.error("{} Verification Failed Error Exception : {} {}", Constant.ERROR_DOTS_START, e.getMessage(), Constant.ERROR_DOTS_END);
        CommonResponse<String> response = new CommonResponse<>(
                CommonResponseMessages.DATA_RETRIEVE_FAILED_CODE,
                CommonResponseMessages.DATA_RETRIEVE_FAILED_STATUS,
                CommonResponseMessages.DATA_RETRIEVE_FAILED_MESSAGE,
                e.getMessage(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(UnAuthenticateErrorExceptionHandler.class)
    public ResponseEntity<CommonResponse<String>> handleUnAuthenticateErrorException(UnAuthenticateErrorExceptionHandler e) {
        logger.error("{} UnAuthenticate Error Exception : {} {}", Constant.ERROR_DOTS_START, e.getMessage(), Constant.ERROR_DOTS_END);
        CommonResponse<String> response = new CommonResponse<>(
                CommonResponseMessages.USER_NOT_FOUND_ERROR_CODE,
                CommonResponseMessages.USER_NOT_FOUND_ERROR_STATUS,
                CommonResponseMessages.USER_NOT_FOUND_ERROR_MESSAGE,
                e.getMessage(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(TerminateFailedErrorExceptionHandler.class)
    public ResponseEntity<CommonResponse<String>> handleTerminateFailedErrorException(TerminateFailedErrorExceptionHandler e) {
        logger.error("{} Terminate Failed Error Exception : {} {}", Constant.ERROR_DOTS_START, e.getMessage(), Constant.ERROR_DOTS_END);
        CommonResponse<String> response = new CommonResponse<>(
                CommonResponseMessages.DATA_RETRIEVE_FAILED_CODE,
                CommonResponseMessages.DATA_RETRIEVE_FAILED_STATUS,
                CommonResponseMessages.DATA_RETRIEVE_FAILED_MESSAGE,
                e.getMessage(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }


    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<CommonResponse<String>> handleNotFoundErrorException(
            NoHandlerFoundException e) {

        logger.error(
                "{} API Not Found : {} {}",
                Constant.ERROR_DOTS_START,
                e.getRequestURL(),
                Constant.ERROR_DOTS_END
        );

        CommonResponse<String> response = new CommonResponse<>(
                CommonResponseMessages.BAD_REQUEST_CODE,
                CommonResponseMessages.BAD_REQUEST_STATUS,
                CommonResponseMessages.BAD_REQUEST_MESSAGES,
                "Endpoint not found: " + e.getRequestURL(),
                Instant.now()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

}

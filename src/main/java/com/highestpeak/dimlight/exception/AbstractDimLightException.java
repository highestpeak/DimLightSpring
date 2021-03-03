package com.highestpeak.dimlight.exception;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * Base exception of the project.
 *
 * @author johnniang
 * @author ryan0up
 * @date 2019-03-15
 */
public abstract class AbstractDimLightException extends RuntimeException {

    /**
     * Error errorData.
     */
    private Object errorData;

    public AbstractDimLightException(String message) {
        super(message);
    }

    public AbstractDimLightException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Http status code
     *
     * @return {@link HttpStatus}
     */
    @NonNull
    public abstract HttpStatus getStatus();

    @Nullable
    public Object getErrorData() {
        return errorData;
    }

    /**
     * Sets error errorData.
     *
     * @param errorData error data
     * @return current exception.
     */
    @NonNull
    public AbstractDimLightException setErrorData(@Nullable Object errorData) {
        this.errorData = errorData;
        return this;
    }
}

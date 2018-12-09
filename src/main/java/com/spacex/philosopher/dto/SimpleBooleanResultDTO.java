package com.spacex.philosopher.dto;

public class SimpleBooleanResultDTO {
    private boolean result;

    public SimpleBooleanResultDTO(boolean result) {
        this.result = result;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}

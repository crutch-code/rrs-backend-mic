package com.ilyak.entity.responses;

import com.fasterxml.jackson.annotation.JsonInclude;

public final class AppResponseWithObject extends DefaultAppResponse{

    @JsonInclude
    private Object source;

    public AppResponseWithObject() {
        source = null;
    }

    public AppResponseWithObject(Object source) {
        this.source = source;
    }

    public AppResponseWithObject(int internal_code, String message, String endpoint, Object source) {
        super(internal_code, message, endpoint);
        this.source = source;
    }

    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }
}

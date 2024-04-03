package com.SpringBoot.Stock_viewer_4_SpringBatch_Junit.service;

public class FileUploadResponse {
	
    private boolean flag;
    private String message;
    // Optionally, include more fields as needed

    // Constructors, getters, and setters below
    public FileUploadResponse(boolean flag, String message) {
        this.flag = flag;
        this.message = message;
    }

    // Getters and setters
    public boolean getFlag() {
        return flag;
    }

    public void setFlag(boolean processed) {
        this.flag = processed;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

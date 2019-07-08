package com.dimnikol.foodshazam;

import lombok.Data;

@Data
public class Response {

    private String status;
    private String exception;
    private String food;
    private String [] ingredient;
    private String [] recipe;

}

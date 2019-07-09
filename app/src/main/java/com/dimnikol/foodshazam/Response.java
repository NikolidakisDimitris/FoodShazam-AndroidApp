package com.dimnikol.foodshazam;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Response {

    private String status;
    private Object exception;
    private String food;
    private List<String> ingredient;
    private String recipe;

}

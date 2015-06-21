package com.marfeel.exercise.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Created by eduardo.ponte on 19/06/2015.
 */
public interface Qualifier {
    boolean isMarfeelizable(ResponseEntity<String> responseEntity);
}

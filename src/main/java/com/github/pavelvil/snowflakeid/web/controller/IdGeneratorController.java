package com.github.pavelvil.snowflakeid.web.controller;

import com.github.pavelvil.snowflakeid.generator.SnowflakeIdGenerator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/id/generator")
public class IdGeneratorController {

    private final SnowflakeIdGenerator generator;

    public IdGeneratorController(SnowflakeIdGenerator generator) {
        this.generator = generator;
    }

    @GetMapping("/next-id")
    public ResponseEntity<Long> nextId() {
        return ResponseEntity.ok(generator.nextId());
    }
}

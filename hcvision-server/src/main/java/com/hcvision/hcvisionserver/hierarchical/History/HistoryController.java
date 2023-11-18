package com.hcvision.hcvisionserver.hierarchical.History;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hierarchical/history")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService service;

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<History.ProjectHistory> getHistoryById(@PathVariable long id, @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        return ResponseEntity.ok(service.getHistoryById(id, jwt));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteHistory(@RequestBody long id, @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        return ResponseEntity.ok(service.deleteHistory(id, jwt));
    }

    @GetMapping()
    public ResponseEntity<List<History.ProjectHistoryList>> getAllHistory(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        return ResponseEntity.ok(service.getAllHistory(jwt));
    }
}

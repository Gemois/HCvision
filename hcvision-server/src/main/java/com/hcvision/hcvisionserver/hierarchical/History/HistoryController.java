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

    @GetMapping(value = "/{historyId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<History.ProjectHistory> getHistoryById(@PathVariable() long historyId,
                                                  @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt ) {
        return service.findHistoryById(historyId, jwt);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteHistory(@RequestParam("history") long historyId,
                                                @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        service.deleteHistory(historyId, jwt);
        return ResponseEntity.ok("User deleted along with all his information.");
    }

    @GetMapping("/list")
    public ResponseEntity<List<History.ProjectHistoryList>> getAllHistory(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        return ResponseEntity.ok(service.getAllHistory(jwt));
    }

}

package com.hcvision.hcvisionserver.hierarchical;

import com.hcvision.hcvisionserver.hierarchical.script.Optimal.Optimal;
import com.hcvision.hcvisionserver.hierarchical.script.Optimal.OptimalRequest;
import com.hcvision.hcvisionserver.hierarchical.script.ScriptType;
import com.hcvision.hcvisionserver.hierarchical.script.analysis.Analysis;
import com.hcvision.hcvisionserver.hierarchical.script.analysis.AnalysisRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/hierarchical")
@RequiredArgsConstructor
public class HierarchicalController {

    private final HierarchicalService service;

    @PostMapping(value = "/optimal", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Optimal.ProjectOptimal> predict(@RequestBody OptimalRequest request,
                                                          @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        return ResponseEntity.ok(service.getOptimalParams(request, jwt));

    }

    @PostMapping(value = "/analysis", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Analysis.ProjectAnalysis> analyze(@RequestBody AnalysisRequest request,
                                                            @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        return ResponseEntity.ok(service.getAnalysis(request, jwt));
    }


    @GetMapping(value = "/{scriptType}/{id}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> analyze(@PathVariable ScriptType scriptType,
                                                            @PathVariable long id,
                                                            @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        return ResponseEntity.ok(service.getScriptStatus(scriptType, id, jwt));
    }

}

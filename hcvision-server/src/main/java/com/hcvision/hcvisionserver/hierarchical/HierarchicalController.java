package com.hcvision.hcvisionserver.hierarchical;

import com.hcvision.hcvisionserver.dataset.dto.AccessType;
import com.hcvision.hcvisionserver.hierarchical.script.Linkage;
import com.hcvision.hcvisionserver.hierarchical.script.Optimal.Optimal;
import com.hcvision.hcvisionserver.hierarchical.script.analysis.Analysis;
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

    @GetMapping(value = "/optimal", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Optimal.ProjectOptimal> predict(@RequestParam("filename") String filename,
                                           @RequestParam("type") AccessType accessType,
                                           @RequestParam("max_clusters") int maxClusters,
                                           @RequestParam("sample") boolean isSample,
                                           @RequestParam("attributes") String attributes,
                                           @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
            return service.getOptimalParams(filename, accessType, maxClusters, attributes, isSample, jwt);

    }

    @GetMapping(value = "/analysis", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Analysis.ProjectAnalysis> analyze(@RequestParam("filename") String filename,
                                                            @RequestParam("type") AccessType accessType,
                                                            @RequestParam("linkage") Linkage linkage,
                                                            @RequestParam("num_clusters") int clusters,
                                                            @RequestParam("sample") boolean isSample,
                                                            @RequestParam("attributes") String attributes,
                                                            @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        return service.getAnalysis(filename, accessType, linkage, clusters, attributes, isSample, jwt);
    }

}

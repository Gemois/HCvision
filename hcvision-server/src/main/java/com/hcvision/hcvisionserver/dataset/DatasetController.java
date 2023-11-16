package com.hcvision.hcvisionserver.dataset;

import com.hcvision.hcvisionserver.dataset.dto.AccessType;
import com.hcvision.hcvisionserver.dataset.dto.UploadDatasetRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/datasets")
@RequiredArgsConstructor
public class DatasetController {

    private final DatasetService service;

    @PostMapping(value = "/upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> uploadDataset(@ModelAttribute UploadDatasetRequest request,
                                                @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        return ResponseEntity.ok(service.saveDataset(request, jwt));
    }

    @GetMapping(value = "/download")
    public ResponseEntity<UrlResource> downloadDataset(@RequestParam("dataset") String filename,
                                                       @RequestParam("access_type") AccessType accessType,
                                                       @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .body(service.getDatasetFile(filename, accessType, jwt));
    }

    @DeleteMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteDataset(@RequestParam("dataset") String filename,
                                           @RequestParam("access_type") AccessType accessType,
                                           @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        return ResponseEntity.ok(service.deleteDataset(filename, accessType, jwt));
    }

    @GetMapping(value = "/read", produces="application/json")
    public ResponseEntity<?> getDataset(@RequestParam("dataset") String filename,
                                        @RequestParam("access_type") AccessType accessType,
                                        @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        return ResponseEntity.ok(service.getDataset(filename, accessType, jwt));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Dataset.ProjectNameAndAccessType>> getDatasetList(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        return ResponseEntity.ok(service.getDatasetList(jwt));
    }

}

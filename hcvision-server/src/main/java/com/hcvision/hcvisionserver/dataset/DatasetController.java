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
@RequestMapping("/api/v1/dataset")
@RequiredArgsConstructor
public class DatasetController {

    private final DatasetService service;

    @PostMapping(value = "/upload", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<String> upload(@ModelAttribute UploadDatasetRequest uploadDatasetRequest,
                                         @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        return service.saveFile(uploadDatasetRequest, jwt);
    }

    @GetMapping(value = "/download")
    public ResponseEntity<UrlResource> download(@RequestParam("filename") String filename,
                                                @RequestParam("type") AccessType accessType,
                                                @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        return service.findFile(filename, accessType, jwt);
    }

    @DeleteMapping(value = "/delete", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<String> delete(@RequestParam("filename") String filename,
                                         @RequestParam("type") AccessType accessType,
                                         @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        return service.deleteFile(filename, accessType, jwt);
    }

    @GetMapping(produces="application/json")
    public ResponseEntity<String> readDataset(@RequestParam("filename") String filename,
                                              @RequestParam("type") AccessType accessType,
                                              @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        return service.getDatasetInJson(filename, accessType, jwt);
    }

    @GetMapping(value = "/list", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<List<Dataset.ProjectNameAndAccessType>> getDatasets(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        return ResponseEntity.ok(service.getDatasets(jwt));
    }

}

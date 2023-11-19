package com.hcvision.hcvisionserver.resource;


import com.hcvision.hcvisionserver.hierarchical.script.ScriptType;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/resources")
@AllArgsConstructor
public class ResourceController {

    private final ResourceService service;

    @GetMapping(value = "/logo-light", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<?> getLightLogo() {
        return service.getLogo();
    }


    @GetMapping(value = "/{scriptType}/{id}")
    public ResponseEntity<?> getResult(@PathVariable ScriptType scriptType,
                                       @PathVariable long id,
                                       @RequestParam("resource") ResourceType resourceType,
                                       @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        return service.getResource(scriptType, id, resourceType, jwt);
    }

}

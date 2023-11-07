package com.hcvision.hcvisionserver.resource;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/resources")

public class ResourceController {

    @GetMapping("/logo-light")
    public ResponseEntity<ClassPathResource> getLightLogo() {

        ClassPathResource resource = new ClassPathResource("static/logo_light.png");
        MediaType mediaType = MediaType.IMAGE_PNG;
        return ResponseEntity.ok().contentType(mediaType).body(resource);

    }

}

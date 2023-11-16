package com.hcvision.hcvisionserver.dataset.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadDatasetRequest {

    @JsonProperty("file")
    private MultipartFile file;

    @JsonProperty("access_type")
    private AccessType access_type;

}

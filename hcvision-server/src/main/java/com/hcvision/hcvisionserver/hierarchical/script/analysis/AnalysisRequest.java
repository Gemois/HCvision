package com.hcvision.hcvisionserver.hierarchical.script.analysis;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hcvision.hcvisionserver.dataset.dto.AccessType;
import com.hcvision.hcvisionserver.hierarchical.script.Linkage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.StringJoiner;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnalysisRequest {

    @JsonProperty("filename")
    String filename;

    @JsonProperty("access_type")
    AccessType accessType;

    @JsonProperty("linkage")
    Linkage linkage;

    @JsonProperty("n_clusters")
    int numClusters;

    @JsonProperty("sample")
    boolean isSample;

    @JsonProperty("attributes")
    List<String> attributes;

    public String getAttributes() {
        StringJoiner stringJoiner = new StringJoiner(",");
        for (String s : attributes) {
            stringJoiner.add(s);
        }
        return stringJoiner.toString();
    }

}

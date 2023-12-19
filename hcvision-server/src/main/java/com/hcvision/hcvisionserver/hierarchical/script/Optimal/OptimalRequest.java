package com.hcvision.hcvisionserver.hierarchical.script.Optimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hcvision.hcvisionserver.dataset.dto.AccessType;
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
public class OptimalRequest {

    @JsonProperty("filename")
    String filename;

    @JsonProperty("access_type")
    AccessType accessType;

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

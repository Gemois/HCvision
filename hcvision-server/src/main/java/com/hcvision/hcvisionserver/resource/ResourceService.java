package com.hcvision.hcvisionserver.resource;

import com.hcvision.hcvisionserver.exception.BadRequestException;
import com.hcvision.hcvisionserver.exception.InternalServerErrorException;
import com.hcvision.hcvisionserver.hierarchical.HierarchicalService;
import com.hcvision.hcvisionserver.hierarchical.script.Optimal.Optimal;
import com.hcvision.hcvisionserver.hierarchical.script.Optimal.OptimalRepository;
import com.hcvision.hcvisionserver.hierarchical.script.ScriptType;
import com.hcvision.hcvisionserver.hierarchical.script.analysis.Analysis;
import com.hcvision.hcvisionserver.hierarchical.script.analysis.AnalysisRepository;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@AllArgsConstructor
public class ResourceService {

    private final OptimalRepository optimalRepository;
    private final AnalysisRepository analysisRepository;

    public ResponseEntity<?> getResource(ScriptType scriptType, long id, ResourceType resourceType) {
        Optimal optimal;
        Analysis analysis;
        File result;
        if (scriptType.equals(ScriptType.optimal)) {
            optimal = optimalRepository.findById(id).orElseThrow(() -> new BadRequestException("Script id not valid"));
            result = new File(HierarchicalService.getBaseResultPathByPythonScript(optimal) + File.separator + optimal.getOptimalParamsResultFileName());

        } else {
            String filename;
            analysis = analysisRepository.findById(id).orElseThrow(() -> new BadRequestException("Script id not valid"));

            if (resourceType.equals(ResourceType.cluster_assignments)) {
                filename = analysis.getClusterAssignmentResultFileName();
            } else if (resourceType.equals(ResourceType.dendrogram)) {
                filename = analysis.getDendrogramResultFileName();
            } else {
                filename = analysis.getParallelCoordinatesResultFileName();
            }
            result = new File(HierarchicalService.getBaseResultPathByPythonScript(analysis) + File.separator + filename);
        }
        if (resourceType.equals(ResourceType.optimal_params) || resourceType.equals(ResourceType.cluster_assignments)) {
            try {
                String jsonResult = Files.readString(Path.of(result.getPath()));
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(jsonResult);
            } catch (Exception e) {
                throw new InternalServerErrorException("An error occurred while processing the resource");
            }
        } else {
            try {
                Resource resource = new FileSystemResource(result);
                byte[] imageBytes = Files.readAllBytes(Path.of(resource.getURI()));
                return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
            } catch (IOException e) {
                throw new InternalServerErrorException("An error occurred while processing the resource");
            }
        }
    }

    public ResponseEntity<ClassPathResource> getLogo() {
        ClassPathResource resource = new ClassPathResource("static/logo_light.png");
        MediaType mediaType = MediaType.IMAGE_PNG;
        return ResponseEntity.ok().contentType(mediaType).body(resource);
    }
}

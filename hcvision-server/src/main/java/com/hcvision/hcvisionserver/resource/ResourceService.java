package com.hcvision.hcvisionserver.resource;

import com.hcvision.hcvisionserver.exception.BadRequestException;
import com.hcvision.hcvisionserver.exception.ForbiddenException;
import com.hcvision.hcvisionserver.exception.InternalServerErrorException;
import com.hcvision.hcvisionserver.hierarchical.HierarchicalService;
import com.hcvision.hcvisionserver.hierarchical.script.Optimal.Optimal;
import com.hcvision.hcvisionserver.hierarchical.script.Optimal.OptimalRepository;
import com.hcvision.hcvisionserver.hierarchical.script.PythonScript;
import com.hcvision.hcvisionserver.hierarchical.script.ScriptType;
import com.hcvision.hcvisionserver.hierarchical.script.analysis.Analysis;
import com.hcvision.hcvisionserver.hierarchical.script.analysis.AnalysisRepository;
import com.hcvision.hcvisionserver.user.User;
import com.hcvision.hcvisionserver.user.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Service
@AllArgsConstructor
@Slf4j
public class ResourceService {

    private final OptimalRepository optimalRepository;
    private final AnalysisRepository analysisRepository;
    private final UserService userService;


    public ResponseEntity<?> getResource(ScriptType scriptType, long id, ResourceType resourceType, String jwt) {
        File result;
        PythonScript script;

        if (scriptType == ScriptType.optimal) {
            Optimal optimal = optimalRepository.findById(id).orElseThrow(() -> new BadRequestException("Script id not valid"));
            checkScriptOwnership(optimal.getUser(), jwt);
            script = optimal;
            result = new File(HierarchicalService.getBaseResultPathByPythonScript(optimal) + File.separator + Optimal.getOptimalParamsResultFileName());
        } else {
            Analysis analysis = analysisRepository.findById(id).orElseThrow(() -> new BadRequestException("Script id not valid"));
            checkScriptOwnership(analysis.getUser(), jwt);
            script = analysis;

            String filename = switch (resourceType) {
                case cluster_assignments -> Analysis.getClusterAssignmentResultFileName();
                case dendrogram -> Analysis.getDendrogramResultFileName();
                default -> Analysis.getParallelCoordinatesResultFileName();
            };
            result = new File(HierarchicalService.getBaseResultPathByPythonScript(analysis) + File.separator + filename);
        }

        try {
            if (resourceType == ResourceType.optimal_params || resourceType == ResourceType.cluster_assignments) {
                String jsonResult = Files.readString(result.toPath());
                log.info("Successfully retrieved JSON resource for script id {}", script.getId());
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(jsonResult);
            } else {
                byte[] imageBytes = Files.readAllBytes(result.toPath());
                log.info("Successfully retrieved image resource for script id {}", script.getId());
                return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
            }
        } catch (IOException e) {
            log.error("Error processing the resource file for script id {}", id);
            throw new InternalServerErrorException("Error processing the resource file for script id " + script.getId());
        }
    }

    public ResponseEntity<ClassPathResource> getLogo() {
        ClassPathResource resource = new ClassPathResource("static/logo_light.png");
        MediaType mediaType = MediaType.IMAGE_PNG;
        return ResponseEntity.ok().contentType(mediaType).body(resource);
    }


    private void checkScriptOwnership(User user, String jwt) {
        User requester = userService.getUserFromJwt(jwt);
        if (!requester.equals(user))
            throw new ForbiddenException("You dont have permission to access this resource");
    }

}

package com.backend.recruitAi.interview.service;

import com.backend.recruitAi.config.OcrServerProperties;
import com.backend.recruitAi.global.exception.BusinessException;
import com.backend.recruitAi.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.nio.file.Files;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OcrService {

    private final OcrServerProperties ocrServerProperties;

    public String sendFileToPython(File file) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            byte[] fileBytes = Files.readAllBytes(file.toPath());

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new ByteArrayResource(fileBytes) {
                @Override
                public String getFilename() {
                    return file.getName();
                }
            });

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            String url = ocrServerProperties.getFileUrl();

            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new BusinessException(ErrorCode.FILE_PROCESSING_FAILED);
            }

            return (String) response.getBody().get("ocr_output");

        } catch (Exception e) {
            throw new BusinessException(ErrorCode.FILE_PROCESSING_FAILED);
        }
    }

}

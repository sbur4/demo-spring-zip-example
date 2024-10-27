package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.util.CompressUtils;
import org.example.util.FileUtils;
import org.example.util.JavaZipUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
public class Controller {
    @PostMapping("/hello/{text}")
    public ResponseEntity<String> javaZipManager(@PathVariable("text") String text) {
        log.debug("Received a request for greeting with name: {}", text);

        String pathToZip = "./java_test.zip";
        String pathToFile = "./test.txt";
        String response;

        if (FileUtils.doesFileExist(pathToZip)) {
            response = JavaZipUtils.updateZipFile(pathToZip, pathToFile, text);
            log.debug("Zip file '{}' updated with content from file '{}'", pathToZip, pathToFile);
        }else{
            response = JavaZipUtils.createNewZipFile(pathToZip, pathToFile, text);
            log.debug("Zip file '{}' created with new content '{}'", pathToZip, text);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/hello/{text}")
    public ResponseEntity<String> apacheZipManager(@PathVariable("text") String text) {
        log.debug("Received a request for greeting with name: {}", text);

        String pathToZip = "./java_test.zip";
        String pathToFile = "./test.txt";
        String response;

        if (FileUtils.doesFileExist(pathToZip)) {
            response = CompressUtils.updateZipFile(pathToZip, pathToFile, text);
            log.debug("Zip file '{}' updated with content from file '{}'", pathToZip, pathToFile);
        }else{
            response = CompressUtils.createNewZipFile(pathToZip, pathToFile, text);
            log.debug("Zip file '{}' created with new content '{}'", pathToZip, text);
        }

        return ResponseEntity.ok(response);
    }
}
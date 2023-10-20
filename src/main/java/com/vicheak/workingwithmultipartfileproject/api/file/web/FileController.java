package com.vicheak.workingwithmultipartfileproject.api.file.web;

import com.vicheak.workingwithmultipartfileproject.api.file.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @GetMapping("/download/{name}")
    public ResponseEntity<?> download(@PathVariable("name") String name) {
        Resource resource = fileService.download(name);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=" + resource.getFilename())
                .body(resource);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/single", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FileDto singleUpload(@RequestPart MultipartFile file) {
        return fileService.singleUpload(file);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/multiple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<FileDto> multipleUpload(@RequestPart List<MultipartFile> files) {
        return fileService.multipleUpload(files);
    }

    @GetMapping("/{name}")
    public FileDto findByName(@PathVariable("name") String name) throws IOException {
        return fileService.findByName(name);
    }

    @GetMapping
    public List<FileDto> findAll() {
        return fileService.findAll();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{name}")
    public void deleteByName(@PathVariable("name") String name) {
        fileService.deleteByName(name);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping()
    public void deleteAll() {
        fileService.deleteAll();
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{name}")
    public void updateByName(@PathVariable("name") String name, @RequestPart MultipartFile file) {
        fileService.updateByName(name, file);
    }

}

package com.vicheak.workingwithmultipartfileproject.api.file;

import com.vicheak.workingwithmultipartfileproject.api.file.web.FileDto;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {
    Resource download(String name);

    FileDto singleUpload(MultipartFile file);

    List<FileDto> multipleUpload(List<MultipartFile> files);

    FileDto findByName(String name) throws IOException;

    List<FileDto> findAll();

    void deleteByName(String name);

    void deleteAll();

    void updateByName(String name, MultipartFile file);

}

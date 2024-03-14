package com.vicheak.workingwithmultipartfileproject.api.file;

import com.vicheak.workingwithmultipartfileproject.api.file.web.FileDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Value("${file.server-path}")
    private String serverPath;

    @Value("${file.base-uri}")
    private String fileBaseUri;

    @Value("${file.download-uri}")
    private String fileDownloadUri;

    @Override
    public Resource download(String name) {
        Path path = Paths.get(serverPath + name);

        if (Files.exists(path)) {
            return UrlResource.from(path.toUri());
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Resource does not exist!");
    }

    @Override
    public FileDto singleUpload(MultipartFile file) {
        return this.save(file);
    }

    @Override
    public List<FileDto> multipleUpload(List<MultipartFile> files) {
        return files.stream()
                .map(this::save)
                .toList();
    }

    @Override
    public FileDto findByName(String name) throws IOException {
        Path path = Paths.get(serverPath + name);

        if (Files.exists(path)) {
            Resource resource = UrlResource.from(path.toUri());
            return FileDto.builder()
                    .name(name)
                    .fileBaseUri(fileBaseUri + name)
                    .downloadUri(fileDownloadUri + name)
                    .size(resource.contentLength())
                    .extension(this.getExtension(name))
                    .build();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Resource does not exist!");
    }

    @Override
    public List<FileDto> findAll() {
        Path path = Paths.get(serverPath);

        //@TODO
        try {
            /*return Files.list(path).map(p -> {
                try {
                    return FileDto.builder()
                            .name(p.getFileName().toString())
                            .fileBaseUri(fileBaseUri + p.getFileName().toString())
                            .size(UrlResource.from(p.toUri()).contentLength())
                            .extension(this.getExtension(p.getFileName().toString()))
                            .build();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).toList();*/

            List<Path> pathList = Files.list(path).toList();
            List<FileDto> fileDtoList = new ArrayList<>(pathList.size());
            for (Path p : pathList) {
                fileDtoList.add(FileDto.builder()
                        .name(p.getFileName().toString())
                        .fileBaseUri(fileBaseUri + p.getFileName().toString())
                        .downloadUri(fileDownloadUri + p.getFileName().toString())
                        .size(UrlResource.from(p.toUri()).contentLength())
                        .extension(this.getExtension(p.getFileName().toString()))
                        .build());
            }
            return fileDtoList;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteByName(String name) {
        Path path = Paths.get(serverPath + name);

        try {
            boolean isDeleted = Files.deleteIfExists(path);
            if (!isDeleted)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Resource does not exist!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAll() {
        Path path = Paths.get(serverPath);

        try {
            List<Path> pathList = Files.list(path).toList();
            for (Path p : pathList) {
                Files.delete(p);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateByName(String name, MultipartFile file) {
        Path path = Paths.get(serverPath + name);

        Resource resource = UrlResource.from(path.toUri());
        //get old uuid from file name
        String oldName = resource.getFilename();

        try {
            boolean isDeleted = Files.deleteIfExists(path);
            if (!isDeleted)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Resource does not exist!");
            else {
                //update here
                //cut the extension from old name
                assert oldName != null;
                int lastDotIndex = oldName.lastIndexOf(".");
                String oldUuid = oldName.substring(0, lastDotIndex);
                String extension = this.getExtension(Objects.requireNonNull(file.getOriginalFilename()));
                String newName = oldUuid + "." + extension;

                path = Paths.get(serverPath + newName);

                Files.copy(file.getInputStream(), path);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        return fileName.substring(lastDotIndex + 1);
    }

    private FileDto save(MultipartFile file) {
        if (file.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty! try again");

        String extension = this.getExtension(Objects.requireNonNull(file.getOriginalFilename()));
        String name = UUID.randomUUID() + "." + extension;

        Path path = Paths.get(serverPath + name);

        try {
            Files.copy(file.getInputStream(), path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return FileDto.builder()
                .name(name)
                .fileBaseUri(fileBaseUri + name)
                .downloadUri(fileDownloadUri + name)
                .size(file.getSize())
                .extension(extension)
                .build();
    }
}

package com.vicheak.workingwithmultipartfileproject.api.file.web;

import lombok.Builder;

@Builder
public record FileDto(String name,
                      String fileBaseUri,
                      Long size,
                      String extension) {
}

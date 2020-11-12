package com.javalab.logs.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileInfo {
    private String filePath;
    private String name;
    private Long size;
    private String mail;
}

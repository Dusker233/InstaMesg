package cn.edu.sdu.db.instamesg.pojo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

@Entity
@Table(name = "files")
public class File {
    @Id
    @Column(name = "file_id", nullable = false)
    private Integer id;

    @Size(max = 64)
    @NotNull
    @Column(name = "file_name", nullable = false, length = 64)
    private String fileName;

    @NotNull
    @Column(name = "file", nullable = false)
    private String file;

    @NotNull
    @Column(name = "upload_time", nullable = false)
    private Instant uploadTime;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "uploader_id", nullable = false)
    private User uploader;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Instant getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Instant uploadTime) {
        this.uploadTime = uploadTime;
    }

    public User getUploader() {
        return uploader;
    }

    public void setUploader(User uploader) {
        this.uploader = uploader;
    }

}
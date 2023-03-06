package cn.edu.sdu.db.instamesg.pojo;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "files")
public class File {
    @Id
    @Column(name = "fileId", nullable = false)
    private Integer id;

    @Column(name = "fileName", nullable = false, length = 64)
    private String fileName;

    @Column(name = "file", nullable = false)
    private byte[] file;

    @Column(name = "uploadTime", nullable = false)
    private Instant uploadTime;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "uploaderId", nullable = false)
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

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
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
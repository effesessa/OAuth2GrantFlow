package unical.it.oauth2nativeapps.model;

public class DriveFile {

    private String title;

    private String fileExtension;

    private String downloadUrl;

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "DriveFile{" +
                "title='" + title + '\'' +
                "," + "\n" + "fileExtension='" + fileExtension + '\'' +
                "," + "\n" + "downloadUrl='" + downloadUrl + '\'' +
                '}';
    }
}

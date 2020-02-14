package id.go.kemdikbud.pkpberbasiszonasi.Master;

import java.util.ArrayList;

public class ModuleContent {
    private String type ; //string   //a file or a folder or external link
    private String filename ; //string   //filename
    private String filepath ; //string   //filepath
    private int filesize ; //int   //filesize
    private String fileurl ; //string  Opsional //downloadable file url
    private String content ; //string  Opsional //Raw content, will be used when type is content
    private int timecreated ; //int   //Time created
    private int timemodified ; //int   //Time modified
    private int sortorder ; //int   //Content sort order
    private String mimetype ; //string  Opsional //File mime type.
    private boolean isexternalfile ; //int  Opsional //Whether is an external file.
    private String repositorytype ; //string  Opsional //The repository type for external files.
    private int userid ; //int   //User who added this content to moodle
    private String author ; //string   //Content owner
    private String license ; //string   //Content license
    private ArrayList<ModuleContentTag> tags;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public int getFilesize() {
        return filesize;
    }

    public void setFilesize(int filesize) {
        this.filesize = filesize;
    }

    public String getFileurl() {
        return fileurl;
    }

    public void setFileurl(String fileurl) {
        this.fileurl = fileurl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getTimecreated() {
        return timecreated;
    }

    public void setTimecreated(int timecreated) {
        this.timecreated = timecreated;
    }

    public int getTimemodified() {
        return timemodified;
    }

    public void setTimemodified(int timemodified) {
        this.timemodified = timemodified;
    }

    public int getSortorder() {
        return sortorder;
    }

    public void setSortorder(int sortorder) {
        this.sortorder = sortorder;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public String getRepositorytype() {
        return repositorytype;
    }

    public void setRepositorytype(String repositorytype) {
        this.repositorytype = repositorytype;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public ArrayList<ModuleContentTag> getTags() {
        return tags;
    }

    public void setTags(ArrayList<ModuleContentTag> tags) {
        this.tags = tags;
    }

    public boolean isIsexternalfile() {
        return isexternalfile;
    }

    public void setIsexternalfile(boolean isexternalfile) {
        this.isexternalfile = isexternalfile;
    }
}

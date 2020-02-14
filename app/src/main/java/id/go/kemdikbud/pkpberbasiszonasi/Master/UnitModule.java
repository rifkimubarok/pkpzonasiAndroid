package id.go.kemdikbud.pkpberbasiszonasi.Master;

import java.util.List;

public class UnitModule {
    private int id = 0; // int   //activity id
    private String url = ""; //string  Opsional //activity url
    private String name = ""; //string   //activity module name
    private int instance = 0; // int  Opsional //instance id
    private String description = ""; //string  Opsional //activity description
    private int visible = 0; // int  Opsional //is the module visible
    private boolean uservisible = false; // int  Opsional //Is the module visible for the user?
    private String availabilityinfo = ""; //string  Opsional //Availability information.
    private int visibleoncoursepage = 0; // int  Opsional //is the module visible on course page
    private String modicon = ""; //string   //activity icon url
    private String modname = ""; //string   //activity module type
    private String modplural = ""; //string   //activity module plural name
    private String availability = ""; //string  Opsional //module availability settings
    private int indent = 0; // int   //number of identation in the site
    private String onclick = ""; //string  Opsional //Onclick action.
    private String afterlink = ""; //string  Opsional //After link info to be displayed.
    private String customdata = ""; //string  Opsional //Custom data (JSON encoded).
    private int completion = 0; // int  Opsional //Type of completion tracking:
    private List<ModuleContent> contents;

    public UnitModule(){

    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getInstance() {
        return instance;
    }

    public void setInstance(int instance) {
        this.instance = instance;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getVisible() {
        return visible;
    }

    public void setVisible(int visible) {
        this.visible = visible;
    }

    public String getAvailabilityinfo() {
        return availabilityinfo;
    }

    public void setAvailabilityinfo(String availabilityinfo) {
        this.availabilityinfo = availabilityinfo;
    }

    public int getVisibleoncoursepage() {
        return visibleoncoursepage;
    }

    public void setVisibleoncoursepage(int visibleoncoursepage) {
        this.visibleoncoursepage = visibleoncoursepage;
    }

    public String getModicon() {
        return modicon;
    }

    public void setModicon(String modicon) {
        this.modicon = modicon;
    }

    public String getModname() {
        return modname;
    }

    public void setModname(String modname) {
        this.modname = modname;
    }

    public String getModplural() {
        return modplural;
    }

    public void setModplural(String modplural) {
        this.modplural = modplural;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public int getIndent() {
        return indent;
    }

    public void setIndent(int indent) {
        this.indent = indent;
    }

    public String getOnclick() {
        return onclick;
    }

    public void setOnclick(String onclick) {
        this.onclick = onclick;
    }

    public String getAfterlink() {
        return afterlink;
    }

    public void setAfterlink(String afterlink) {
        this.afterlink = afterlink;
    }

    public String getCustomdata() {
        return customdata;
    }

    public void setCustomdata(String customdata) {
        this.customdata = customdata;
    }

    public int getCompletion() {
        return completion;
    }

    public void setCompletion(int completion) {
        this.completion = completion;
    }

    public List<ModuleContent> getContents() {
        return contents;
    }

    public void setContents(List<ModuleContent> contents) {
        this.contents = contents;
    }

    public boolean isUservisible() {
        return uservisible;
    }

    public void setUservisible(boolean uservisible) {
        this.uservisible = uservisible;
    }
}

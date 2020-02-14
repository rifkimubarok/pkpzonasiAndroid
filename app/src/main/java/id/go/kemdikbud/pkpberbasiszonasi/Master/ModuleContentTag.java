package id.go.kemdikbud.pkpberbasiszonasi.Master;

public class ModuleContentTag {
    private int id; // int   //Tag id.
    private String name; // string   //Tag name.
    private String rawname; // string   //The raw, unnormalised name for the tag as entered by users.
    private int isstandard; // int   //Whether this tag is standard.
    private int tagcollid; // int   //Tag collection id.
    private int taginstanceid; // int   //Tag instance id.
    private int taginstancecontextid; // int   //Context the tag instance belongs to.
    private int itemid; // int   //Id of the record tagged.
    private int ordering; // int   //Tag ordering.
    private int flag; // int   //Whether the tag is flagged as inappropriate.


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRawname() {
        return rawname;
    }

    public void setRawname(String rawname) {
        this.rawname = rawname;
    }

    public int getIsstandard() {
        return isstandard;
    }

    public void setIsstandard(int isstandard) {
        this.isstandard = isstandard;
    }

    public int getTagcollid() {
        return tagcollid;
    }

    public void setTagcollid(int tagcollid) {
        this.tagcollid = tagcollid;
    }

    public int getTaginstanceid() {
        return taginstanceid;
    }

    public void setTaginstanceid(int taginstanceid) {
        this.taginstanceid = taginstanceid;
    }

    public int getTaginstancecontextid() {
        return taginstancecontextid;
    }

    public void setTaginstancecontextid(int taginstancecontextid) {
        this.taginstancecontextid = taginstancecontextid;
    }

    public int getItemid() {
        return itemid;
    }

    public void setItemid(int itemid) {
        this.itemid = itemid;
    }

    public int getOrdering() {
        return ordering;
    }

    public void setOrdering(int ordering) {
        this.ordering = ordering;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}

package id.go.kemdikbud.pkpberbasiszonasi.Master;

import java.util.ArrayList;

public class UnitCourses {
    private int id; // int   //Section ID
    private String name; // string   //Section name
    private int visible; // int  Opsional //is the section visible
    private String summary; // string   //Section description
    private int summaryformat; // int   //summary format (1 = HTML, 0 = MOODLE, 2 = PLAIN or 4 = MARKDOWN)
    private int section; // int  Opsional //Section number inside the course
    private int hiddenbynumsections; // int  Opsional //Whether is a section hidden in the course format
    private int uservisible; // int  Opsional //Is the section visible for the user?
    private String availabilityinfo; // string  Opsional //Availability information.
    private ArrayList<ModuleContent> modules;

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

    public int getVisible() {
        return visible;
    }

    public void setVisible(int visible) {
        this.visible = visible;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public int getSummaryformat() {
        return summaryformat;
    }

    public void setSummaryformat(int summaryformat) {
        this.summaryformat = summaryformat;
    }

    public int getSection() {
        return section;
    }

    public void setSection(int section) {
        this.section = section;
    }

    public int getHiddenbynumsections() {
        return hiddenbynumsections;
    }

    public void setHiddenbynumsections(int hiddenbynumsections) {
        this.hiddenbynumsections = hiddenbynumsections;
    }

    public int getUservisible() {
        return uservisible;
    }

    public void setUservisible(int uservisible) {
        this.uservisible = uservisible;
    }

    public String getAvailabilityinfo() {
        return availabilityinfo;
    }

    public void setAvailabilityinfo(String availabilityinfo) {
        this.availabilityinfo = availabilityinfo;
    }

    public ArrayList<ModuleContent> getModules() {
        return modules;
    }

    public void setModules(ArrayList<ModuleContent> modules) {
        this.modules = modules;
    }
}

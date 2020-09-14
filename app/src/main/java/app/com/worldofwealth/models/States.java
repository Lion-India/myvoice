package app.com.worldofwealth.models;

public class States
{

    String code = null;
    String fname = null;
    String id = null;

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    String lname = null;
    boolean selected = false;

    public States(String code, String fname, String lname, String id, boolean selected)
    {
        super();
        this.code = code;
        this.fname = fname;
        this.lname = lname;
        this.selected = selected;
        this.id=id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }
        public boolean isSelected()
    {
        return selected;
    }

    public void setSelected(boolean selected)
    {
        this.selected = selected;
    }

}
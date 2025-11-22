package com.deptassoc.model;

/**
 * POJO representing an Association entity.
 */
public class Association {
    private int assocId;
    private String assocName;
    private int establishmentYear;
    private int departmentId;
    private String description;
    
    public Association() {}
    
    public Association(int assocId, String assocName, int establishmentYear, 
                      int departmentId, String description) {
        this.assocId = assocId;
        this.assocName = assocName;
        this.establishmentYear = establishmentYear;
        this.departmentId = departmentId;
        this.description = description;
    }
    
    public int getAssocId() { return assocId; }
    public void setAssocId(int assocId) { this.assocId = assocId; }
    
    public String getAssocName() { return assocName; }
    public void setAssocName(String assocName) { this.assocName = assocName; }
    
    public int getEstablishmentYear() { return establishmentYear; }
    public void setEstablishmentYear(int establishmentYear) { this.establishmentYear = establishmentYear; }
    
    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    @Override
    public String toString() {
        return "Association{" +
                "assocId=" + assocId +
                ", assocName='" + assocName + '\'' +
                ", establishmentYear=" + establishmentYear +
                ", departmentId=" + departmentId +
                ", description='" + description + '\'' +
                '}';
    }
}

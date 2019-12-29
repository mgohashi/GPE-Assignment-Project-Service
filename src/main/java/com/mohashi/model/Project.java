package com.mohashi.model;

public class Project {
    private String _id;
    private int projectId;
    private String ownerFirstName;
    private String ownerLastName;
    private String ownerEmail;
    private String projectTitle;
    private String projectDescription;
    private ProjectStatus projectStatus;

    public Project() {
    }

    public Project(int projectId, String ownerFirstName, String ownerLastName, String ownerEmail, String projectTitle,
            String projectDescription, ProjectStatus projectStatus) {
        this.projectId = projectId;
        this.ownerFirstName = ownerFirstName;
        this.ownerLastName = ownerLastName;
        this.ownerEmail = ownerEmail;
        this.projectTitle = projectTitle;
        this.projectDescription = projectDescription;
        this.projectStatus = projectStatus;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
    
    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getOwnerFirstName() {
        return ownerFirstName;
    }

    public void setOwnerFirstName(String ownerFirstName) {
        this.ownerFirstName = ownerFirstName;
    }

    public String getOwnerLastName() {
        return ownerLastName;
    }

    public void setOwnerLastName(String ownerLastName) {
        this.ownerLastName = ownerLastName;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public ProjectStatus getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(ProjectStatus projectStatus) {
        this.projectStatus = projectStatus;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + projectId;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Project other = (Project) obj;
        if (projectId != other.projectId)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Project [ownerEmail=" + ownerEmail + ", ownerFirstName=" + ownerFirstName + ", ownerLastName="
                + ownerLastName + ", projectDescription=" + projectDescription + ", projectId=" + projectId
                + ", projectStatus=" + projectStatus + ", projectTitle=" + projectTitle + "]";
    }

}

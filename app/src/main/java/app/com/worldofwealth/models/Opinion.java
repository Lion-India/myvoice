package app.com.worldofwealth.models;

import java.io.Serializable;

public class Opinion implements Serializable {

    String id, title, description, opinioncategory, opiniontype,
            videourl, videothumbnailurl, imageurl, createdby, createdbyname, upvote, downvote, comments, datatype, createddate, modifieddate, status;

    public Opinion(String title, String description, String spinnerOption,String imageurl,String videourl) {
        this.title = title;
        this.description = description;
        this.opinioncategory = spinnerOption;
        this.imageurl=imageurl;
        this.videourl=videourl;
    }

    public Opinion() {

    }


    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setOpinioncategory(String opinioncategory) {
        this.opinioncategory = opinioncategory;
    }

    public void setOpiniontype(String opiniontype) {
        this.opiniontype = opiniontype;
    }

    public void setVideourl(String videourl) {
        this.videourl = videourl;
    }

    public void setVideothumbnailurl(String videothumbnailurl) {
        this.videothumbnailurl = videothumbnailurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public void setCreatedby(String createdby) {
        this.createdby = createdby;
    }

    public void setCreatedbyname(String createdbyname) {
        this.createdbyname = createdbyname;
    }

    public void setUpvote(String upvote) {
        this.upvote = upvote;
    }

    public void setDownvote(String downvote) {
        this.downvote = downvote;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public void setCreateddate(String createddate) {
        this.createddate = createddate;
    }

    public void setModifieddate(String modifieddate) {
        this.modifieddate = modifieddate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getOpinioncategory() {
        return opinioncategory;
    }

    public String getOpiniontype() {
        return opiniontype;
    }

    public String getVideourl() {
        return videourl;
    }

    public String getVideothumbnailurl() {
        return videothumbnailurl;
    }

    public String getImageurl() {
        return imageurl;
    }

    public String getCreatedby() {
        return createdby;
    }

    public String getCreatedbyname() {
        return createdbyname;
    }

    public String getUpvote() {
        return upvote;
    }

    public String getDownvote() {
        return downvote;
    }

    public String getComments() {
        return comments;
    }

    public String getDatatype() {
        return datatype;
    }

    public String getCreateddate() {
        return createddate;
    }

    public String getModifieddate() {
        return modifieddate;
    }

    public String getStatus() {
        return status;
    }
}

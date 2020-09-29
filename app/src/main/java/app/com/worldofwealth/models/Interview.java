package app.com.worldofwealth.models;

import java.io.Serializable;

public class Interview implements Serializable {
    String id;
    String title;
    String desc;
    String startdate;
    String starttime;
    String enddate;
    String endtime;
    String createdby;
    String invitedusers;
    String videoid;
    String upvote;
    String downvote;
    String comments;
    String datatype;
    String status;
    String videourl;
    String createddate;
    String audiourl;

    public String getCreatedbyimage() {
        return createdbyimage;
    }

    public void setCreatedbyimage(String createdbyimage) {
        this.createdbyimage = createdbyimage;
    }

    String createdbyimage;

    public String getVideothumbnailurl() {
        return videothumbnailurl;
    }

    public void setVideothumbnailurl(String videothumbnailurl) {
        this.videothumbnailurl = videothumbnailurl;
    }

    String videothumbnailurl;

    public String getAudiourl() {
        return audiourl;
    }

    public void setAudiourl(String audiourl) {
        this.audiourl = audiourl;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    String imageurl;

    public String getSourcename() {
        return sourcename;
    }

    public void setSourcename(String sourcename) {
        this.sourcename = sourcename;
    }

    String sourcename;

    public String getCreatedbytype() {
        return createdbytype;
    }

    public void setCreatedbytype(String createdbytype) {
        this.createdbytype = createdbytype;
    }

    String createdbytype;

    public String getCreatedbyname() {
        return createdbyname;
    }

    public void setCreatedbyname(String createdbyname) {
        this.createdbyname = createdbyname;
    }

    String createdbyname;
   private boolean ispublished;

    public boolean isIspublished() {
        return ispublished;
    }

    public void setIspublished(boolean ispublished) {
        this.ispublished = ispublished;
    }

    public String getCreateddate() {
        return createddate;
    }

    public void setCreateddate(String createddate) {
        this.createddate = createddate;
    }

    public String getVideourl() {
        return videourl;
    }

    public void setVideourl(String videourl) {
        this.videourl = videourl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUpvote() {
        return upvote;
    }

    public void setUpvote(String upvote) {
        this.upvote = upvote;
    }

    public String getDownvote() {
        return downvote;
    }

    public void setDownvote(String downvote) {
        this.downvote = downvote;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public String getVideoid() {
        return videoid;
    }

    public void setVideoid(String videoid) {
        this.videoid = videoid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getCreatedby() {
        return createdby;
    }

    public void setCreatedby(String createdby) {
        this.createdby = createdby;
    }

    public String getInvitedusers() {
        return invitedusers;
    }

    public void setInvitedusers(String invitedusers) {
        this.invitedusers = invitedusers;
    }
}

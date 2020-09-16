package app.com.worldofwealth.models;

public class Call {
    private String callerName;

    public String getQuestionid() {
        return questionid;
    }

    private String questionid;

    public String getUpvote() {
        return upvote;
    }

    public String getDownvote() {
        return downvote;
    }

    private String upvote;
    private String downvote;

    public String getId() {
        return id;
    }

    private String id;

    public Call(String callerName, String upvote, String downvote, String questionid, String id) {
        this.callerName = callerName;
        this.upvote= upvote;
        this.downvote =downvote;
        this.questionid =questionid;
        this.id =id;
    }

    public String getCallerName() {
        return callerName;
    }


}

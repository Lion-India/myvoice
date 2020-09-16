package app.com.worldofwealth.models;

import java.util.List;

public class ResponseModal {
    public String id, title, description, createdby, media, mediatype, mediaid, mediaurl, datatype, createddate, modifieddate;
    public boolean isactive;
    public int _ts;
    public List<Questions> questions;

    public static class Questions {
        public String questionid, questiontitle, callerName;
        public List<Vote> upvote;
        public List<Vote> downvote;

        public String getQuestionid() {
            return questionid;
        }

        public String getQuestiontitle() {
            return questiontitle;
        }

        public String getCallerName() {
            return callerName;
        }
    }

    public static class Vote {
        public String userid, createddate;
    }

}

package teammates.storage.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.Text;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnSave;
import com.googlecode.objectify.annotation.Translate;
import com.googlecode.objectify.annotation.Unindex;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.util.Const;

/**
 * Represents a feedback question.
 */
@Entity
@Index
public class FeedbackQuestion extends BaseEntity {

    // TODO: where applicable, we should specify fields as @Unindex to prevent GAE from building unnecessary indexes.

    /**
     * Setting this to true prevents changes to the lastUpdate time stamp. Set
     * to true when using scripts to update entities when you want to preserve
     * the lastUpdate time stamp.
     **/
    @Ignore
    public boolean keepUpdateTimestamp;

    @Id
    private Long feedbackQuestionId;

    private String feedbackSessionName;

    private String courseId;

    // TODO: Do we need this field since creator of FS = creator of qn? (can be removed -damith)
    private String creatorEmail;

    /**
     * Serialized {@link teammates.common.datatransfer.questions.FeedbackQuestionDetails} stored as a string.
     *
     * @see teammates.common.datatransfer.attributes.FeedbackQuestionAttributes#getQuestionDetails()
     */
    @Unindex
    private Text questionText;

    @Unindex
    private Text questionDescription;

    private int questionNumber;

    private FeedbackQuestionType questionType;

    private FeedbackParticipantType giverType;

    private FeedbackParticipantType recipientType;

    // Check for consistency in questionLogic/questionAttributes.
    // (i.e. if type is own team, numberOfEntities must = 1).
    private int numberOfEntitiesToGiveFeedbackTo;

    private List<FeedbackParticipantType> showResponsesTo = new ArrayList<>();

    private List<FeedbackParticipantType> showGiverNameTo = new ArrayList<>();

    private List<FeedbackParticipantType> showRecipientNameTo = new ArrayList<>();

    @Translate(value = InstantTranslatorFactory.class)
    private Instant createdAt;

    @Translate(value = InstantTranslatorFactory.class)
    private Instant updatedAt;

    @SuppressWarnings("unused")
    private FeedbackQuestion() {
        // required by Objectify
    }

    public FeedbackQuestion(
            String feedbackSessionName, String courseId, String creatorEmail,
            String questionText, String questionDescription, int questionNumber, FeedbackQuestionType questionType,
            FeedbackParticipantType giverType,
            FeedbackParticipantType recipientType,
            int numberOfEntitiesToGiveFeedbackTo,
            List<FeedbackParticipantType> showResponsesTo,
            List<FeedbackParticipantType> showGiverNameTo,
            List<FeedbackParticipantType> showRecipientNameTo) {

        this.feedbackQuestionId = null; // Allow GAE to generate key.
        this.feedbackSessionName = feedbackSessionName;
        this.courseId = courseId;
        this.creatorEmail = creatorEmail;
        setQuestionText(questionText);
        setQuestionDescription(questionDescription);
        this.questionNumber = questionNumber;
        this.questionType = questionType;
        this.giverType = giverType;
        this.recipientType = recipientType;
        this.numberOfEntitiesToGiveFeedbackTo = numberOfEntitiesToGiveFeedbackTo;
        this.showResponsesTo = showResponsesTo == null ? new ArrayList<FeedbackParticipantType>() : showResponsesTo;
        this.showGiverNameTo = showGiverNameTo == null ? new ArrayList<FeedbackParticipantType>() : showGiverNameTo;
        this.showRecipientNameTo =
                showRecipientNameTo == null ? new ArrayList<FeedbackParticipantType>() : showRecipientNameTo;
        this.setCreatedAt(Instant.now());
    }

    public Instant getCreatedAt() {
        return createdAt == null ? Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP : createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt == null ? Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP : updatedAt;
    }

    public void setCreatedAt(Instant newDate) {
        this.createdAt = newDate;
        setLastUpdate(newDate);
    }

    public void setLastUpdate(Instant newDate) {
        if (!keepUpdateTimestamp) {
            this.updatedAt = newDate;
        }
    }

    public String getId() {
        return Key.create(FeedbackQuestion.class, feedbackQuestionId).toWebSafeString();
    }

    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }

    public void setFeedbackSessionName(String feedbackSessionName) {
        this.feedbackSessionName = feedbackSessionName;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }

    public String getQuestionMetaData() {
        return questionText == null ? null : questionText.getValue();
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText == null ? null : new Text(questionText);
    }

    public String getQuestionDescription() {
        return questionDescription == null ? null : questionDescription.getValue();
    }

    public void setQuestionDescription(String questionDescription) {
        this.questionDescription = questionDescription == null ? null : new Text(questionDescription);
    }

    public FeedbackQuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(FeedbackQuestionType questionType) {
        this.questionType = questionType;
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }

    public FeedbackParticipantType getGiverType() {
        return giverType;
    }

    public void setGiverType(FeedbackParticipantType giverType) {
        this.giverType = giverType;
    }

    public FeedbackParticipantType getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(FeedbackParticipantType recipientType) {
        this.recipientType = recipientType;
    }

    public int getNumberOfEntitiesToGiveFeedbackTo() {
        return numberOfEntitiesToGiveFeedbackTo;
    }

    public void setNumberOfEntitiesToGiveFeedbackTo(
            int numberOfEntitiesToGiveFeedbackTo) {
        this.numberOfEntitiesToGiveFeedbackTo = numberOfEntitiesToGiveFeedbackTo;
    }

    public List<FeedbackParticipantType> getShowResponsesTo() {
        return showResponsesTo;
    }

    public void setShowResponsesTo(List<FeedbackParticipantType> showResponsesTo) {
        this.showResponsesTo = showResponsesTo;
    }

    public List<FeedbackParticipantType> getShowGiverNameTo() {
        return showGiverNameTo;
    }

    public void setShowGiverNameTo(List<FeedbackParticipantType> showGiverNameTo) {
        this.showGiverNameTo = showGiverNameTo;
    }

    public List<FeedbackParticipantType> getShowRecipientNameTo() {
        return showRecipientNameTo;
    }

    public void setShowRecipientNameTo(
            List<FeedbackParticipantType> showRecipientNameTo) {
        this.showRecipientNameTo = showRecipientNameTo;
    }

    @OnSave
    public void updateLastUpdateTimestamp() {
        this.setLastUpdate(Instant.now());
    }
}

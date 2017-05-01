package br.edu.ffb.pedro.aulap2pprofessor.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonIgnore;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.Transient;

import java.util.Arrays;
import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

@SuppressWarnings("WeakerAccess")
@JsonObject
@Entity
public class Questionnaire {

    @JsonIgnore
    @Transient
    public static final long DEFAULT_QUESTIONNAIRE = 1;

    @JsonIgnore
    @Transient
    public static final String TAG = "Questionnaire";

    @JsonField
    @Id(autoincrement = true)
    public Long id;

    @JsonField
    public float overallScore;

    @JsonField
    public String name;

    @JsonField
    public String studentName;

    @JsonField
    @ToMany(referencedJoinProperty = "questionnaireId")
    public List<Question> questions;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1490406364)
    private transient QuestionnaireDao myDao;

    @Generated(hash = 1531558956)
    public Questionnaire(Long id, float overallScore, String name,
            String studentName) {
        this.id = id;
        this.overallScore = overallScore;
        this.name = name;
        this.studentName = studentName;
    }

    @Generated(hash = 1622293676)
    public Questionnaire() {
    }

    @Override
    public String toString() {
        return "Questionnaire{" +
                "id=" + getId() +
                ", overallScore=" + getOverallScore() +
                ", name='" + getName() + '\'' +
                ", studentName='" + getStudentName() + '\'' +
                ", questions=" + Arrays.toString(getQuestions().toArray()) +
                '}';
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public float getOverallScore() {
        return this.overallScore;
    }

    public void setOverallScore(float overallScore) {
        this.overallScore = overallScore;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStudentName() {
        return this.studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1332835056)
    public List<Question> getQuestions() {
        if (questions == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            QuestionDao targetDao = daoSession.getQuestionDao();
            List<Question> questionsNew = targetDao
                    ._queryQuestionnaire_Questions(id);
            synchronized (this) {
                if (questions == null) {
                    questions = questionsNew;
                }
            }
        }
        return questions;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1619718141)
    public synchronized void resetQuestions() {
        questions = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1537524527)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getQuestionnaireDao() : null;
    }
    
}

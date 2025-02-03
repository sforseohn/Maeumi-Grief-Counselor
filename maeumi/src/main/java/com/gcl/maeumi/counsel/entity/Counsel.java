package com.gcl.maeumi.counsel.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.gcl.maeumi.counsel.service.ResponseData;
import com.gcl.maeumi.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "`counsel`")
@Getter
@NoArgsConstructor
public class Counsel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @Column(nullable = false, unique = true)
    private String sessionId;

    @Column(nullable = false)
    private int sessionNumber;

    @Column(nullable = true)
    @ElementCollection
    private List<ResponseData> responses = new ArrayList<>();;

    @Column(nullable = false)
    private Date startTime;

    @Column(nullable = true)
    private Date endTime;

    @Builder
    public Counsel(Long id, User user, String sessionId, int sessionNumber, Date startTime) {
        this.id = id;
        this.user = user;
        this.sessionId = sessionId;
        this.sessionNumber = sessionNumber;
        this.startTime = startTime;
    }

    public void addResponse(Integer questionId, String userResponse) {
        this.responses.add(new ResponseData(questionId, userResponse));
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}


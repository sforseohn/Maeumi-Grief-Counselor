package com.gcl.maeumi.member.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.gcl.maeumi.counsel.entity.Counsel;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.util.List;

@Entity
@Table(name = "`member`")
@Getter
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 10)
    private String name;

    @Column(nullable = false)
    private int currentSession;

    @Column(nullable = true)
    private String connectionTime;

    @JsonManagedReference
    @OneToMany(cascade=CascadeType.ALL, mappedBy="member", orphanRemoval=true)
    private List<Counsel> counsels;

    @Builder
    private Member(Long id, String username, String password, String name) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.currentSession = 1;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}

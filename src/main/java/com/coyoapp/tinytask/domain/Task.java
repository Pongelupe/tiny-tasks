package com.coyoapp.tinytask.domain;

import java.time.Instant;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.Setter;

@Table(name = "task")
@Entity
@Setter
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Task {

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name = "id", nullable = false, updatable = false)
	private String id;

	private String name;

	@CreatedDate
	private Instant created;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "attach_id", referencedColumnName = "id")
	private Attach attach;

}

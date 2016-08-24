package com.primeradiants.oniri.user;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a email validation token attached to a ONIRI user.
 * @author Shanira
 * @since 0.1.1
 */
@Entity
@Table(name="emailvalidationtoken", uniqueConstraints={@UniqueConstraint(columnNames={"id"})})
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmailValidationTokenEntity {

	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id", nullable=false, unique=true, length=11)
    private int id;
	
	@Column(nullable=false, unique=true, length=255)
	private String token;
	
	@ManyToOne(fetch = FetchType.EAGER) 
    @JoinColumn(nullable=false, updatable=false)
	private UserEntity user;
	
	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;
}

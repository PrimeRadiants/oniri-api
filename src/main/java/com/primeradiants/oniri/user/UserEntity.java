package com.primeradiants.oniri.user;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a person who uses ONIRI.
 * @author Shanira
 * @since 0.1.0
 */
@Entity
@Table(name="user", uniqueConstraints={@UniqueConstraint(columnNames={"id"})})
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserEntity 
{
	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id", nullable=false, unique=true, length=11)
    private int id;
	
	@Column(nullable=false, unique=true, length=35)
	private String username;
	
	@Column(nullable=false, unique=true, length=255)
	private String email;
	
	@Column(nullable=false)
	private String password;
	
	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;
	
	@Column
	private Boolean admin;
}

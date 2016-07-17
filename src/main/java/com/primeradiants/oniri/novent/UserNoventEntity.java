package com.primeradiants.oniri.novent;

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

import com.primeradiants.oniri.user.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the link between a novent and a user library
 * @author Shanira
 * @since 0.1.0
 */
@Entity
@Table(name="user_novent", uniqueConstraints={@UniqueConstraint(columnNames={"id"})})
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserNoventEntity {

	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id", nullable=false, unique=true, length=11)
    private int id;
	
	@ManyToOne(fetch = FetchType.EAGER) 
    @JoinColumn(nullable=false, updatable=false)
	private UserEntity user;
	
	@ManyToOne(fetch = FetchType.EAGER) 
    @JoinColumn(nullable=false, updatable=false)
	private NoventEntity novent;
	
	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date purchase;
}

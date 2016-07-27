package com.primeradiants.oniri.novent;

import java.util.Date;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Wraps the useful data of a Novent.
 * @author Shanira
 * @since 0.1.0
 */
@Entity
@Table(name="novent", uniqueConstraints={@UniqueConstraint(columnNames={"id"})})
@AllArgsConstructor
@NoArgsConstructor
@Data
public class NoventEntity {

	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id", nullable=false, unique=true, length=11)
    private int id;
	
	@Column(nullable=false, unique=true, length=255)
	private String title;
	
	@ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name="novent_authors", joinColumns=@JoinColumn(name="novent_id"))
	@Column(name="authors", nullable=false, length=255)
	private List<String> authors;
	
	@Column(columnDefinition="Text")
	private String description;
	
	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date publication;
	
	//File system URI to the novent cover image
	@Column
	private String coverPath;
	
	//File system URI to the corresponding .novent file
	@Column
	private String noventPath;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NoventEntity other = (NoventEntity) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}
}

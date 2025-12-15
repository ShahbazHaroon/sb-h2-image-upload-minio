/*
 * @author Muhammad Ubaid Ur Raheem Ahmad AKA Shahbaz Haroon
 * Email: shahbazhrn@gmail.com
 * Cell: +923002585925
 * GitHub: https://github.com/ShahbazHaroon
 */

package com.ubaidsample.h2.entity;

import com.ubaidsample.h2.dto.common.AuditHistoryDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Comment("Stores user information")
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_user_username", columnNames = "user_name"),
				@UniqueConstraint(name = "uk_user_idempotency_key", columnNames = "idempotency_key")
        })
public class User implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "user_id", nullable = false, updatable = false)
	@Comment("Unique identifier for each user")
    private Long userId;

	@Column(name= "idempotency_key", nullable = false, updatable = false)
	@Comment("Unique idempotency key for each user")
	private String idempotencyKey;

    @Column(name = "user_name", nullable = false, length = 50)
	@Comment("User name")
    private String userName;

    @Column(name = "email", nullable = false, length = 50)
	@Comment("User email")
    private String email;

    @Column(name = "password", nullable = false, length = 255)
	@Comment("User password")
    private String password;

    @Column(name = "date_of_birth", nullable = false)
	@Comment("User dateOfBirth")
	private LocalDate dateOfBirth;

	@Column(name = "date_of_leaving", nullable = false)
	@Comment("User dateOfLeaving")
	private LocalDate dateOfLeaving;

	@Column(name = "postal_code", nullable = false)
	@Comment("User postalCode")
	private Integer postalCode;

	@Column(name = "profile_image_object_name")
	@Comment("User profile image object name")
	private String profileImageObjectName; // Save only objectName

	@Column(name = "profile_image_bucket")
	@Comment("User profile image bucket")
	private String profileImageBucket; // Optional, if you use multiple buckets

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "createdBy", column = @Column(name = "created_by")),
			@AttributeOverride(name = "createdDate", column = @Column(name = "created_date")),
			@AttributeOverride(name = "updatedBy", column = @Column(name = "updated_by")),
			@AttributeOverride(name = "updatedDate", column = @Column(name = "updated_date")),
	})
	@Comment("Auditing related fields")
	private AuditHistoryDTO auditHistoryDTO  = new AuditHistoryDTO();
}
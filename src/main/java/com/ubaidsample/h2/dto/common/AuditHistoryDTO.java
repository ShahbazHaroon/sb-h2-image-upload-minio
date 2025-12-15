/*
 * @author Muhammad Ubaid Ur Raheem Ahmad AKA Shahbaz Haroon
 * Email: shahbazhrn@gmail.com
 * Cell: +923002585925
 * GitHub: https://github.com/ShahbazHaroon
 */

package com.ubaidsample.h2.dto.common;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class AuditHistoryDTO {

    @CreatedBy
    @Column(name = "created_by", nullable = false, updatable = false)
    @Comment("Who saves this")
    private String createdBy;

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    @Comment("At what date the data was saved")
    private LocalDateTime createdDate;

    @LastModifiedBy
    @Column(name = "updated_by", nullable = true)
    @Comment("Who updated this")
    private String updatedBy;

    @LastModifiedDate
    @Column(name = "updated_date", nullable = true)
    @Comment("At what date the data was updated")
    private LocalDateTime updatedDate;

    @Column(name = "is_deleted", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    @Comment("Flag to mark soft delete")
    private boolean deleted = false;

    @Column(name = "deleted_date")
    @Comment("At what date the data was deleted")
    private LocalDateTime deletedDate;
}
package com.ead.course.repository;


import com.ead.course.models.CourseModel;

import java.util.UUID;

public interface CourseRepository extends JpaRepository<CourseModel, UUID> {
}

package com.smuhack13.server.api.repository;

import com.smuhack13.server.api.domain.Label;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabelRepository extends JpaRepository<Label, Long> {
}

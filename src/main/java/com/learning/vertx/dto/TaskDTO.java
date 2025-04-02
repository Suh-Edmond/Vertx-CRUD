package com.learning.vertx.dto;

import java.time.LocalDateTime;

public class TaskDTO {
  private String id;

  private String content;

  private Boolean completed;

  private String userId;

  private LocalDateTime createdAt;

  public TaskDTO() {
  }

  public TaskDTO(String id, String content, Boolean completed, String userId, LocalDateTime createdAt) {
    this.id = id;
    this.content = content;
    this.completed = completed;
    this.userId = userId;
    this.createdAt = createdAt;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Boolean getCompleted() {
    return completed;
  }

  public void setCompleted(Boolean completed) {
    this.completed = completed;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}

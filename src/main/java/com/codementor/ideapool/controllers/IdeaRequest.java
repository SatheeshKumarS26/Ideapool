package com.codementor.ideapool.controllers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Created by Vishwa Mohan, 19th April 2019
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdeaRequest {
  private String content;
  private float impact;
  private float ease;
  private float confidence;

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public float getImpact() {
    return impact;
  }

  public void setImpact(float impact) {
    this.impact = impact;
  }

  public float getEase() {
    return ease;
  }

  public void setEase(float ease) {
    this.ease = ease;
  }

  public float getConfidence() {
    return confidence;
  }

  public void setConfidence(float confidence) {
    this.confidence = confidence;
  }
}

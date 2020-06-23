package com.codementor.ideapool.beans;

import java.util.Date;
import java.util.Objects;


/**
 * Created by Vishwa Mohan, 19th Feb 2019
 */
public class Idea implements Comparable<Idea> {

  private String id;
  private String content;
  private float impact;
  private float ease;
  private float confidence;
  private float average_score;
  private long created_at;

  public Idea(String id, String content, float impact, float ease, float confidence) {
    this.id = id;
    this.content = content;
    this.impact = impact;
    this.ease = ease;
    this.confidence = confidence;
    this.average_score = (impact + ease + confidence) / 3;
    this.created_at = (new Date()).getTime();
  }

  public void generateAverageScore() {
    this.average_score = (this.impact + this.ease + this.confidence) / 3;
  }

  @Override
  public int compareTo(Idea otherIdead) {
    return Double.compare(this.average_score, otherIdead.getAverage_score());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Idea idea = (Idea) o;
    return Float.compare(idea.impact, impact) == 0 && Float.compare(idea.ease, ease) == 0
        && Float.compare(idea.confidence, confidence) == 0 && Float.compare(idea.average_score, average_score) == 0
        && id.equals(idea.id) && content.equals(idea.content);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, content, impact, ease, confidence, average_score);
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

  public float getAverage_score() {
    return average_score;
  }

  public void setAverage_score(float average_score) {
    this.average_score = average_score;
  }

  public long getCreated_at() {
    return created_at;
  }

  public void setCreated_at(long created_at) {
    this.created_at = created_at;
  }
}

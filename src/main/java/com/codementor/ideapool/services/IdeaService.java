package com.codementor.ideapool.services;

import com.codementor.ideapool.beans.Idea;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;


/**
 * Created by Vishwa Mohan, 19th April 2019
 */

@Component
public class IdeaService {

  private Map<String, List<Idea>> userIdeas;

  @PostConstruct
  public void init() {
    userIdeas = new HashMap<>();
  }

  public void saveIdea(String emailId, Idea idea) {
    if (this.userIdeas.containsKey(emailId)) {
      this.userIdeas.get(emailId).add(idea);
    } else {
      List<Idea> ideaList = new ArrayList<>();
      ideaList.add(idea);
      this.userIdeas.put(emailId, ideaList);
    }
  }

  /**
   *
   * @param emailId
   * @param count
   * @return
   */
  public List<Idea> getIdeas(String emailId, int count) {
    List<Idea> ideas = new ArrayList<>();
    if (this.userIdeas.containsKey(emailId)) {
      List<Idea> totalIdeas = this.userIdeas.get(emailId);
      Collections.sort(totalIdeas ,Collections.reverseOrder());
      ideas = totalIdeas.subList(0,Math.min(totalIdeas.size(),count));

    }

    return ideas;
  }

  /**
   *
   * @param emailId
   * @param id
   */
  public void deleteIdea(String emailId, String id) {
    if (this.userIdeas.containsKey(emailId)) {
      List<Idea> ideas = this.userIdeas.get(emailId);

      Idea ideadToBeRemoved = null;
      for (Idea idea : ideas) {
        if (idea.getId().equals(id)) {
          ideadToBeRemoved = idea;
        }
      }
      if (ideadToBeRemoved != null) {
        ideas.remove(ideadToBeRemoved);
      }
    }
  }

  public Idea updateIdea(String emailId, String id, String content, float impact, float ease, float confidence) {

    Idea ideaToBeModified = null;
    if (this.userIdeas.containsKey(emailId)) {
      List<Idea> ideas = this.userIdeas.get(emailId);

      for (Idea idea : ideas) {
        if (idea.getId().equals(id)) {
          ideaToBeModified = idea;
        }
      }
      if (ideaToBeModified != null) {

        ideaToBeModified.setContent(content);
        ideaToBeModified.setImpact(impact);
        ideaToBeModified.setEase(ease);
        ideaToBeModified.setConfidence(confidence);
        ideaToBeModified.generateAverageScore();
      }
    }
    return ideaToBeModified;
  }
}

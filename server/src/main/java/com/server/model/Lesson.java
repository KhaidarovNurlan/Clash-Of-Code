package com.server.model;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="lessons")
public class Lesson {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="course_id") @JsonIgnore private Course course;
  private String title;
  private String content;
  @Column(name="order_number") private Integer orderNumber;
  private Integer points = 0;

  public Lesson(){}
  public Long getId(){return id;}
  public Course getCourse(){return course;}
  public void setCourse(Course c){this.course=c;}
  public String getTitle(){return title;}
  public void setTitle(String s){this.title=s;}
  public String getContent(){return content;}
  public void setContent(String c){this.content=c;}
  public Integer getOrderNumber(){return orderNumber;}
  public void setOrderNumber(Integer o){this.orderNumber=o;}
  public Integer getPoints(){return points;}
  public void setPoints(Integer p){this.points=p;}
}

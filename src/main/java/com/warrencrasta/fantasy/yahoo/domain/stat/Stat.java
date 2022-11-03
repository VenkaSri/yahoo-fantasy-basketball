package com.warrencrasta.fantasy.yahoo.domain.stat;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@Entity
public class Stat {

  @Id
  private String Player;
  private double FGP;
  private double threePP;
  private double FTP;
  private double TRB;
  private double AST;
  private double STL;
  private double BLK;
  private double TOV;
  private double PTS;
}

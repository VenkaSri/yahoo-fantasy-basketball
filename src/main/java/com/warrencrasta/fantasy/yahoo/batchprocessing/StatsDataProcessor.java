package com.warrencrasta.fantasy.yahoo.batchprocessing;

import com.warrencrasta.fantasy.yahoo.domain.stat.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class StatsDataProcessor implements ItemProcessor<StatsInput, Stat> {

  private static final Logger log = LoggerFactory.getLogger(StatsDataProcessor.class);

  @Override
  public Stat process(final StatsInput statsInput) throws Exception {

    Stat stat = new Stat();
    stat.setPlayer(statsInput.getPlayer());
    stat.setAST(Double.parseDouble(statsInput.getAST()));
    stat.setBLK(Double.parseDouble(statsInput.getBLK()));
    stat.setFGP(Double.parseDouble(statsInput.getFGP()));
    stat.setFTP(Double.parseDouble(statsInput.getFTP()));
    stat.setPTS(Double.parseDouble(statsInput.getPTS()));
    stat.setSTL(Double.parseDouble(statsInput.getSTL()));
    stat.setThreePP(Double.parseDouble(statsInput.getThreePP()));
    stat.setTRB(Double.parseDouble(statsInput.getTRB()));
    stat.setTOV(Double.parseDouble(statsInput.getTOV()));

    return stat;

  }

}

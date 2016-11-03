package io.hops.gvod.responses;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author jsvhqr
 */
@XmlRootElement
public class HopsContentsSummaryJSON {

  private List<ElementSummaryJSON> contents = new ArrayList<>();

  public HopsContentsSummaryJSON(List<ElementSummaryJSON> contents) {
    this.contents = contents;
  }

  public HopsContentsSummaryJSON() {
  }

  public List<ElementSummaryJSON> getContents() {
    return contents;
  }

  public void setContents(List<ElementSummaryJSON> contents) {
    this.contents = contents;
  }
}

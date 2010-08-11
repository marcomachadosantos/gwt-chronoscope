package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.data.tuple.Tuple5D;
import org.timepedia.chronoscope.client.util.Array1D;

/**
 * @author chad takahashi
 */
public final class FlyweightTuple implements Tuple5D {
  private int dataPointIndex = 0;
  private double[] domainData;
  private double[][] rangeTupleData;
  private int tupleLength;
  
  public FlyweightTuple(Array1D domain, Array1D[] rangeTuples) {
    this.tupleLength = 1 + rangeTuples.length;
    rangeTupleData = new double[rangeTuples.length][];
    setDomainAndRange(domain, rangeTuples);
  }
  
  public void setDomainAndRange(Array1D domain, Array1D[] rangeTuples) {
    domainData = domain.backingArray();
    for (int i = 0; i < this.rangeTupleData.length; i++) {
      rangeTupleData[i] = rangeTuples[i].backingArray();
    }
  }
  
  public double getRange(int rangeTupleIndex) {
    return rangeTupleData[rangeTupleIndex][this.dataPointIndex];
  }

  public int size() {
    return tupleLength;
  }

  public double getDomain() {
    return domainData[this.dataPointIndex];
  }

  public double getRange0() {
    return rangeTupleData[0][this.dataPointIndex];
  }

  public double getRange1() {
    return rangeTupleData[1][this.dataPointIndex];
  }

  public double getRange2() {
    return rangeTupleData[2][this.dataPointIndex];
  }

  public double getRange3() {
    return rangeTupleData[3][this.dataPointIndex];
  }
  
  public void setDataPointIndex(int dataPointIndex) {
    this.dataPointIndex = dataPointIndex;
  }
}

package com.guidewire.solr;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.docvalues.DoubleDocValues;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

public class RecentFunction extends ValueSource {

  protected final ValueSource variableDate;
  protected final long targetDate;

  private static final long MILLISECONDS_IN_DAY = 86400000L;

  public RecentFunction(long targetDate, ValueSource variableDate) {
    this.targetDate = targetDate;
    this.variableDate = variableDate;
  }

  private double recip(long x, long m, long a, long b) {
    return (double)a/(double)(m*x+b);
  }

  @Override
  public FunctionValues getValues(final Map map, final AtomicReaderContext atomicReaderContext) throws IOException {

    final FunctionValues inputValues = variableDate.getValues(map, atomicReaderContext);

    return new DoubleDocValues(this) {
      @Override
      public double doubleVal(int doc) {
        String dateStr = inputValues.strVal(doc);
        try {
          return recip(Math.abs(RecentFunctionParser.format.parse(dateStr).getTime() - targetDate), 1L, MILLISECONDS_IN_DAY, MILLISECONDS_IN_DAY);
        } catch (ParseException e) {
          System.out.println(String.format("Could not parse variableDate: %s", dateStr));
        }
        return 0.0D;
      }
    };
  }

  @Override
  public boolean equals(Object o) {
    if (this.getClass() != o.getClass()) {
      return false;
    }
    RecentFunction that = (RecentFunction) o;
    return this.variableDate.equals(that.variableDate);
  }

  @Override
  public int hashCode() {
    long combinedHashes = this.variableDate.hashCode();
    return (int) (combinedHashes ^ (combinedHashes >>> 32));
  }

  @Override
  public String description() {
    return "recent function";
  }
}

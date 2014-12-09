package com.guidewire.solr;

import org.apache.lucene.queries.function.ValueSource;
import org.apache.solr.search.FunctionQParser;
import org.apache.solr.search.SyntaxError;
import org.apache.solr.search.ValueSourceParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class RecentFunctionParser extends ValueSourceParser {

  static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // Solr Zulu format

  @Override
  public ValueSource parse(FunctionQParser functionQParser) throws SyntaxError {
    long targetDate;
    try {
      targetDate  = format.parse(functionQParser.parseArg()).getTime();
    } catch (ParseException pe) {
      throw new SyntaxError("Cannot parse target date", pe);
    }
    ValueSource variableDate = functionQParser.parseValueSource();
    return new RecentFunction(targetDate, variableDate);
  }
}

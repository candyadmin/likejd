 package com.shopping.lucene;
 
 import com.shopping.core.tools.CommUtil;
 import org.apache.lucene.analysis.Analyzer;
 import org.apache.lucene.queryParser.ParseException;
 import org.apache.lucene.queryParser.QueryParser;
 import org.apache.lucene.search.NumericRangeQuery;
 import org.apache.lucene.search.Query;
 import org.apache.lucene.util.Version;
 
 public class ShopQueryParser extends QueryParser
 {
   public ShopQueryParser(Version matchVersion, String f, Analyzer a)
   {
     super(matchVersion, f, a);
   }
 
   protected Query getRangeQuery(String field, String part1, String part2, boolean inclusive)
     throws ParseException
   {
     if ("store_price".equals(field)) {
       return NumericRangeQuery.newDoubleRange(field, 
         Double.valueOf(CommUtil.null2Double(part1)), Double.valueOf(CommUtil.null2Double(part2)), 
         inclusive, inclusive);
     }
     if ("add_time".equals(field)) {
       return NumericRangeQuery.newLongRange(field, Long.valueOf(Long.parseLong(part1)), 
         Long.valueOf(Long.parseLong(part2)), inclusive, inclusive);
     }
     return super.newRangeQuery(field, part1, part2, inclusive);
   }
 }

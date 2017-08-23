package com.shopping.lucene;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.wltea.analyzer.lucene.IKAnalyzer;
import org.wltea.analyzer.lucene.IKSimilarity;

import com.shopping.core.tools.CommUtil;

public class LuceneUtil {
	private static File index_file = null;

	private static Analyzer analyzer = null;

	private static LuceneUtil lucence = new LuceneUtil();

	private static QueryParser parser;

	private static String index_path;

	private int textmaxlength = 2000;

	private static String prefixHTML = "<font color='red'>";

	private static String suffixHTML = "</font>";

	private int pageSize = 20;

	public LuceneUtil() {
		analyzer = new IKAnalyzer();
		parser = new ShopQueryParser(Version.LUCENE_35, "title", analyzer);
	}

	public static LuceneUtil instance() {
		return lucence;
	}

	public static void setIndex_path(String index_path2) {
		index_path = index_path2;
	}

	public List<LuceneVo> searchIndex(String keyword, int start, int size, double begin_price, double end_price, Sort sort) throws IOException {
		IndexSearcher searcher = null;
		List list = new ArrayList();
		IndexReader reader = null;
		try {
			index_file = new File(index_path);
			reader = IndexReader.open(FSDirectory.open(index_file));
			searcher = new IndexSearcher(reader);
			searcher.setSimilarity(new IKSimilarity());
			if (keyword.indexOf("title:") < 0) {
				keyword = "(title:" + keyword + " OR content:" + keyword + ")";
			}
			if ((begin_price >= 0.0D) && (end_price > 0.0D)) {
				keyword = keyword + " AND store_price:[" + begin_price + " TO " + end_price + "]";
			}

			parser.setAllowLeadingWildcard(true);
			Query query = parser.parse(keyword);
			TopDocs topDocs = null;
			if (sort != null)
				topDocs = searcher.search(query, size + start, sort);
			else {
				topDocs = searcher.search(query, size + start);
			}
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			int end = size + start < topDocs.totalHits ? size + start : topDocs.totalHits;
			for (int i = start; i < end; i++) {
				Document doc = searcher.doc(scoreDocs[i].doc);
				LuceneVo vo = new LuceneVo();

				SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter(prefixHTML, suffixHTML);
				Highlighter highlighter = new Highlighter(simpleHTMLFormatter, new QueryScorer(query));
				highlighter.setTextFragmenter(new SimpleFragmenter(this.textmaxlength));
				String content = highlighter.getBestFragment(analyzer, "content", doc.get("content"));
				String title = highlighter.getBestFragment(analyzer, "title", doc.get("title"));
				if (content == null)
					vo.setVo_content(doc.get("content"));
				else {
					vo.setVo_content(content);
				}

				vo.setVo_id(CommUtil.null2Long(doc.get("id")));
				vo.setVo_title(title);
				vo.setVo_url(doc.get("url"));
				vo.setVo_add_time(CommUtil.null2Long(doc.get("add_time")).longValue());
				vo.setVo_store_price(CommUtil.null2Double(doc.get("store_price")));
				list.add(vo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			searcher.close();
			reader.close();
		}
		return list;
	}

	public LuceneResult search(String params, int pageNo, double begin_price, double end_price, ScoreDoc after, Sort sort) {
		LuceneResult pList = new LuceneResult();
		IndexSearcher isearcher = null;
		List list = new ArrayList();
		IndexReader reader = null;
		try {
			index_file = new File(index_path);
			if (!index_file.exists()) {
				LuceneResult localLuceneResult1 = pList;
				return localLuceneResult1;
			}
			reader = IndexReader.open(FSDirectory.open(index_file), true);
			isearcher = new IndexSearcher(reader);

			isearcher.setSimilarity(new IKSimilarity());
			if (params.indexOf("title:") < 0) {
				params = "(title:" + params + " OR content:" + params + ")";
			}
			if ((begin_price >= 0.0D) && (end_price > 0.0D)) {
				params = params + " AND store_price:[" + begin_price + " TO " + end_price + "]";
			}
			parser.setAllowLeadingWildcard(true);
			Query query = parser.parse(params);
			TopDocs topDocs = isearcher.searchAfter(after, query, this.pageSize);

			int pages = (topDocs.totalHits + this.pageSize - 1) / this.pageSize;
			int intPageNo = pageNo > pages ? pages : pageNo;
			if (intPageNo < 1)
				intPageNo = 1;
			List vo_list = searchIndex(params, (intPageNo - 1) * this.pageSize, this.pageSize, begin_price, end_price, sort);
			pList.setPages(pages);
			pList.setRows(topDocs.totalHits);
			pList.setCurrentPage(intPageNo);
			pList.setVo_list(vo_list);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (isearcher != null)
				try {
					isearcher.close();
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		if (isearcher != null) {
			try {
				isearcher.close();
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		return pList;
	}

	public void writeIndex(List<LuceneVo> list) throws IOException {
		IndexWriter writer = openIndexWriter();
		try {
			for (LuceneVo lucenceVo : list) {
				Document document = builderDocument(lucenceVo);
				writer.addDocument(document);
			}
			writer.optimize();
		} finally {
			writer.close();
		}
	}

	public void writeIndex(LuceneVo vo) {
		IndexWriter writer = null;
		try {
			writer = openIndexWriter();
			Document document = builderDocument(vo);
			writer.addDocument(document);
			writer.optimize();
		} catch (IOException e1) {
			e1.printStackTrace();
			try {
				writer.close();
			} catch (CorruptIndexException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} finally {
			try {
				writer.close();
			} catch (CorruptIndexException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void update(String id, LuceneVo vo) {
		try {
			index_file = new File(index_path);
			Directory directory = FSDirectory.open(index_file);
			IndexWriterConfig writerConfig = new IndexWriterConfig(Version.LUCENE_35, analyzer);
			IndexWriter writer = new IndexWriter(directory, writerConfig);
			Document doc = builderDocument(vo);
			Term term = new Term("id", String.valueOf(id));
			writer.updateDocument(term, doc);

			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void delete_index(String id) {
		try {
			index_file = new File(index_path);
			Directory directory = FSDirectory.open(index_file);
			IndexWriterConfig writerConfig = new IndexWriterConfig(Version.LUCENE_35, analyzer);
			IndexWriter writer = new IndexWriter(directory, writerConfig);
			Term term = new Term("id", String.valueOf(id));
			writer.deleteDocuments(term);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteAllIndex(boolean isdeletefile) {
		IndexReader reader = null;
		index_file = new File(index_path);
		if ((index_file.exists()) && (index_file.isDirectory())) {
			try {
				reader = IndexReader.open(FSDirectory.open(index_file), false);
				for (int i = 0; i < reader.maxDoc(); i++) {
					reader.deleteDocument(i);
				}
				reader.close();
			} catch (Exception ex) {
				ex.printStackTrace();

				if (reader != null)
					try {
						reader.close();
					} catch (IOException localIOException) {
					}
			} finally {
				if (reader != null)
					try {
						reader.close();
					} catch (IOException localIOException1) {
					}
			}
			deleteAllFile();
		}
	}

	private void deleteAllFile() {
		index_file = new File(index_path);
		File[] files = index_file.listFiles();
		for (int i = 0; i < files.length; i++)
			files[i].delete();
	}

	private IndexWriter openIndexWriter() throws IOException {
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_35, analyzer);

		index_file = new File(index_path);
		indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
		IndexWriter writer = new IndexWriter(FSDirectory.open(index_file), indexWriterConfig);
		writer.setMaxBufferedDocs(100);
		return writer;
	}

	private Document builderDocument(LuceneVo luceneVo) {
		Document document = new Document();
		Whitelist white = new Whitelist();
		Field id = new Field("id", String.valueOf(luceneVo.getVo_id()), Field.Store.YES, Field.Index.ANALYZED);
		Field title = new Field("title", Jsoup.clean(luceneVo.getVo_title(), Whitelist.none()), Field.Store.YES, Field.Index.ANALYZED);
		title.setBoost(10.0F);
		Field content = new Field("content", Jsoup.clean(luceneVo.getVo_content(), Whitelist.none()), Field.Store.YES, Field.Index.ANALYZED);
		Field type = new Field("type", luceneVo.getVo_type(), Field.Store.YES, Field.Index.ANALYZED);
		NumericField store_price = new NumericField("store_price", Field.Store.YES, true);
		store_price.setDoubleValue(luceneVo.getVo_store_price());
		Field add_time = new Field("add_time", CommUtil.null2String(Long.valueOf(luceneVo.getVo_add_time())), Field.Store.YES, Field.Index.ANALYZED);
		Field goods_salenum = new Field("goods_salenum", CommUtil.null2String(Integer.valueOf(luceneVo.getVo_goods_salenum())), Field.Store.YES, Field.Index.ANALYZED);
		document.add(id);
		document.add(title);
		document.add(content);
		document.add(type);
		document.add(store_price);
		document.add(add_time);
		document.add(goods_salenum);
		return document;
	}

	public static void main(String[] args) {
		LuceneUtil lucence = instance();
		setIndex_path("E:\\apache-tomcat-7.0.42\\luence\\goods");
		Date d1 = new Date();
		LuceneResult list = lucence.search("专柜正品黑色时尚冬装男", 0, 0.0D, 500.0D, null, null);
		Date d2 = new Date();
		System.out.println("查询时间为：" + (d2.getTime() - d1.getTime()) + "毫秒");
		for (int i = 0; i < list.getVo_list().size(); i++) {
			LuceneVo vo = (LuceneVo) list.getVo_list().get(i);
			System.out.println("标题：" + vo.getVo_title());
			System.out.println("价格:" + vo.getVo_store_price());
			System.out.println("添加时间:" + vo.getVo_add_time());
		}
		System.out.println("查询结果为:" + list.getVo_list().size());
	}
}

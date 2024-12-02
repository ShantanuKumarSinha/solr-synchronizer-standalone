package app.shann;

import app.shann.model.Product;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, World!");

        // connecting to solr server
        String urlString = "http://localhost:8983/solr/bigboxstore";
        HttpSolrClient solr = new HttpSolrClient.Builder(urlString).build();
        //SolrJ uses a binary format, rather than XML, as its default response format. For compatibility with Solr, it’s required to explicitly invoke setParser() to XML
        solr.setParser(new XMLResponseParser());

        // add the document to solr
        createIndices(solr);

        //do a solr query
        QueryResponse response = getQueryResponse(solr);

        SolrDocumentList docList = response.getResults();
        assertEquals(docList.getNumFound(), 1);
        for (SolrDocument doc : docList) {
            assertEquals((String) doc.getFieldValue("id"), "888");
            assertEquals((Double) doc.getFieldValue("price"), (Double) 299.99);
        }
    }

    private static void createIndices(HttpSolrClient solr) {
        try {
            solr.addBean(new Product("888", "Apple iPhone 6s", "299.99"));
            solr.commit();
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }
    }

    private static QueryResponse getQueryResponse(HttpSolrClient solr) {
        try {
            SolrQuery query = new SolrQuery();
            query.set("q", "price:299.99");
            return solr.query(query);
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void assertEquals(Double price, Double aDouble) {
        System.out.println(price.equals(aDouble));
    }

    private static void assertEquals(String id, String number) {
        System.out.println(id.equals(number));
    }

    private static void assertEquals(long numFound, long i) {
        System.out.println(numFound == i);
    }
}
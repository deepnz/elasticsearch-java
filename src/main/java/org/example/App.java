package org.example;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.TransportUtils;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;

import org.elasticsearch.client.RestClient;

import javax.net.ssl.SSLContext;
import java.io.IOException;


public class App {
    public static void main(String[] args) throws IOException {

        String fingerprint = "b6ad27e2cadd7748e7eabc83a599476b025c3ed2d846f0af72d0646b9f3e4167";

        SSLContext sslContext = TransportUtils
                .sslContextFromCaFingerprint(fingerprint);

        BasicCredentialsProvider credsProv = new BasicCredentialsProvider();
        credsProv.setCredentials(
                AuthScope.ANY, new UsernamePasswordCredentials("elastic", "S_aOKCCXwTT40L8K7gQF")
        );


        RestClient restClient = RestClient
                .builder(new HttpHost("localhost", 9200, "https"))
                .setHttpClientConfigCallback(hc -> hc
                        .setSSLContext(sslContext)
                        .setDefaultCredentialsProvider(credsProv)
                )
                .build();


// Create the transport with a Jackson mapper
        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());

// And create the API client
        ElasticsearchClient esClient = new ElasticsearchClient(transport);

        Product product  = new Product ("1", "Product 1", "Product 1 description- bike");

        IndexRequest<Object> indexRequest =
                new IndexRequest.Builder<>()
                .index("products")
                .id("1")
                .document(product)
                .build();

        esClient.index(indexRequest);
//        IndexResponse response = esClient.index(g -> g
//                        .index("products")
//                        .id(product.getId())
//                .document(  product));

        String searchText = "bike";

        SearchResponse<Product> response = esClient.search(s -> s
                        .index("products")
                        .query(q -> q
                                .match(t -> t
                                        .field("id")
                                        .query(searchText)
                                )
                        ),
                Product.class
        );

//        if (response.found()) {
//            Product productRes = response.();
            System.out.println("Products- " + response);

    }
}
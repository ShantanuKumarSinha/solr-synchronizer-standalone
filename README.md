# solr-synchronizer-standalone
This application usage solr to index the application

## Setup and Requirements
* Install apache-solr 9.7
* Follow the solr set up guide here [Solr QuickStart Guide](https://solr.apache.org/guide/solr/latest/getting-started/solr-tutorial.html).

### The installation process is simple 
    just download the zip/tar package, 
    extract the contents, 
    and start the server from the command line. 
    For this article, we’ll create a Solr server with a core called ‘bigboxstore’:

### Excute below in CLI
    bin/solr start
    bin/solr create -c 'bigboxstore'

By default, Solr listens to port 8983 for incoming HTTP queries. You can verify that it is successfully launched by opening the http://localhost:8983/solr/#/bigboxstore URL in a browser and observing the Solr Dashboard.

Solr Queries you can see like this

![](https://github.com/ShantanuKumarSinha/solr-synchronizer-standalone/blob/main/src/main/resources/images/Solr_Query_Result.png "Solr Search Query") 
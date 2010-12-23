package dev;

import org.apache.jena.larq.IndexBuilderModel;
import org.apache.jena.larq.IndexBuilderString;
import org.apache.jena.larq.LARQ;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sparql.algebra.Algebra;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.vocabulary.RDF;

public class Report_LARQ_TDB {

    public static void main(String[] args) {
        Dataset ds = TDBFactory.createDataset();
        Model m = ds.getDefaultModel();
        //Model m = ModelFactory.createDefaultModel();
        //Dataset ds = DatasetFactory.create(m);
        m.add(RDF.first, RDF.first, "text");
        index(m);
        Query q = QueryFactory.create(
                "PREFIX pf:     <http://jena.hpl.hp.com/ARQ/property#>" +
                "" +
                "select * where {" +
                "?doc ?p ?lit ." +
                "(?lit ?score ) pf:textMatch '+text' ." +
//                "?x  pf:versionARQ ?y ."+
                "{} UNION { ?doc ?p ?lit .}" +
                "}"
                );
        
        Op op = Algebra.compile(q) ;
        op = Algebra.optimize(op) ;
        System.out.println(op) ;
        
        QueryExecution qe = QueryExecutionFactory.create(q, ds);
        ResultSet res = qe.execSelect();
        ResultSetFormatter.out(res);
        qe.close();
    }

    public static void index(Model m) {
        IndexBuilderModel larqBuilder = new IndexBuilderString();
        //IndexBuilderModel larqBuilder = new IndexBuilderSubject();
        StmtIterator iter = m.listStatements();
        larqBuilder.indexStatements(iter);
        larqBuilder.closeWriter();

        LARQ.setDefaultIndex(larqBuilder.getIndex());
    }
}
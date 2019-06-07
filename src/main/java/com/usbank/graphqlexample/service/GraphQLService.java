package com.usbank.graphqlexample.service;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.usbank.graphqlexample.model.Book;
import com.usbank.graphqlexample.repository.BookRepository;
import com.usbank.graphqlexample.service.datafetcher.AllBooksDataFetcher;
import com.usbank.graphqlexample.service.datafetcher.BookDataFetcher;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

@Service
public class GraphQLService {

    @Autowired
    BookRepository bookRepository;
    
	@Value("classpath:books.graphql")
	Resource resource;
	
	private GraphQL graphQL;
	@Autowired
	private AllBooksDataFetcher allBooksDataFetcher;
	@Autowired
	private BookDataFetcher bookDataFetcher;
	
	// load schema at application start up
    @PostConstruct
    private void loadSchema() throws IOException {

        //Load Books into the Book Repository
        loadDataIntoHSQL();

        // get the schema
        File schemaFile = resource.getFile();
        // parse schema
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(schemaFile);
        RuntimeWiring wiring = buildRuntimeWiring();
        GraphQLSchema schema = new SchemaGenerator().makeExecutableSchema(typeRegistry, wiring);
        graphQL = GraphQL.newGraphQL(schema).build();
    }


    private void loadDataIntoHSQL() {
 
        Stream.of(
                new Book("1", "Harry Potter", "Scholastic",
                        new String[] {
                        "JK Rowling"
                        }, "Nov 2017"),
                new Book("2", "The Dictionary", "Penguin",
                        new String[] {
                                "Merriam", "Webster"
                        }, "Jan 2000")
        ).forEach(book -> {
            bookRepository.save(book);
        });
        
       
    }

	private RuntimeWiring buildRuntimeWiring() {
		return RuntimeWiring.newRuntimeWiring().type("Query", typeWiring -> 
			typeWiring
				.dataFetcher("allBooks", allBooksDataFetcher)
				.dataFetcher("book", bookDataFetcher))
				.build();
	}
    
    public GraphQL getGraphQL() {
        return graphQL;
    }


}

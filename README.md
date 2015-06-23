# qualifier-crawler

Welcome to this demo webapp. You feed a site list and it performs a concurrent qualification for all the sites on the list. Results are persisted and can be retrieved.

## Getting started

Make sure you have jdk 8 and [Maven](https://maven.apache.org/) installed. Clone the repo and start playing with Maven:

```shell
mvn test
```

Start the application:
```shell
mvn tomcat7:run
```

### Basic usage

You have two different approaches to post a site list. In any of the cases `Content-Type` must be `application/json` and the site list should look like:

```
[
    {
        "url": "centrallecheraasturiana.es"
    },
    {
        "url": "guiafull.com"
    }
]
```

- Send a site list to and receive and immediate response with the count of crawling processes:

```
POST http://localhost:8080/sites/nowait
```
You should see something like:
```
{
    "totalEnqueuedForProcessing": 2
}
```

- Send a site list to the webapp and expect a response with the detailed view of the qualifications:

```
POST http://localhost:8080/sites
```
In that case, although the site crawling and qualification is non-blocking and performed concurrently at the server-side, you as a client will have to wait until the whole crawling/qualification is completed.

```
[
    {
        "url": "guiafull.com",
        "isMarfeelizable": false,
        "lastScanned": "2015-06-21T18:22:28.952+0000"
    },
    {
        "url": "centrallecheraasturiana.es",
        "isMarfeelizable": false,
        "lastScanned": "2015-06-21T18:22:31.215+0000"
    }
]
```

### After crawling/qualification

For demonstration purposes, the qualifications are persisted into an H2 in-memory store. A couple of useful routes have been added to the API. Just try them and see how the qualifications went.

- Retrieve all the qualified sites:
```
GET http://localhost:8080/qsites
```

- Retrieve only the sites qualified as marfeelizable:
```
GET http://localhost:8080/marfeelizable
```

## Technical hints

### Reactive, Async, non-blocking guide

The qualifier-crawler webapp takes advantage of some reactive techniques to achieve non-blocking behaviour. Those techniques, somewhat popular with Scala or Play Framework, are not so common around the servlet container world. Here we have explored what you can do with a Tomcat+Spring approach:
- Async servlet: Since Servlet API 3.0, async servlet makes possible to let the thread that handled the request back to the pool while performing whichever operation, i.e. long operations or async-by-nature operations. Spring's `DeferredResult` makes it easier to interact with an async servlet from a Future's world.
- [AsyncRestTemplate](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/client/AsyncRestTemplate.html): While it may seem an easier approach to annotate the SiteCheckerService as async, IO to retrieve the site home page would be blocking the thread. Only a pure Async client could help with that, and here is where `AsyncRestTemplate` comes to the rescue
- Java 8 [CompletableFuture](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html) class and Spring 4 [ListenableFuture](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/util/concurrent/ListenableFuture.html) interface: Those two can work together as is demostrated by the webapp. While `AsyncRestTemplate` returns a `ListenableFuture`, it feels more natural for our services to work with `CompletableFuture`. The interfaces and services become beautiful Java8 and we can wait for the completion of all the futures to do some task, if required.
- When it comes to upgrade from the current in-memory store to something more solid (and that means IO blocking) one should decide either for traditional thread pools or some asynchronous driver. JPA will become a stopper for the second option, and from the whole spring data familiy only [Spring Data Cassandra](http://projects.spring.io/spring-data-cassandra/) seems to be providing some kind of async support. If you opt to go without the safety net of Spring data, you will find some more options, specially for such NoSQL engines as MongoDB.

### Extend me

- Both the `SiteCheckerService` and `Qualifier` interfaces can be re-implemented and wired as spring beans, making thus possible to change the implementation for the whole site checking behaviour or just the qualifying responsability.
- Regarding persistence, the `QualifiedSiteRepository` is easy to change, too. To switch to another database engine is just a matter of bean configuration, thanks to the JPA abstraction.

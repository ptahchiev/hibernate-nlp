Reproduce
===========
To reproduce the issue simply run `mvn spring-boot:run`.
One of the time it will blow with this error:
```
2021-01-31 15:56:04.142  INFO 94271 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Executing step: [search-export]
2021-01-31 15:56:04.468 ERROR 94271 --- [search-export-1] ication$$EnhancerBySpringCGLIB$$cd210429 : processing product: 527 with name: Viva
2021-01-31 15:56:04.469 ERROR 94271 --- [search-export-1] ication$$EnhancerBySpringCGLIB$$cd210429 : processing product: 545 with name: Tin
2021-01-31 15:56:04.469 ERROR 94271 --- [search-export-1] ication$$EnhancerBySpringCGLIB$$cd210429 : processing product: 157 with name: Bitchip
2021-01-31 15:56:04.470 ERROR 94271 --- [search-export-1] ication$$EnhancerBySpringCGLIB$$cd210429 : processing product: 905 with name: Sub-Ex
2021-01-31 15:56:04.471 ERROR 94271 --- [search-export-1] ication$$EnhancerBySpringCGLIB$$cd210429 : processing product: 685 with name: Fixflex
2021-01-31 15:56:04.471 ERROR 94271 --- [search-export-1] ication$$EnhancerBySpringCGLIB$$cd210429 : processing product: 299 with name: Trippledex
2021-01-31 15:56:04.472 ERROR 94271 --- [search-export-1] ication$$EnhancerBySpringCGLIB$$cd210429 : processing product: 580 with name: Pannier
2021-01-31 15:56:04.473 ERROR 94271 --- [search-export-1] ication$$EnhancerBySpringCGLIB$$cd210429 : processing product: 680 with name: Tin
2021-01-31 15:56:04.473 ERROR 94271 --- [search-export-1] ication$$EnhancerBySpringCGLIB$$cd210429 : processing product: 922 with name: Sonsing
2021-01-31 15:56:04.474 ERROR 94271 --- [search-export-1] ication$$EnhancerBySpringCGLIB$$cd210429 : processing product: 922 with name: Sonsing
2021-01-31 15:56:04.475 ERROR 94271 --- [search-export-1] ication$$EnhancerBySpringCGLIB$$cd210429 : processing product: 784 with name: Aerified
2021-01-31 15:56:04.541 ERROR 94271 --- [           main] o.s.batch.core.step.AbstractStep         : Encountered an error executing step search-export in job searchExportJob

java.lang.NullPointerException: null
	at org.hibernate.collection.internal.PersistentMap.equalsSnapshot(PersistentMap.java:118) ~[hibernate-core-5.4.27.Final.jar:5.4.27.Final]
	at org.hibernate.engine.spi.CollectionEntry.dirty(CollectionEntry.java:158) ~[hibernate-core-5.4.27.Final.jar:5.4.27.Final]
	at org.hibernate.engine.spi.CollectionEntry.preFlush(CollectionEntry.java:182) ~[hibernate-core-5.4.27.Final.jar:5.4.27.Final]
	at org.hibernate.event.internal.AbstractFlushingEventListener.lambda$prepareCollectionFlushes$0(AbstractFlushingEventListener.java:194) ~[hibernate-core-5.4.27.Final.jar:5.4.27.Final]
	at org.hibernate.engine.internal.StatefulPersistenceContext.forEachCollectionEntry(StatefulPersistenceContext.java:1136) ~[hibernate-core-5.4.27.Final.jar:5.4.27.Final]
	at org.hibernate.event.internal.AbstractFlushingEventListener.prepareCollectionFlushes(AbstractFlushingEventListener.java:193) ~[hibernate-core-5.4.27.Final.jar:5.4.27.Final]
	at org.hibernate.event.internal.AbstractFlushingEventListener.flushEverythingToExecutions(AbstractFlushingEventListener.java:85) ~[hibernate-core-5.4.27.Final.jar:5.4.27.Final]
	at org.hibernate.event.internal.DefaultAutoFlushEventListener.onAutoFlush(DefaultAutoFlushEventListener.java:50) ~[hibernate-core-5.4.27.Final.jar:5.4.27.Final]

```

*Please notice that it works fine (although a lot slower) if I comment out the `taskExecutor` reference in the step, like this:*

```
        return stepBuilders.get("search-export").<ProductEntity, Map<String, Object>>chunk(200)
                        .reader(productReader)
                        .processor(processor)
                        .writer(writer)
                        .transactionAttribute(transactionAttribute)
                        //.taskExecutor(searchExportTaskExecutor).throttleLimit(4)
                        .build();
```

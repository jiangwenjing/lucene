# Lucene Example 5
# orginal scripts by Dr. Gary Munnelly
# adapted for 2020 by Colin Daly

## Build

```mvn package```

## Run Vector Space Model

```java -jar target/search-1.2.jar```

## Run BM25

```java -jar target/search-1.2.jar BM25```


## Evaluate with Vector Space Model
```cd../trec_eval-9.0.7```
```make```
```./trec_eval -m official QRelsCorrectedforTRECeval ../cran/VSM_result.txt```

## Evaluate with BM25
```./trec_eval -m official QRelsCorrectedforTRECeval ../cran/BM25_result.txt```
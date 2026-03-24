package com.luopc.platform.web.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentService {

    // 注入由Starter自动创建的EmbeddingModel
    private final EmbeddingModel embeddingModel;

    // 注入我们自己创建的EmbeddingStore Bean
    private final EmbeddingStore embeddingStore;

    @PostConstruct
    public List<String> loadSplitAndEmbed() throws IOException {
        org.springframework.core.io.Resource resource = new ClassPathResource("docs/");
        String path = Objects.requireNonNull(resource).getFile().getPath();
        Path documentPath = Paths.get(path + File.separator + "product-info.txt");
        Document document = FileSystemDocumentLoader.loadDocument(documentPath, new TextDocumentParser());

        // 2. 将文档分割成片段
        DocumentSplitter splitter = DocumentSplitters.recursive(300, 10);
        List<TextSegment> segments = splitter.split(document);

        System.out.println("Document split into " + segments.size() + " segments.");

        // 3. 将片段嵌入并存储到向量数据库中
        // LangChain4j提供了一个方便的EmbeddingStoreIngestor来处理这个流程
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(splitter) // 可以在这里也指定分割器
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();

        // 开始摄入文档
        ingestor.ingest(document);

        System.out.println("Document ingested and stored in the embedding store.");
        return segments.stream().map(TextSegment::text).collect(Collectors.toList());
    }


    public List<String> search(String query) {
        System.out.println("\n--- Performing search for query: '" + query + "' ---");

        // 1. 将用户问题也进行嵌入，得到查询向量
        Response<Embedding> queryEmbedding = embeddingModel.embed(query);

        // 2. 在向量存储中查找最相关的N个匹配项
        // 参数1: 查询向量
        // 参数2: 返回的最大结果数
        EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding.content()).build();

        List<EmbeddingMatch<TextSegment>> relevant = embeddingStore.search(request).matches();

        // 3. 打印结果
        System.out.println("Found " + relevant.size() + " relevant segments:");
        relevant.forEach(match -> {
            System.out.println("--------------------");
            System.out.println("Score: " + match.score()); // 相似度得分
            System.out.println("Text: " + match.embedded().text()); // 原始文本
        });

        return relevant.stream().map(match -> match.embedded()
                .text()).collect(Collectors.toList());
    }
}

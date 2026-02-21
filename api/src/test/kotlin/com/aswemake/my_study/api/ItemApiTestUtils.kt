package com.aswemake.my_study.api

import com.aswemake.my_study.CacheStrategy
import com.aswemake.my_study.domain.command.ItemCreateCommand
import com.aswemake.my_study.domain.command.ItemUpdateCommand
import com.aswemake.my_study.service.dto.ItemPageResponse
import com.aswemake.my_study.service.dto.ItemResponse
import org.springframework.http.MediaType
import org.springframework.web.client.RestClient

object ItemApiTestUtils {
    var restClient: RestClient = RestClient.create("http://localhost:8080")

    fun read(cacheStrategy: CacheStrategy, itemId: Long): ItemResponse? {
        return restClient.get()
            .uri("/cache-strategy/${cacheStrategy.name}/items/${itemId}")
            .retrieve()
            .body(ItemResponse::class.java)
    }

    fun readAll(cacheStrategy: CacheStrategy, page: Long, pageSize: Long): ItemPageResponse {
        return restClient.get()
            .uri("/cache-strategy/${cacheStrategy.name}/items?page=${page}&pageSize=${pageSize}")
            .retrieve()
            .body(ItemPageResponse::class.java)!!
    }

    fun create(cacheStrategy: CacheStrategy, command: ItemCreateCommand): ItemResponse {
        return restClient.post()
            .uri("/cache-strategy/${cacheStrategy.name}/items")
            .body(command)
            .contentType(MediaType.APPLICATION_JSON)
            .retrieve()
            .body(ItemResponse::class.java)!!
    }

    fun update(cacheStrategy: CacheStrategy, itemId: Long, command: ItemUpdateCommand): ItemResponse {
        return restClient.put()
            .uri("/cache-strategy/${cacheStrategy.name}/items/${itemId}")
            .body(command)
            .contentType(MediaType.APPLICATION_JSON)
            .retrieve()
            .body(ItemResponse::class.java)!!
    }

    fun delete(cacheStrategy: CacheStrategy, itemId: Long) {
        restClient.delete()
            .uri("/cache-strategy/${cacheStrategy.name}/items/${itemId}")
            .retrieve()
            .toBodilessEntity()
    }
}
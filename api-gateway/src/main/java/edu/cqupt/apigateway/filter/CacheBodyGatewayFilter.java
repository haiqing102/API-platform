package edu.cqupt.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class CacheBodyGatewayFilter implements Ordered, GlobalFilter {

	public static final String CACHE_REQUEST_BODY_OBJECT_KEY = "cachedRequestBodyObject";

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		if (exchange.getRequest().getHeaders().getContentType() == null) {
			return chain.filter(exchange);
		} else {
			return DataBufferUtils.join(exchange.getRequest().getBody())
					.flatMap(dataBuffer -> {
						DataBufferUtils.retain(dataBuffer);
						Flux<DataBuffer> cachedFlux = Flux
								.defer(() -> Flux.just(dataBuffer.slice(0, dataBuffer.readableByteCount())));
						ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(
								exchange.getRequest()) {
							@Override
							public Flux<DataBuffer> getBody() {
								return cachedFlux;
							}

						};
						exchange.getAttributes().put(CacheBodyGatewayFilter.CACHE_REQUEST_BODY_OBJECT_KEY, cachedFlux);

						return chain.filter(exchange.mutate().request(mutatedRequest).build());
					});
		}
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}
}
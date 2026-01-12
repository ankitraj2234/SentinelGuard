package com.sentinelguard.security.ratelimit

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * RateLimiter: Token bucket rate limiter for API calls.
 * 
 * Prevents abuse of external APIs by limiting request frequency.
 * Implements token bucket algorithm with:
 * - Per-endpoint rate limits
 * - Graceful backoff with 429-style responses
 * - Configurable thresholds
 * 
 * OWASP Best Practice: Protects against resource exhaustion attacks.
 */
@Singleton
class RateLimiter @Inject constructor() {
    
    companion object {
        // Default limits (requests per minute)
        const val DEFAULT_REQUESTS_PER_MINUTE = 30
        const val DEFAULT_BURST_SIZE = 5
        
        // Specific endpoint limits
        const val GOOGLE_GEOLOCATION_RPM = 10      // Conservative for free tier
        const val OPENCELLID_RPM = 20
        const val MOZILLA_LS_RPM = 30
        const val UNWIRED_LABS_RPM = 15
    }
    
    /**
     * Token bucket state for an endpoint
     */
    private data class TokenBucket(
        var tokens: Double,
        var lastRefillTime: Long,
        val maxTokens: Double,
        val refillRate: Double // tokens per millisecond
    )
    
    /**
     * Rate limit result
     */
    data class RateLimitResult(
        val allowed: Boolean,
        val remainingTokens: Int,
        val retryAfterMs: Long = 0,
        val message: String = ""
    )
    
    // Buckets per endpoint
    private val buckets = ConcurrentHashMap<String, TokenBucket>()
    private val mutex = Mutex()
    
    // Predefined rate limits for known endpoints
    private val endpointLimits = mapOf(
        "google_geolocation" to GOOGLE_GEOLOCATION_RPM,
        "opencellid" to OPENCELLID_RPM,
        "mozilla_ls" to MOZILLA_LS_RPM,
        "unwired_labs" to UNWIRED_LABS_RPM
    )
    
    /**
     * Check if a request is allowed for the given endpoint.
     * Consumes a token if allowed.
     * 
     * @param endpoint Identifier for the API endpoint
     * @param requestsPerMinute Optional custom rate limit
     * @return RateLimitResult indicating if request is allowed
     */
    suspend fun checkRateLimit(
        endpoint: String,
        requestsPerMinute: Int = endpointLimits[endpoint] ?: DEFAULT_REQUESTS_PER_MINUTE
    ): RateLimitResult = mutex.withLock {
        val now = System.currentTimeMillis()
        
        // Get or create bucket
        val bucket = buckets.getOrPut(endpoint) {
            createBucket(requestsPerMinute)
        }
        
        // Refill tokens based on elapsed time
        refillBucket(bucket, now)
        
        // Check if we have tokens
        if (bucket.tokens >= 1.0) {
            bucket.tokens -= 1.0
            RateLimitResult(
                allowed = true,
                remainingTokens = bucket.tokens.toInt(),
                message = "Request allowed"
            )
        } else {
            // Calculate retry-after
            val tokensNeeded = 1.0 - bucket.tokens
            val retryAfterMs = (tokensNeeded / bucket.refillRate).toLong()
            
            RateLimitResult(
                allowed = false,
                remainingTokens = 0,
                retryAfterMs = retryAfterMs,
                message = "Rate limit exceeded. Retry after ${retryAfterMs}ms"
            )
        }
    }
    
    /**
     * Check rate limit without consuming a token (peek)
     */
    suspend fun peekRateLimit(endpoint: String): RateLimitResult = mutex.withLock {
        val bucket = buckets[endpoint] ?: return@withLock RateLimitResult(
            allowed = true,
            remainingTokens = DEFAULT_BURST_SIZE
        )
        
        refillBucket(bucket, System.currentTimeMillis())
        
        RateLimitResult(
            allowed = bucket.tokens >= 1.0,
            remainingTokens = bucket.tokens.toInt()
        )
    }
    
    /**
     * Reset rate limit for an endpoint (e.g., after long inactivity)
     */
    suspend fun resetRateLimit(endpoint: String): Unit = mutex.withLock {
        buckets.remove(endpoint)
        Unit
    }
    
    /**
     * Get current status of all rate limiters
     */
    suspend fun getStatus(): Map<String, RateLimitResult> = mutex.withLock {
        val now = System.currentTimeMillis()
        buckets.mapValues { (_, bucket) ->
            refillBucket(bucket, now)
            RateLimitResult(
                allowed = bucket.tokens >= 1.0,
                remainingTokens = bucket.tokens.toInt()
            )
        }
    }
    
    private fun createBucket(requestsPerMinute: Int): TokenBucket {
        val maxTokens = minOf(requestsPerMinute.toDouble(), DEFAULT_BURST_SIZE.toDouble() * 2)
        val refillRate = requestsPerMinute.toDouble() / 60000.0 // tokens per millisecond
        
        return TokenBucket(
            tokens = maxTokens, // Start with full bucket
            lastRefillTime = System.currentTimeMillis(),
            maxTokens = maxTokens,
            refillRate = refillRate
        )
    }
    
    private fun refillBucket(bucket: TokenBucket, now: Long) {
        val elapsed = now - bucket.lastRefillTime
        if (elapsed > 0) {
            val tokensToAdd = elapsed * bucket.refillRate
            bucket.tokens = minOf(bucket.tokens + tokensToAdd, bucket.maxTokens)
            bucket.lastRefillTime = now
        }
    }
}

/**
 * Extension function for convenient rate limiting
 */
suspend inline fun <T> RateLimiter.withRateLimit(
    endpoint: String,
    crossinline onRateLimited: (RateLimiter.RateLimitResult) -> T,
    crossinline action: suspend () -> T
): T {
    val result = checkRateLimit(endpoint)
    return if (result.allowed) {
        action()
    } else {
        onRateLimited(result)
    }
}

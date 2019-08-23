package com.zd.jraft.node;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;

import java.util.Collections;
import java.util.Map;

/**
 * Node 性能指标
 */
public class NodeMetrics {

    private final MetricRegistry metrics;

    public NodeMetrics(final boolean enableMetrics) {
        if (enableMetrics) {
            this.metrics = new MetricRegistry();
        } else {
            this.metrics = null;
        }
    }

    public Map<String, Metric> getMetrics() {
        if (this.metrics != null) {
            return this.metrics.getMetrics();
        }
        return Collections.emptyMap();
    }

    public MetricRegistry getMetricRegistry() {
        return this.metrics;
    }

    public boolean isEnabled() {
        return this.metrics != null;
    }

    /**
     * 记录操作时间
     */
    public void recordTimes(final String key, final long times) {
        if (this.metrics != null) {
            this.metrics.counter(key).inc(times);
        }
    }

}

package com.zd.jraft.conf;

import com.zd.jraft.entity.ConfigurationEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;

public class ConfigurationManager {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationManager.class);

    private final LinkedList<ConfigurationEntry> configurations = new LinkedList<>();

    /**
     * @return 添加配置，如果不为空则检索最后一个元素，比较他们index
     */
    public boolean add(final ConfigurationEntry entry) {
        if (!this.configurations.isEmpty()) {
            if (this.configurations.peekLast().getId().getIndex() >= entry.getId().getIndex()) {
                LOG.error("Did you forget to call truncateSuffix before the last log index goes back.");
                return false;
            }
        }
        return this.configurations.add(entry);
    }

}

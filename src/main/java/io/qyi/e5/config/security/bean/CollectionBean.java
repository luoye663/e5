package io.qyi.e5.config.security.bean;

import lombok.Data;
import org.springframework.security.access.ConfigAttribute;

import java.util.Collection;

/**
 * @program: wds
 * @description:
 * @author: 落叶随风
 * @create: 2020-07-09 00:59
 **/
@Data
public class CollectionBean {
    private String url;
    private Collection<ConfigAttribute> configAttributes;

    public CollectionBean(String url, Collection<ConfigAttribute> configAttributes) {
        this.url = url;
        this.configAttributes = configAttributes;
    }
}

package com.conversant.chump.common;

import lombok.Data;
import lombok.experimental.Builder;
import org.apache.camel.Processor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jhill on 31/12/14.
 */
@Data
@Builder
public class ChumpOperation {

    private final Boolean trx;
    private final RestOperation rest;
    private final String uri;
    private final List<Processor> preProcessors;
    private final List<ProcessToPair> to;
    private final List<Processor> postProcessors;

    public ChumpOperation(Boolean trx, RestOperation rest, String uri, List<Processor> preProcessors, List<ProcessToPair> to, List<Processor> postProcessors) {
        this.trx = trx != null ? trx : true;
        this.rest = rest;
        this.uri = uri != null ? uri : (rest != null ? rest.getUri() : null);
        this.to = to != null ? to : new ArrayList<>();

        this.preProcessors = new ArrayList<>();
        if (preProcessors != null)
            this.preProcessors.addAll(preProcessors);

        this.postProcessors = new ArrayList<>();
        if (postProcessors != null)
            this.postProcessors.addAll(postProcessors);
    }

    public static ProcessToPair single(String to) {
        return new ProcessToPair(null, to);
    }

    public static ProcessToPair pair(Processor processor, String to) {
        return new ProcessToPair(processor, to);
    }

    @Data
    public static class ProcessToPair {
        private final Processor processor;
        private final String to;
        private Filter filter;

        public ProcessToPair excludable(String name) {
            setFilter(Filter.exclude(name));
            return this;
        }

        public ProcessToPair includable(String name) {
            setFilter(Filter.include(name));
            return this;
        }
    }

    @Data
    public static class Filter {

        public enum Type {INCLUDE, EXCLUDE}
        public static final String PREFIX = "filter.";

        private final Type type;
        private final String name;

        /**
         * Explicit include filter. If supplied pair will be include, else it will be skipped.
         * @param name Name of filter
         * @return Filter
         */
        public static Filter include(String name) {
            return new Filter(Type.INCLUDE, PREFIX + "include." + name);
        }

        /**
         * Explicit exclude filter. If supplied pair will be excluded, else if will be processed.
         * @param name Name of filter
         * @return Filter
         */
        public static Filter exclude(String name) {
            return new Filter(Type.EXCLUDE, PREFIX + "exclude." + name);
        }
    }
}

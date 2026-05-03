package pipelinestater;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class plAuthState {
    public List<CookieData> cookies;
    public List<OriginData> origins;
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CookieData {
        public String name;
        public String value;
        public String domain;
        public String path;
        public double expires;
        public boolean httpOnly;
        public boolean secure;
        public String sameSite;
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OriginData {
        public String origin;
        public List<LocalStorageItem> localStorage;
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LocalStorageItem {
        public String name;
        public String value;
    }
}
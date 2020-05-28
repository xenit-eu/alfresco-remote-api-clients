package eu.xenit.alfresco.client.solrapi.api.model;

import java.io.InputStream;
import java.util.function.Supplier;

public class GetTextContentResponse implements Supplier<InputStream> {
    InputStream content;
    SolrApiContentStatus status;
    String transformException;
    String transformStatusStr;
    Long transformDuration;
    String contentEncoding;

    @Override
    public InputStream get() {
        return content;
    }

    // no constructor, because it depends on the framework used

    public static enum SolrApiContentStatus {
        NOT_MODIFIED, OK, NO_TRANSFORM, NO_CONTENT, UNKNOWN, TRANSFORM_FAILED, GENERAL_FAILURE;

        public static SolrApiContentStatus getStatus(String statusStr) {
            if (statusStr.equals("ok")) {
                return OK;
            } else if (statusStr.equals("transformFailed")) {
                return TRANSFORM_FAILED;
            } else if (statusStr.equals("noTransform")) {
                return NO_TRANSFORM;
            } else if (statusStr.equals("noContent")) {
                return NO_CONTENT;
            } else {
                return UNKNOWN;
            }
        }
    }

    public InputStream getContent() {
        return content;
    }

    public void setContent(InputStream content) {
        this.content = content;
    }

    public SolrApiContentStatus getStatus() {
        return status;
    }

    public void setStatus(SolrApiContentStatus status) {
        this.status = status;
    }

    public String getTransformException() {
        return transformException;
    }

    public void setTransformException(String transformException) {
        this.transformException = transformException;
    }

    public String getTransformStatusStr() {
        return transformStatusStr;
    }

    public void setTransformStatusStr(String transformStatusStr) {
        this.transformStatusStr = transformStatusStr;
    }

    public Long getTransformDuration() {
        return transformDuration;
    }

    public void setTransformDuration(Long transformDuration) {
        this.transformDuration = transformDuration;
    }

    public String getContentEncoding() {
        return contentEncoding;
    }

    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }
}

